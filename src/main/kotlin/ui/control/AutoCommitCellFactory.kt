package ui.control

import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TextField
import javafx.util.Callback

class AutoCommitCellFactory<S> : Callback<TableColumn<S, String>, TableCell<S, String>> {

    override fun call(param: TableColumn<S, String>?): TableCell<S, String> {
        return AutoCommitCell()
    }

    private class AutoCommitCell<S>(private var textField: TextField?) : TableCell<S, String>() {
        constructor() : this(null)

        override fun startEdit() {
            if (!isEmpty) {
                super.startEdit()
                createTextField()
                text = null
                graphic = textField
                textField?.selectAll()
            }
        }

        override fun cancelEdit() {
            super.cancelEdit()

            text = item.toString()
            graphic = null
        }

        override fun updateItem(item: String?, empty: Boolean) {
            super.updateItem(item, empty)

            if (empty) {
                text = null
                graphic = null
            } else {
                if (isEditing) {
                    textField?.text = getString()
                    text = null
                    graphic = textField
                } else {
                    text = getString()
                    graphic = null
                }
            }
        }

        private fun createTextField() {
            textField = TextField(getString())
            textField?.minWidth = width - graphicTextGap * 2
            textField?.focusedProperty()?.addListener { _, _, arg2 ->
                if (!arg2) {
                    commitEdit(textField?.text)
                }
            }
        }

        private fun getString(): String {
            return item?.toString() ?: ""
        }
    }

}