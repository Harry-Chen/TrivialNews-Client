# TrivialNews-Client

This is Android client for TrivialNews, a project of Java course in Tsinghua University, Summer 2018.

The project is licensed under GPLv3.

## Build Project

### The easy way

Install the latest version of Android Studio, open project, and build!

### The hard way

* Install:  
  * JDK 8.0 and Kotlin compiler
  * Android SDK 28.0
  * Android build tools 28.0
  * extra-android-m2repository (including every support library)
  * extra-google-m2repository
* Set corresponding environment variables correctly, such as `$PATH` and `$ANDROID_HOME`
* Run:
  * `chmod +x gradlew
  * Debug APK: `./gradlew assembleDebug`
  * Release APK:  `./gradlew assembleNormalRelease` or `./gradlew assembleThuRelease`

`thuRelease` and `normalRelease` differ only in package name and launcher label, you might need to specify the signing keystore.