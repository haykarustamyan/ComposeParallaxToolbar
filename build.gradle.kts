plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply  false
    alias(libs.plugins.jetbrainsCompose) apply  false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

// Add a task to build the iOS framework for easier access
tasks.register("buildIosFramework") {
    group = "build"
    description = "Builds the iOS framework for use in Xcode projects"
    
    // Create XCFrameworkTask for each platform
    dependsOn(
        ":compose-parallax-toolbar-kmp:linkReleaseFrameworkIosArm64",
        ":compose-parallax-toolbar-kmp:linkReleaseFrameworkIosSimulatorArm64",
        ":compose-parallax-toolbar-kmp:linkReleaseFrameworkIosX64"
    )
    
    doLast {
        println("iOS frameworks built successfully!")
        println("You can find the frameworks at: compose-parallax-toolbar-kmp/build/bin/")
        println("Next, combine them with Xcode to create an XCFramework")
    }
}
