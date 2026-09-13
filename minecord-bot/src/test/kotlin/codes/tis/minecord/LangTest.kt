package codes.tis.minecord

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.equals.shouldNotEqual

class LangTest : FunSpec({
    context("localize") {
        test("localize returns localized string") {
            localize(TranslationKey("minecord.generic.true")) shouldEqual "True"
        }

        test("localize with raw string returns localized string") {
            localize("minecord.generic.false") shouldEqual "False"
        }

        test("localize with raw string returns empty if not present") {
            localize("minecord.definitely.does.not.exist123") shouldEqual null
        }

        test("localize with translation format returns formatted string") {
            val localized = localize(TranslationKey("minecord.error.invalid_long").format("arg", "value"))
            localized shouldEqual "`arg` must be an integer but was `value`"
        }
    }

    context("TranslationFormat") {
        test("key returns the translation key") {
            val format = TranslationKey("minecord.generic.true").format()
            format.key() shouldEqual TranslationKey("minecord.generic.true")
        }

        test("args returns the translation arguments") {
            val format = TranslationKey("minecord.generic.true").format("yes", 123)
            format.args().toList() shouldEqual listOf("yes", 123)
        }

        test("equals returns true for identical key and args") {
            val format1 = TranslationKey("minecord.generic.true").format("yes", 123)
            val format2 = TranslationKey("minecord.generic.true").format("yes", 123)
            format1 shouldEqual format2
        }

        test("equals returns false when key differs") {
            val format1 = TranslationKey("minecord.generic.true").format("yes", 123)
            val format2 = TranslationKey("minecord.generic.false").format("no", 456)
            format1 shouldNotEqual format2
        }

        test("equals returns false when args differ") {
            val format1 = TranslationKey("minecord.generic.true").format("yes", 123)
            val format2 = TranslationKey("minecord.generic.true").format("no", 456)
            format1 shouldNotEqual format2
        }

        test("initializer throws IllegalArgumentException for Array arg") {
            shouldThrow<IllegalArgumentException> {
                TranslationKey("minecord.error.invalid_long").format(arrayOf(1, 2))
            }
        }
    }
})
