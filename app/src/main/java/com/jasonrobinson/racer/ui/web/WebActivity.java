package com.jasonrobinson.racer.ui.web;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.BaseActivity;
import com.jasonrobinson.racer.ui.web.WebFragment.WebCallback;
import com.metova.slim.annotation.Extra;
import com.metova.slim.annotation.Layout;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.web_activity)
public class WebActivity extends BaseActivity implements WebCallback {

    public static final String EXTRA_URL = "url";

    @Extra(EXTRA_URL)
    String mUrl;

    WebFragment mWebFragment;

    @Bind(R.id.progress)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setTitle(R.string.forum_post);

        mWebFragment = (WebFragment) getSupportFragmentManager().findFragmentById(R.id.web_fragment);
        mWebFragment.loadUrl(mUrl);
    }

    @Override
    public void onBackPressed() {
        if (!mWebFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.web_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_browser:
                openInBrowser(mUrl);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
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

    private void openInBrowser(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
