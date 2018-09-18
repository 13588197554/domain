package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.GenreDao;
import com.fly.dao.MovieDao;
import com.fly.dao.MovieGenreDao;
import com.fly.enums.StatusEnum;
import com.fly.pojo.DoubanGenre;
import com.fly.pojo.DoubanMovie;
import com.fly.pojo.DoubanMovieGenre;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sun.nio.ch.IOUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class MovieInfoSpider {

    @Autowired
    private RedisUtil jedis;
    @Autowired
    private MovieDao md;
    @Autowired
    private MovieGenreDao mgd;
    @Autowired
    private GenreDao gd;

    private static String MOVIE_KEY = "MOVIE_JOB";
    private static String MOVIE_URL_PREFIX = "https://api.douban.com/v2/movie/subject/";
    private static String APIKEY = "0b2bdeda43b5688921839c8ecb20399b";

    public void movieInfoSpider() {
        if (!jedis.exists(MOVIE_KEY)) {
            initMovieJob2Redis();
        }

        while (jedis.exists(MOVIE_KEY)) {
            String id = jedis.lpop(MOVIE_KEY);
            this.movieSpider(id);
        }
    }

    @Transactional
    protected void movieSpider(String id) {
        String url = MOVIE_URL_PREFIX + id + "?apikey=" + APIKEY;
        System.out.println("process url : " + url + ", process time: " + Util.getCurrentFormatTime());
        Connection.Response res = null;
        try {
            res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)")
                    .execute();
            String body = res.body();
            Optional<DoubanMovie> op = md.findById(id);
            if (!op.isPresent()) {
                return;
            }

            // movie info
            DoubanMovie movie = op.get();
            JSONObject jo = JSON.parseObject(body);
            Integer reviewsCount = Arr.get(jo, "reviews_count", Integer.class);
            Integer wishCount = Arr.get(jo, "wishCount", Integer.class);
            Integer doCount = Arr.get(jo, "do_count", Integer.class);
            String summary = Arr.get(jo, "summary");
            Integer seasonCount = Arr.get(jo, "season_count", Integer.class);
            Integer episodesCount = Arr.get(jo, "episodes_count", Integer.class);
            Integer collectCount = Arr.get(jo, "collect_count", Integer.class);
            Integer commentsCount = Arr.get(jo, "comments_count", Integer.class);
            Integer ratingsCount = Arr.get(jo, "ratings_count", Integer.class);

            movie.setReviewsCount(reviewsCount);
            movie.setWishCount(wishCount);
            movie.setDoCount(doCount);
            movie.setSummary(summary);
            movie.setSeasonCount(seasonCount);
            movie.setEpisodesCount(episodesCount);
            movie.setCollectCount(collectCount);
            movie.setCommentsCount(commentsCount);
            movie.setRatingsCount(ratingsCount);

            // countries
            String countries = Arr.get(jo, "countries");
            movie.setCountries(countries);

            // genres
            String genresJson = Arr.get(jo, "genres");
            List<String> names = JSON.parseArray(genresJson, String.class);
            for (String name : names) {
                DoubanGenre ge = gd.findByName(name);
                if (ge == null) {
                    ge = new DoubanGenre();
                    ge.setName(name);
                    ge.setCreateTime(Util.getCurrentFormatTime());
                    ge.setUpdateTime(Util.getCurrentFormatTime());
                    ge.setStatus(StatusEnum.ACTIVE.getName());
                    ge = gd.save(ge);
                }

                DoubanMovieGenre movieGenre = mgd.findByMovieIdAndGenreId(id, ge.getId());
                if (movieGenre == null) {
                    movieGenre = new DoubanMovieGenre();
                    movieGenre.setGenreId(ge.getId());
                    movieGenre.setMovieId(id);
                    movieGenre.setCreateTime(Util.getCurrentFormatTime());
                    movieGenre.setUpdateTime(Util.getCurrentFormatTime());
                    mgd.save(movieGenre);
                }
            }

            // aka
            String akaJson = Arr.get(jo, "aka");
            movie.setAka(akaJson);
            movie.setExtra(jo.toJSONString());

            md.save(movie);
        } catch (HttpStatusException hse) {
            hse.printStackTrace();
            LogUtil.info(MovieSpider.class, "movieInfoSpider", hse);

            if (400 == hse.getStatusCode()) {
                Util.getRandomSleep(5 * 60);
            }

            if (403 == hse.getStatusCode()) {
                Util.getRandomSleep(3 * 3600);
            }

            if (404 == hse.getStatusCode()) {
            }

            return;
        } catch (IOException e) {
            LogUtil.info(MovieSpider.class, "movieInfoSpider", e);
            jedis.rpush(MOVIE_KEY, id);
            e.printStackTrace();
        } catch (Exception e) {
            jedis.rpush(MOVIE_KEY, id);
            e.printStackTrace();;
        } finally {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initMovieJob2Redis() {
        List<String> all = md.findAllIds();
        jedis.rpush(MOVIE_KEY, all);
    }

}
