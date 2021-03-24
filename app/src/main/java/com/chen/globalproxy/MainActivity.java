package com.chen.globalproxy;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.kongzue.dialogx.DialogX;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener;

public class MainActivity extends AppCompatActivity {

    final private String pm = "pm grant com.chen.globalproxy android.permission.WRITE_SECURE_SETTINGS";

    final private String msg = "adb shell " + pm;

    private static final String TAG = "MainActivity";

    final String permissionName = Manifest.permission.WRITE_SECURE_SETTINGS; //android.permission.WRITE_SECURE_SETTINGS

    ContentResolver contentResolver;

    Button btnAuthorize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentResolver = this.getContentResolver();
        DialogX.init(this);


        if (checkPermission()) {
            setContentView(R.layout.activity_main);
            activity_main_layout();
        } else {
            setContentView(R.layout.activity_main_msg);
            activity_main_msg_layout();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_proxy:
                CustomDialog.build()
                        .setCustomView(new OnBindView<CustomDialog>(R.layout.layout_custom_dialog) {
                            @Override
                            public void onBind(final CustomDialog dialog, View v) {
                                TextView addressInput = v.findViewById(R.id.address_input);
                                TextView portInput = v.findViewById(R.id.port_input);
                                View btnOk = v.findViewById(R.id.btn_selectPositive);
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String address = addressInput.getText().toString();
                                        Integer port = Integer.parseInt(portInput.getText().toString());
                                        String proxyStr = address + ":" + port;
                                        Log.d(TAG, "onClick: " + proxyStr);
                                        Toast.makeText(MainActivity.this, "当前代理为:" + proxyStr, Toast.LENGTH_SHORT).show();
                                        setGlobalProxy(proxyStr);
                                        dialog.dismiss();
                                    }
                                });
                                View btnCancel = v.findViewById(R.id.btn_selectNegative);
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .setMaskColor(Color.parseColor("#4D000000")) //Dialog阴影遮罩
                        .setCancelable(false)
                        .show();
//
                break;
//            case R.id.delete_all:
//                MessageDialog.show("是否清空代理记录？", null, "确定", "取消")
//                        .setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
//                            @Override
//                            public boolean onClick(MessageDialog baseDialog, View v) {
//                                proxyViewModel.clearProxy();
//                                return false;
//                            }
//                        });
//                Toast.makeText(MainActivity.this, "删除全部", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.cat_proxy:
                Toast.makeText(MainActivity.this, "当前代理为:" + getGlobalProxy(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.reset_proxy:
                setGlobalProxy(":0");
                Toast.makeText(MainActivity.this, "已取消代理，请重连WIFI", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 查看权限页面
     */
    private void activity_main_msg_layout() {
        setContentView(R.layout.activity_main_msg);
        btnAuthorize = findViewById(R.id.btn_authorize);
        btnAuthorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialog messageDialog = new MessageDialog("授权方式", "直接授权需要你的设备已经root", "直接授权", "取消", "使用ADB");
                messageDialog.setOtherButton(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog baseDialog, View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", msg);
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(MainActivity.this, "已将ADB命令复制到剪贴板", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                messageDialog.setOkButton(new OnDialogButtonClickListener<MessageDialog>() {
                    @Override
                    public boolean onClick(MessageDialog baseDialog, View v) {
                        Toast.makeText(MainActivity.this, "直接授权", Toast.LENGTH_SHORT).show();
                        String s = MyTool.execRootCmd(pm);
                        setContentView(R.layout.activity_main); //没有动画，切换很生硬
                        Log.d(TAG, "onClick: " + s);
                        return false;
                    }
                });
                messageDialog.setButtonOrientation(LinearLayout.VERTICAL)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    /**
     * 主页面
     */
    private void activity_main_layout() {
        int i = ContextCompat.checkSelfPermission(this, permissionName);
        Log.d(TAG, "checkPermission: " + i);
    }


    /**
     * 设置全局代理
     *
     * @param ipAndPort
     */
    void setGlobalProxy(String ipAndPort) {
        Settings.Global.putString(contentResolver, Settings.Global.HTTP_PROXY, ipAndPort);
    }

    /**
     * 获取全局代理
     *
     * @return
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


}