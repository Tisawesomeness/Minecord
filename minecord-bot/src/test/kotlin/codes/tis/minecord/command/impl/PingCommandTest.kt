package codes.tis.minecord.command.impl

import codes.tis.minecord.testutil.TestContext
import codes.tis.minecord.testutil.TestResolver
import codes.tis.minecord.testutil.assertion.shouldBeOk
import codes.tis.minecord.util.unwrap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class PingCommandTest : FunSpec({
    test("PingCommand replies pong") {
        val resolver = TestResolver(options = emptyMap())
        val parseResult = PingCommand.parse(resolver)

        parseResult.shouldBeOk()

        val handleFunc = parseResult.unwrap()
        val context = TestContext()
        handleFunc(context)

        context.replies shouldContainExactly listOf("pong")
    }
})
