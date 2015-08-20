package com.sjtu.icarer.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.sjtu.icarer.R;

public class WebviewActivity extends IcarerFragmentActivity{
	private WebView webView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String downloadUrl = getIntent().getStringExtra("downloadUrl");
		setContentView(R.layout.activity_webview);
		init(downloadUrl);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void init(String url){
        webView = (WebView) findViewById(R.id.webView);
        //WebView加载web资源
       webView.loadUrl(url);
       WebSettings webSettings = webView.getSettings();
       webSettings.setJavaScriptEnabled(true);
       webView.setWebViewClient(new WebViewClient());
    }
	
    /**
     * 按键响应，在WebView中查看网页时，按返回键的时候按浏览历史退回,如果不做此项处理则整个WebView返回退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            // 返回键退回
        	   webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up
        // to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
}
