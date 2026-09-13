package codes.tis.minecord

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual

class LangTest : FunSpec({
    test("localize returns localized string") {
        localize(TranslationKey("minecord.generic.true")) shouldEqual "True"
    }

    test("localize with raw string returns localized string") {
        localize("minecord.generic.false") shouldEqual "False"
    }

    test("localize with raw string returns empty if not present") {
        localize("minecord.definitely.does.not.exist123") shouldEqual null
    }
})
