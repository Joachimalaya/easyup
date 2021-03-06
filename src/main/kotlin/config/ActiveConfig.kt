package config

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.common.io.Files
import exec.appDirectory
import java.io.File


private fun acquireDefaultConfigFile(): File {
    val file = File("$appDirectory/config/config.json")
    Files.createParentDirs(file)
    return file
}

private fun loadFromDefault(): ActiveConfig {
    return try {
        if (defaultConfigFile.exists()) {
            defaultConfigFile.inputStream().use { jsonMapper.readValue(it, ActiveConfig::class.java) }
        } else {
            ActiveConfig()
        }
    } catch (jme: JsonMappingException) {
        // reset config to default
        ActiveConfig()
    }
}

// jsonMapper setup
val jsonMapper: ObjectMapper = ObjectMapper(JsonFactory()).registerModule(JavaTimeModule()).enable(SerializationFeature.INDENT_OUTPUT)

val defaultConfigFile = acquireDefaultConfigFile()
val activeConfig = loadFromDefault()

class ActiveConfig {

    var lastVisitedVideoDirectory = appDirectory
        get() {
            if (!field.exists() || !field.isDirectory) {
                field = appDirectory
            }
            return field
        }

    var lastVisitedThumbnailDirectory = appDirectory
        get() {
            if (!field.exists() || !field.isDirectory) {
                field = appDirectory
            }
            return field
        }

    var lastVisitedTemplateDirectory = appDirectory
        get() {
            if (!field.exists() || !field.isDirectory) {
                field = appDirectory
            }
            return field
        }

    var preferredVideoFormatIndex = 0

    fun writeToDefault() = defaultConfigFile.outputStream().use { jsonMapper.writeValue(it, this) }
}
