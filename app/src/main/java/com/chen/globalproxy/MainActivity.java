package com.chen.globalproxy;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.InputDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialogx.interfaces.OnInputDialogButtonClickListener;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements EditPortDialogFragment.EditPortDialogListener {

    private static final String TAG = "MainActivity";

    final private String pm = "pm grant com.chen.globalproxy android.permission.WRITE_SECURE_SETTINGS";

    final private String msg = "adb shell " + pm;

    final String permissionName = Manifest.permission.WRITE_SECURE_SETTINGS; //android.permission.WRITE_SECURE_SETTINGS

    ProxyViewModel proxyViewModel;

    static ActionBar actionBar;

    RecyclerView recyclerView;

    MyAdapter myAdapter;

    ContentResolver contentResolver;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = this.getContentResolver();
        DialogX.init(this);

        actionBar = getSupportActionBar();

        proxyViewModel = new ViewModelProvider(this).get(ProxyViewModel.class);

        proxyViewModel.getProxyStrLiveData().observe(this, s -> actionBar.setTitle("当前 " + s));

        sp = getSharedPreferences(ProxyViewModel.CUSTOM_CONFIG_KEY, Context.MODE_PRIVATE);

        proxyViewModel.getPortLiveData().observe(this, integer -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(ProxyViewModel.PROXY_PORT_KEY, integer);
            editor.apply();//立即更新sp对象中的值，但还是会异步写入磁盘中
            //commit()会同步写入磁盘，避免在主线程中调用
        });

        proxyViewModel.setProxyStrLiveData(getGlobalProxy());

        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.proxyRecylerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myAdapter = new MyAdapter(this, proxyViewModel);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
        proxyViewModel.getProxyLiveData().observe(this, proxies -> {
            myAdapter.setProxyList(proxies);
            myAdapter.notifyDataSetChanged();
        });
//        int i = ContextCompat.checkSelfPermission(this, permissionName);
//        Log.d(TAG, "checkPermission: " + i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_proxy:
                new InputDialog("新增代理", "请输入代理地址", "确定", "取消", "10.1.")
                        .setCancelable(false)
                        .setOkButton((baseDialog, v, inputStr) -> {
                            String proxyStr = inputStr + ":" + proxyViewModel.getPortLiveData().getValue();
                            Toast.makeText(MainActivity.this, "当前代理为:" + proxyStr, Toast.LENGTH_SHORT).show();
                            proxyViewModel.setProxyStrLiveData(proxyStr);
                            proxyViewModel.insertProxy(new Proxy(inputStr, new Date()));
                            return false;
                        })
                        .show();
                break;
            case R.id.delete_all:
                MessageDialog.show("是否清空代理记录？", null, "确定", "取消")
                        .setOkButton((baseDialog, v) -> {
                            proxyViewModel.clearProxy();
                            return false;
                        });
                Toast.makeText(MainActivity.this, "删除全部", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cat_proxy:
                Toast.makeText(MainActivity.this, "当前代理为:" + getGlobalProxy(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.reset_proxy:
                proxyViewModel.setProxyStrLiveData(":0");
                Toast.makeText(MainActivity.this, "已取消代理，请重连WIFI", Toast.LENGTH_SHORT).show();
                break;
            case R.id.authorize:
                MessageDialog messageDialog = new MessageDialog("授权方式", "直接授权需要你的设备已经root", "直接授权", "取消", "使用ADB");
                messageDialog.setOtherButton((baseDialog, v) -> {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", msg);
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(MainActivity.this, "已将ADB命令复制到剪贴板", Toast.LENGTH_SHORT).show();
                    return false;
                });
                messageDialog.setOkButton((baseDialog, v) -> {
                    Toast.makeText(MainActivity.this, "直接授权，请授予Root权限", Toast.LENGTH_SHORT).show();
                    MyTool.execRootCmd(pm);
                    return false;
                });
                messageDialog.setButtonOrientation(LinearLayout.VERTICAL)
                        .setCancelable(false)
                        .show();
                break;
            case R.id.settings: // 菜单栏设置
                EditPortDialogFragment editPortDialogFragment = new EditPortDialogFragment();
                Bundle bundle = new Bundle();
                Integer value = proxyViewModel.getPortLiveData().getValue();
                if (value == null) {
                    value = 8888;
                }
                bundle.putInt("proxy_port", value);
                editPortDialogFragment.setArguments(bundle);
                editPortDialogFragment.show(getSupportFragmentManager(), "port_dialog");

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


//    /**
//     * 设置全局代理
//     *
//     * @param ipAndPort 代理
//     */
//    void setGlobalProxy(String ipAndPort) {
//        Settings.Global.putString(contentResolver, Settings.Global.HTTP_PROXY, ipAndPort);
//        proxyViewModel.setProxyStrLiveData(ipAndPort);
//    }


    /**
     * 获取全局代理
     *
     * @return 返回代理
     */
    String getGlobalProxy() {
        return Settings.Global.getString(contentResolver, Settings.Global.HTTP_PROXY);
    }

    /**
     * 查看是否拥有 android.permission.WRITE_SECURE_SETTINGS 权限
     *
     * @return true为拥有
     */
    boolean checkPermission() {
        boolean requested = false;
        int granted = ContextCompat.checkSelfPermission(this, permissionName);
        if (granted == PackageManager.PERMISSION_GRANTED) {
            requested = true;
        }
        return requested;

    }


    @Override
    public void onDialogPositiveClick(Integer port) {
        Log.d(TAG, "onDialogPositiveClick: " + port);
        proxyViewModel.getPortLiveData().setValue(port);
    }
}