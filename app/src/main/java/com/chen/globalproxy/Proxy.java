package com.chen.globalproxy;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Proxy {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "host")
    private String host;


    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;


    public Proxy() {
    }

    // room只能识别一个构造器函数，定义多个需要使用Ignore标签
//    @Ignore
//    public Proxy(String host) {
//        this.host = host;
//    }

    @Ignore
    public Proxy(String host, Date modifiedAt) {
        this.host = host;
        this.modifiedAt = modifiedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

}
