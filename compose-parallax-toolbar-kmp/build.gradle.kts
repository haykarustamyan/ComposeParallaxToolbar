import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
}

kotlin {
    // Every public declaration must say so; nothing leaks into the API by omission.
    explicitApi()

    // Public API surface is dumped to api/ and checked in CI; run updateAbi after intended changes.
    @OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
    abiValidation()

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
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "compose-parallax-toolbar-kmp"
            freeCompilerArgs += listOf("-Xbinary=bundleId=am.highapps.parallaxtoolbar")
            isStatic = true
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            // UI tests need a browser; the desktop and Android runs cover the shared code.
            testTask { enabled = false }
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
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.compose.ui.test)
        }
        getByName("desktopTest").dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            // Skia native runtime for headless Compose UI tests on the JVM.
            @Suppress("DEPRECATION")
            implementation(compose.desktop.currentOs)
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

dokka {
    moduleName.set("ComposeParallaxToolbar")
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory.set(file("src"))
            remoteUrl("https://github.com/haykarustamyan/ComposeParallaxToolbar/tree/main/compose-parallax-toolbar-kmp/src")
            remoteLineSuffix.set("#L")
        }
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
                classes(
                    "am.highapps.parallaxtoolbar.*Sample*",
                    "am.highapps.parallaxtoolbar.*Preview*",
                    "am.highapps.parallaxtoolbar.*Screen*",
                    "am.highapps.parallaxtoolbar.*ViewController*",
                )
                annotatedBy("androidx.compose.ui.tooling.preview.Preview")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true,
        ),
    )

    signAllPublications()

    coordinates(
        groupId = "am.highapps.parallaxtoolbar",
        artifactId = "compose-parallax-toolbar-kmp",
        version = "2.0.0",
    )

    pom {
        name = "ComposeParallaxToolbar"
        description =
            "Collapsing toolbar layout for Compose Multiplatform: parallax header, any scrollable body, scroll modes, " +
            "pinned tabs, pull-to-refresh. Android, iOS, desktop and web."
        inceptionYear = "2025"
        url = "https://github.com/haykarustamyan/ComposeParallaxToolbar"
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/license/mit")
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
