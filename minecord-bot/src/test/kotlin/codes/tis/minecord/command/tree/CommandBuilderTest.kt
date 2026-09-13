package codes.tis.minecord.command.tree

import codes.tis.minecord.command.argument.DoubleArgument
import codes.tis.minecord.command.argument.LongArgument
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.types.shouldBeInstanceOf

class CommandBuilderTest : FunSpec({

    test("genericCommand topLevel Kdoc example compiles and creates expected tree") {
        val tree = genericCommand("ping").topLevel {
            command { }
        }

        tree.name.toString() shouldEqual "ping"
        val topLevel = tree.shouldBeInstanceOf<TopLevel<*>>()
        topLevel.command.shouldBeInstanceOf<Command0<*>>()
    }

    test("genericCommand grouped Kdoc example compiles and creates expected tree") {
        val tree = genericCommand("random").grouped {
            subcommandGroup("number") {
                subcommand("uniform") {
                    command(
                        required("min", LongArgument),
                        required("max", LongArgument),
                    ) { _, _, _ -> }
                }
                subcommand("normal") {
                    command(
                        optional("mean", DoubleArgument),
                        optional("stddev", DoubleArgument),
                    ) { _, _, _ -> }
                }
            }
            subcommand("uuid") {
                command { }
            }
        }

        tree.name.toString() shouldEqual "random"
        val grouped = tree.shouldBeInstanceOf<Grouped<*>>()
        grouped.subcommandGroupings.size shouldEqual 2

        val numberGroup = grouped.subcommandGroupings[0].shouldBeInstanceOf<SubcommandGroup<*>>()
        numberGroup.name.toString() shouldEqual "number"
        numberGroup.subcommands.size shouldEqual 2
        numberGroup.subcommands[0].name.toString() shouldEqual "uniform"
        numberGroup.subcommands[1].name.toString() shouldEqual "normal"

        val uuidSubcommand = grouped.subcommandGroupings[1].shouldBeInstanceOf<Subcommand<*>>()
        uuidSubcommand.name.toString() shouldEqual "uuid"
    }

    test("grouped with no subcommands throws IllegalStateException") {
        shouldThrow<IllegalStateException> {
            genericCommand("test").grouped { }
        }
    }
})
