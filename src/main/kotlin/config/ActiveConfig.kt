package config

import com.google.common.io.Files
import exec.appDirectory
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import java.io.File


private fun acquireDefaultConfigFile(): File {
    val file = File(appDirectory.toString() + "/config/config.json")
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
val jsonMapper: ObjectMapper = ObjectMapper(JsonFactory()).registerModule(JavaTimeModule())

val defaultConfigFile = acquireDefaultConfigFile()
val activeConfig = loadFromDefault()

class ActiveConfig {

    var lastVisitedDirectory = appDirectory
        get() {
            if (!field.exists() || !field.isDirectory) {
                field = appDirectory
            }
            return field
        }
        private set

    fun updateLastVisitedDirectory(selected: File?) {
        if (selected == null) {
            return
        }
        lastVisitedDirectory = when (selected.isDirectory) {
            true -> selected
            false -> selected.parentFile
        }
    }

    var preferredVideoFormatIndex = 0

    fun writeToDefault() = defaultConfigFile.outputStream().use { jsonMapper.writeValue(it, this) }
}
