package com.cabinet.webviewloadjs;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description : 通过拦截url，解析url达到交互
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/4/29
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class Main3Activity extends AppCompatActivity{
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

        // 加载html代码
        webView.loadUrl("file:///android_asset/test2.html");


        webView.setWebViewClient(webViewClient);

    }

    // 复写WebViewClient类的shouldOverrideUrlLoading方法
    @SuppressLint("NewApi")
    WebViewClient webViewClient = new WebViewClient(){

        /**
         * 拦截所有请求
         * @param view
         * @param request
         * @return
         */
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            // 根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）可以自己约定格式
            //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

            Uri uri = request.getUrl();
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            if ( uri.getScheme().equals("js")) {
                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.getAuthority().equals("webview")) {
                    // 执行JS所需要调用的逻辑
                    System.out.println("js调用了Android的方法");
                    // 可以在协议上带有参数并传递到Android上
                    Set<String> collection = uri.getQueryParameterNames();
                    final StringBuilder builder = new StringBuilder();
                    for (String s : collection) {
                        builder.append(s).append(":").append(uri.getQueryParameter(s)).append("\n");
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Main3Activity.this, builder.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            return new WebResourceResponse("image/png", "UTF-8", null);
        }
    };

}
