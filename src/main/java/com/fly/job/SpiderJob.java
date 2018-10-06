package com.fly.job;

import com.fly.spider.BookCommentSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SpiderJob implements CommandLineRunner {

    @Autowired
    private BookCommentSpider bcs;

    @Override
    public void run(String... args) {
        bcs.commentSpider();
    }
}
