package com.chen.globalproxy;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProxyViewModel extends AndroidViewModel {

    private final ProxyRepository proxyRepository;

    private MutableLiveData<String> proxyStrLiveData;

    private MutableLiveData<Integer> portLiveData;

//    private final Integer port = 2333; //代理端口，暂时写死

    private final Application application;


    public ProxyViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        proxyRepository = new ProxyRepository(application);
    }

    public MutableLiveData<String> getProxyStrLiveData() {
        if (proxyStrLiveData == null) {
            proxyStrLiveData = new MutableLiveData<>();
            proxyStrLiveData.setValue(":0");
        }
        return proxyStrLiveData;
    }

    public MutableLiveData<Integer> getPortLiveData() {
        if (portLiveData == null) {
            portLiveData = new MutableLiveData<>();
            SharedPreferences sp = application.getSharedPreferences("custom_config", Context.MODE_PRIVATE);
            int port = sp.getInt("proxy_port", 8888);
            portLiveData.setValue(port);
        }
        return portLiveData;
    }

    public void setProxyStrLiveData(String ipAndPort) {
        proxyStrLiveData.setValue(ipAndPort);
        ContentResolver contentResolver = application.getContentResolver();
        Settings.Global.putString(contentResolver, Settings.Global.HTTP_PROXY, ipAndPort);
    }

//    public Integer getPort() {
//        return port;
//    }

    public LiveData<List<Proxy>> getProxyLiveData() {
        return proxyRepository.getProxyLiveData();
    }

    void insertProxy(Proxy... proxies) {
        proxyRepository.insertProxy(proxies);
    }

    void updateProxy(Proxy... proxies) {
        proxyRepository.updateProxy(proxies);
    }

    void deleteProxy(Proxy... proxies) {
        proxyRepository.deleteProxy(proxies);
    }

    void clearProxy() {
        proxyRepository.clearProxy();
    }
}
