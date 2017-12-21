package com.omneagate.GifAnimation;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class GifWebView extends WebView {

    public GifWebView(Context context) {
        super(context);
    }

    public GifWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GifWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadPath(String path){
        loadUrl(path);
    }

}