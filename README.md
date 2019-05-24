# AAGateWay

A super simple app which allows the connection to Android Auto over Wifi. It requires an Android Auto compatible car in the first place.

# License

You are free to use the code for personal use in any shape or form you want, and implement any modification you wish, however you are stictly forbiden in creating and publishing app with the same or similar purposer, regardless if the app is free or comrecial. If you wish to use the code in building and releaseing your own app, please seek written approval before proceeding.

# Copyright
Emil Borconi-Szedressy (C) 2017 - Wakefield - United Kingdom

# Requirements

* Android Studio 3.4.1 or higher
* Gradle 5.1.1
* Android API 27


# Build 

```
$> ./gradlew assemble 
```

This will generate an `apk` file inside build directory `./app/build/outputs/apk/debug`

# Install in debug device

```
$> ./gradlew installDebug
```
