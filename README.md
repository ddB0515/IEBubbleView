# IEBubbleView
Custom Android Bubbleview with curved Bezier Paths as I didn't find any similar that will fit my need so I have created ones.
Hope Someone will find it useful :)

##Screenshot
<img src="https://github.com/InvalidExcepti0n/IEBubbleView/raw/master/Screenshot_IEBubbleView.png" data-canonical-src="https://github.com/InvalidExcepti0n/IEBubbleView/raw/master/Screenshot_IEBubbleView.png" width="300" height="500" />

##Usage
```
<com.iebubble.IEBubbleView
        android:layout_width="150dp"
        android:layout_height="100dp"
        android:id="@+id/view3"
        app:ieb_distance="30dp"
        app:ieb_orientation="right"
        app:ieb_position="bottom_right"
        app:ieb_radius="15dp"
        app:ieb_type="normal"
        app:ieb_color="@android:color/white"
        android:layout_below="@+id/imageView"
        android:layout_toEndOf="@+id/imageView" />
```
###Details:
`app:ieb_color` color of IEBubbleView

`app:ieb_position` posititon `top_left` `top_right` `bottom_left` `bottom_right`
  
`app:ieb_orientation` orientation `left` `right`

`app:ieb_type` there are 2 types `small` `normal`

`app:ieb_radius` radius for curved corners

`app:ieb_distance` tail distance from left/right

`app:ieb_marginLeft` custom margins

`app:ieb_marginTop` custom margins

`app:ieb_marginRight` custom margins

`app:ieb_marginBottom` custom margins


##Import to your project
###Gradle
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.InvalidExcepti0n:IEBubbleView:1.0.0'
}
```
