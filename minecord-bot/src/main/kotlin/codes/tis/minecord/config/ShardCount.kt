package codes.tis.minecord.config

import com.fasterxml.jackson.annotation.JsonCreator

@JvmInline
value class ShardCount private constructor(val value: Int) {
    companion object {
        fun make(value: Int): ShardCount? {
            return if (value == -1 || value > 0) ShardCount(value) else null
        }

        @JvmStatic
        @JsonCreator
        fun must(value: Int) = requireNotNull(make(value)) { "must be -1 or positive but was $value" }
    }

    override fun toString() = value.toString()
}
