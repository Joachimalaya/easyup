package ui.tab

import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import javafx.stage.Stage
import playlist.create.CreatePlaylistItem
import playlist.create.CreatePlaylistService
import youtube.video.PrivacyStatus

class PlaylistCreatorController {

    @FXML
    private lateinit var rootPane: Pane

    @FXML
    private lateinit var titleInput: TextField
    @FXML
    private lateinit var descriptionInput: TextArea
    @FXML
    private lateinit var tagInput: TextField
    @FXML
    private lateinit var privacyStatus: ChoiceBox<PrivacyStatus>

    @FXML
    private fun handleCreate() {
        CreatePlaylistService.createPlaylist(CreatePlaylistItem(
                titleInput.text,
                descriptionInput.text,
                tagInput.text.split(',').map(String::trim),
                privacyStatus.value
        ))
    }

    @FXML
    private fun handleCancel() = (rootPane.scene.window as Stage).close()
}
