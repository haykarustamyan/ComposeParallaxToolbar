plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":sample"))
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "am.highapps.parallaxtoolbar.sample.desktop.MainKt"
    }
}
