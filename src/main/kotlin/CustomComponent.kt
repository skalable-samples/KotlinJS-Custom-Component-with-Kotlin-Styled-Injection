import react.*
import styled.CustomStyledProps
import styled.css
import styled.styledDiv
import util.css

/**
 * We use an interface to inject in props. It allows us to create
 * clean DSL builders for our custom components.
 *
 * By extending form CustomStyledProps we can benefit from adding CSS
 * directly through our props.
 */
external interface CustomComponentProps : CustomStyledProps {
    var name: String
}

/**
 * A data class can be used as a state
 * class to maintain the state of a component
 */
external interface CustomComponentState : RState {
    var name: String
}

/**
 * We extend from RComponent and tell it the types of Props and State to expect internally.
 *
 * This is our custom component.
 */
@JsExport
class CustomComponent(props: CustomComponentProps) : RComponent<CustomComponentProps, CustomComponentState>(props) {

    /**
     * To begin, we set the initial state to the name in the prop we injected.
     */
    override fun CustomComponentState.init(props: CustomComponentProps) {
        name = props.name
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                /**
                 * We make use of our CustomStyledProps extension function by
                 * setting the from the returned value RuleSet.
                 */
                +props.css()
            }
            +"Hello there ${state.name} from your very own custom component!"
        }
    }
}

/**
 * Using an extension function on RBuilder we can construct our DSL.
 *
 * Here we apply each variable within the props to the child class of our Custom component,
 * Setting each as an attribute of the component.
 *
 */
fun RBuilder.customComponent(handler: CustomComponentProps.() -> Unit) = child(CustomComponent::class) {
    attrs(handler)
}