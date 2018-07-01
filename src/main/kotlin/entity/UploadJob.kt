package entity

import com.google.api.services.youtube.model.Playlist
import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Tab
import upload.resumable.RestorableUpload
import youtube.video.PrivacyStatus
import java.time.LocalDateTime

/**
 * Contains all the data needed to upload a video to YouTube.
 */
class UploadJob(
        val placeholders: List<Placeholder>,
        val progressBar: DoubleProperty,
        val progressText: StringProperty,
        val uploadTab: Tab,
        val publishDate: LocalDateTime,
        val privacyStatus: PrivacyStatus,
        val playlist: Playlist?,
        val inputData: RestorableUpload
)