import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kover)
}

kotlin {
    android {
        namespace = "am.highapps.parallaxtoolbar"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}.configure {
            isIncludeAndroidResources = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        packaging {
            resources {
                excludes.add("META-INF/AL2.0")
                excludes.add("META-INF/LGPL2.1")
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "compose-parallax-toolbar-kmp"
            freeCompilerArgs += listOf("-Xbinary=bundleId=am.highapps.parallaxtoolbar")
            isStatic = true
        }
    }

    applyDefaultHierarchyTemplate()

    compilerOptions {
        // The test base class uses expect/actual; the warning is noise here.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            // Pin the Compose runtime stack to the plugin version; Material 3 alone would pull an older one.
            api(libs.compose.ui)
            api(libs.compose.foundation)
            implementation(libs.compose.material3)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test)
        }
        // Android host tests render Compose through Robolectric.
        getByName("androidHostTest").dependencies {
            implementation(libs.junit)
            implementation(libs.androidx.test.junit)
            implementation(libs.robolectric)
            implementation(libs.androidx.compose.ui.test.junit4)
            implementation(libs.androidx.compose.ui.test.manifest)
        }
    }
}


// Add a task to build the iOS framework for easier access
tasks.register("buildIosFramework") {
    group = "build"
    description = "Builds the iOS framework for use in Xcode projects"

    dependsOn(
        "linkReleaseFrameworkIosArm64",
        "linkReleaseFrameworkIosSimulatorArm64"
    )

    doLast {
        println("iOS frameworks built successfully!")
        println("You can find the frameworks at:")
        println("- build/bin/iosArm64/releaseFramework/compose-parallax-toolbar-kmp.framework")
        println("- build/bin/iosSimulatorArm64/releaseFramework/compose-parallax-toolbar-kmp.framework")
    }
}

compose.resources {
    // The library ships no Compose resources, so skip generating the Res accessor class.
    generateResClass = never
}

kover {
    reports {
        verify {
            rule("line coverage of the library code") {
                minBound(95)
            }
        }
        filters {
            excludes {
                // Sample and preview code is documentation, not library behavior.
                classes("am.highapps.parallaxtoolbar.*Sample*", "am.highapps.parallaxtoolbar.*Preview*", "am.highapps.parallaxtoolbar.*Screen*", "am.highapps.parallaxtoolbar.*ViewController*")
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(
        groupId = "am.highapps.parallaxtoolbar",
        artifactId = "compose-parallax-toolbar-kmp",
        version = "1.4.0"
    )

    pom {
        name = "ComposeParallaxToolbar"
        description =
            "ComposeParallaxToolbar is a Jetpack Compose Multiplatform library that provides a customizable collapsing toolbar/app bar with a parallax background effect for both iOS and Android."
        inceptionYear = "2025"
        url = "https://github.com/haykarustamyan/ComposeParallaxToolbar"
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "am.highapps"
                name = "Hayk Arustamyan"
                email = "highapps2019@gmail.com"
                url = "https://github.com/haykarustamyan"
            }
        }
        scm {
            connection = "scm:git:git:https://github.com/haykarustamyan/ComposeParallaxToolbar.git"
            developerConnection =
                "scm:git:ssh://git@github.com/haykarustamyan/ComposeParallaxToolbar.git"
            url = "https://github.com/haykarustamyan/ComposeParallaxToolbar"
        }
        issueManagement {
            system = "GitHub"
            url = "https://github.com/haykarustamyan/ComposeParallaxToolbar/issues"
        }
    }
}

//mavenPublishing {
//    coordinates(
//        groupId = "am.highapps.parallaxtoolbar",
//        artifactId = "compose-parallax-toolbar-kmp",
//        version = "1.0.0"
//    )
//}