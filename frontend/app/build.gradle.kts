plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.man"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.man"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation ("androidx.navigation:navigation-fragment:2.7.7")
    implementation ("androidx.navigation:navigation-ui:2.7.7")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.a520wcf.yllistview:YLListView:1.0.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.github.apg-mobile:android-round-textview:v1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.youth5201314:banner:2.2.3")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.loopeer.library:itemtouchhelperextension:1.0.6")
    implementation("com.github.markushi:circlebutton:1.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("com.github.dmytrodanylyk:circular-progress-button:1.4")
    implementation("me.spark:submitbutton:1.0.1")
    implementation("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    implementation("com.shizhefei:MultiTypeView:1.0.1")
    implementation("com.android.support:support-v4:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.wajahatkarim3.BufferTextInputLayout:buffertextinputlayout:1.2.0")
    implementation("com.github.rjsvieira:floatingMenu:1.3.0")
    implementation("com.4ert:audioview:1.4.12")
}