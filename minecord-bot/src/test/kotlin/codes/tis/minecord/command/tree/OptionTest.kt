package codes.tis.minecord.command.tree

import codes.tis.minecord.command.argument.LongArgument
import codes.tis.minecord.command.argument.StringArgument
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual

class OptionTest : FunSpec({
    test("RequiredOption.toString returns correct format") {
        val option = RequiredOption("name", StringArgument)
        option.toString() shouldEqual "<name: String>"
    }

    test("OptionalOption.toString returns correct format") {
        val option = OptionalOption("name", StringArgument)
        option.toString() shouldEqual "[<name: String>]"
    }

    test("RequiredOption.toString returns correct format with LongArgument") {
        val option = RequiredOption("value", LongArgument)
        option.toString() shouldEqual "<value: Long>"
    }

    test("OptionalOption.toString returns correct format with LongArgument") {
        val option = OptionalOption("value", LongArgument)
        option.toString() shouldEqual "[<value: Long>]"
    }
})
