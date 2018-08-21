package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.dao.BookDao;
import com.fly.dao.ImageDao;
import com.fly.dao.TagDao;
import com.fly.dao.TagObjectDao;
import com.fly.enums.StatusEnum;
import com.fly.pojo.Book;
import com.fly.pojo.DoubanImage;
import com.fly.pojo.DoubanTag;
import com.fly.pojo.TagObject;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.RedisUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author david
 * @date 15/08/18 20:02
 */
@Component
public class BookSpider {

    @Autowired
    private BookDao bd;
    @Autowired
    private TagDao td;
    @Autowired
    private TagObjectDao tod;
    @Autowired
    private ImageDao id;
    @Autowired
    private RedisUtil jedis;

    private static String TAG_MAP = "DOUBAN_TAG_MAP";
    private static Integer startId = 1000224;
//    private static Integer startId = 1200126;
    private static String baseUrl = "https://api.douban.com/v2/book/";

    public void bookSpider() throws InterruptedException {
        while (true) {
            System.out.println("processing id: " + startId);
            String url = baseUrl + startId;
            try {
                Optional<Book> op = bd.findById(String.valueOf(startId));
                if (op.isPresent()) { // already exist
                    continue;
                }

                Connection.Response res = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (compatible; TweetedTimes Bot/1.0;  http://tweetedtimes.com)")
                        .ignoreContentType(true)
                        .execute();
                String body = res.body();
                if (body.contains("book_not_found")) {
                    continue;
                }

                Book book = new Book();
                JSONObject jo = JSON.parseObject(body);
                this.handleBookInfo(book, jo);
                String tagsJson = Arr.get(jo, "tags");
                List<DoubanTag> tags = JSON.parseArray(tagsJson, DoubanTag.class);
                for (DoubanTag tag : tags) {
                    TagObject to = new TagObject();
                    String tagName = tag.getName();
                    String tagId = jedis.hget(TAG_MAP, tagName);

                    if (tagId != null) {
                        to.setFk(book.getId());
                        to.setTagId(tagId);
                        to.setExtra(tag.toString());
                        to.setCreateTime(Util.getCurrentFormatTime());
                        to.setUpdateTime(Util.getCurrentFormatTime());
                        to.setStatus("ACTIVE");
                        tod.save(to);
                    }
                }

            } catch (HttpStatusException hse) {
                hse.printStackTrace();
                LogUtil.info(Book.class, "bookSpider", hse);
                if (400 == hse.getStatusCode()) {
                    Util.getRandomSleep(15 * 60);
                    continue;
                }

                if (403 == hse.getStatusCode()) {
                    Util.getRandomSleep(5 * 3600);
                    continue;
                }

                if (404 == hse.getStatusCode()) {
                    continue;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                LogUtil.info(BookSpider.class, "bookSpider", e);
                e.printStackTrace();
            } finally {
                startId++;
                System.out.println("normally sleeping: " + Util.getCurrentFormatTime());
                Util.getRandomSleep(25, 45);
            }
        }
    }

    private void handleBookInfo(Book book, JSONObject jo) {
        book.setName(Arr.get(jo, "title"));
        book.setOriginWorkName(Arr.get(jo, "orign_title"));
        book.setIntro(Arr.get(jo, "summary", ""));
        book.setImageUrl(Arr.get(jo, "image"));
        book.setAuthor(Arr.get(jo, "author", ""));
        book.setCategory(Arr.get(jo, "category", ""));
        book.setTranslator(Arr.get(jo, "translator", ""));
        book.setBinding(Arr.get(jo, "bing", ""));
        book.setPageCount(Arr.get(jo, "pages", "-1"));
        book.setPrice(Arr.get(jo, "price", ""));
        book.setPublisher(Arr.get(jo, "publisher"));
        book.setId(Arr.get(jo, "id"));
        book.setPublishTime(Arr.get(jo, "pubdate"));

        String rating = Arr.get(jo, "rating");
        JSONObject rateJson = JSON.parseObject(rating);
        String average = Arr.get(rateJson, "average", "");
        book.setStars(average);
        book.setStatus(StatusEnum.ACTIVE.getName());
        book.setCreateTime(Util.getCurrentFormatTime());
        book.setUpdateTime(Util.getCurrentFormatTime());
        book.setSpider(1);
        book.setExtra(jo.toString());
        bd.save(book);

        String imageJson = Arr.get(jo, "images");
        JSONObject imageJsonO = JSON.parseObject(imageJson);
        DoubanImage image = new DoubanImage();
        image.setFk(book.getId());
        image.setCreateTime(Util.getCurrentFormatTime());
        image.setUpdateTime(Util.getCurrentFormatTime());
        image.setLarge(Arr.get(imageJsonO, "large"));
        image.setMedium(Arr.get(imageJsonO, "medium"));
        image.setSmall(Arr.get(imageJsonO, "small"));
        id.save(image);
    }

}
