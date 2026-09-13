package codes.tis.minecord.util

import codes.tis.minecord.testutil.assertion.shouldHaveErr
import codes.tis.minecord.testutil.assertion.shouldHaveValue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual

class ResultTest : FunSpec({
    context("orErr") {
        test("with non-null value returns Ok") {
            val result = 42.orErr { "error" }
            result.shouldHaveValue(42)
        }

        test("with null value returns Err") {
            val result: Result<Int, String> = null.orErr { "error" }
            result.shouldHaveErr("error")
        }

        test("error function is not evaluated when value is not null") {
            var evaluated = false
            val _ = 42.orErr {
                evaluated = true
                "error"
            }
            evaluated shouldEqual false
        }
    }

    context("unwrap") {
        test("returns value when Ok") {
            Ok(42).unwrap() shouldEqual 42
        }

        test("throws error when Err is Throwable") {
            val exception = RuntimeException("boom")
            shouldThrow<RuntimeException> { Err(exception).unwrap<Int, Throwable>() } shouldEqual exception
        }

        test("throws IllegalStateException when Err is not Throwable") {
            shouldThrow<IllegalStateException> { Err("failure").unwrap<Int, String>() }
            val thrown: IllegalStateException = shouldThrow<IllegalStateException> { Err("failure").unwrap<Int, String>() }
            thrown.message shouldEqual "failure"
        }
    }

    context("notNull") {
        test("Ok.notNull returns this Ok when value is not null") {
            val result: Result<Int, String> = Ok(42).notNull { "error" }
            result.shouldHaveValue(42)
        }

        test("Ok.notNull returns Err when value is null") {
            val result: Result<Int?, String> = Ok(null).notNull { "error" }
            result.shouldHaveErr("error")
        }

        test("Ok.notNull func is not evaluated when value is not null") {
            var evaluated = false
            val _ = Ok(42).notNull {
                evaluated = true
                "error"
            }
            evaluated shouldEqual false
        }

        test("Err.notNull returns the Err unchanged") {
            val result: Result<Int?, String> = Err("error").notNull { "other error" }
            result.shouldHaveErr("error")
        }
    }

    context("and") {
        test("Ok.and applies function and returns its Result") {
            val result: Result<Int, String> = Ok(2).and { Ok(it * 3) }
            result.shouldHaveValue(6)
        }

        test("Ok.and with Err function returns that Err") {
            val result: Result<Int, String> = Ok(2).and { Err("fail") }
            result.shouldHaveErr("fail")
        }

        test("Err.and returns the Err unchanged") {
            val result: Result<Int, String> = Err("error").and { Ok(3) }
            result.shouldHaveErr("error")
        }
    }

    context("flatMap") {
        test("is alias for and") {
            val result: Result<Int, String> = Ok(2).flatMap { Ok(it * 3) }
            result.shouldHaveValue(6)
        }
    }

    context("orElse") {
        test("Ok.orElse returns the value") {
            Ok(42).orElse { 0 } shouldEqual 42
        }

        test("Err.orElse calls func and returns its result") {
            Err("error").orElse { 0 } shouldEqual 0
        }

        test("Err.orElse func has Err receiver") {
            var captured: String? = null
            Err("error").orElse { captured = err }
            captured shouldEqual "error"
        }

        test("Ok.orElse early return: allows execution to continue") {
            fun computeResult(): Result<Int, String> {
                val result: Result<Int, String> = Ok(42)
                val _ = result.orElse { return this }
                return Ok(7)
            }
            computeResult().shouldHaveValue(7)
        }

        test("Err.orElse early return: returns result immediately") {
            fun computeResult(): Result<Int, String> {
                val result: Result<Int, String> = Err("error")
                val _ = result.orElse { return this }
                return Ok(7)
            }
            computeResult().shouldHaveErr("error")
        }

        test("fallback is not evaluated for Ok") {
            var evaluated = false
            val _ = Ok(1).orElse {
                evaluated = true
                0
            }
            evaluated shouldEqual false
        }
    }

    context("or") {
        test("Ok.or returns this Ok") {
            val result: Result<Int, String> = Ok(42).or { Ok(0) }
            result.shouldHaveValue(42)
        }

        test("Err.or calls func and returns its Result") {
            val result: Result<Int, String> = Err("error").or { Ok(42) }
            result.shouldHaveValue(42)
        }

        test("fallback is not evaluated for Ok") {
            var evaluated = false
            val _ = Ok(1).or {
                evaluated = true
                Ok(0)
            }
            evaluated shouldEqual false
        }
    }
})
