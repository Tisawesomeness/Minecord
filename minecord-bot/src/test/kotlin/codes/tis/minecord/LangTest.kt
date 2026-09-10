package codes.tis.minecord

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class LangTest : FunSpec({
    test("localize returns localized string") {
        localize(TranslationKey("minecord.true")) shouldBe "True"
    }
    test("localize with raw string returns localized string") {
        localize("minecord.false") shouldBe "False"
    }
    test("localize with raw string returns empty if not present") {
        localize("minecord.definitely.does.not.exist123") shouldBe null
    }
})
