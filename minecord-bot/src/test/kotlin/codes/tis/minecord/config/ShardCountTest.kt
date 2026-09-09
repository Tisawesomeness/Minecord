package codes.tis.minecord.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual

class ShardCountTest : FunSpec({
    test("make rejects zero") {
        ShardCount.make(0) shouldEqual null
        shouldThrow<IllegalArgumentException> { ShardCount.must(0) }
    }

    test("make rejects negative") {
        ShardCount.make(-2) shouldEqual null
        shouldThrow<IllegalArgumentException> { ShardCount.must(-2) }
    }

    test("value property returns the input int") {
        ShardCount.must(42).value shouldEqual 42
    }

    test("toString() returns the input int") {
        ShardCount.must(42).toString() shouldEqual "42"
    }
})
