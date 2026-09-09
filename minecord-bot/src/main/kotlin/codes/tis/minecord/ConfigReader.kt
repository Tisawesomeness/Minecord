package codes.tis.minecord

import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.MapperFeature
import tools.jackson.dataformat.yaml.YAMLMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule
import tools.jackson.module.kotlin.readValue
import java.io.InputStream

@PublishedApi
internal val MAPPER: YAMLMapper = run {
    val kotlinModule = KotlinModule
        .Builder()
        .enable(KotlinFeature.NullIsSameAsDefault)
        .build()
    YAMLMapper
        .builder()
        .addModule(kotlinModule)
        .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .enable(DeserializationFeature.USE_NULL_FOR_MISSING_REFERENCE_VALUES)
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
        .build()
}

inline fun <reified T> readConfig(input: InputStream) = MAPPER.readValue<T>(input)
