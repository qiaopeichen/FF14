package com.example.ff14.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.ff14.R;
import com.example.ff14.utils.ADFilterTool;

public class MyWebView extends FrameLayout {

    private Context mContext;

    private WebView mWebView;
    private ProgressBar mProcessBar;

    public MyWebView(Context context) {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
        initWebView();
        initData();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.framelayout_webview, this);
        mWebView = rootView.findViewById(R.id.webView);
        mProcessBar = rootView.findViewById(R.id.progressBar);
    }

    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);  //支持js，如果不设置本行，html中的js代码都会失效

        // 设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);  //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        webSettings.supportMultipleWindows();  //多窗口

        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口，不明白什么意思
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    private void initData() {
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri requestUrl = request.getUrl();
                if (requestUrl != null) {
                    view.loadUrl(requestUrl.toString());   //在当前的webview中跳转到新的url
                    System.out.println("qiaopc:" + requestUrl.toString());
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProcessBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProcessBar.setVisibility(View.GONE);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view, WebResourceRequest request) {
                Uri requestUrl = request.getUrl();
                String url = requestUrl.toString().toLowerCase();
                if (!ADFilterTool.hasAd(mContext, url)) {
                    return super.shouldInterceptRequest(view, request);//正常加载
                } else {
                    //含有广告资源屏蔽请求
                    return new WebResourceResponse(null, null, null);
                }
            }
        });
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    public void goBack() {
        if (mWebView != null) {
            mWebView.goBack();
        }
    }
}
