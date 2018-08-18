package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fly.util.Util;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "v2_post")
@JsonIgnoreProperties({"createTime, updateTime, spider"})
public class V2Post implements Serializable {
    @Id
    private String id;
    private String type;
    private String title;
    private String url;
    private String content;
    private long created;
    @Column(name = "last_modified")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private long lastModified;
    @Column(name = "last_touched")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private long lastTouched;
    @Column(name = "node_id")
    private String nodeId;
    @Column(name = "member_id")
    private String memberId;
    @Column(name = "create_time")
    private String createTime = Util.getCurrentFormatTime();
    @Column(name = "update_time")
    private String updateTime = Util.getCurrentFormatTime();

    @Column(name = "is_spider")
    private Boolean isSpider = false;

    @Transient
    private V2Node node;
    @Transient
    private V2Member member;
    @Transient
    private List<V2Comment> comments = new ArrayList<>();
    @Transient
    @JsonProperty("comment_count")
    private Integer commentCount = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getLastTouched() {
        return lastTouched;
    }

    public void setLastTouched(long lastTouched) {
        this.lastTouched = lastTouched;
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

    public V2Node getNode() {
        return node;
    }

    public void setNode(V2Node node) {
        this.node = node;
    }

    public V2Member getMember() {
        return member;
    }

    public void setMember(V2Member member) {
        this.member = member;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSpider() {
        return isSpider;
    }

    public void setSpider(Boolean spider) {
        isSpider = spider;
    }

    public List<V2Comment> getComments() {
        return comments;
    }

    public void setComments(List<V2Comment> comments) {
        this.comments = comments;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "V2Post{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", content='" + content + '\'' +
                ", created=" + created +
                ", lastModified=" + lastModified +
                ", lastTouched=" + lastTouched +
                ", nodeId='" + nodeId + '\'' +
                ", isSpider=" + isSpider +
                ", memberId='" + memberId + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", node=" + node +
                ", member=" + member +
                ", comments=" + comments +
                ", commentCount=" + commentCount +
                '}';
    }
}
