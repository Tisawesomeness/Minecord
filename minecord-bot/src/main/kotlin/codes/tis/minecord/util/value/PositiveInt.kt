package codes.tis.minecord.util.value

import com.fasterxml.jackson.annotation.JsonCreator

@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object {
        fun make(value: Int): PositiveInt? {
            return if (value > 0) PositiveInt(value) else null
        }

        @JvmStatic
        @JsonCreator
        fun must(value: Int) = requireNotNull(make(value)) { "must be positive but was $value" }
    }

    override fun toString() = value.toString()
}
