package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "douban_book_short_comment")
public class BookShortComment implements Serializable {

    @Id
    private String id;
    private String type;
    private String content;
    @Column(name = "book_id")
    private String bookId;
    private String stars;
    private Integer votes;
    @Column(name = "creator_name")
    private String creatorName;
    @Column(name = "creator_href")
    private String creatorHref;
    @Column(name = "helpful_count")
    private Integer helpfulCount;
    private String status;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private String updateTime;

    @Transient
    private DoubanUser author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatorHref() {
        return creatorHref;
    }

    public void setCreatorHref(String creatorHref) {
        this.creatorHref = creatorHref;
    }

    public Integer getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(Integer helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public DoubanUser getAuthor() {
        return author;
    }

    public void setAuthor(DoubanUser author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "BookShortComment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", bookId='" + bookId + '\'' +
                ", stars='" + stars + '\'' +
                ", creatorName='" + creatorName + '\'' +
                ", creatorHref='" + creatorHref + '\'' +
                ", helpfulCount=" + helpfulCount +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
