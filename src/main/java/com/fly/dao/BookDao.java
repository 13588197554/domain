package com.fly.dao;

import com.fly.pojo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author david
 * @date 01/08/18 13:53
 */
public interface BookDao extends JpaRepository<Book, String> {

    @Query(value = "select * from douban_book where spider = 0 order by id desc limit 1", nativeQuery = true)
    Book findFirstOrderByIdDesc();

    @Query(nativeQuery = true,
            value = "SELECT * FROM douban_book WHERE spider = 1 ORDER BY id ASC limit 1")
    Book findFirstInfoSpider();

    @Query(nativeQuery = true,
            value = "select * from douban_book where comment_spider = 0 order by id DESC limit 1")
    Book findFirstByCommentSpider();

    @Query("select b.id from Book b")
    List<String> findAllIds();
}
