import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    id("dev.icerock.mobile.multiplatform-resources")

}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export("dev.icerock.moko:resources:0.22.3")
            export("dev.icerock.moko:graphics:0.9.0")
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }


        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(libs.napier)

                implementation(libs.voyager.navigator)
                implementation(libs.voyager.bottomSheet.navigator)
                implementation(libs.voyager.transitions)
                implementation(libs.moko.mvvm)


            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.activity.compose)
                implementation(libs.compose.ui.tooling)

            }


        }
        val iosArm64Main by getting {
            dependsOn(commonMain)

            dependencies {

            }
        }
        val iosArm64Test by getting {
            dependsOn(commonMain)

            dependencies {

            }
        }
        val iosX64Main by getting {
            dependsOn(commonMain)

            dependencies {

            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(commonMain)

            dependencies {

            }
        }

    }
}

android {
    namespace = "irancell.nwg.wfm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    defaultConfig {
        applicationId = "irancell.nwg.wfm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


}
dependencies {
    debugImplementation(libs.compose.ui.tooling)
    commonMainApi(libs.bundles.moko.resources)
}

multiplatformResources {
    multiplatformResourcesPackage = "irancell.nwg.wfm"
}