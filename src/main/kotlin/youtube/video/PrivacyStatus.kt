package youtube.video

/**
 * Enum wrapper for the three possible values for VideoStatus.privacyStatus.
 *
 * @see <a href="https://developers.google.com/youtube/v3/docs/videos#status.privacyStatus">Google Developers Documentation</a>
 */
enum class PrivacyStatus(val privacyStatus: String) {

    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE"),
    UNLISTED("UNLISTED")

}