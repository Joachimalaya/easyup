package upload.resumable

import entity.Placeholder
import entity.UploadJob
import entity.UploadTemplate
import exec.appDirectory

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