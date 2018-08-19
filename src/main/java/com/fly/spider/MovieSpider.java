package com.fly.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fly.config.SystemConfig;
import com.fly.dao.*;
import com.fly.enums.StatusEnum;
import com.fly.enums.TypeEnum;
import com.fly.pojo.*;
import com.fly.util.Arr;
import com.fly.util.LogUtil;
import com.fly.util.Util;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class MovieSpider {

    private static Properties prop = SystemConfig.global;

    private static String baseUrl = Arr.getString(prop, "MOVIE_URL");
    private static String MOVIE_TAG = Arr.getString(prop, "MOVIE_TAG");
    private static Integer PAGE = Arr.getInteger(prop, "MOVIE_PAGE");
    private static Integer COUNT = 20;
    private static String TAG_ID = null;
    private static String TAG_TYPE = "DOUBAN_MOVIE";

    @Autowired
    private MovieDao md;
    @Autowired
    private GenreDao gd;
    @Autowired
    private ImageDao id;
    @Autowired
    private ParticipantDao ptd;
    @Autowired
    private MoviePersonDao mpd;
    @Autowired
    private MovieGenreDao mgd;
    @Autowired
    private TagDao td;
    @Autowired
    private TagObjectDao tod;

    public void start() throws InterruptedException {
        TAG_ID = td.findIdByNameAndType(MOVIE_TAG, TAG_TYPE);

        while (true) {
            String url = baseUrl + "?tag=" + MOVIE_TAG + "&start=" + PAGE * COUNT;
            System.out.println("processing url: " + url + ", -- process time: " + Util.getCurrentFormatTime());
            try {
                Connection.Response res = Jsoup.connect(url)
                        .ignoreContentType(true)
                        .execute();
                String body = res.body();
                List<JSONObject> jsonArray = getJsonArray(body);
                if (jsonArray.size() == 0) {
                    System.out.println("job has finished!");
                    break;
                }

                for (JSONObject jo : jsonArray) {
                    try {
                        movieSpider(jo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(jo);
                        LogUtil.info(MovieSpider.class, "start", e);
                    }
                }
                PAGE++;
                System.out.println("normally sleep: " + Util.getCurrentFormatTime());
                Util.getRandomSleep(26, 35);
            } catch (HttpStatusException hse) {
                hse.printStackTrace();
                LogUtil.info(MovieSpider.class, "movieSpider", hse);

                if (400 == hse.getStatusCode()) {
                    Util.getRandomSleep(15 * 60);
                    return;
                }

                if (403 == hse.getStatusCode()) {
                    Util.getRandomSleep(5 * 3600);
                }

                if (404 == hse.getStatusCode()) {
                    return;
                }
            } catch (IOException e) {
                LogUtil.info(MovieSpider.class, "movieSpider", e);
                e.printStackTrace();
            }
        }
    }

    @Transactional
    protected void movieSpider(JSONObject jo) {
        String id = Arr.get(jo, "id");
        Optional<DoubanMovie> op = md.findById(id);
        if (op.isPresent()) {
            DoubanMovie movie = op.get();
            TagObject to = tod.findByFkAndTagId(movie.getId(), TAG_ID);
            if (to == null) {
                this.saveTags(movie);
            }
            return;
        }
        DoubanMovie movie = new DoubanMovie();
        this.movieUnit(jo, movie);
        this.participantUnit(jo, "casts", movie);
        this.participantUnit(jo, "directors", movie);
        this.participantUnit(jo, "writers", movie);
        this.saveGenres(jo, movie);
        this.saveTags(movie);

        System.out.println("process movie : " + movie.getTitle() + " -- process time: " + Util.getCurrentFormatTime());
    }

    private void participantUnit(JSONObject jo, String column, DoubanMovie movie) {
        DoubanParticipant participant = new DoubanParticipant();
        String columnJson = Arr.get(jo, column);

        if (columnJson == null || "".equalsIgnoreCase(columnJson.trim())) {
            return;
        }

        List<DoubanParticipant> casts = JSON.parseArray(columnJson, DoubanParticipant.class);
        for (DoubanParticipant e : casts) {
            if (e.getId() == null || "null".equalsIgnoreCase(e.getId())) {
                e.setId(Util.getUUid());
            }

            List<DoubanParticipant> ps = ptd.findByName(e.getName());
            if (ps.size() == 0) {
                switch (column.toUpperCase()) {
                    case "CASTS":
                        e.setType(TypeEnum.CAST.getName());
                        break;
                    case "DIRECTORS":
                        e.setType(TypeEnum.DIRECTOR.getName());
                        break;
                    case "WRITERS":
                        e.setType(TypeEnum.WRITER.getName());
                        break;
                }
                DoubanImage image = new DoubanImage();
                String avatarJson = e.getAvatars();
                image = JSONObject.parseObject(avatarJson, DoubanImage.class);
                if (image != null) {
                    image.setFk(e.getId());
                    image.setCreateTime(Util.getCurrentFormatTime());
                    image.setUpdateTime(Util.getCurrentFormatTime());
                    image.setStatus(StatusEnum.ACTIVE.getName());
                    id.save(image);
                }

                ptd.save(e);
            }

            DoubanMoviePerson moviePerson = new DoubanMoviePerson();
            moviePerson.setMovieId(movie.getId());
            moviePerson.setPersonId(e.getId());
            moviePerson.setCreateTime(Util.getCurrentFormatTime());
            moviePerson.setUpdateTime(Util.getCurrentFormatTime());
            mpd.save(moviePerson);
        }
    }

    private void movieUnit(JSONObject jo, DoubanMovie movie) {
        String rateJson = Arr.get(jo, "rating");
        JSONObject rateJsonOb = JSON.parseObject(rateJson);
        String stars = Arr.get(rateJsonOb, "stars");
        Float average = Float.valueOf(Arr.get(rateJsonOb, "average"));
        movie.setId(Arr.get(jo, "id"));
        movie.setAverage(average);
        movie.setStars(stars);
        movie.setTitle(Arr.get(jo, "title"));
        movie.setSummary(Arr.get(jo, "summary"));
        movie.setReviewsCount(Integer.valueOf(Arr.get(jo, "reviews_count", "0")));
        movie.setWishCount(Integer.valueOf(Arr.get(jo, "wish_count", "0")));
        movie.setEpisodesCount(Integer.valueOf(Arr.get(jo, "episodes_count", "0")));
        movie.setMainlandPubdate(Arr.get(jo, "mainland_pubdate"));
        movie.setCollectCount(Integer.valueOf(Arr.get(jo, "collect_count", "0")));
        movie.setOriginalTitle(Arr.get(jo, "original_title"));
        movie.setSubtype(Arr.get(jo, "subtype"));
        movie.setYear(Integer.valueOf(Arr.get(jo, "year", "0")));
        movie.setAlt(Arr.get(jo, "alt"));
        movie.setAka(Arr.get(jo, "aka", ""));
        movie.setCountries(Arr.get(jo, "countries", ""));
        md.save(movie);
    }

    private void saveGenres(JSONObject jo, DoubanMovie movie) {
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

            DoubanMovieGenre movieGenre = new DoubanMovieGenre();
            movieGenre.setGenreId(ge.getId());
            movieGenre.setMovieId(movie.getId());
            movieGenre.setCreateTime(Util.getCurrentFormatTime());
            movieGenre.setUpdateTime(Util.getCurrentFormatTime());
            mgd.save(movieGenre);
        }
    }

    private void saveTags(DoubanMovie movie) {
        TagObject tagObject = new TagObject();
        tagObject.setExtra("");
        tagObject.setStatus(StatusEnum.ACTIVE.getName());
        tagObject.setCreateTime(Util.getCurrentFormatTime());
        tagObject.setUpdateTime(Util.getCurrentFormatTime());
        tagObject.setFk(movie.getId());
        tagObject.setTagId(TAG_ID);
        tod.save(tagObject);
    }

    private List<JSONObject> getJsonArray(String body) {
        JSONObject jsonObject = JSON.parseObject(body);
        String subJson = Arr.get(jsonObject, "subjects");
        List<JSONObject> array = JSON.parseArray(subJson, JSONObject.class);
        return array;
    }

}
