package com.chen.globalproxy;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProxyViewModel extends ViewModel {

    private MutableLiveData<String> proxyStrLiveData;


    public MutableLiveData<String> getProxyStrLiveData() {
        if (proxyStrLiveData == null) {
            proxyStrLiveData = new MutableLiveData<>();
            proxyStrLiveData.setValue(":0");
        }
        return proxyStrLiveData;
    }

    public void setProxyStrLiveData(String idAndPort) {
        proxyStrLiveData.setValue(idAndPort);
    }

}
