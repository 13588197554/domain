package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fly.util.Util;
import com.fly.enums.StatusEnum;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "v2_comment")
@JsonIgnoreProperties({"create_time", "update_time"})
public class V2Comment implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "post_id")
    @JsonProperty("post_id")
    private String postId;
    @Column(name = "member_id")
    @JsonProperty("member_id")
    private String memberId;
    private String content;
    private String device;
    @Column(name = "floor_number")
    private Integer floorNumber;
    private String status;
    @Column(name = "create_time")
    @JsonProperty("create_time")
    private String createTime = Util.getCurrentFormatTime();
    @Column(name = "update_time")
    @JsonProperty("upate_time")
    private String updateTime = Util.getCurrentFormatTime();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getStatus() {
        if (status == null) {
            status = StatusEnum.ACTIVE.getName();
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        if (this.createTime == null) {
            createTime = Util.getCurrentFormatTime();
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        if (updateTime == null) {
            this.updateTime = Util.getCurrentFormatTime();
        }
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "V2Comment{" +
                "id=" + id +
                ", postId='" + postId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", content='" + content + '\'' +
                ", floorNumber=" + floorNumber +
                ", status='" + status + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
