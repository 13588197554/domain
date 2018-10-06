package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.BookCommentDao;
import com.fly.dao.BookDao;
import com.fly.dao.UserDao;
import com.fly.enums.StatusEnum;
import com.fly.pojo.Book;
import com.fly.pojo.BookShortComment;
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

import java.util.List;
import java.util.Optional;

import static com.fly.config.Constants.*;

/**
 * comment spider :
 *  -1 已经爬取，但是不完整的数据
 *   1 数据已经爬取完整
 *   0 未爬取
 *  -2 出现异常
 * @author david
 * @date 10/09/18 15:35
 */
@Component
@Slf4j
public class BookCommentSpider {

    @Autowired
    private BookDao bd;
    @Autowired
    private BookCommentDao bcd;
    @Autowired
    private UserDao ud;
    @Autowired
    private RedisUtil jedis;

    private String baseUrl = "https://api.douban.com/v2/book/";

    public void commentSpider() {
        initRedis();

        while (jedis.exists(BOOK_COMMENT_KEY)) {
            Integer start = 0;
            Integer count = 100;
            String id = jedis.lpop(BOOK_COMMENT_KEY);
            Optional<Book> op = bd.findById(id);
            if (!op.isPresent()) {
                log.info(id + "'s not exist!");
                continue;
            }

            Book book = op.get();
            while (true) {
                try {
                    String url = baseUrl + book.getId() +
                            "/comments?apikey=" + API_KEY +
                            "&start=" + start +
                            "&count=" + count;
                    Connection conn = Jsoup.connect(url)
                            .ignoreContentType(true)
                            .userAgent("polybot 1.0 (http://cis.poly.edu/polybot/)");
                    Connection.Response res = conn.execute();
                    String body = res.body();
                    List<JSONObject> joArr = this.getJsonArray(body);

                    if (joArr.size() == 0) {
                        log.info("book: " + book.getName() + " comment has finished! ");
                        JSONObject jo = JSON.parseObject(body);
                        String total = Arr.get(jo, "total");
                        book.setCommentsCount(Integer.valueOf(total));
                        book.setCommentSpider(2);
                        bd.save(book);
                        break;
                    }

                    for (JSONObject jo : joArr) {
                        this.handleCommentInfo(book, jo);
                    }
                    start += count;
                } catch (HttpStatusException hse) {
                    hse.printStackTrace();
                    LogUtil.info(MovieSpider.class, "movieSpider", hse);
                    if (400 == hse.getStatusCode()) {
                        Util.getRandomSleep(5 * 60);
                        continue;
                    }

                    if (403 == hse.getStatusCode()) {
                        Util.getRandomSleep(3 * 3600);
                        continue;
                    }

                    if (404 == hse.getStatusCode()) {
                        jedis.rpush(EXP_BOOK_COMMENT_KEY, id);
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    jedis.rpush(EXP_BOOK_COMMENT_KEY, id);
                    break;
                }
            }
        }
    }

    @Transactional
    public Book handleCommentInfo(Book book, JSONObject jo) {
        BookShortComment comment = new BookShortComment();
        comment.setId(String.valueOf((Arr.get(jo, "id"))));
        comment.setVotes(Arr.get(jo, "votes", 0, Integer.class));
        comment.setContent(Arr.get(jo, "summary", "", String.class));
        comment.setCreateTime(Arr.get(jo, "published", String.class));
        comment.setBookId(book.getId());
        comment.setStatus(StatusEnum.ACTIVE.getName());
        comment.setUpdateTime(Util.getCurrentFormatTime());

        String rateJson = Arr.get(jo, "rating");
        JSONObject rateJo = JSON.parseObject(rateJson);
        String stars = "";
        if (rateJo != null) {
             stars = Arr.get(rateJo, "value", String.class);
        }

        comment.setStars(stars);

        String userJson = Arr.get(jo, "author");
        DoubanUser author = JSON.parseObject(userJson, DoubanUser.class);

        comment.setCreatorName(author.getName());
        comment.setCreatorHref(author.getAlt());

        ud.save(author);
        bcd.save(comment);
        return book;
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "comments");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    private void initRedis() {
        if (!jedis.exists(BOOK_COMMENT_KEY)) {
            List<String> ids = bd.findIdsByCondition();
            jedis.rpush(BOOK_COMMENT_KEY, ids);
        }
    }

}
