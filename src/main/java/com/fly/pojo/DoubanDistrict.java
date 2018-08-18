package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fly.util.Util;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "douban_district")
@JsonIgnoreProperties({"createTime", "updateTime"})
public class DoubanDistrict {

    @Id
    private String id;
    private String pid;
    private String name;
    @Column(name = "create_time")
    private String createTime = Util.getCurrentFormatTime();
    @Column(name = "update_time")
    private String updateTime = Util.getCurrentFormatTime();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DoubanDistrict{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
