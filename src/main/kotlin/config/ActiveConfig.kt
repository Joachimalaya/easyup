package config

import com.google.common.io.Files
import exec.appDirectory
import org.codehaus.jackson.JsonFactory
import org.codehaus.jackson.map.ObjectMapper
import java.io.File

private fun acquireDefaultConfigFile(): File {
    val file = File(appDirectory.toString() + "/config/config.json")
    Files.createParentDirs(file)
    return file
}

private fun loadFromDefault(): ActiveConfig {
    return if(defaultConfigFile.exists()) {
        defaultConfigFile.inputStream().use { jsonMapper.readValue(it, ActiveConfig::class.java) }
    } else {
        ActiveConfig()
    }
}

val jsonMapper = ObjectMapper(JsonFactory())
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

    fun writeToDefault() = defaultConfigFile.outputStream().use { jsonMapper.writeValue(it, this) }
}
