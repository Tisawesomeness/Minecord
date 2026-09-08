package com.tis.minecord

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldNotEqual
import io.kotest.matchers.string.shouldNotContain

class BuildPropertiesTest : FunSpec({
    test("String length should return the length of the string") {
        BuildProperties.version shouldNotEqual "" shouldNotContain "$"
    }
})
