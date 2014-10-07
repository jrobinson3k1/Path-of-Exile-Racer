package com.jasonrobinson.racer.ui.web;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseActivity;
import com.jasonrobinson.racer.ui.web.WebFragment.WebCallback;

import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

public class WebActivity extends BaseActivity implements WebCallback {

    public static final String EXTRA_URL = "url";

    @InjectFragment(tag = "web_fragment")
    WebFragment mWebFragment;
    @InjectView(tag = "progress")
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.forum_post);

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null) {
            throw new IllegalArgumentException("url not found in intent");
        }

        mWebFragment.loadUrl(url);
    }

    @Override
    public void onBackPressed() {

        if (!mWebFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onProgressChanged(int progress) {

        mProgressBar.setAnimation(null);
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(progress);

        if (mProgressBar.getProgress() == mProgressBar.getMax()) {
            Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            anim.setFillAfter(true);
            anim.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                    // no-op
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                    // no-op
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    mProgressBar.setVisibility(View.GONE);
                }
            });

            mProgressBar.startAnimation(anim);
        }
    }
}
