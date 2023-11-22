# android-compose-simple-timer(最小限の実装)
JetpackComposeを利用して作成したシンプルなタイマーアプリ

>[!CAUTION]
>このコードは不完全です。  
>・タイマーをストップしたのにタイマーが裏で継続されてしまっています。  
>・タイマーの目盛りの表示がずれています。  
>・ダークモードに対応していません。

## 想定環境
使用言語: Kotlin  
Android Studio Giraffe | 2022.3.1  
OS: Windows 11  
minSdkVersion: 26  
targetSdkVersion: 33 

## 使用ライブラリ
不要なライブラリを追加している可能性があります。
### Navigation
```
dependencies {
    implementation "androidx.navigation:navigation-fragment-ktx:2.5.3"
    implementation "androidx.navigation:navigation-ui-ktx:2.5.3"
    implementation "androidx.navigation:navigation-compose:2.5.3"
}
```
### ViewModel
```
dependencies {
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1"
    implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    implementation "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.5.1"
}
```
### Room
```
plugins {
    id 'com.google.devtools.ksp' version "1.8.21-1.0.11"
}
dependencies {
    implementation "androidx.room:room-runtime:2.5.0"
    annotationProcessor "androidx.room:room-compiler:2.5.0"
    ksp "androidx.room:room-compiler:2.5.0"
    implementation "androidx.room:room-ktx:2.5.0"
}
```

## 使用権限
AlarmManagerがアプリを閉じているときに作動しないことが起こっており、それを解消するために様々な権限の付与を試みています。`android.permission.VIBRATE`は通知の際に振動で知らせるための権限です。
```xml
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>
```




<img src="img/screenshot_simple_timer_1.jpg" width="320px">
<img src="img/screenshot_simple_timer_2.jpg" width="320px">
