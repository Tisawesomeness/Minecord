package codes.tis.minecord.testutil.assertion

import codes.tis.minecord.util.Err
import codes.tis.minecord.util.Ok
import io.kotest.core.spec.style.FunSpec

class ResultAssertionsTest : FunSpec({
    context("shouldBeOk") {
        test("passes for Ok") {
            Ok("hello").shouldBeOk()
        }

        test("fails for Err") {
            Err("error").shouldNotBeOk()
        }

        test("passes for Ok<Int>") {
            Ok(42).shouldBeOk()
        }
    }

    context("shouldHaveValue") {
        test("passes for matching value") {
            Ok("hello").shouldHaveValue("hello")
        }

        test("fails for different value") {
            Ok("hello").shouldNotHaveValue("world")
        }

        test("passes for Int value") {
            Ok(42).shouldHaveValue(42)
        }
    }

    context("shouldBeErr") {
        test("passes for Err") {
            Err("error").shouldBeErr()
        }

        test("fails for Ok") {
            Ok("hello").shouldNotBeErr()
        }

        test("passes for Err<String>") {
            Err("fail").shouldBeErr()
        }
    }

    context("shouldHaveErr") {
        test("passes for matching error") {
            Err("error").shouldHaveErr("error")
        }

        test("fails for different error") {
            Err("error").shouldNotHaveErr("other")
        }

        test("passes for String error") {
            Err("fail").shouldHaveErr("fail")
        }
    }
})