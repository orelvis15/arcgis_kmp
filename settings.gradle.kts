enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://esri.jfrog.io/artifactory/arcgis") }
    }
}

rootProject.name = "GISLIB"
include(":shared")
include(":sample:composeApp")
