package entity

import javafx.beans.property.DoubleProperty
import javafx.beans.property.StringProperty

class UploadJob(val template: UploadTemplate, val placeholders: List<Placeholder>, val progressBar: DoubleProperty, val progressText: StringProperty)