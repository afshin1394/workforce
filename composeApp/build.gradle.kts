import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {


    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.compose.compiler)

    id("dev.icerock.mobile.multiplatform-resources")

    id ("io.sentry.android.gradle") version "3.12.0"


}

sentry {
    // List the build types that should be ignored (e.g. "release").
    ignoredBuildTypes.set(setOf("debug"))

}

kotlin {

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata")
    }
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
            export("com.mohamedrejeb.calf:calf-ui:0.3.1")
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
                implementation(libs.moko.permission)
                implementation(libs.essenty.lifecycle)
                implementation(libs.kotlin.x.datetime)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.kotlin.arrow.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.kotlin.serialization)
                implementation(libs.uuid)
                api(libs.calf.ui)
                implementation(libs.konnectivity)
                implementation(libs.room.runtime)
                implementation(libs.sqlite.bundled)
                implementation("com.github.skydoves:landscapist-coil3:2.3.2")

       
                implementation("io.ktor:ktor-client-cio:2.3.2")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.2")
                implementation("io.ktor:ktor-http:2.3.2")


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
                implementation(libs.play.service.location)

                implementation(libs.koin.android)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.logging)
                implementation(libs.accompanist.permission)
                implementation (libs.android.database.sqlcipher)

                implementation("org.osmdroid:osmdroid-android:6.1.6")

            }
        }


        val iosArm64Main by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.ktor.client.darwin)

            }
        }
        val iosArm64Test by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.ktor.client.darwin)

            }
        }
        val iosX64Main by getting {
            dependsOn(commonMain)

            dependencies {

                implementation(libs.ktor.client.darwin)

            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.ktor.client.darwin)

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

room {
    schemaDirectory("$projectDir/schemas")
}


dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    debugImplementation(libs.compose.ui.tooling)
    commonMainApi(libs.bundles.moko.resources)
    add("kspCommonMainMetadata", libs.room.compiler)

}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata" ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "irancell.nwg.wfm"
}



composeCompiler {
    enableStrongSkippingMode = true
}


