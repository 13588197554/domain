package com.fly.dao;

import com.fly.pojo.BookShortComment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 * @date 10/09/18 16:15
 */
public interface BookCommentDao extends JpaRepository<BookShortComment, String> {

    Long countByBookId(String id);

}
