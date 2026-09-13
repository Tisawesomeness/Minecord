package codes.tis.minecord.command.tree

import codes.tis.minecord.command.CommandContext
import codes.tis.minecord.command.DiscordContext
import codes.tis.minecord.command.argument.ChannelArgument
import codes.tis.minecord.command.argument.DoubleArgument
import codes.tis.minecord.command.argument.LongArgument
import codes.tis.minecord.command.argument.RoleArgument
import codes.tis.minecord.command.argument.UserArgument
import codes.tis.minecord.command.discordCommand
import codes.tis.minecord.testutil.TestContext
import codes.tis.minecord.testutil.TestResolver
import codes.tis.minecord.testutil.assertion.shouldBeErr
import codes.tis.minecord.testutil.assertion.shouldBeOk
import codes.tis.minecord.util.unwrap
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

val pingCommand: CommandTree<CommandContext> = genericCommand("ping").topLevel {
    command { ctx ->
        ctx.reply("pong")
    }
}

val randomCommand: CommandTree<CommandContext> = genericCommand("random").grouped {
    subcommandGroup("number") {
        subcommand("uniform") {
            command(
                required("min", LongArgument),
                required("max", LongArgument),
            ) { ctx, min, max ->
                ctx.reply("uniform $min $max")
            }
        }
        subcommand("normal") {
            command(
                optional("mean", DoubleArgument),
                optional("stddev", DoubleArgument),
            ) { ctx, mean, stddev ->
                ctx.reply("normal $mean $stddev")
            }
        }
    }
    subcommand("uuid") {
        command { ctx ->
            ctx.reply("uuid")
        }
    }
}

val setCommand: CommandTree<CommandContext> = genericCommand("set").topLevel {
    command(
        required("value", LongArgument),
    ) { ctx, value ->
        ctx.reply("value $value")
    }
}

val permissionsCommand: CommandTree<DiscordContext> = discordCommand("permissions").grouped {
    subcommandGroup("user") {
        subcommand("get") {
            command(
                required("user", UserArgument),
                optional("channel", ChannelArgument),
            ) { _, _, _ -> }
        }
        subcommand("edit") {
            command(
                required("user", UserArgument),
                optional("channel", ChannelArgument),
            ) { _, _, _ -> }
        }
    }
    subcommandGroup("role") {
        subcommand("get") {
            command(
                required("role", RoleArgument),
                optional("channel", ChannelArgument),
            ) { _, _, _ -> }
        }
        subcommand("edit") {
            command(
                required("role", RoleArgument),
                optional("channel", ChannelArgument),
            ) { _, _, _ -> }
        }
    }
}

