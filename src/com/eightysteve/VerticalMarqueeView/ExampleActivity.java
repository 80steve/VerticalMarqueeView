package com.eightysteve.VerticalMarqueeView;

import android.app.Activity;
import android.os.Bundle;

public class ExampleActivity extends Activity {
    private VerticalMarqueeView mVmv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mVmv = (VerticalMarqueeView) findViewById(R.id.marquee);
    }

    @Override
    public void onResume() {
        super.onResume();
        mVmv.setMarqueeText(new String[]{"hi1", "hi2", "hi3"});
        mVmv.startMarquee();
    }

    @Override
    public void onPause() {
        super.onPause();
        mVmv.pauseMarquee();
    }

    @Override
    public void onStop() {
        super.onStop();
        mVmv.stopMarquee();
    }
}
