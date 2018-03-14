package entity

import exec.appDirectory
import java.io.File
import java.time.LocalDateTime
import java.util.*

/**
 * Structure of a template that may be edited with the template editor.
 */
open class UploadTemplate(var title: String, var description: String, var game: String, var tags: Array<String>, var videoFile: File, var thumbnailFile: File?, var publishDate: LocalDateTime, var scheduledPublish: Boolean) {

    constructor(templateRaw: RawUploadTemplate, videoFile: File, thumbnailFile: File?) : this(templateRaw.titleTemplate, templateRaw.descriptionTemplate, templateRaw.game, templateRaw.tags, videoFile, thumbnailFile, LocalDateTime.now(), false)

    constructor() : this(RawUploadTemplate(), appDirectory, null)

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
        if (publishDate != other.publishDate) return false
        if (scheduledPublish != other.scheduledPublish) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + game.hashCode()
        result = 31 * result + Arrays.hashCode(tags)
        result = 31 * result + videoFile.hashCode()
        result = 31 * result + (thumbnailFile?.hashCode() ?: 0)
        result = 31 * result + publishDate.hashCode()
        result = 31 * result + scheduledPublish.hashCode()
        return result
    }


}
