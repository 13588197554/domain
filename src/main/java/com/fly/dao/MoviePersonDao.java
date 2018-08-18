package com.fly.dao;

import com.fly.pojo.DoubanMoviePerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoviePersonDao extends JpaRepository<DoubanMoviePerson, Integer> {
}
