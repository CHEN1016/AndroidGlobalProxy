package com.chen.globalproxy;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chen.globalproxy.databinding.ActivityMainBinding;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements EditPortDialogFragment.EditPortDialogListener, NewProxyDialogFragment.NewProxyDialogListener {

    private static final String TAG = "MainActivity";

    final private String pm = "pm grant com.chen.globalproxy android.permission.WRITE_SECURE_SETTINGS";

    final private String msg = "adb shell " + pm;

    final String permissionName = Manifest.permission.WRITE_SECURE_SETTINGS; //android.permission.WRITE_SECURE_SETTINGS

    ProxyViewModel proxyViewModel;

    static ActionBar actionBar;

    MyAdapter myAdapter;

    ContentResolver contentResolver;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = this.getContentResolver();

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

        int i = ContextCompat.checkSelfPermission(this, permissionName);
        if (i != -1) {
            proxyViewModel.setProxyStrLiveData(getGlobalProxy());
        } else {
            permissionDialog();
        }

        com.chen.globalproxy.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.proxyRecylerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        myAdapter = new MyAdapter(this, proxyViewModel);
        binding.proxyRecylerView.setLayoutManager(new LinearLayoutManager(this));
        binding.proxyRecylerView.setAdapter(myAdapter);
        proxyViewModel.getProxyLiveData().observe(this, proxies -> {
            myAdapter.setProxyList(proxies);
            myAdapter.notifyDataSetChanged();
        });
    }

    private void permissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("授权方式").setMessage("直接授权需要您的设备已经root")
                .setPositiveButton("授权", (dialog, which) -> {
                    Toast.makeText(MainActivity.this, "请授予Root权限", Toast.LENGTH_SHORT).show();
                    MyTool.execRootCmd(pm);
                })
                .setNeutralButton("使用ADB", (dialog, which) -> {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData mClipData = ClipData.newPlainText("Label", msg);
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(MainActivity.this, "已将ADB命令复制到剪贴板", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(false)
                .show();
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
                NewProxyDialogFragment newProxyDialogFragment = new NewProxyDialogFragment();
                newProxyDialogFragment.show(getSupportFragmentManager(), "id_dialog");
                break;
            case R.id.delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("是否清空代理记录？")
                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                            proxyViewModel.clearProxy();
                            Toast.makeText(MainActivity.this, "删除全部", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                break;
            case R.id.cat_proxy:
                Toast.makeText(MainActivity.this, "当前代理为:" + getGlobalProxy(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.reset_proxy:
                proxyViewModel.setProxyStrLiveData(":0");
                Toast.makeText(MainActivity.this, "已取消代理，请重连WIFI", Toast.LENGTH_SHORT).show();
                break;
            case R.id.authorize:
                permissionDialog();
                break;
            case R.id.setting_port: // 菜单栏设置
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
    public void editPortDialogPositive(Integer port) {
        Log.d(TAG, "editPortDialogPositive: " + port);
        proxyViewModel.getPortLiveData().setValue(port);
    }

    @Override
    public void newProxyDialogPositive(String ipAddress) {
        Log.d(TAG, "newProxyDialogPositive: " + ipAddress);
        String proxyStr = ipAddress + ":" + proxyViewModel.getPortLiveData().getValue();
        Toast.makeText(MainActivity.this, "当前代理为:" + proxyStr, Toast.LENGTH_SHORT).show();
        proxyViewModel.setProxyStrLiveData(proxyStr);
        proxyViewModel.insertProxy(new Proxy(ipAddress, new Date()));


    }
}