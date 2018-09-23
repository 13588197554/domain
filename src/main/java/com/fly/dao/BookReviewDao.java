package com.fly.dao;

import com.fly.pojo.DoubanBookReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewDao extends JpaRepository<DoubanBookReview, Integer> {
}
