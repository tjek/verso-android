## Verso-android

A multi-paged viewer for Android.

## Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Then, add the library to your module `build.gradle`
```gradle
dependencies {
    compile 'com.shopgun.android:verso:0.8.0'
}
```

## Features
- Easily create a multi-paged `ViewPager` like viewer.
- Zoom and pan on any view, using multi-touch.
- Callbacks available for virtually all actions

## Usage
There is a [sample](https://github.com/shopgun/verso-android/tree/master/sample) 
