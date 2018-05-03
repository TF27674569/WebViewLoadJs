package com.cabinet.webviewloadjs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * Description : android调用js
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/4/29
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webView);

        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 加载html代码
        webView.loadUrl("file:///android_asset/javascript.html");

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {

                Log.e("TAG", "url: " + url + ",\nmessage:" + message + ", result:" + result);
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                result.confirm();

                // 是否禁掉js上面的弹窗
                return false;
            }

        });
    }

    public void click(View view) {
        // 注意调用的JS方法名要对应上
        // 调用javascript的callJS()方法
//        webView.loadUrl("javascript:callJS()");
        // Android版本变量
        final int version = Build.VERSION.SDK_INT;
        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
        if (version < 19) {
            webView.loadUrl("javascript:callJS()");
        } else {
            webView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                    Toast.makeText(MainActivity.this, "onReceiveValue:"+value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
