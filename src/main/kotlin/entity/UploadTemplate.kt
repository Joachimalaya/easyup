package entity

import exec.appDirectory
import upload.resumable.RestorableUpload
import java.io.File
import java.util.*

/**
 * Structure of a template that may be edited with the template editor.
 */
open class UploadTemplate(var title: String, var description: String, var game: String, var tags: Array<String>, var videoFile: File, var thumbnailFile: File?) {

    constructor(templateRaw: RawUploadTemplate, videoFile: File, thumbnailFile: File?) : this(templateRaw.titleTemplate, templateRaw.descriptionTemplate, templateRaw.game, templateRaw.tags, videoFile, thumbnailFile)

    constructor() : this(RawUploadTemplate(), appDirectory, null)

    constructor(restorable: RestorableUpload) : this(restorable.title, restorable.description, restorable.game, restorable.tags, restorable.videoFile, restorable.thumbnailFile)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadTemplate

        if (title != other.title) return false
        if (description != other.description) return false
        if (game != other.game) return false
        if (!Arrays.equals(tags, other.tags)) return false
        if (videoFile != other.videoFile) return false
        if (thumbnailFile != other.thumbnailFile) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + game.hashCode()
        result = 31 * result + Arrays.hashCode(tags)
        result = 31 * result + videoFile.hashCode()
        result = 31 * result + (thumbnailFile?.hashCode() ?: 0)
        return result
    }


}
