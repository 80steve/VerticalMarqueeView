/*
*
* Copyright 2014 Steve Chan, http://80steve.com
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
package com.eightysteve.VerticalMarqueeView;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class VerticalMarqueeView extends ScrollView {
    private boolean mShouldStop;
    private boolean mShouldPause;
    private String[] mMarqueeText;
    private Context mContext;
    private LinearLayout mLL;
    private ScrollTask mCurrentScrollTask;
    private MarqueeOnClickListener mMarqueeOnClickListener;
    private String mOnClickFunction;
    private float mTextSize;
    private int mRefTextHeight;
    private int mTextColor;
    private int mDelay;

    public VerticalMarqueeView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public VerticalMarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalMarqueeView);
        if (ta != null) {
            mTextSize = ta.getDimension(R.styleable.VerticalMarqueeView_vmvTextSize, mTextSize);
            mTextColor = ta.getColor(R.styleable.VerticalMarqueeView_vmvTextColor, mTextColor);
            mDelay = ta.getInteger(R.styleable.VerticalMarqueeView_vmvDelay, mDelay);
            mOnClickFunction = ta.getString(R.styleable.VerticalMarqueeView_vmvMarqueeOnClick);
            mRefTextHeight = ta.getDimensionPixelSize(R.styleable.VerticalMarqueeView_android_layout_height, 0);
        }
    }

    public VerticalMarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VerticalMarqueeView);
        if (ta != null) {
            mTextSize = ta.getDimension(R.styleable.VerticalMarqueeView_vmvTextSize, mTextSize);
            mTextColor = ta.getColor(R.styleable.VerticalMarqueeView_vmvTextColor, mTextColor);
            mDelay = ta.getInteger(R.styleable.VerticalMarqueeView_vmvDelay, mDelay);
            mOnClickFunction = ta.getString(R.styleable.VerticalMarqueeView_vmvMarqueeOnClick);
            mRefTextHeight = ta.getDimensionPixelSize(R.styleable.VerticalMarqueeView_android_layout_height, 0);
        }
    }

    private void init() {
        mShouldStop = false;
        mShouldPause = false;
        mTextColor = getResources().getColor(android.R.color.black);
        mTextSize = 20;
        mDelay = 2000;
        mLL = new LinearLayout(mContext);
        mLL.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mLL.setOrientation(LinearLayout.VERTICAL);
        this.addView(mLL);
        this.setVerticalScrollBarEnabled(false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void setMarqueeText(String[] marqueeText) {
        boolean needRestart = false;
        if (mCurrentScrollTask != null) {
            if (mCurrentScrollTask.getStatus() == AsyncTask.Status.RUNNING) needRestart = true;
            mCurrentScrollTask.cancel(true);
        }
        this.mMarqueeText = marqueeText;
        mLL.removeAllViews();
        for (int i = 0; i < mMarqueeText.length; i++) {
            String text = mMarqueeText[i];
            TextView tv = new TextView(mContext);
            tv.setSingleLine(true);
            tv.setText(text);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setTextColor(mTextColor);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            tv.setTag(i);
            tv.setMinHeight(mRefTextHeight);
            tv.setMaxHeight(mRefTextHeight);
            tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer)v.getTag();
                    if (mMarqueeOnClickListener != null) {
                        mMarqueeOnClickListener.onClick(position);
                    } else if (mOnClickFunction != null && !mOnClickFunction.isEmpty()) {
                        try {
                            mContext.getClass().getDeclaredMethod(mOnClickFunction, new Class[]{Integer.class});
                        } catch (NoSuchMethodException e) {
                        }
                    }
                }
            });
            mLL.addView(tv);
        }
        if (needRestart) startMarquee();
    }

    public void setMarqueeOnClickListener(MarqueeOnClickListener marqueeOnClickListener) {
        this.mMarqueeOnClickListener = marqueeOnClickListener;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public int getDelay() {
        return mDelay;
    }

    public void setDelay(int delay) {
        this.mDelay = delay;
    }

    public void startMarquee() {
        mCurrentScrollTask = new ScrollTask();
        mCurrentScrollTask.execute();
    }

    public void resumeMarquee() {
        mShouldPause = false;
        if (mShouldStop) {
            mShouldStop = false;
            mCurrentScrollTask = new ScrollTask();
            mCurrentScrollTask.execute();
        }
    }

    public void pauseMarquee() {
        mShouldPause = true;
    }

    public void stopMarquee() {
        mShouldStop = true;
    }

    private class ScrollTask extends AsyncTask<Void, Void, Void> {
        private int mPixelCount;
        private int mScrollInterval;

        @Override
        protected Void doInBackground(Void... params) {

            while (mLL.getChildCount() == 0 || ((TextView) mLL.getChildAt(0)).getLineCount() <= 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            mScrollInterval = mLL.getHeight() / VerticalMarqueeView.this.mMarqueeText.length;
            mPixelCount = mLL.getHeight() - VerticalMarqueeView.this.getHeight();

            while (!mShouldStop) {
                if (mShouldPause) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                try {
                    Thread.sleep(mDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!mShouldStop) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (VerticalMarqueeView.this.getScrollY() == mPixelCount) {
                                VerticalMarqueeView.this.smoothScrollTo(0, 0);
                            } else {
                                VerticalMarqueeView.this.smoothScrollBy(0, mScrollInterval);
                            }
                        }
                    });
                }
            }
            return null;
        }
    }

    interface MarqueeOnClickListener {
        void onClick(int position);
    }
}

