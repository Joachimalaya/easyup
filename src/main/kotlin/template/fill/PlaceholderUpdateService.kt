package template.fill

import entity.Placeholder
import entity.UploadDataTemplate
import javafx.scene.control.TableColumn
import javafx.scene.control.TextArea
import javafx.scene.control.TextField

class PlaceholderUpdateService {

    fun updatePlaceholders(event: TableColumn.CellEditEvent<Placeholder, String>, titlePreview: TextField, descriptionPreview: TextArea, activeTemplate: UploadDataTemplate) {
        event.tableView.items[event.tablePosition.row].value = event.newValue

        var replacedTitlePreview = activeTemplate.titleTemplate
        var replacedDescriptionPreview = activeTemplate.descriptionTemplate
        event.tableView.items.filtered { it.value.isNotBlank() }.forEach {
            replacedTitlePreview = replacedTitlePreview.replace(Regex("\\{${it.key}}"), it.value)
            replacedDescriptionPreview = replacedDescriptionPreview.replace(Regex("\\{${it.key}}"), it.value)
        }

        titlePreview.text = replacedTitlePreview
        descriptionPreview.text = replacedDescriptionPreview
    }

}