package com.fly.pojo;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

//@Entity
//@Table(name = "tag")
public class FlyTag implements Serializable {

    @Id
    private String id;
//    @Column(name = "tag_name")
    private String tagName;
//    @Column(name = "pid")
    private String pid;
//    @Column(name = "tag_type")
    private String tagType;
    private String status;
//    @Column(name = "create_time")
    private Timestamp createTime;
//    @Column(name = "create_time")
    private Timestamp updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String toString() {
        return "FlyTag{" +
                "id='" + id + '\'' +
                ", pid='" + pid + '\'' +
                ", tagName='" + tagName + '\'' +
                ", tagType='" + tagType + '\'' +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
