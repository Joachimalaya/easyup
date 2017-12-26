package youtube.video

/**
 * Enum wrapper for the three possible values for VideoStatus.privacyStatus.
 *
 * @see https://developers.google.com/youtube/v3/docs/videos#status.privacyStatus
 */
enum class PrivacyStatus(val privacyStatus: String) {

    PUBLIC("public"),
    PRIVATE("PRIVATE"),
    UNLISTED("unlisted")

}