package com.fly.job;

import com.fly.spider.DoubanEventSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieJob implements CommandLineRunner {

    @Autowired
    private DoubanEventSpider des;

    @Override
    public void run(String... args) throws InterruptedException {
        des.eventSpider();
    }
}
