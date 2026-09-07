import assertk.all
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isNotEmpty
import com.tis.minecord.BuildProperties
import kotlin.test.Test

internal class BuildPropertiesTest {
    @Test
    fun versionWasParsed() {
        assertThat(BuildProperties.version).all {
            isNotEmpty()
            doesNotContain("$")
        }
    }
}
