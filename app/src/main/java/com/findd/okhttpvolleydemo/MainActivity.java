package com.findd.okhttpvolleydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.findd.okhttpvolley.Callback;
import com.findd.okhttpvolley.OKHttpVolley;
import com.findd.okhttpvolley.ProgressCallback;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ProgressBar pbFile;
    Request request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File("/storage/emulated/0/DCIM/Camera/test.jpg");
                if (null == file || !file.exists()) {
                    Toast.makeText(MainActivity.this, "no file", Toast.LENGTH_SHORT).show();
                    return;
                }
                OKHttpVolley.from(MainActivity.this)
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
            }
        });

        pbFile = (ProgressBar) findViewById(R.id.pb_file);
        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File("/sdcard/maomao/wandoujia_cancel.apk");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request = OKHttpVolley.from(MainActivity.this)
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
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != request) {
                    request.cancel();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
