import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.render
import util.toCss

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            /**
             * Our custom DSL to create the CustomComponent.
             * Here we use the cool style
             * @see CustomComponentStyles.cool
             */
            customComponent {
                /**
                 * Using our extension function we can create the desired list
                 * that the CustomStyledProps requires.
                 * @see CustomComponentProps.css
                 *
                 * Our Custom style can be found in
                 * @see CustomComponentStyles.cool
                 */
                css = CustomComponentStyles.cool.toCss()
                name = "Sean"
            }
            /**
             * The same component but this time
             * we use the cooler style
             * @see CustomComponentStyles.cooler
             */
            customComponent {
                css = CustomComponentStyles.cooler.toCss()
                name = "Seb"
            }
        }
    }
}