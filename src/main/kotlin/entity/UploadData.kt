package entity

import exec.appDirectory
import java.io.File

data class UploadData(var title: String, var description: String, var game: String, var videoFile: File) {

    constructor(template: UploadDataTemplate, videoFile: File) : this(template.titleTemplate, template.descriptionTemplate, template.game, videoFile)

    constructor() : this(UploadDataTemplate(), appDirectory)


}
