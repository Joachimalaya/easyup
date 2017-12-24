package auth

import com.google.api.client.auth.oauth2.StoredCredential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.youtube.YouTube
import exec.appDirectory
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.io.File
import java.io.InputStreamReader

object Authorization {

    private val authDirectory = File(appDirectory.toString() + "/auth")
    private val jsonFactory = JacksonFactory()
    private val httpTransport = NetHttpTransport()



    fun createAuthorizedYouTube(): YouTube {

        val scopes = listOf("https://www.googleapis.com/auth/youtube.upload")

        // check for client secret
        val clientId = File(authDirectory.toString() + "/client_id.json")
        if (!clientId.exists()) {
            Alert(Alert.AlertType.ERROR, "Could not find $clientId, necessary to connect to YouTube.\nYou can create credential files using Google APIs.\nWill now terminate.", ButtonType.OK).showAndWait()
            throw RuntimeException("Could not find $clientId, necessary to connect to YouTube.\nYou can create credential files using Google APIs.\nWill now terminate.")
        }

        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(clientId.inputStream()))
        val datastore = FileDataStoreFactory(authDirectory).getDataStore<StoredCredential>("easyUpDatastore")

        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes).setCredentialDataStore(datastore).build()

        val localReceiver = LocalServerReceiver.Builder().setPort(8080).build()

        val credential = AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user")

        return YouTube.Builder(httpTransport, jsonFactory, credential).setApplicationName("easyUp").build()
    }
}
