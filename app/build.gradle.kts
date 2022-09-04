/*
 * Copyright 2022 VastGui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.gcode.plugin.version.*

plugins{
    id("com.android.application")
    id("com.gcode.plugin.version")
}

android {
    compileSdk = Version.compile_sdk_version
    buildToolsVersion = Version.build_tools_version

    defaultConfig {
        applicationId = "com.gcode.bluetoothdemo"
        minSdk = Version.min_sdk_version
        targetSdk = Version.target_sdk_version
        versionCode = Version.version_code
        versionName = Version.version_name
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = File("D:\\AndroidKey\\Bluetooth.jks")
            storePassword = project.property("myStorePassword") as String?
            keyPassword = project.property("myKeyPassword") as String?
            keyAlias = project.property("myKeyAlias") as String?
        }
        create("release") {
            storeFile = File("D:\\AndroidKey\\Bluetooth.jks")
            storePassword = project.property("myStorePassword") as String?
            keyPassword = project.property("myKeyPassword") as String?
            keyAlias = project.property("myKeyAlias") as String?
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs","include" to listOf("*.jar"))))
    implementation(files("libs/VastTools_0.0.9_Cancey.jar"))
    implementation(Libraries.permissionx)
    implementation(Libraries.vastadapter)
    implementation(AndroidX.appcompat)
    implementation(AndroidX.constraintlayout)
    testImplementation(Libraries.junit)
    androidTestImplementation(AndroidX.junit)
    androidTestImplementation(AndroidX.espresso_core)
}