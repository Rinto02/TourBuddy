val defaultApplicationId by extra("com.jubier.Travels")
buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.0.0-alpha02" apply false
}
