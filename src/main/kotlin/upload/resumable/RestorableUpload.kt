package upload.resumable

import entity.Placeholder
import entity.UploadData

class RestorableUpload(var placeholders: List<Placeholder>, uploadData: UploadData) : UploadData() {
    init {
        title = uploadData.title
        description = uploadData.description
        game = uploadData.game
        tags = uploadData.tags
        videoFile = uploadData.videoFile
        thumbnailFile = uploadData.thumbnailFile
        scheduledPublish = uploadData.scheduledPublish
        publishDate = uploadData.publishDate
    }

    constructor() : this(emptyList(), UploadData())

}