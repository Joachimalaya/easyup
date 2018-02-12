package entity

import exec.appDirectory
import java.io.File
import java.util.*

open class UploadData(var title: String, var description: String, var game: String, var tags: Array<String>, var videoFile: File, var thumbnailFile: File?) {

    constructor(template: UploadDataTemplate, videoFile: File, thumbnailFile: File?) : this(template.titleTemplate, template.descriptionTemplate, template.game, template.tags, videoFile, thumbnailFile)

    constructor() : this(UploadDataTemplate(), appDirectory, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadData

        if (title != other.title) return false
        if (description != other.description) return false
        if (game != other.game) return false
        if (videoFile != other.videoFile) return false
        if (!Arrays.equals(tags, other.tags)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + game.hashCode()
        result = 31 * result + videoFile.hashCode()
        result = 31 * result + Arrays.hashCode(tags)
        return result
    }


}
