package com.cabinet.webviewloadjs;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

/**
 * Description : 通过对象映射达到交互
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/4/29
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class Main2Activity extends AppCompatActivity{
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidtoJs.init(getApplication());

        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //参数1：Javascript对象
        //参数2：Java对象名 h5里面调用时必须使用此名称
        webView.addJavascriptInterface(new AndroidtoJs(), "test");//AndroidtoJS类对象映射到js的test对象

        // 加载html代码
        webView.loadUrl("file:///android_asset/test1.html");

    }
}
