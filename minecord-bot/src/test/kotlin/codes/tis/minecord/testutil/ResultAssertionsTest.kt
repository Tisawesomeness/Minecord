package codes.tis.minecord.testutil

import codes.tis.minecord.util.Err
import codes.tis.minecord.util.Ok
import io.kotest.core.spec.style.FunSpec

class ResultAssertionsTest : FunSpec({
    test("shouldBeOk passes for Ok") {
        Ok("hello").shouldBeOk()
    }

    test("shouldBeOk fails for Err") {
        Err("error").shouldNotBeOk()
    }

    test("shouldHaveValue passes for matching value") {
        Ok("hello").shouldHaveValue("hello")
    }

    test("shouldHaveValue fails for different value") {
        Ok("hello").shouldNotHaveValue("world")
    }

    test("shouldBeErr passes for Err") {
        Err("error").shouldBeErr()
    }

    test("shouldBeErr fails for Ok") {
        Ok("hello").shouldNotBeErr()
    }

    test("shouldHaveErr passes for matching error") {
        Err("error").shouldHaveErr("error")
    }

    test("shouldHaveErr fails for different error") {
        Err("error").shouldNotHaveErr("other")
    }

    test("shouldBeOk passes for Ok<Int>") {
        Ok(42).shouldBeOk()
    }

    test("shouldHaveValue passes for Int value") {
        Ok(42).shouldHaveValue(42)
    }

    test("shouldBeErr passes for Err<String>") {
        Err("fail").shouldBeErr()
    }

    test("shouldHaveErr passes for String error") {
        Err("fail").shouldHaveErr("fail")
    }
})
