package codes.tis.minecord.command.tree

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.equals.shouldEqual

class CommandNameTest : FunSpec({
    test("make rejects null") {
        CommandName.make(null) shouldEqual null
    }

    context("make accepts valid input") {
        withTests(
            "a",
            "a".repeat(32),
            "with-hyphen",
            "with_underscore",
            "café",
            "सर्वेन्द्रिय",
            "ไทย",
            "1command",
        ) { input ->
            CommandName.make(input)?.toString() shouldEqual input
        }
    }

    context("make rejects invalid input") {
        withTests(
            nameFn = { "input `$it`" },
            "",
            "with space",
            "with@symbol",
            "a".repeat(33),
        ) { input ->
            CommandName.make(input) shouldEqual null
        }
    }

    test("toString returns the input string") {
        val name = CommandName.make("ping")
        name?.toString() shouldEqual "ping"
    }

    test("must returns a CommandName for valid input") {
        val name = CommandName.must("ping")
        name.toString() shouldEqual "ping"
    }

    context("must throws IllegalArgumentException for invalid input") {
        withTests(
            nameFn = { "input `$it`" },
            "",
            "with space",
            "a".repeat(33),
        ) { input ->
            shouldThrow<IllegalArgumentException> {
                CommandName.must(input)
            }
        }
    }
})
