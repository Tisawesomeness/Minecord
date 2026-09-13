package codes.tis.minecord.util.value

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.equals.shouldNotEqual
import io.kotest.matchers.nulls.shouldNotBeNull

class NonEmptyListTest : FunSpec({
    test("make returns null for empty list") {
        NonEmptyList.make(emptyList<Int>()) shouldEqual null
    }

    test("make returns NonEmptyList for non-empty list") {
        NonEmptyList.make(listOf(1, 2, 3)).shouldNotBeNull()
    }

    test("list property returns the input list") {
        NonEmptyList.make(listOf(1, 2, 3))?.list shouldEqual listOf(1, 2, 3)
    }

    test("toString() returns the list string") {
        NonEmptyList.make(listOf(1, 2, 3))?.toString() shouldNotEqual ""
    }
})
