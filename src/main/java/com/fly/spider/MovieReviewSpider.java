package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.DoubanUserDao;
import com.fly.dao.MovieDao;
import com.fly.dao.MovieReviewDao;
import com.fly.pojo.DoubanMovieReview;
import com.fly.pojo.DoubanUser;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import lombok.extern.slf4j.Slf4j;
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

import static com.fly.config.Constants.*;

/**
 * @author david
 * @date 22/09/18 13:23
 */
@Component
@Slf4j
public class MovieReviewSpider {

    private final Logger logger = LoggerFactory.getLogger(MovieReviewSpider.class);

    @Autowired
    private RedisUtil jedis;
    @Autowired
    private MovieReviewDao mrd;
    @Autowired
    private DoubanUserDao ud;
    @Autowired
    private MovieDao md;

    public void start() {
        initRedis();

        while (jedis.exists(MOVIE_KEY)) {
            String id = jedis.lpop(MOVIE_KEY);
            int start = 0;
            int count = 100;
            while (true) {
                try {
                    String url = BASE_MOVIE_URL + id + "/reviews?apikey=" + API_KEY + "&count=100&start=" + start;
                    logger.info("processing url : " + url + ", process time: " + Util.getCurrentFormatTime());
                    Connection.Response res = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .userAgent("findlinks/1.1-a7 (+http://wortschatz.uni-leipzig.de/findlinks/)")
                            .execute();
                    String body = res.body();
                    List<JSONObject> joArr = this.getJsonArray(body);
                    if (joArr.size() == 0) {
                        logger.info(id + "'s reviews has finished!");
                        break;
                    }

                    for (JSONObject jo : joArr) {
                        reviewUnit(jo);
                    }

                    start += count;
                } catch (HttpStatusException hse) {
                    hse.printStackTrace();
                    LogUtil.info(MovieSpider.class, "movieInfoSpider", hse);

                    if (400 == hse.getStatusCode()) {
                        Util.getRandomSleep(5 * 60);
                    }

                    if (403 == hse.getStatusCode()) {
                        Util.getRandomSleep(3 * 3600);
                    }

                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    List<String> list = jedis.lrange(MOVIE_EXP_KEY, 0, -1);
                    if (!list.contains(id)) {
                        jedis.rpush(MOVIE_EXP_KEY, id);
                    }
                    break;
                }
            }
        }
    }

    @Transactional
    public void reviewUnit(JSONObject jo) {
        String ratingJson = Arr.get(jo, "rating");
        JSONObject rateJo = JSON.parseObject(ratingJson);
        String stars = Arr.get(rateJo, "value");
        Integer usefulCount = Arr.get(jo, "useful_count", Integer.class);
        String createdAt = Arr.get(jo, "created_at");
        String title = Arr.get(jo, "title");
        String summary = Arr.get(jo, "summary");
        String content = Arr.get(jo, "content");
        Integer uselessCount = Arr.get(jo, "useless_count", Integer.class);
        Integer commentsCount = Arr.get(jo, "comments_count", Integer.class);
        String alt = Arr.get(jo, "alt");
        String id = Arr.get(jo, "id");
        String subjectId = Arr.get(jo, "subject_id");

        DoubanMovieReview review = new DoubanMovieReview();
        review.setStars(Float.valueOf(stars));
        review.setUsefulCount(usefulCount);
        review.setCreatedAt(createdAt);
        review.setUpdatedAt(Util.getCurrentFormatTime());
        review.setTitle(title);
        review.setSummary(summary);
        review.setContent(content);
        review.setUselessCount(uselessCount);
        review.setCommentsCount(commentsCount);
        review.setAlt(alt);
        review.setId(Integer.valueOf(id));
        review.setSubjectId(subjectId);

        String authorJson = Arr.get(jo, "author");
        userUnit(authorJson);

        mrd.save(review);
    }

    private void userUnit(String authorJson) {
        JSONObject authorJo = JSON.parseObject(authorJson);
        String id = Arr.get(authorJo, "id");

        Optional<DoubanUser> op = ud.findById(Integer.valueOf(id));
        String uid = Arr.get(authorJo, "uid");
        String avatar = Arr.get(authorJo, "avatar");
        String signature = Arr.get(authorJo, "signature");
        String alt = Arr.get(authorJo, "alt");
        String name = Arr.get(authorJo, "name");
        DoubanUser author = null;
        if (op.isPresent()) {
            author = op.get();
        } else {
            author = new DoubanUser();
            author.setId(Integer.valueOf(id));
        }

        author.setUid(uid);
        author.setAvatar(avatar);
        author.setSignature(signature);
        author.setAlt(alt);
        author.setName(name);

        ud.save(author);
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "reviews");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    private void initRedis() {
        if (!jedis.exists(MOVIE_KEY)) {
            List<String> movieIds = md.findAllIds();
            jedis.rpush(MOVIE_KEY, movieIds);
        }
    }

}
