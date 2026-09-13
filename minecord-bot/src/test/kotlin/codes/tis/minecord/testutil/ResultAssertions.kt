package codes.tis.minecord.testutil

import codes.tis.minecord.util.Err
import codes.tis.minecord.util.Ok
import codes.tis.minecord.util.Result
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldBeOk(): Result<T, E> {
    this should beOk()
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldNotBeOk(): Result<T, E> {
    this shouldNot beOk()
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldHaveValue(value: T): Result<T, E> {
    this should haveValue(value)
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldNotHaveValue(value: T): Result<T, E> {
    this shouldNot haveValue(value)
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldBeErr(): Result<T, E> {
    this should beErr()
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldNotBeErr(): Result<T, E> {
    this shouldNot beErr()
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldHaveErr(err: E): Result<T, E> {
    this should haveErr(err)
    return this
}

@IgnorableReturnValue
fun <T, E> Result<T, E>.shouldNotHaveErr(err: E): Result<T, E> {
    this shouldNot haveErr(err)
    return this
}

private fun <T, E> beOk() = Matcher<Result<T, E>> { value ->
    MatcherResult(
        value is Ok<T>,
        { "Result should be Ok but was ${value::class.simpleName}" },
        { "Result should not be Ok" },
    )
}

private fun <T, E> haveValue(value: T) = Matcher<Result<T, E>> { result ->
    MatcherResult(
        result is Ok<T> && result.value == value,
        { "Result should have value $value but was $result" },
        { "Result should not have value $value" },
    )
}

private fun <T, E> beErr() = Matcher<Result<T, E>> { value ->
    MatcherResult(
        value is Err<E>,
        { "Result should be Err but was ${value::class.simpleName}" },
        { "Result should not be Err" },
    )
}

private fun <T, E> haveErr(err: E) = Matcher<Result<T, E>> { result ->
    MatcherResult(
        result is Err<E> && result.err == err,
        { "Result should have error $err but was $result" },
        { "Result should not have error $err" },
    )
}
