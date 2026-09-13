package codes.tis.minecord.command.argument

import codes.tis.minecord.TranslationKey
import codes.tis.minecord.util.Ok
import codes.tis.minecord.util.orErr

object StringArgument : StringArgumentType<String> {
    override val name = "String"

    override fun resolve(name: String, raw: String) = Ok(raw)
}

object BooleanArgument : BooleanArgumentType<Boolean> {
    override val name = "Boolean"

    override fun resolve(name: String, raw: String, boolean: Boolean?) = boolean.orErr {
        TranslationKey("minecord.error.invalid_boolean").format(name, raw)
    }
}

object LongArgument : LongArgumentType<Long> {
    override val name = "Long"

    override fun resolve(name: String, raw: String, long: Long?) = long.orErr {
        TranslationKey("minecord.error.invalid_long").format(name, raw)
    }
}

object DoubleArgument : DoubleArgumentType<Double> {
    override val name = "Double"

    override fun resolve(name: String, raw: String, double: Double?) = double.orErr {
        TranslationKey("minecord.error.invalid_double").format(name, raw)
    }
}
