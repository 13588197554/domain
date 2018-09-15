package com.fly.spider;

import com.fly.dao.MovieDao;
import com.fly.pojo.DoubanMovie;
import com.fly.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovieInfoSpider {

    @Autowired
    private RedisUtil jedis;
    @Autowired
    private MovieDao md;

    private static String MOVIE_KEY = "MOVIE_JOB";
    private static String MOVIE_URL_PREFIX = "https://api.douban.com/v2/movie/subject/";
    private static String APIKEY = "";

    public void movieInfoSpider() {
        if (!jedis.exists(MOVIE_KEY)) {
            initMovieJob2Redis();
        }

        while (jedis.exists(MOVIE_KEY)) {
            String id = jedis.lpop(MOVIE_KEY);

        }
    }

    private void initMovieJob2Redis() {
        List<String> all = md.findAllIds();
        jedis.rpush(MOVIE_KEY, all);
    }

}
