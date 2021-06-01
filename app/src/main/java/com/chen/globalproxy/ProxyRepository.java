package com.chen.globalproxy;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ProxyRepository {

    private final ProxyDao proxyDao;
    private final LiveData<List<Proxy>> proxyLiveData;

    public ProxyRepository(Context context) {
        ProxyDatabase proxyDatabase = ProxyDatabase.getInstance(context.getApplicationContext());
        proxyDao = proxyDatabase.getProxyDao();
        proxyLiveData = proxyDao.queryAll();
    }

    public LiveData<List<Proxy>> getProxyLiveData() {
        return proxyLiveData;
    }

    void insertProxy(Proxy... proxies) {
        new InsertAsyncTask(proxyDao).execute(proxies);
    }

    void updateProxy(Proxy... proxies) {
        new UpdateAsyncTask(proxyDao).execute(proxies);
    }

    void deleteProxy(Proxy... proxies) {
        new DeleteAsyncTask(proxyDao).execute(proxies);
    }

    void clearProxy() {
        new ClearAsyncTask(proxyDao).execute();
    }


    static class InsertAsyncTask extends AsyncTask<Proxy, Void, List<Long>> {
        private final ProxyDao proxyDao;

        public InsertAsyncTask(ProxyDao proxyDao) {
            this.proxyDao = proxyDao;
        }

        @Override
        protected List<Long> doInBackground(Proxy... proxies) {
            return proxyDao.insert(proxies);
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Proxy, Void, Integer> {
        private final ProxyDao proxyDao;

        public UpdateAsyncTask(ProxyDao proxyDao) {
            this.proxyDao = proxyDao;
        }

        @Override
        protected Integer doInBackground(Proxy... proxies) {
            return proxyDao.update(proxies);
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Proxy, Void, Integer> {
        private final ProxyDao proxyDao;

        public DeleteAsyncTask(ProxyDao proxyDao) {
            this.proxyDao = proxyDao;
        }

        @Override
        protected Integer doInBackground(Proxy... proxies) {
            return proxyDao.delete(proxies);
        }
    }

    static class ClearAsyncTask extends AsyncTask<Void, Void, Void> {
        private final ProxyDao proxyDao;

        public ClearAsyncTask(ProxyDao proxyDao) {
            this.proxyDao = proxyDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            proxyDao.clear();
            return null;
        }
    }

}
