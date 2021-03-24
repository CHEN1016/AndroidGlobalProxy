package com.chen.globalproxy;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyTool {


    private static final String TAG = "MyTool";

    /**
     * @param cmd 执行的命令
     * @return 执行结果
     */
    public static String execRootCmd(String cmd) {
        StringBuilder result = new StringBuilder();
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            // /system/bin/sh
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
//            Process p = Runtime.getRuntime().exec("/system/bin/sh");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            Log.i(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result.append(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "execRootCmd: " + e.getMessage());
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * 校验代理是否有效
     *
     * @param ip   代理地址
     * @param port 代理端口
     * @return true为有效
     */
    public static boolean checkProxy(String ip, Integer port) {
        //todo 校验代理是否有效
        return true;
    }

}
