package util

import GlobalStyles
import kotlinx.css.RuleSet
import styled.CustomStyledProps

/**
 * An extension function to convert the returned Ruleset
 * into the ArrayList<RuleSet> that CustomStyledProps.css desires.
 * @see CustomStyledProps.css
 */
fun RuleSet.toCss(): ArrayList<RuleSet> {
    return arrayListOf(this)
}

/**
 * An extension function to pick the first RuleSet from the
 * list in the CustomStyledProps. Using our empty style here
 * means we will never need to handle a null scenario in any of
 * our components.
 */
fun CustomStyledProps.css(): RuleSet {
    return this.css?.first() ?: GlobalStyles.empty
}