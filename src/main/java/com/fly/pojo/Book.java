package com.fly.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "douban_book")
public class Book implements Serializable {

    @Id
    private String id;
    private String name;
    @Column(name = "tag_id")
    private String tagId;
    private String author;
    private String publisher;
    @Column(name = "origin_work_name")
    private String originWorkName;
    private String translator;
    @Column(columnDefinition = "TEXT")
    private String category;
    @Column(name = "publish_time")
    private String publishTime;
    @Column(name = "page_count")
    private String pageCount;
    private String binding;
    private String price;
    @Column(name = "image_url")
    private String imageUrl;
    private String stars;
    private String intro;
    @Column(columnDefinition = "TEXT")
    private String extra;
    private String status;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private String updateTime;

    private int spider;
    @Column(name = "comment_spider")
    private int commentSpider = 0;

//    @ManyToOne(fetch = FetchType.LAZY, targetEntity = FlyTag.class)
//    @Transient
//    private FlyTag tag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getOriginWorkName() {
        return originWorkName;
    }

    public void setOriginWorkName(String originWorkName) {
        this.originWorkName = originWorkName;
    }

    public String getTranslator() {
        return translator;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStars() {
        return stars;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public int getSpider() {
        return spider;
    }

    public void setSpider(int spider) {
        this.spider = spider;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCommentSpider() {
        return commentSpider;
    }

    public void setCommentSpider(int commentSpider) {
        this.commentSpider = commentSpider;
    }

    @Override
    public String toString() {
        return "{" +
                "id=\"" + id + "\"" +
                ", name=\"" + name + "\"" +
                ", author=\"" + author + "\"" +
                ", publisher=\"" + publisher + "\"" +
                ", originWorkName=\"" + originWorkName + "\"" +
                ", translator=\"" + translator + "\"" +
                ", publishTime=\"" + publishTime + "\"" +
                ", pageCount=\"" + pageCount + "\"" +
                ", imageUrl=\"" + imageUrl + "\"" +
                ", category=\"" + category + "\"" +
                ", tagId=\"" + tagId + "\"" +
                ", binding=\"" + binding + "\"" +
                ", price=\"" + price + "\"" +
                ", stars=\"" + stars + "\"" +
                ", intro=\"" + intro + "\"" +
                ", extra=\"" + extra + "\"" +
                ", status=\"" + status + "\"" +
                ", createTime=\"" + createTime + "\"" +
                ", updateTime=\"" + updateTime + "\"" +
                '}';
    }
}
