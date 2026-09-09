package codes.tis.minecord

import ch.qos.logback.classic.Level
import com.xenomachina.argparser.ArgParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import java.nio.file.Path

class ArgsTest : FunSpec({
    test("version flag parsing") {
        val testCases = listOf(
            arrayOf("-v") to true,
            arrayOf("--version") to true,
            emptyArray<String>() to false,
        )

        for ((args, expected) in testCases) {
            val parsed = ArgParser(args).parseInto(::Args)
            parsed.version shouldEqual expected
        }
    }

    test("config directory parsing") {
        val parsed = ArgParser(arrayOf("-c", "custom/config")).parseInto(::Args)
        parsed.configDir shouldEqual Path.of("custom/config")
    }

    test("config directory defaults to minecord") {
        val parsed = ArgParser(emptyArray()).parseInto(::Args)
        parsed.configDir shouldEqual Path.of("minecord")
    }

    test("log level parsing") {
        val testCases = mapOf(
            "TRACE" to Level.TRACE,
            "DEBUG" to Level.DEBUG,
            "INFO" to Level.INFO,
            "WARN" to Level.WARN,
            "ERROR" to Level.ERROR,
        )

        for ((input, expected) in testCases) {
            val parsed = ArgParser(arrayOf("--log-level", input)).parseInto(::Args)
            parsed.logLevel shouldEqual expected
        }
    }

    test("log level defaults to INFO") {
        val parsed = ArgParser(emptyArray()).parseInto(::Args)
        parsed.logLevel shouldEqual Level.INFO
    }

    test("invalid log level") {
        shouldThrow<Exception> {
            ArgParser(arrayOf("--log-level", "ALL")).parseInto(::Args)
        }
    }
})
