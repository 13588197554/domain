package com.fly.dao;

import com.fly.pojo.DoubanMovieComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieCommentDao extends JpaRepository<DoubanMovieComment, Integer> {

}
