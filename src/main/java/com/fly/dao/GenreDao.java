package com.fly.dao;

import com.fly.pojo.DoubanGenre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreDao extends JpaRepository<DoubanGenre, Integer> {

    DoubanGenre findByName(String name);

}
