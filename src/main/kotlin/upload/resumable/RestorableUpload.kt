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
    // TODO: add playlist

    var placeholders = emptyList<Placeholder>()
    var privacyStatus = PrivacyStatus.PRIVATE
    var videoFile: File
    var thumbnailFile: File?

    // need to provide empty constructor for deserialization
    init {
        game = ""
        tags = emptyArray()
        // TODO: videoFile defaults to appDirectory; not that useful but also only temporary
        videoFile = appDirectory
        thumbnailFile = null
    }

    constructor(template: RawUploadTemplate, videoFile: File, thumbnailFile: File?, placeholders: List<Placeholder>) : this() {
        this.titleTemplate = template.titleTemplate
        this.descriptionTemplate = template.descriptionTemplate
        this.game = template.game
        this.tags = template.tags
        this.videoFile = videoFile
        this.thumbnailFile = thumbnailFile
        this.placeholders = placeholders
    }

    constructor(uploadJob: UploadJob) : this() {
        titleTemplate = uploadJob.inputData.titleTemplate
        descriptionTemplate = uploadJob.inputData.descriptionTemplate
        game = uploadJob.inputData.game
        tags = uploadJob.inputData.tags
        videoFile = uploadJob.inputData.videoFile
        thumbnailFile = uploadJob.inputData.thumbnailFile
        placeholders = uploadJob.placeholders
        privacyStatus = uploadJob.privacyStatus
    }
}