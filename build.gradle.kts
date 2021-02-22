plugins {
    kotlin("js") version "1.4.30"
}

val kotlinJS = "pre.113-kotlin-1.4.0"

group = "skalable"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
}

dependencies {
    testImplementation(kotlin("test-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
    implementation("org.jetbrains:kotlin-react:16.13.1-${kotlinJS}")
    implementation("org.jetbrains:kotlin-react-dom:16.13.1-${kotlinJS}")
    implementation("org.jetbrains:kotlin-styled:1.0.0-${kotlinJS}")
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                useExperimentalAnnotation("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}
