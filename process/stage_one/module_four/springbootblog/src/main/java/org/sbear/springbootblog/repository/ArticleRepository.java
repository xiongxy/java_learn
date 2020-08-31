package org.sbear.springbootblog.repository;

import org.sbear.springbootblog.pojo.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xxyWi
 */
public interface ArticleRepository extends JpaRepository<Article,Integer> {
}
