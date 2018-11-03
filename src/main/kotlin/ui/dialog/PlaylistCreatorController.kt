package ui.dialog

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.stage.Stage
import playlist.create.CreatePlaylistItem
import playlist.create.CreatePlaylistService
import ui.alert.SizedAlert
import youtube.video.PrivacyStatus
import java.net.URL
import java.util.*

class PlaylistCreatorController : Initializable {

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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        privacyStatus.value = PrivacyStatus.PRIVATE
    }

    @FXML
    private fun handleCreate() {
        CreatePlaylistService.createPlaylist(CreatePlaylistItem(
                titleInput.text,
                descriptionInput.text,
                tagInput.text.split(',').map(String::trim),
                privacyStatus.value
        ))
        SizedAlert(Alert.AlertType.CONFIRMATION, "Playlist created.", ButtonType.OK).showAndWait()
        (rootPane.scene.window as Stage).hide()
    }

    @FXML
    private fun handleCancel() = (rootPane.scene.window as Stage).close()
}
