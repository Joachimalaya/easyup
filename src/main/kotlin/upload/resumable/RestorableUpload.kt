package upload.resumable

import entity.Placeholder
import entity.RawUploadTemplate
import entity.UploadJob
import entity.UploadTemplate
import exec.appDirectory
import java.io.File

/**
 * A serializable and deserializable collection of data needed to restore an unfinished UploadJob.
 */
class RestorableUpload() : UploadTemplate() {

    var placeholders: List<Placeholder> = emptyList()

    // need to provide empty constructor for deserialization
    init {
        title = ""
        description = ""
        game = ""
        tags = emptyArray()
        // TODO: videoFile defaults to appDirectory; not that useful but also only temporary
        videoFile = appDirectory
        thumbnailFile = null
    }

    constructor(template: RawUploadTemplate, videoFile: File, thumbnailFile: File?, placeholders: List<Placeholder>) : this() {
        this.title = template.titleTemplate
        this.description = template.descriptionTemplate
        this.game = template.game
        this.tags = template.tags
        this.videoFile = videoFile
        this.thumbnailFile = thumbnailFile
        this.placeholders = placeholders
    }

    constructor(uploadJob: UploadJob) : this() {
        title = uploadJob.template.title
        description = uploadJob.template.description
        game = uploadJob.template.game
        tags = uploadJob.template.tags
        videoFile = uploadJob.template.videoFile
        thumbnailFile = uploadJob.template.thumbnailFile
        placeholders = uploadJob.placeholders
    }

}