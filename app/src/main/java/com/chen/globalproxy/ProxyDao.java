package com.chen.globalproxy;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProxyDao {

    @Insert
    List<Long> insert(Proxy... proxy);

    @Query("select * from proxy order by modified_at desc")
    LiveData<List<Proxy>> queryAll();

    @Update()
    Integer update(Proxy... proxy);

    @Delete
    Integer delete(Proxy... proxies);

    @Query("delete from proxy")
    void clear();

}
