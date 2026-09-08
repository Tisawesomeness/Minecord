package codes.tis.minecord

/**
 * A non-empty token. [toString] is redacted as a sanity measure, not as a security measure.
 */
@JvmInline
value class Token private constructor(val token: String) {

    companion object {
        /**
         * Makes a token from a non-empty string.
         */
        fun make(token: String?): Token? {
            return if (token.isNullOrEmpty()) null else Token(token)
        }
    }

    fun value() = token

    override fun toString() = "<token>"

}
