package upload

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DecimalFormat

class UploadProgressListenerTest {

    @Test
    fun testProgressFeedback() {
        // run
        val feedback = UploadProgressListener.progressFeedback(0.5, 60000)

        // assert
        assertEquals("50${DecimalFormat().decimalFormatSymbols.decimalSeparator}0% uploaded; ETA: 0:01:00", feedback)
    }

    @Test
    fun testShortFeedback() {
        // run
        val shortFeedback = UploadProgressListener.shortProgressFeedback(0.5, 60000)

        // assert
        assertEquals("50% 0:01", shortFeedback)
    }

    @Test
    fun testShortestFeedback() {
        // run
        val shortFeedback = UploadProgressListener.shortProgressFeedback(0.01, 1000)

        // assert
        assertEquals("1% 0:01", shortFeedback)
    }

}