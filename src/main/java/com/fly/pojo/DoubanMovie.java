package com.fly.pojo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fly.enums.StatusEnum;
import com.fly.util.Util;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "douban_movie")
@JsonIgnoreProperties({"createTime", "updateTime", "status"})
public class DoubanMovie {

    @Id
    private String id;
    private Float average;
    private String stars;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String summary; // 电影简介
    @Column(name = "reviews_count")
    @JsonProperty("reviews_count")
    private Integer reviewsCount;
    @Column(name = "wish_count")
    @JsonProperty("wish_count")
    private Integer wishCount; // 想看人数
    @Column(name = "do_count")
    @JsonProperty("do_count")
    private Integer doCount;
    @Column(name = "episodes_count")
    @JsonProperty("episodes_count")
    private Integer episodesCount;
    @Column(name = "comments_count")
    @JsonProperty("comments_count")
    private Integer commentsCount;
    @Column(name = "mainland_pubdate")
    @JsonProperty("mainland_pubdate")
    private String mainlandPubdate; // 大陆上映日期
    @Column(columnDefinition = "TEXT")
    private String pubdate;
    private String type; // 电影类型，按照豆瓣分类
    @Column(name = "collect_count")
    @JsonProperty("collect_count")
    private Integer collectCount; // 收集人数
    @Column(name = "season_count")
    @JsonProperty("season_count")
    private Integer seasonCount;
    @Column(name = "ratings_count")
    @JsonProperty("ratings_count")
    private Integer ratingsCount;
    @Column(name = "original_title")
    @JsonPropertyOrder("original_title")
    private String originalTitle; // 原名
    @Column(columnDefinition = "TEXT")
    private String durations;
    private String subtype; // 条目分类
    private Integer year;
    private String alt; // 电影豆瓣url
    private String aka; // 别名 json
    private String countries; // 制片国家 json
    private String extra;
    private String status = StatusEnum.ACTIVE.getName();
    @Column(name = "create_time")
    private String createTime = Util.getCurrentFormatTime();
    @Column(name = "update_time")
    private String updateTime = Util.getCurrentFormatTime();

    @Transient
    private DoubanImage image = new DoubanImage(); // 图片
    @Transient
    private List<DoubanGenre> genres = new ArrayList<>(); // 体裁
    @Transient
    private List<DoubanParticipant> casts = new ArrayList<>(); // 演员阵容
    @Transient
    private List<DoubanParticipant> directors = new ArrayList<>(); // 导演

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
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

    public DoubanImage getImage() {
        return image;
    }

    public void setImage(DoubanImage image) {
        this.image = image;
    }

    public List<DoubanGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<DoubanGenre> genres) {
        this.genres = genres;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DoubanParticipant> getCasts() {
        return casts;
    }

    public void setCasts(List<DoubanParticipant> casts) {
        this.casts = casts;
    }

    public List<DoubanParticipant> getDirectors() {
        return directors;
    }

    public void setDirectors(List<DoubanParticipant> directors) {
        this.directors = directors;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public Integer getWishCount() {
        return wishCount;
    }

    public void setWishCount(Integer wishCount) {
        this.wishCount = wishCount;
    }

    public String getMainlandPubdate() {
        return mainlandPubdate;
    }

    public void setMainlandPubdate(String mainlandPubdate) {
        this.mainlandPubdate = mainlandPubdate;
    }

    public Integer getEpisodesCount() {
        return episodesCount;
    }

    public void setEpisodesCount(Integer episodesCount) {
        this.episodesCount = episodesCount;
    }

    public List<String> getAka() {
        return JSON.parseArray(this.aka, String.class);
    }

    public void setAka(String aka) {
        this.aka = aka;
    }

    public List<String> getCountries() {
        return JSON.parseArray(this.countries, String.class);
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public Integer getDoCount() {
        return doCount;
    }

    public void setDoCount(Integer doCount) {
        this.doCount = doCount;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(Integer commentsCount) {
        this.commentsCount = commentsCount;
    }

    public Integer getSeasonCount() {
        return seasonCount;
    }

    public void setSeasonCount(Integer seasonCount) {
        this.seasonCount = seasonCount;
    }

    public Integer getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(Integer ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getDurations() {
        return durations;
    }

    public void setDurations(String durations) {
        this.durations = durations;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    @Override
    public String toString() {
        return "DoubanMovie{" +
                "id='" + id + '\'' +
                ", average=" + average +
                ", stars='" + stars + '\'' +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", reviewsCount=" + reviewsCount +
                ", wishCount=" + wishCount +
                ", doCount=" + doCount +
                ", episodesCount=" + episodesCount +
                ", commentsCount=" + commentsCount +
                ", mainlandPubdate='" + mainlandPubdate + '\'' +
                ", type='" + type + '\'' +
                ", collectCount=" + collectCount +
                ", seasonCount=" + seasonCount +
                ", ratingsCount=" + ratingsCount +
                ", originalTitle='" + originalTitle + '\'' +
                ", subtype='" + subtype + '\'' +
                ", year=" + year +
                ", alt='" + alt + '\'' +
                ", aka='" + aka + '\'' +
                ", countries='" + countries + '\'' +
                ", durations='" + durations + '\'' +
                ", pubdate='" + pubdate + '\'' +
                ", extra='" + extra + '\'' +
                ", status='" + status + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", image=" + image +
                ", genres=" + genres +
                ", casts=" + casts +
                ", directors=" + directors +
                '}';
    }
}
