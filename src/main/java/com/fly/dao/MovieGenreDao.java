package com.fly.dao;

import com.fly.pojo.DoubanMovieGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieGenreDao extends JpaRepository<DoubanMovieGenre, Integer> {
}
