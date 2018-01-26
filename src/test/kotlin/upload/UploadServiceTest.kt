package upload

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.DecimalFormat

class UploadServiceTest {

    @Test
    fun testprogressFeedback() {
        // run
        val feedback = UploadService.progressFeedback(0.5, 60000)

        // assert
        assertEquals("50${DecimalFormat().decimalFormatSymbols.decimalSeparator}0% uploaded; ETA: 0:01:00", feedback)
    }


}