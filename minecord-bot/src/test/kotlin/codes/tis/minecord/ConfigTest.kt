package codes.tis.minecord

import codes.tis.minecord.config.Config
import codes.tis.minecord.util.openResource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import java.io.InputStream

class ConfigTest : FunSpec({
    test("Default config.yml deserializes without error") {
        val input: InputStream = openResource("/config.yml")
        val config: Config = readConfig(input)
        config.token shouldEqual ""
    }
})
