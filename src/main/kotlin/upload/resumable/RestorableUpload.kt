package upload.resumable

import entity.Placeholder
import entity.RawUploadTemplate
import entity.UploadJob
import exec.appDirectory
import youtube.video.PrivacyStatus
import java.io.File

/**
 * A serializable and deserializable collection of data needed to restore an unfinished UploadJob.
 */
class RestorableUpload() : RawUploadTemplate() {

    var placeholders = emptyList<Placeholder>()
    var privacyStatus = PrivacyStatus.PRIVATE
    var title: String
    var description: String
    var videoFile: File
    var thumbnailFile: File?

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
        this.titleTemplate = template.titleTemplate
        this.descriptionTemplate = template.descriptionTemplate
    }

    constructor(uploadJob: UploadJob) : this() {
        title = uploadJob.inputData.titleTemplate
        description = uploadJob.inputData.descriptionTemplate
        game = uploadJob.inputData.game
        tags = uploadJob.inputData.tags
        videoFile = uploadJob.inputData.videoFile
        thumbnailFile = uploadJob.inputData.thumbnailFile
        placeholders = uploadJob.placeholders
        privacyStatus = uploadJob.privacyStatus
    }
}