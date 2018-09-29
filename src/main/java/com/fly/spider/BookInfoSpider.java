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

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.fly.config.Constants.BOOK_INFO_KEY;
import static com.fly.config.Constants.EXP_BOOK_INFO_KEY;

/**
 * spider :
 *  1 代表原始未更新状态,
 *  2 代表更新完成状态,
 * -1 代表异常状态
 */
@Component
public class BookInfoSpider {

    @Autowired
    private BookDao bd;
    @Autowired
    private TagDao td;
    @Autowired
    private TagObjectDao tod;
    @Autowired
    private ImageDao imageDao;
    @Autowired
    private RedisUtil jedis;

    private static String baseUrl = "https://api.douban.com/v2/book/";

    public void start() {
        initRedis();

        while (jedis.exists(BOOK_INFO_KEY)) {
            String id = jedis.lpop(BOOK_INFO_KEY);
            Optional<Book> op = bd.findById(id);
            if (!op.isPresent()) {
                continue;
            }

            Book book = op.get();
            System.out.println("process book : " + book.getId() + " process time : " + Util.getCurrentFormatTime());
            try {
                this.bookInfoSpider(book);
            } catch (HttpStatusException hse) {
                LogUtil.info(BookInfoSpider.class, "bookInfoSpider", hse);
                if (hse.getStatusCode() == 403) {
                    Util.getRandomSleep(3 * 3600);

                    continue;
                }

                if (hse.getStatusCode() == 404) {
                    Util.getRandomSleep(30, 45);
                    continue;
                }

                if (hse.getStatusCode() == 400) {
                    Util.getRandomSleep(15 * 60);
                    continue;
                }

            } catch (Exception e) {
                LogUtil.info(BookInfoSpider.class, "bookInfoSpider", e);
                jedis.rpush(EXP_BOOK_INFO_KEY, id);
                book.setSpider(-1);
                bd.save(book);
                continue;
            }
        }
    }

    @Transactional
    public void bookInfoSpider(Book book) throws InterruptedException, IOException {
        String id = book.getId();
        String url = baseUrl + id + "?apikey=0b2bdeda43b5688921839c8ecb20399b";
        Connection.Response res = Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("ia_archiver/8.2 (Windows 7.6; en-US;)")
                .execute();
        String body = res.body();
        JSONObject jo = JSON.parseObject(body);
        String catalog = Arr.get(jo, "catalog", "", String.class);
        String binding = Arr.get(jo, "binding", "", String.class);
        String intro = Arr.get(jo, "summary", "", String.class);
        String originTitle = Arr.get(jo, "origin_title", "", String.class);
        book.setCategory(catalog);
        book.setBinding(binding);
        book.setIntro(intro);
        book.setOriginWorkName(originTitle);
        book.setExtra(jo.toJSONString());

        List<DoubanImage> imageModel = imageDao.findByFk(id);
        if (imageModel.size() == 0) {
            String imageJson = Arr.get(jo, "images");
            DoubanImage image = JSON.parseObject(imageJson, DoubanImage.class);
            image.setFk(id);
            imageDao.save(image);
        }

        String tagJson = Arr.get(jo, "tags");
        List<JSONObject> tagArr = JSON.parseArray(tagJson, JSONObject.class);
        for (JSONObject tj : tagArr) {
            String tagName = Arr.get(tj, "name", String.class);
            List<String> tagIds = td.findIdByNameAndType(tagName, "DOUBAN_BOOK");
            if (tagIds.size() > 0) {
                String tagId = tagIds.get(0);
                List<TagObject> tos = tod.findByFkAndTagId(id, tagId);
                if (tos.size() > 0) {
                    continue;
                }

                TagObject to = new TagObject();
                to.setTagId(tagId);
                to.setFk(id);
                to.setExtra(to.toString());
                to.setStatus(StatusEnum.ACTIVE.getName());
                to.setCreateTime(Util.getCurrentFormatTime());
                to.setUpdateTime(Util.getCurrentFormatTime());
                tod.save(to);
            }
        }

        book.setSpider(2);
        bd.save(book);
    }

    private void initRedis() {
        if (!jedis.exists(BOOK_INFO_KEY)) {
            List<String> bookIds = bd.findIds();
            jedis.rpush(BOOK_INFO_KEY, bookIds);
        }
    }

}
