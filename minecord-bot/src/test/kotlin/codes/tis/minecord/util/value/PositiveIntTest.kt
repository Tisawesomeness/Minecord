package codes.tis.minecord.util.value

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual

class PositiveIntTest : FunSpec({
    test("make rejects zero") {
        PositiveInt.make(0) shouldEqual null
        shouldThrow<IllegalArgumentException> { PositiveInt.must(0) }
    }

    test("make rejects negative") {
        PositiveInt.make(-1) shouldEqual null
        shouldThrow<IllegalArgumentException> { PositiveInt.must(-1) }
    }

    test("value property returns the input int") {
        PositiveInt.must(42).value shouldEqual 42
    }

    test("toString() returns the input int") {
        PositiveInt.must(42).toString() shouldEqual "42"
    }
})
