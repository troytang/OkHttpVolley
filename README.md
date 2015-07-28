# README #

This is a http framework for android. base on volley and okhttp.

## Version ##
```
1.0
```

## How to use ##

### Common http request ###

* Http get request
```
OKHttpVolley.from(this)
        .url("http://www.baidu.com")
        .param("param1", "value1")
        .header("test", "value")
        .callback(new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                ((TextView) findViewById(R.id.tv_hello)).setText(result.toString());
            }

            @Override
            public void onFail(VolleyError error) {
                ((TextView) findViewById(R.id.tv_hello)).setText(error.toString());
            }
        }).get();
```

* Http post request
```
OKHttpVolley.from(this)
        .url("http://www.baidu.com")
        .param("param1", "value1")
        .header("test", "value")
        .callback(new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                ((TextView) findViewById(R.id.tv_hello)).setText(result.toString());
            }

            @Override
            public void onFail(VolleyError error) {
                ((TextView) findViewById(R.id.tv_hello)).setText(error.toString());
            }
        }).post();
```

* Upload a file to server
```
File file = new File("/storage/emulated/0/DCIM/Camera/test.jpg");
if (null == file || !file.exists()) {
    Toast.makeText(MainActivity.this, "no file", Toast.LENGTH_SHORT).show();
    return;
}
OKHttpVolley.from(this)
        .url("http://192.168.22.144:10401/statuses/filesUpload")
        .header("X-Requested-userId", "4dde32ccb597b12f6c1d7832")
        .header("X-Requested-networkId", "52beff9c24ac7e293eaeb256")
        .file(file)
        .callback(new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }).post();
```

* Download a file from server
```
File file = new File("/sdcard/maomao/wandoujia_cancel.apk");
try {
    file.createNewFile();
} catch (IOException e) {
    e.printStackTrace();
}
OKHttpVolley.from(this)
        .download("https://dl.wandoujia.com/files/phoenix/latest/wandoujia-wandoujia_web.apk")
        .param("timestamp", "1409388568830")
        .save(file)
        .progress(new ProgressCallback() {
            @Override
            public void pregress(long current, long total) {
                pbFile.setMax((int) total);
                pbFile.setProgress((int) current);
            }

            @Override
            public void done(File file) {
                Toast.makeText(MainActivity.this, "File downloaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(Exception error) {
                Toast.makeText(MainActivity.this, error.getCause().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void cancel(File fileCanceled) {
                Toast.makeText(MainActivity.this, "download cancel", Toast.LENGTH_SHORT).show();
                fileCanceled.delete();
            }
        }).start();
```

* Cancel a http request
```
Request request = OkHttpVolley.from(this)...get();
// if you want to cancel a http request, call
request.cancel();
```

### Base ###

* google volley
* square okhttp

### License ###

```
Copyright 2015 Troy Tang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```