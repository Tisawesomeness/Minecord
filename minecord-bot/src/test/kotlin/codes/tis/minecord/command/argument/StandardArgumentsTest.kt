package codes.tis.minecord.command.argument

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.testutil.assertion.shouldHaveErr
import codes.tis.minecord.testutil.assertion.shouldHaveValue
import io.kotest.core.spec.style.FunSpec

class StandardArgumentsTest : FunSpec({
    context("StringArgument") {
        test("resolve returns Ok with raw value") {
            StringArgument.resolve("name", "hello").shouldHaveValue("hello")
        }

        test("resolve returns Ok even for empty string") {
            StringArgument.resolve("name", "").shouldHaveValue("")
        }
    }

    context("BooleanArgument") {
        test("resolve(name, raw) returns Ok for 'true'") {
            BooleanArgument.resolve("flag", "true").shouldHaveValue(true)
        }

        test("BooleanArgument.resolve(name, raw) returns Ok for 'false' (case-insensitive)") {
            BooleanArgument.resolve("flag", "true").shouldHaveValue(true)
            BooleanArgument.resolve("flag", "FALSE").shouldHaveValue(false)
            BooleanArgument.resolve("flag", "False").shouldHaveValue(false)
        }

        test("BooleanArgument.resolve(name, raw) returns Err for invalid boolean string") {
            val result = BooleanArgument.resolve("flag", "yes")
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_boolean").format("flag", "yes"))
        }

        test("BooleanArgument.resolve(name, raw, value) returns Ok for non-null value") {
            BooleanArgument.resolve("flag", "raw", true).shouldHaveValue(true)
            BooleanArgument.resolve("flag", "raw", false).shouldHaveValue(false)
        }

        test("BooleanArgument.resolve(name, raw, value) returns Err for null value") {
            val result = BooleanArgument.resolve("flag", "raw", null)
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_boolean").format("flag", "raw"))
        }
    }

    context("LongArgument") {
        test("resolve(name, raw) returns Ok for valid long string") {
            LongArgument.resolve("number", "12345678901234").shouldHaveValue(12345678901234L)
        }

        test("resolve(name, raw) returns Err for invalid long string") {
            val result = LongArgument.resolve("number", "abc")
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_long").format("number", "abc"))
        }

        test("resolve(name, raw, value) returns Ok for non-null value") {
            LongArgument.resolve("number", "raw", 42L).shouldHaveValue(42L)
        }

        test("resolve(name, raw, value) returns Err for null value") {
            val result = LongArgument.resolve("number", "raw", null)
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_long").format("number", "raw"))
        }
    }

    context("DoubleArgument") {
        test("resolve(name, raw) returns Ok for valid double string") {
            DoubleArgument.resolve("number", "3.14159265358979").shouldHaveValue(3.14159265358979)
        }

        test("resolve(name, raw) returns Err for invalid double string") {
            val result = DoubleArgument.resolve("number", "abc")
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_double").format("number", "abc"))
        }

        test("resolve(name, raw, value) returns Ok for non-null value") {
            DoubleArgument.resolve("number", "raw", 3.14159265358979).shouldHaveValue(3.14159265358979)
        }

        test("resolve(name, raw, value) returns Err for null value") {
            val result = DoubleArgument.resolve("number", "raw", null)
            result.shouldHaveErr(TranslationKey("minecord.error.invalid_double").format("number", "raw"))
        }
    }
})
