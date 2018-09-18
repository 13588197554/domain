package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "douban_movie_comment")
@JsonIgnoreProperties({"updateAt"})
public class DoubanMovieComment {

    @Id
    private Integer id;
    private Float stars;
    @Column(name = "useful_count")
    @JsonProperty("useful_count")
    private Integer usefulCount;
    @Column(name = "subject_id", length = 20)
    @JsonProperty("subject_id")
    private String subjectId;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(name = "created_at")
    @JsonProperty("created_at")
    private String createdAt;
    @Column(name = "update_at")
    private String updateAt;

    @Transient
    private DoubanUser author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getStars() {
        return stars;
    }

    public void setStars(Float stars) {
        this.stars = stars;
    }

    public Integer getUsefulCount() {
        return usefulCount;
    }

    public void setUsefulCount(Integer usefulCount) {
        this.usefulCount = usefulCount;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public DoubanUser getAuthor() {
        return author;
    }

    public void setAuthor(DoubanUser author) {
        this.author = author;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "DoubanMovieComment{" +
                "id=" + id +
                ", stars=" + stars +
                ", usefulCount=" + usefulCount +
                ", subjectId='" + subjectId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", author=" + author +
                '}';
    }
}
