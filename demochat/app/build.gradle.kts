plugins {
    id("com.android.application")
    id("com.google.gms.google-services") version "4.4.2"
}

android {
    signingConfigs {
        create("laohac_config") {
            storeFile = file("C:\\Users\\tonhu\\keystore.jks")
            storePassword = "123123456xuan"
            keyAlias = "laohac"
            keyPassword = "123123456xuan"
        }
    }
    namespace = "com.example.demochat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.demochat"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("laohac_config")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Phone number
    implementation("com.hbb20:ccp:2.5.0")
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    // Rounded ImageView
    implementation("com.makeramen:roundedimageview:2.3.0")

    // Import the BoM for the Firebase platform
    implementation (platform("com.google.firebase:firebase-bom:33.6.0"))

    // Declare the dependencies for the desired Firebase products without specifying versions
    // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")

    // FirebaseUI for Cloud Fires tore
    implementation ("com.firebaseui:firebase-ui-firestore:8.0.2")

    implementation ("com.google.android.gms:play-services-auth:20.0.0")

    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("com.squareup.picasso:picasso:2.8")
    //Tràn màng hình
    implementation("androidx.activity:activity:1.7.2")

    implementation("com.google.android.material:material:1.9.0")

    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")

    implementation ("com.github.dhaval2404:imagepicker:2.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")



}
