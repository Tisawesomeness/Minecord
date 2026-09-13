package codes.tis.minecord.util.value

@Suppress("JavaDefaultMethodsNotOverriddenByDelegation") // non-overridden method is deprecated
@JvmInline
value class NonEmptyList<T> private constructor(val list: List<T>) : List<T> by list {
    companion object {
        fun <T> make(list: List<T>): NonEmptyList<T>? {
            return if (list.isEmpty()) null else NonEmptyList(list)
        }
    }
}
