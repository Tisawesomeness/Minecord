package codes.tis.minecord

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.equals.shouldNotEqual

class TokenTest : FunSpec({
    test("make rejects null") {
        Token.make(null) shouldEqual null
    }

    test("make rejects empty string") {
        Token.make("") shouldEqual null
    }

    test("value() returns the input string") {
        val token = Token.make("abc123")
        token?.value() shouldEqual "abc123"
    }

    test("toString() does not return the input string") {
        val token = Token.make("abc123")
        token?.toString() shouldNotEqual "abc123"
    }
})
