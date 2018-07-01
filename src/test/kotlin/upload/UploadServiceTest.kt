package upload

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class UploadServiceTest {

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