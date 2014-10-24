package com.jasonrobinson.racer.ui.web;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jasonrobinson.racer.R;
import com.jasonrobinson.racer.ui.base.BaseFragment;
import com.metova.slim.annotation.Callback;

import butterknife.InjectView;

public class WebFragment extends BaseFragment {

    @InjectView(R.id.webview)
    WebView mWebView;

    @Callback
    WebCallback mCallback;

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
