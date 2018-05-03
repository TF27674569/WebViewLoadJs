package com.cabinet.webviewloadjs;

import android.app.Application;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2018/4/29
 * Email : 27674569@qq.com
 * Version : 1.0
 */
public class AndroidtoJs {

    private static Application app;

    public static void init(Application application){
        app = application;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        Toast.makeText(app, "JS调用了Android的hello方法:"+msg, Toast.LENGTH_SHORT).show();
    }
}
