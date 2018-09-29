package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.BookDao;
import com.fly.dao.BookReviewDao;
import com.fly.dao.DoubanUserDao;
import com.fly.pojo.Book;
import com.fly.pojo.DoubanBookReview;
import com.fly.pojo.DoubanUser;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.fly.config.Constants.*;

@Component
@Slf4j
public class BookReviewSpider {

    @Autowired
    private DoubanUserDao ud;
    @Autowired
    private BookReviewDao brd;
    @Autowired
    private BookDao bd;
    @Autowired
    private RedisUtil jedis;

    public void start() {
        initRedis();

        while (jedis.exists(BOOK_KEY)) {
            String id = jedis.lpop(BOOK_KEY);
            int start= 0;
            int count = 100;
            while (true) {
                String url = BASE_BOOK_URL + id + "/reviews?apikey=" + API_KEY + "&count=100&start=" + start;
                log.info("process url : " + url + ", process time: " + Util.getCurrentFormatTime());
                try {
                    Connection.Response res = Jsoup.connect(url)
                            .userAgent("boitho.com-robot/1.0")
                            .ignoreContentType(true)
                            .execute();
                    String body = res.body();
                    JSONObject jo = JSON.parseObject(body);
                    List<JSONObject> joArr = this.getJsonArray(jo);
                    if (joArr.size() == 0) {
                        Integer total = Arr.get(jo, "total", Integer.class);
                        Optional<Book> op = bd.findById(id);
                        if (op.isPresent()) {
                            Book book = op.get();
                            book.setReviewsCount(total);
                            bd.save(book);
                        }
                        log.info(id + "'s reviews has finished!");
                        break;
                    }

                    for (JSONObject joItem : joArr) {
                        spider(joItem, id);
                    }
                    start += count;
                }catch (HttpStatusException hse) {
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
                    jedis.rpush(BOOK_EXP_KEY, id);
                }
            }
        }
    }

    @Transactional
    public void spider(JSONObject jo, String bookId) {
        Integer votes = Arr.get(jo, "votes", Integer.class);
        String title = Arr.get(jo, "title");
        String updated = Arr.get(jo, "updated");
        String published = Arr.get(jo, "published");
        String alt = Arr.get(jo, "alt");
        String id = Arr.get(jo, "id");
        Integer comments = Arr.get(jo, "comments", Integer.class);
        String summary = Arr.get(jo, "summary");
        Integer useless = Arr.get(jo, "useless", Integer.class);

        DoubanBookReview review = new DoubanBookReview();
        review.setVotes(votes);
        review.setTitle(title);
        review.setUpdated(updated);
        review.setPublished(published);
        review.setAlt(alt);
        review.setId(Integer.valueOf(id));
        review.setComments(comments);
        review.setSummary(summary);
        review.setBookId(bookId);
        review.setUseless(useless);

        String authorJson = Arr.get(jo, "author");
        userUnit(authorJson);

        brd.save(review);
    }

    public void userUnit(String authorJson) {
        JSONObject authorJo = JSON.parseObject(authorJson);
        String id = Arr.get(authorJo, "id");
        String name = Arr.get(authorJo, "name");
        String isBanned = Arr.get(authorJo, "is_banned");
        String isSuicide = Arr.get(authorJo, "is_suicide");
        String avatar = Arr.get(authorJo, "avatar");
        String uid = Arr.get(authorJo, "uid");
        String alt = Arr.get(authorJo, "alt");
        String type = Arr.get(authorJo, "type");
        String largeAvatar = Arr.get(authorJo, "large_avatar");

        Optional<DoubanUser> op = ud.findById(Integer.valueOf(id));
        DoubanUser author = null;
        if (op.isPresent()) {
            author = op.get();
        } else {
            author = new DoubanUser();
            author.setId(Integer.valueOf(id));
        }
        author.setName(name);
        author.setBanned(Boolean.valueOf(isBanned));
        author.setSuicide(Boolean.valueOf(isSuicide));
        author.setAlt(alt);
        author.setAvatar(avatar);
        author.setUid(uid);
        author.setType(type);
        author.setLargeAvatar(largeAvatar);

        ud.save(author);
    }

    private List<JSONObject> getJsonArray(JSONObject jsonObject) {
        String subJson = Arr.get(jsonObject, "reviews");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    private void initRedis() {
        if (!jedis.exists(BOOK_KEY)) {
            List<String> bookIds = bd.findAllIds();
            jedis.rpush(BOOK_KEY, bookIds);
        }
    }

}
