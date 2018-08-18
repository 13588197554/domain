package com.fly.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fly.util.Util;

import javax.persistence.*;

@Entity
@Table(name = "douban_movie_genre")
@JsonIgnoreProperties({"createTime", "updateTime"})
public class DoubanMovieGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "movie_id")
    @JsonPropertyOrder("movie_id")
    private String movieId;
    @Column(name = "genre_id")
    @JsonProperty("genre_id")
    private Integer genreId;
    @Column(name = "create_time")
    private String createTime = Util.getCurrentFormatTime();
    @Column(name = "update_time")
    private String updateTime = Util.getCurrentFormatTime();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getCreateTime() {
        if (this.createTime == null) {
            this.createTime = Util.getCurrentFormatTime();
        }
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        if (this.updateTime == null) {
            this.updateTime = Util.getCurrentFormatTime();
        }
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DoubanMovieGenre{" +
                "id=" + id +
                ", movieId='" + movieId + '\'' +
                ", genreId=" + genreId +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
