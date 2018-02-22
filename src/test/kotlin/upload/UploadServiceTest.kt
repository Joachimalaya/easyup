package upload

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DecimalFormat
import java.time.LocalDateTime

class UploadServiceTest {

    @Test
    fun testProgressFeedback() {
        // run
        val feedback = UploadService.progressFeedback(0.5, 60000)

        // assert
        assertEquals("50${DecimalFormat().decimalFormatSymbols.decimalSeparator}0% uploaded; ETA: 0:01:00", feedback)
    }

    @Test
    fun testShortFeedback() {
        // run
        val shortFeedback = UploadService.shortProgressFeedback(0.5, 60000)

        // assert
        assertEquals("50% 0:01", shortFeedback)
    }

    @Test
    fun testShortestFeedback() {
        // run
        val shortFeedback = UploadService.shortProgressFeedback(0.01, 1000)

        // assert
        assertEquals("1% 0:01", shortFeedback)
    }

    // TODO: this test currently only works for CET; research mocking or another solution
    @Test
    fun testDateConversion() {
        // init
        val publishDate = LocalDateTime.of(2016, 12, 24, 13, 45)

        // run
        val dateTime = UploadService.publishDateToGoogleDateTime(publishDate)

        // assert
        assertEquals(1482583500000, dateTime.value)
    }

}