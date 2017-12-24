package entity

import java.io.File

class UploadData(title: String, description: String, game: String, videoFile: File) {

    constructor(template: UploadDataTemplate, videoFile: File) : this(template.titleTemplate, template.descriptionTemplate, template.game, videoFile)


}
