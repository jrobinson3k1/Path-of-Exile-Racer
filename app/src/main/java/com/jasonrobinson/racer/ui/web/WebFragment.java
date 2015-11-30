package com.jasonrobinson.racer.ui.web;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.BaseFragment;
import com.metova.slim.annotation.Callback;
import com.metova.slim.annotation.Layout;

import butterknife.Bind;
import butterknife.ButterKnife;

@Layout(R.layout.web_fragment)
public class WebFragment extends BaseFragment {

    @Bind(R.id.webview)
    WebView mWebView;

    @Callback
    WebCallback mCallback;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mCallback.onProgressChanged(newProgress);
            }
        });
    }

    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return false;
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public interface WebCallback {

        void onProgressChanged(int progress);
    }
}
