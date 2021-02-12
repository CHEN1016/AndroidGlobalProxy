package com.chen.globalproxy;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    final String permissionName = Manifest.permission.WRITE_SECURE_SETTINGS; //android.permission.WRITE_SECURE_SETTINGS
    final String packageName = "com.chen.globalproxy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentResolver contentResolver = this.getContentResolver();

        TextView ipAddrTextView = findViewById(R.id.ip_address);
        TextView ipPortTextView = findViewById(R.id.port_number);


        Button catButton = findViewById(R.id.cat_proxy);
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proxy = Settings.Global.getString(contentResolver, Settings.Global.HTTP_PROXY);
                Toast.makeText(MainActivity.this, "当前代理为：" + proxy, Toast.LENGTH_SHORT).show();
            }
        });
        Button resetButton = findViewById(R.id.reset_proxy);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Settings.Global.putString(contentResolver, Settings.Global.HTTP_PROXY, ":0");
                    Toast.makeText(MainActivity.this, "已取消代理，请重连WIFI", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "代理设置失败，请使用ADB赋予权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button applyButton = findViewById(R.id.apply_button);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddr = ipAddrTextView.getText().toString();
                String ipPort = ipPortTextView.getText().toString();
                if (checkPermission()) {
                    Settings.Global.putString(contentResolver, Settings.Global.HTTP_PROXY, ipAddr + ":" + ipPort);
                    Toast.makeText(MainActivity.this, "代理设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "代理设置失败，请使用ADB赋予权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * 查看是否拥有 android.permission.WRITE_SECURE_SETTINGS 权限
     *
     * @return true为拥有
     */
    boolean checkPermission() {
        boolean requested = false;
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if (Arrays.asList(requestedPermissions).contains(permissionName)) {
                requested = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return requested;

    }

}