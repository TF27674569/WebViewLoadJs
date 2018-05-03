# WebView与JS交互

### **一、android调用js**
&nbsp;　　1.1 设置交互权限等
```java
 WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 加载html代码
        webView.loadUrl("file:///android_asset/javascript.html");
```
&nbsp;　　1.2 根据不同的版本选择不同的调用方法
```java
final int version = Build.VERSION.SDK_INT;
        // 因为该方法在 Android 4.4 版本才可使用，所以使用时需进行版本判断
        if (version < 19) {
            // 调用callJS()函数
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
```

&nbsp;　　1.3 h5代码
```java
<!--文本名：javascript-->
<!DOCTYPE html>
<html>

   <head>
      <meta charset="GBK">
      <title>Carson_Ho</title>

<!--JS代码-->


 <script>
	 <!--Android需要调用的方法-->
   function callJS(){
      alert("Android调用了JS的callJS方法");
   }
</script>

   </head>

</html>
```
### **二、通过映射关系，h5调用java代码**
```java
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
        
        
        // h5
        <!DOCTYPE html>
        <html>
           <head>
              <meta charset="GBK">
              <title>Carson</title>  
              <script>
        
                 function callAndroid(){
        			<!--由于对象映射，所以调用test对象等于调用Android映射的对象-->
                    test.hello("js调用了android中的hello方法");
                 }
        
              </script>
           </head>
        
           <body>
               <!--点击按钮则调用callAndroid函数-->
              <button type="button" id="button1"  onclick="callAndroid()"  style="width: 200px; height: 60px;">call android method!!</button>
           </body>
           
           
        </html>
```

### **三、通过拦截url,java解析url定义协议交互**
```java
 WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 加载html代码
        webView.loadUrl("file:///android_asset/test2.html");


        webView.setWebViewClient(webViewClient);
        
            
        // 拦截对象
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
```
### **四、通过弹窗，解析弹窗信息**

```java
WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        // 加载html代码
        webView.loadUrl("file:///android_asset/test3.html");


        webView.setWebChromeClient(webViewClient);
        
        
        // 复写WebViewClient类的shouldOverrideUrlLoading方法
            @SuppressLint("NewApi")
            WebChromeClient webViewClient = new WebChromeClient(){
                // 拦截输入框(原理Main3)
                // 参数message:代表promt（）的内容（不是url）
                // 参数result:代表输入框的返回值
                @Override
                public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                    // 根据协议的参数，判断是否是所需要的url(原理同方式2)
                    // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                    //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）
        
                    Uri uri = Uri.parse(message);
                    // 如果url的协议 = 预先约定的 js 协议
                    // 就解析往下解析参数
                    if ( uri.getScheme().equals("js")) {
        
                        // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                        // 所以拦截url,下面JS开始调用Android需要的方法
                        if (uri.getAuthority().equals("webview")) {
        
                            // 执行JS所需要调用的逻辑
                            // 可以在协议上带有参数并传递到Android上
                            Set<String> collection = uri.getQueryParameterNames();
                            final StringBuilder builder = new StringBuilder();
                            for (String s : collection) {
                                builder.append(s).append(":").append(uri.getQueryParameter(s)).append("\n");
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Main4Activity.this, builder.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            //参数result:代表消息框的返回值(输入值)
                            result.confirm("js调用了Android的方法成功啦");
                        }
                        return true;
                    }
                    result.confirm("js调用了Android的方法成功啦");
                    return super.onJsPrompt(view, url, message, defaultValue, result);
                }
        
                // 通过alert()和confirm()拦截的原理相同，此处不作过多讲述
        
                // 拦截JS的警告框
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
        
                // 拦截JS的确认框
                @Override
                public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                    return super.onJsConfirm(view, url, message, result);
                }
            };
```







