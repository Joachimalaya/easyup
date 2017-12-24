package config

import com.google.common.io.Files
import exec.appDirectory
import org.codehaus.jackson.JsonFactory
import org.codehaus.jackson.map.ObjectMapper
import java.io.File

private fun getDefaultConfigFile(): File {
    val file = File(appDirectory.toString() + "/config/config.json")
    Files.createParentDirs(file)
    return file
}

private fun loadFromDefault(): ActiveConfig {
    return if(defaultConfigFile.exists()) {
        mapper.readValue(defaultConfigFile.inputStream(), ActiveConfig::class.java)
    } else {
        ActiveConfig()
    }
}

private val mapper = ObjectMapper(JsonFactory())
private val defaultConfigFile = getDefaultConfigFile()
val activeConfig: ActiveConfig = loadFromDefault()

class ActiveConfig {

    var lastVisitedDirectory = appDirectory

    fun updateLastVisitedDirectory(selected: File?) {
        if (selected == null) {
            return
        }
        lastVisitedDirectory = when (selected.isDirectory) {
            true -> selected
            false -> selected.parentFile
        }
    }

    fun writeToDefault() = mapper.writeValue(defaultConfigFile.outputStream(), this)
}
