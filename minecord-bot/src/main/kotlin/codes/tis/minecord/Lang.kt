package codes.tis.minecord

import org.jetbrains.annotations.PropertyKey
import java.util.ResourceBundle

private const val baseName = "lang.lang"
private val bundle: ResourceBundle = ResourceBundle.getBundle(baseName)

fun localize(key: TranslationKey): String = bundle.getString(key.key)
fun localize(key: String): String? = if (bundle.containsKey(key)) bundle.getString(key) else null

@JvmInline
value class TranslationKey(val key: @PropertyKey(resourceBundle = baseName) String) {
    override fun toString() = key
}
