package com.chen.globalproxy;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


@Database(entities = {Proxy.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class ProxyDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "android_global_proxy";

    private static ProxyDatabase databaseInstance;

    public static synchronized ProxyDatabase getInstance(Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(), ProxyDatabase.class, DATABASE_NAME)
//                    .allowMainThreadQueries() // 允许主线程操作数据，默认不允许在做这个耗时操作，否则会报错
                    .build();
        }
        return databaseInstance;
    }


    public abstract ProxyDao getProxyDao();
}
