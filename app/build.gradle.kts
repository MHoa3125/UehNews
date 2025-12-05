plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "hoatran.st.ueh.edu.uehnews"
    // Giữ compileSdk là 34, nó rất ổn định và tương thích với các thư viện bạn đang dùng
    compileSdk = 34

    defaultConfig {
        applicationId = "hoatran.st.ueh.edu.uehnews"
        minSdk = 29
        // Luôn để targetSdk bằng với compileSdk để đảm bảo tính nhất quán
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
        }
    }
    compileOptions {
        // Nên dùng Java 8 (VERSION_1_8) vì nó tương thích rộng rãi và được hỗ trợ tốt
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        // Bật tính năng này nếu bạn dùng ViewBinding
        viewBinding = true
    }
}

dependencies {
    // Các thư viện cơ bản
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Cấu hình Firebase chuẩn ---
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Sử dụng phiên bản play-services-auth ổn định, không gây xung đột
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    // --------------------------------

    // Các thư viện cho testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
