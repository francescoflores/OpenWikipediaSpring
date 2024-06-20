package com.open.openwikipedia.common.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.open.openwikipedia.common.model.Article;

public interface ArticleRepository extends MongoRepository<Article, String> {
    @Query("{ 'title': { '$regex': ?0, '$options': 'i' } }")
    List<Article> findByTitleContainingIgnoreCase(String title);
    Optional<Article> findByTitle(String title);
    List<Article> findTopNByOrderByCounterDesc(int n);
}
