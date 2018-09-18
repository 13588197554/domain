package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.DoubanUserDao;
import com.fly.dao.MovieCommentDao;
import com.fly.dao.MovieDao;
import com.fly.pojo.DoubanMovie;
import com.fly.pojo.DoubanMovieComment;
import com.fly.pojo.DoubanUser;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author david
 * @date 11/09/18 20:44
 */
@Component
public class MovieCommentSpider {

    private static final Logger logger = LoggerFactory.getLogger(MovieCommentSpider.class);

    @Autowired
    private RedisUtil jedis;
    @Autowired
    private MovieDao md;
    @Autowired
    private MovieCommentDao mcd;
    @Autowired
    private DoubanUserDao ud;

    private static final String BASE_URL = "https://api.douban.com/v2/movie/subject/";
    private static final String API_KEY = "0b2bdeda43b5688921839c8ecb20399b";
    private static final String MOVIE_KEY = "MOVIE_KEY";

    public void start() {
        initRedis();

        while (jedis.exists(MOVIE_KEY)) {
            movieCommentSpider();
        }
    }

    @Transactional
    protected void movieCommentSpider() {
        String id = jedis.lpop(MOVIE_KEY);
        Integer start = 0;
        while (true) {
            String url = BASE_URL + id + "/comments?apikey=" + API_KEY + "&count=100&start=" + start;
            logger.info("processing url : " + url);
            try {
                Optional<DoubanMovie> op = md.findById(id);
                if (!op.isPresent()) {
                    logger.info("该电影条目不存在:" + id);
                    return;
                }

                DoubanMovie movie = op.get();
                Connection.Response res = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .userAgent("boitho.com-robot/1.0")
                        .execute();
                String body = res.body();
                JSONObject jo = JSON.parseObject(body);
                List<JSONObject> joArr = this.getJsonArray(body);

                if (joArr.size() == 0) {
                    logger.info(movie.getTitle() + " comment job has finished!");
                    return;
                }

                String subjectJson = Arr.get(jo, "subject");
                movie = movieUnit(subjectJson, movie);

                for (JSONObject commentJo : joArr) {
                    commentUnit(commentJo);
                }
                start += 100;
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
                e.printStackTrace();
            } catch (Exception e) {
                LogUtil.info(MovieSpider.class, "movieInfoSpider", e);
                jedis.rpush(MOVIE_KEY, url);
                e.printStackTrace();
            }
        }
    }

    private DoubanMovie movieUnit(String json, DoubanMovie movie) {
        JSONObject movieJo = JSON.parseObject(json);
        String durationJson = Arr.get(movieJo, "durations");
        List<String> durations = JSON.parseArray(durationJson, String.class);
        movie.setDurations(durations.toString());

        String pubdateJson = Arr.get(movieJo, "pubdate");
        List<String> pubdates = JSON.parseArray(pubdateJson, String.class);
        String pubdate = "[]";
        if (pubdates != null) {
            pubdate = pubdates.toString();
        }
        movie.setPubdate(pubdate);
        String mainlandPubdate = Arr.get(movieJo, "mainland_pubdate");
        movie.setMainlandPubdate(mainlandPubdate);
        md.save(movie);
        return movie;
    }

    private void commentUnit(JSONObject json) {
        String idStr = Arr.get(json, "id");
        Integer usefulCount = Arr.get(json, "useful_count", Integer.class);
        String ratingJson = Arr.get(json, "rating");
        JSONObject rateJson = JSON.parseObject(ratingJson);
        String stars = Arr.get(rateJson, "value");
        String subjectId = Arr.get(json, "subject_id", String.class);
        String content = Arr.get(json, "content", String.class);
        String createdAt = Arr.get(json, "created_at");

        DoubanMovieComment comment = new DoubanMovieComment();
        comment.setId(Integer.valueOf(idStr));
        comment.setUsefulCount(usefulCount);
        comment.setStars(Float.valueOf(stars));
        comment.setSubjectId(subjectId);
        comment.setContent(content);
        comment.setCreatedAt(createdAt);
        comment.setUpdateAt(Util.getCurrentFormatTime());
        mcd.save(comment);

        String authorJson = Arr.get(json, "author");
        JSONObject authorJo = JSON.parseObject(authorJson);
        String authorId = Arr.get(authorJo, "id");
        Optional<DoubanUser> op = ud.findById(Integer.valueOf(authorId));
        DoubanUser user = op.isPresent() ? op.get() : new DoubanUser();
        String uid = Arr.get(authorJo, "uid", String.class);
        String avatar = Arr.get(authorJo, "avatar", String.class);
        String signature = Arr.get(authorJo, "signature", String.class);
        String name = Arr.get(authorJo, "name", String.class);

        if (user.getId() == null) {
            user.setId(Integer.valueOf(idStr));
        }
        user.setUid(uid);
        user.setAvatar(avatar);
        user.setSignature(signature);
        user.setName(name);
        user.setUpdateTime(Util.getCurrentFormatTime());
        ud.save(user);
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "comments");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    public void initRedis() {
        if (!jedis.exists(MOVIE_KEY)) {
            List<String> movieIds = md.findAllIds();
            jedis.rpush(MOVIE_KEY, movieIds);
        }
    }

}
