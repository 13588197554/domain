package com.fly.dao;

import com.fly.pojo.DoubanMovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MovieGenreDao extends JpaRepository<DoubanMovieGenre, Integer> {

    DoubanMovieGenre findByMovieIdAndGenreId(String id, Integer id1);
}
