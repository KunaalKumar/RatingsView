# RatingsView
An android custom view to show ratings

[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[ ![Download](https://api.bintray.com/packages/kunaalkumar/ratings-view/dev.kunaal%3ARatingsView/images/download.svg?version=0.0.3) ](https://bintray.com/kunaalkumar/ratings-view/dev.kunaal%3ARatingsView/0.0.3/link)


<img src="static/preview.gif" alt="sample" title="sample" width="300"/>

## Dependency
Add this to your module `build.gradle`
```gradle
dependencies {
    implementation 'implementation 'dev.kunaal:RatingsView:<latest_version>'
}
```

## XML 
```xml
<dev.kunaal.RatingsView.RatingsView
        app:textColor="@color/colorAccent"
        app:arcColor="@color/colorAccent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```
If `textColor` and/or `arcColor` isn't set, the application's primary color (`R.attr.colorPrimary`) is used.

## Usage
```kotlin
val ratingsView = findViewById<RatingsView>(R.id.ratings_view)

ratingsView.setRatings(84)
```
This will animate the number to `84`, as well as the arc itself.
