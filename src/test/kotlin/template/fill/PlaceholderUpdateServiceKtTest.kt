package template.fill

import entity.Placeholder
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaceholderUpdateServiceKtTest {

    @Test
    fun replacePlaceholders() {
        // init
        val template = "Text without placeholder\nsimple placeholder: {toFill}\ninvalid placeholder: {notToFill\nsome special characters: {äöüßé_ #/}\nno replacement found: {noPlaceholderFound}"
        val unusedPlaceholder = Placeholder("unused", "should not appear")
        val toFill = Placeholder("toFill", "correct")
        val specialCharacters = Placeholder("äöüßé_ #/", "äöüßé_ #/")

        // run
        val result = replacePlaceholders(template, listOf(unusedPlaceholder, toFill, specialCharacters))

        // assert
        assertTrue("unused placeholder should not be inserted", !result.contains(unusedPlaceholder.value))
        assertTrue("correct placeholder needs to be inserted", result.contains(toFill.value))
        assertTrue("placeholder with special characters must be inserted correctly", result.contains(specialCharacters.value) && !result.contains("{äöüßé_ #/}"))
    }
}