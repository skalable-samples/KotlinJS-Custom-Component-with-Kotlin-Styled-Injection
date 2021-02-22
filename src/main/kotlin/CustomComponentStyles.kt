import kotlinx.css.*
import styled.StyleSheet

object CustomComponentStyles : StyleSheet("CustomComponentStyles") {

    /**
     * A custom style declared directly in Kotlin using styled!
     */
    val cool by css {
        backgroundColor = Color.red
        color = Color.black
        fontSize = 24.px
        marginBottom = 20.px
        textAlign = TextAlign.center
    }

    /**
     * Multiple styles can be added to a single styled stylesheet
     */
    val cooler by css {
        backgroundColor = Color.black
        color = Color.red
        fontSize = 24.px
        paddingLeft = 8.px
        textAlign = TextAlign.center
    }
}