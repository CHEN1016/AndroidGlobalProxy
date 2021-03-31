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

    @ColumnInfo(name = "port")
    private Integer port;

    @ColumnInfo(name = "modified_at")
    private Date modifiedAt;


    public Proxy() {
    }

    // room只能识别一个构造器函数，定义多个需要使用Ignore标签
    @Ignore
    public Proxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    @Ignore
    public Proxy(String host, Integer port, Date modifiedAt) {
        this.host = host;
        this.port = port;
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

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
}
