package template.fill

import entity.Placeholder
import entity.UploadTemplate
import javafx.scene.control.TableColumn
import javafx.scene.control.TextArea
import javafx.scene.control.TextField


/**
 * Handles changes in the key-value list of Placeholders by updating the previews.
 */
object PlaceholderUpdateService {

    fun updatePlaceholders(event: TableColumn.CellEditEvent<Placeholder, String>, titlePreview: TextField, descriptionPreview: TextArea, activeTemplate: UploadTemplate) {
        event.tableView.items[event.tablePosition.row].value = event.newValue

        titlePreview.text = replacePlaceholders(activeTemplate.title, event.tableView.items)
        descriptionPreview.text = replacePlaceholders(activeTemplate.description, event.tableView.items)
    }

    fun updatePlaceholders(placeholders: List<Placeholder>, titlePreview: TextField, descriptionPreview: TextArea, activeTemplate: UploadTemplate) {
        titlePreview.text = replacePlaceholders(activeTemplate.title, placeholders)
        descriptionPreview.text = replacePlaceholders(activeTemplate.description, placeholders)
    }

    /**
     * Replaces all placeholder formatted with {} in the given text with the given key-value-like [Placeholder]s.
     */
    fun replacePlaceholders(text: String, placeholders: List<Placeholder>): String {
        var replacedText = text
        placeholders.filter { it.value.isNotBlank() }.forEach {
            replacedText = replacedText.replace(Regex("\\{${it.key}}"), it.value)
        }
        return replacedText
    }

}