plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // plugin ksp necesario para que room genere el codigo sqlite automaticamente
    id("com.google.devtools.ksp") version "2.2.10-2.0.2" apply false
}