import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
}

kotlin {
    android {
        namespace = "am.highapps.parallaxtoolbar"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}

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

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.components.ui.tooling.preview)
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling)
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