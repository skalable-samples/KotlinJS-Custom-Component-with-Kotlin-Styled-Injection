![image](https://storage.googleapis.com/skalable.appspot.com/logo.png)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE.txt)

# Custom Component with Injected Kotlin Styled CSS

At Skalable we love Kotlin!  We work with it at both the frontend and backend.  
One aspect of Kotlin we are most excited by in terms of potential is Kotlin Multiplatform (KMM). We have recently been
doing some work on improving the usability of KMM in a way that creates a friendly environment for engineers working on
building web apps using Kotlin. This article helps to explain what we have built.

Reusing components is no easy feat for newcomers, it can take a deep understanding of various frameworks and patterns of
development. When building a generic style systems that can change the look and feel of a component in most web
frameworks, they are normally created in a separate `.ccs` or `.scss` file. These styles are then imported where needed,
but what if you wanted to keep it all in the same language? While there are quite a lot of examples around this
scenario, the same cannot be said for the incredible
[Kotlin-Styled](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-styled) framework.

_Let's outline what we would like to achieve:_

![Injectable Kotlin CSS](https://storage.googleapis.com/skalable.appspot.com/KotlinJS%20Custom%20Style%20Injection/Custom%20Injected%20CSS-01.png)

While in theory we can just inject the name of the style in our `.css` file that we wish to apply to the custom KotlinJS
component as a `String`. We don't really get the same benefit or capability we would by directly accessing the object
itself following this approach, it also creates more boilerplate from managing constant strings of the style names and
reducing re-usability.

Let us look at how our Kotlin Stylesheet is constructed.

```kotlin
object CustomComponentStyles : StyleSheet("CustomComponentStyles") {
   /**
    * A custom style declared directly in Kotlin using styled!
    */
   val cool by css {
      //Custom style
   }
}
```

To first understand what we need to do we need to look at what `css` does itself in Kotlin Styled.

```kotlin
fun css(vararg parents: RuleSet, builder: RuleSet) = CssHolder(this, *parents, builder)
```

While it looks like this just returns a `CssHolder`, in reality what we get is a `RuleSet`. The delegation happens when
using the [by](https://kotlinlang.org/docs/delegated-properties.html) key in our Kotlin Stylesheet. This then allows us
to use the `css` getter when applying the values to our `cool` variable.

A deeper look into the `CssHolder` reveals the truth of the situation. The overloaded operator on the getValue function
we can see it returns a `RuleSet`. A funny situation to be in when you expected a `CssHolder` unkowningly.

```kotlin
operator fun getValue(thisRef: Any?, property: KProperty<*>): RuleSet = {}
```

## Extension Functions to the rescue!

With this newfound knowledge we can begin to make our code scale in a way that reads well and causes less confusion for
everyone.

To begin we need to analyse what type does a `CustomStyledProps` expect for `css`

```kotlin
external interface CustomStyledProps : RProps {
   var css: ArrayList<RuleSet>?
}
```

Its an `ArrayList` of type `RuleSet` which is nullable, our issue is that it's nullable. While some might say, why is
that an issue? Readability. That's why.

_Let's get building!_

Personally I tend to keep all my [extension functions](https://kotlinlang.org/docs/extensions.html)
in an `util` directory. So let us create a file in there called `CssExt.kt`. This is where our custom CSS extension
functions will live.

Within this file create a function extending from the `RuleSet` itself. This function will translate the `RuleSet`
itself into an `ArrayList<Ruleset>` object that is non nullable as to use it RuleSet must always exist. In this
instance, its called `toCss()` to make reading the code more intuitive.

```kotlin
fun RuleSet.toCss(): ArrayList<RuleSet> {
   return arrayListOf(this)
}
```

While we have a way of adding the `ArrayList<RuleSet>` to the property as a list, we need to be able to also convert it
back into a single `RuleSet`. This brings us back to our Nullability issue. Before we create the second extension to
our `CssExt.kt`
file, lets create a `GlobalStyles` file with a single empty style within.

```kotlin
object GlobalStyles : StyleSheet("GlobalStyles") {

   /**
    * Rather than setting null we will use an empty style.
    */
   val empty by css {}

}
```

Now we can use the power of the [Elvis operator](http://kotlin-quick-reference.com/156-R-elvis-operator.html) `?:`
_(Turn your head sideways to see elvis)_ we can add another extension function to give us a guarantee the `RuleSet` will
have a valid value in our extension. This will be based on the `CustomStyledProps` interface itself.

```kotlin
fun CustomStyledProps.css(): RuleSet {
   return this.css?.first() ?: GlobalStyles.empty
}
```

## Creating a Custom Component

Before we use our new extensions and `CustomSyledProps` though, we need to create a Custom KotlinJS component. _(For
this example `state` is here for extensibility purposes in the future)_

```kotlin
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
data class CustomComponentState(
   var name: String
) : RState

/**
 * We extend from RComponent and tell it the types of Props and State to expect internally.
 *
 * This is our custom component.
 */
class CustomComponent(props: CustomComponentProps) : RComponent<CustomComponentProps, CustomComponentState>(props) {

   /**
    * To begin, we set the initial state to the name in the prop we injected.
    */
   init {
      state = CustomComponentState(props.name)
   }

   override fun RBuilder.render() {
      styledDiv {
         css {
            /**
             * We make use of our CustomStyledProps extension function by
             * setting the from the returned value RuleSet.
             *
             * '+' operator is used to append the style to the existing.
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
```

Within the `styledDiv` there is a `css` section. In here we can embed any custom styles we desire. Using our extension
`css()` we can also call upon the props to fetch the required `RuleSet` that we have injected.

## Result

The extension function at the bottom of the file provides a clean way of building our custom component. Let's see our
new utility functions in action.

```kotlin
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
```

Calling `.toCss()` utilises our extension to create the desired output for the `CustomStyledProps`.

When rendered, we get the following result.

![result output](https://storage.googleapis.com/skalable.appspot.com/KotlinJS%20Custom%20Style%20Injection/Custom%20Injected%20CSS-02.png)

One component, two different styles, no strings, all linked directly with the Power of Kotlin!

# Project

This is a sample project of the above article. It provides a means of development and insight into the functionality.

## Getting Started

It's recommended to use IntelliJ with this. Either Community or Ultimate.

### Dependancies

- Kotlin 1.4.30

### Running

Import the project from the `build.gradle.kts` file. Once synced and all dependencies updated, you can now run KotlinJS.

There are two options to achieve this:

1. `./gradlew browserDevelopmentRun` from the terminal
2. In the gradle tasks manager on the right of intelliJ,
   - `CustomComponentsCSSInjection`
   - `Tasks`
   - `kotlin browser`
   - `browserDevelopmentRun`

The project can be found hosted on `localhost:8080`