class CommandTreeTest : FunSpec({
    context("name validation") {
        test("genericCommand with empty name throws") {
            shouldThrow<IllegalArgumentException> {
                genericCommand("").topLevel { command { _ -> } }
            }
        }

        test("discordCommand with empty name throws") {
            shouldThrow<IllegalArgumentException> {
                discordCommand("").topLevel { command { _ -> } }
            }
        }

        test("genericCommand with invalid name throws") {
            shouldThrow<IllegalArgumentException> {
                genericCommand("invalid name").topLevel { command { _ -> } }
            }
        }

        test("genericCommand with empty subcommand group name throws") {
            shouldThrow<IllegalArgumentException> {
                genericCommand("valid").grouped {
                    subcommandGroup("") {
                        subcommand("valid") { command { _ -> } }
                    }
                }
            }
        }

        test("genericCommand with empty subcommand name throws") {
            shouldThrow<IllegalArgumentException> {
                genericCommand("valid").grouped {
                    subcommand("") { command { _ -> } }
                }
            }
        }

        test("genericCommand with empty subcommand name in group throws") {
            shouldThrow<IllegalArgumentException> {
                genericCommand("valid").grouped {
                    subcommandGroup("group") {
                        subcommand("") { command { _ -> } }
                    }
                }
            }
        }
    }

    context("command execution") {
        test("topLevel ping command parses and executes correctly") {
            val resolver = TestResolver()

            val parseResult = pingCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("pong")
        }

        test("grouped random command uniform subcommand parses and executes with required args") {
            val resolver = TestResolver(
                options = mapOf("min" to "1", "max" to "10"),
                subcommandGroup = "number",
                subcommand = "uniform",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("uniform 1 10")
        }

        test("grouped random command normal subcommand parses and executes with provided args") {
            val resolver = TestResolver(
                options = mapOf("mean" to "3.14", "stddev" to "2.71"),
                subcommandGroup = "number",
                subcommand = "normal",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("normal 3.14 2.71")
        }

        test("grouped random command normal subcommand parses and executes without provided args") {
            val resolver = TestResolver(
                subcommandGroup = "number",
                subcommand = "normal",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("normal null null")
        }

        test("grouped random command normal subcommand with one omitted optional option resolves to null") {
            val resolver = TestResolver(
                options = mapOf("mean" to "3.14"),
                subcommandGroup = "number",
                subcommand = "normal",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("normal 3.14 null")
        }

        test("grouped random command uuid subcommand parses and executes correctly") {
            val resolver = TestResolver(subcommand = "uuid")

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("uuid")
        }

        test("grouped command with no subcommands throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                genericCommand("test").grouped { }
            }
        }

        test("topLevel command with required int argument parses and executes correctly") {
            val resolver = TestResolver(options = mapOf("value" to "42"))

            val parseResult = setCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("value 42")
        }

        test("topLevel ping command with extra options still parses correctly") {
            val resolver = TestResolver(options = mapOf("extra" to "1"))

            val parseResult = pingCommand.parse(resolver)
            parseResult.shouldBeOk()

            val handleFunc = parseResult.unwrap()
            val context = TestContext()
            handleFunc(context)

            context.replies shouldBe listOf("pong")
        }

        test("topLevel set command with no required option fails parsing") {
            val resolver = TestResolver()

            val parseResult = setCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("topLevel set command with invalid option type fails parsing") {
            val resolver = TestResolver(options = mapOf("value" to "not_an_int"))

            val parseResult = setCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("grouped random command uniform subcommand with missing required option fails parsing") {
            val resolver = TestResolver(
                options = mapOf("min" to "1"),
                subcommandGroup = "number",
                subcommand = "uniform",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("grouped random command uniform subcommand with invalid option type fails parsing") {
            val resolver = TestResolver(
                options = mapOf("min" to "abc", "max" to "10"),
                subcommandGroup = "number",
                subcommand = "uniform",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("grouped random command with invalid subcommand group fails parsing") {
            val resolver = TestResolver(
                options = mapOf("min" to "1", "max" to "10"),
                subcommandGroup = "invalid",
                subcommand = "uniform",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("grouped random command with invalid subcommand fails parsing") {
            val resolver = TestResolver(
                options = mapOf("min" to "1", "max" to "10"),
                subcommandGroup = "number",
                subcommand = "invalid",
            )

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeErr()
        }

        test("grouped random command with no subcommand fails parsing") {
            val resolver = TestResolver()

            val parseResult = randomCommand.parse(resolver)
            parseResult.shouldBeErr()
        }
    }

    context("toString") {
        test("topLevel command toString returns correct path with no options") {
            pingCommand.toString() shouldBe "/ping"
        }

        test("topLevel command toString returns correct path with required option") {
            setCommand.toString() shouldBe "/set <value: Long>"
        }

        test("grouped command toString returns correct paths for subcommands") {
            randomCommand.toString() shouldBe
                """
                /random number uniform <min: Long> <max: Long>
                /random number normal [<mean: Double>] [<stddev: Double>]
                /random uuid
                """.trimIndent()
        }

        test("grouped command toString returns correct paths with required and optional options") {
            permissionsCommand.toString() shouldBe
                """
                /permissions user get <user: User> [<channel: Channel>]
                /permissions user edit <user: User> [<channel: Channel>]
                /permissions role get <role: Role> [<channel: Channel>]
                /permissions role edit <role: Role> [<channel: Channel>]
                """.trimIndent()
        }

        test("subcommand group toString returns paths for that group") {
            val userGroup = (permissionsCommand as Grouped<*>).subcommandGroupings.list[0]
            userGroup.toString() shouldBe
                """
                user get <user: User> [<channel: Channel>]
                user edit <user: User> [<channel: Channel>]
                """.trimIndent()
        }

        test("subcommand toString returns path for that subcommand") {
            val userGroup = (permissionsCommand as Grouped<*>).subcommandGroupings.list[0] as SubcommandGroup<*>
            val getSubcommand = userGroup.subcommands[0]
            getSubcommand.toString() shouldBe "get <user: User> [<channel: Channel>]"
        }
    }
})
