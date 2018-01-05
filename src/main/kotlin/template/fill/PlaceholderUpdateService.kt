package template.fill

import entity.Placeholder
import entity.UploadData
import javafx.scene.control.TableColumn
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

/**
 * Replaces all placeholder formatted with {} in the given text with the given key-value-like Placeholders.
 */
fun replacePlaceholders(text: String, placeholders: List<Placeholder>): String {
    var replacedText = text
    placeholders.filter { it.value.isNotBlank() }.forEach{
        replacedText = replacedText.replace(Regex("\\{${it.key}}"), it.value)
    }
    return replacedText
}

/**
 * Handles changes in the key-value list of Placeholders by updating the previews.
 */
class PlaceholderUpdateService {

    fun updatePlaceholders(event: TableColumn.CellEditEvent<Placeholder, String>, titlePreview: TextField, descriptionPreview: TextArea, activeData: UploadData) {
        event.tableView.items[event.tablePosition.row].value = event.newValue

        titlePreview.text = replacePlaceholders(activeData.title, event.tableView.items)
        descriptionPreview.text = replacePlaceholders(activeData.description, event.tableView.items)
    }

}