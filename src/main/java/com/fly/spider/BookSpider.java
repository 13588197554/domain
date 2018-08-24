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
    private static String BOOK_TAG = "BOOK_TAG";
    private static String baseUrl = "https://api.douban.com/v2/book/search?tag=";
    private static String TAG_ID = null;

    public void bookSpider() throws InterruptedException {
        this.initRedis();

        while (true) {
            if (!jedis.exists(BOOK_TAG)) {
                System.out.println("Job has finished!");
                break;
            }

            String tagName = jedis.lpop(BOOK_TAG);
            TAG_ID = jedis.hget(TAG_MAP, tagName);
            Integer start = 0;
            while (true) {
                String url = baseUrl + tagName + "&start=" + start;
                System.out.println("processing url : " + url + " -- process time: " + Util.getCurrentFormatTime());
                try {
                    Connection.Response res = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (compatible; TweetedTimes Bot/1.0;  http://tweetedtimes.com)")
                            .ignoreContentType(true)
                            .execute();
                    String body = res.body();
                    if (body.contains("book_not_found")) {
                        continue;
                    }

                    List<JSONObject> jsonArray = getJsonArray(body);
                    if (jsonArray.size() == 0) {
                        System.out.println("job has finished");
                        break;
                    }

                    for (JSONObject jo : jsonArray) {
                        Book book = new Book();
                        this.bookUnit(book, jo);
                        this.imageUnit(book, jo);
                        this.tagUnit(book, jo);
                    }
                } catch (HttpStatusException hse) {
                    hse.printStackTrace();
                    LogUtil.info(Book.class, "bookSpider", hse);
                    if (400 == hse.getStatusCode()) {
                        Util.getRandomSleep(5 * 60);
                        continue;
                    }

                    if (403 == hse.getStatusCode()) {
                        Util.getRandomSleep(3 * 3600);
                        continue;
                    }

                    if (404 == hse.getStatusCode()) {
                        continue;
                    }
                } catch (IOException e) {
                    LogUtil.info(BookSpider.class, "bookSpider", e);
                    e.printStackTrace();
                } catch (Exception e) {
                    LogUtil.info(BookSpider.class, "bookSpider", e);
                    e.printStackTrace();
                } finally {
                    start += 20;
                    System.out.println("normally sleeping: " + Util.getCurrentFormatTime());
                    Util.getRandomSleep(25, 45);
                }
            }
        }
    }

    private void imageUnit(Book book, JSONObject jo) {
        DoubanImage image = id.findByFk(book.getId());
        if (image == null) {
            image = new DoubanImage();
            String imageJson = Arr.get(jo, "images");
            JSONObject imageJsonO = JSON.parseObject(imageJson);
            image.setFk(book.getId());
            image.setCreateTime(Util.getCurrentFormatTime());
            image.setUpdateTime(Util.getCurrentFormatTime());
            image.setLarge(Arr.get(imageJsonO, "large"));
            image.setMedium(Arr.get(imageJsonO, "medium"));
            image.setSmall(Arr.get(imageJsonO, "small"));
            id.save(image);
        }
    }

    private void tagUnit(Book book, JSONObject jo) {
        TagObject to = tod.findByFkAndTagId(book.getId(), TAG_ID);
        if (to == null) {
            to = new TagObject();
            to.setFk(book.getId());
            to.setTagId(TAG_ID);
            to.setCreateTime(Util.getCurrentFormatTime());
            to.setUpdateTime(Util.getCurrentFormatTime());
            to.setExtra("");
            to.setStatus("ACTIVE");
            tod.save(to);
        }
    }

    private void bookUnit(Book book, JSONObject jo) {
        String id = Arr.get(jo, "id", String.class);
        Optional<Book> op = bd.findById(id);
        if (op.isPresent()) {
            return;
        }
        book.setId(Arr.get(jo, "id"));
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
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "books");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

    private void initRedis() {
        if (!jedis.exists(BOOK_TAG)) {
            List<String> names = td.findNameByTypeAndPid("DOUBAN_BOOK");
            String[] nameArr = new String[]{};
            nameArr = names.toArray(nameArr);
            jedis.rpush(BOOK_TAG, nameArr);
        }
    }

}
