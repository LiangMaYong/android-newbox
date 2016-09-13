package com.liangmayong.android_netbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.liangmayong.netbox.Netbox;
import com.liangmayong.netbox.NetboxConfig;
import com.liangmayong.netbox.defualts.OnDefualtNetboxListener;
import com.liangmayong.netbox.interfaces.OnNetboxListener;
import com.liangmayong.netbox.response.Response;
import com.liangmayong.netbox.throwables.NetboxError;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Netbox.server(GithubService.class).path("/list").param("key", "param").exec(this, new OnNetboxListener() {
            @Override
            public void onResponse(Response response) {
                Toast.makeText(MainActivity.this, response.getBody(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(NetboxError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}