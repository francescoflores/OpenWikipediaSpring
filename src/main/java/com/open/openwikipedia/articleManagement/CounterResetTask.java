package com.open.openwikipedia.articleManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.open.openwikipedia.common.model.Article;
import com.open.openwikipedia.common.repository.ArticleRepository;
import java.util.List;

@Component
public class CounterResetTask {

    @Autowired
    private ArticleRepository articleRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void resetCounters() {
        List<Article> articles = articleRepository.findAll();
        articles.forEach(article -> {
            article.setCounter(0L);
            articleRepository.save(article);
        });
    }
}
