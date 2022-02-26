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
  implementation 'com.github.emreesen27:Android-Nested-Progress:v1.0.1'
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

### Properties

| Property Name | Type          | Default       | Range
| ------------- | ------------- | ------------- | ------------- 
| innerLoaderStrokeWidth | Dimension  | 4.0F x dp | (1.0F - 10.0F ) x dp
| outerLoaderStrokeWidth   | Dimension | 4.5F x dp | (1.0F -10.0F ) x dp
| innerLoaderLength| Float| 260.0F| -
| outerLoaderLength| Float| 320.0F | -
| innerAnimInterpolator| Interpolator| overshoot | -
| outerAnimInterpolator| Interpolator| lineer | -
| innerLoaderAnimDuration| Integer| 1000| -
| outerLoaderAnimDuration| Integer| 1000 | -
| innerLoaderColor| Color| D3DEDC | -
| outerLoaderColor| Color| 2666CF | -
| spaceBetweenCircles| Dimension| 3.0F x dp | (1.0F - 10.0F ) x dp



