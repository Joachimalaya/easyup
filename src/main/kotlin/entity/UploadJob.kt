package entity

import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Tab
import java.time.LocalDateTime

/**
 * Contains all the data needed to upload a video to YouTube.
 */
class UploadJob(val template: UploadTemplate, val placeholders: List<Placeholder>, val progressBar: DoubleProperty, val progressText: StringProperty, val uploadTab: Tab, val publishDate: LocalDateTime, val scheduledPublish: Boolean)