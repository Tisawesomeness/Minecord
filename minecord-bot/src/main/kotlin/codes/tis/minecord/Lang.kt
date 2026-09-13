package codes.tis.minecord

import org.jetbrains.annotations.PropertyKey
import java.text.MessageFormat
import java.util.Locale
import java.util.ResourceBundle

private const val BASE_NAME = "lang.lang"
private val bundle: ResourceBundle = ResourceBundle.getBundle(BASE_NAME)
private val locale = Locale.ENGLISH

fun localize(key: String): String? = if (bundle.containsKey(key)) bundle.getString(key) else null

fun localize(key: TranslationKey): String = bundle.getString(key.key)

fun localize(localizable: Localizable): String {
    return MessageFormat(localize(localizable.key()), locale).format(localizable.args())
}

interface Localizable {
    fun key(): TranslationKey

    fun args(): Array<out Any> = emptyArray()
}

@Suppress("ktlint:standard:annotation")
@JvmInline
value class TranslationKey(val key: @PropertyKey(resourceBundle = BASE_NAME) String) : Localizable {
    override fun key() = this

    fun format(vararg args: Any) = TranslationFormat(this, *args)

    override fun toString() = key
}

/**
 * A translation with arguments. The number of arguments is **NOT** validated.
 * @throws IllegalArgumentException if one of the arguments is an array
 */
class TranslationFormat(val key: TranslationKey, vararg val args: Any) : Localizable {
    init {
        args.forEach { arg ->
            require(arg !is Array<*>) { "Passing an array to `vararg args: Any` is probably a bug, use spread operator" }
        }
    }

    override fun key() = key

    override fun args() = args

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TranslationFormat) return false

        if (key != other.key) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}
