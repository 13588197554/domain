package com.fly.dao;

import com.fly.pojo.DoubanMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieDao extends JpaRepository<DoubanMovie, String> {

    @Query("select id from DoubanMovie")
    List<String> findAllIds();
}
