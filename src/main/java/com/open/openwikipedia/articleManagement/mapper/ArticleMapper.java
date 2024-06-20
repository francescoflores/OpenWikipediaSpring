package com.open.openwikipedia.articleManagement.mapper;

import org.springframework.stereotype.Component;
import com.open.openwikipedia.common.model.Article;
import com.open.openwikipedia.common.model.dto.ArticleResponse;

@Component
public class ArticleMapper {
    public ArticleResponse toArticleResponse(Article article){
        ArticleResponse dto = new ArticleResponse();
        dto.setUrl(article.getUrl());
        dto.setTitle(article.getTitle());
        dto.setParagraphs(article.getParagraphs());
        dto.setInfoTable(article.getInfoTable());
        return dto;
    }
}
