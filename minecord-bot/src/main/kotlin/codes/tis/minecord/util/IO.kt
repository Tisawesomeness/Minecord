package codes.tis.minecord.util

import java.io.InputStream

fun openResource(name: String): InputStream {
    return object {}.javaClass.getResourceAsStream(name)
        ?: throw IllegalArgumentException("The resource `$name` was not found")
}
