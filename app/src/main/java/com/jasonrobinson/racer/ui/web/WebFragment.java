package com.jasonrobinson.racer.ui.web;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseFragment;

import roboguice.inject.InjectView;

public class WebFragment extends BaseFragment {

    @InjectView(tag = "webview")
    WebView mWebView;

    private WebCallback mCallback;

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        mCallback = castActivity(WebCallback.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.web_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
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

        public void onProgressChanged(int progress);
    }
}
