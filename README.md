# Android Nested Progress
Nested Customizable Progress For Anroid

[![](https://jitpack.io/v/emreesen27/Android-Nested-Progress.svg)](https://jitpack.io/#emreesen27/Android-Nested-Progress)

#### Add this in your root build.gradle file
```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
#### Add this to your module's build.gradle file
```gradle
dependencies {
  implementation 'com.github.emreesen27:Android-Nested-Progress:v1.0.0'
}
```

#### Different Combinations
<img src="https://github.com/emreesen27/Android-Nested-Progress/blob/assets/example.gif?raw=true"/> 

#### Usage
* Add view into your layout file

```xml
  <com.sn.lib.NestedProgress
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      app:innerAnimInterpolator="linear"
      app:outerAnimInterpolator="overshoot"
      app:outerLoaderAnimDuration="1500" /> 

```
