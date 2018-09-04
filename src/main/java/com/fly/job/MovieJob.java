package com.fly.job;

import com.fly.spider.MusicSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MovieJob implements CommandLineRunner {

    @Autowired
    private MusicSpider ms;

    @Override
    public void run(String... args) throws InterruptedException {
        ms.musicSpider();
    }
}
