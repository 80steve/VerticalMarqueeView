VerticalMarqueeView
===================

VerticalMarqueeView is an Android View which displays string arrays with a vertical marquee-scrolling effect. Each time, one item in the arrays will be displayed, and after every <user-defined-delay> seconds, the view will be scrolled to display the next item.

## Usage

### Programmatically

```java
private VerticalMarqueeView mVmv = (VerticalMarqueeView) findViewById(R.id.marquee);

mVmv.setTextColor(getResources().getColor(android.R.color.black));
mVmv.setTextSize(50);
mVmv.setDelay(1000);
mVmv.setMarqueeText(new String[]{"item1", "item2", "item3"});
mVmv.startMarquee();

mVmv.setMarqueeOnClickListener(new VerticalMarqueeView.MarqueeOnClickListener() {
    @Override
    public void onClick(int position) {
        // do something with the when user clicked on the marquee
        // position: index of the string arrays
    }
});
```

### XML

```xml
<com.eightysteve.VerticalMarqueeView.VerticalMarqueeView
        xmlns:vmv="http://schemas.android.com/apk/res-auto"
        android:id="@+id/marquee"
        android:layout_width="fill_parent"
        android:layout_height="80dp"
        android:background="@android:color/white"
        vmv:vmvTextColor="@android:color/black"
        vmv:vmvTextSize="25sp"
        vmv:vmvDelay="1000"
        vmv:vmvMarqueeOnClick="onClick">
</com.eightysteve.VerticalMarqueeView.VerticalMarqueeView>
```

## API

**Setting Marquee Text Color**
``` java
setDelay(int:colorResId);
```

**Setting Marquee Text Size**
``` java
setDelay(float:pixel);
```

**Setting Marquee Scrolling Delay (e.g how long the displayed string should stay before swapping)**
``` java
setDelay(int:milliseconds);
```

**Setting Marquee the string arrays to be displayed**
``` java
setMarqueeText(String[]:stringArray);
```

**Setting on click listener to be called when user click on an marquee item**
``` java
setMarqueeOnClickListener(MarqueeOnClickListener:listener);

// listener
new MarqueeOnClickListener() {
    void onClick(int position); // Required
}
```

## License

Copyright 2014 Steve Chan, http://80steve.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
