package com.open.openwikipedia.articleManagement.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.open.openwikipedia.articleManagement.mapper.ArticleMapper;
import com.open.openwikipedia.common.Exceptions.CustomException;
import com.open.openwikipedia.common.model.Article;
import com.open.openwikipedia.common.model.Paragraph;
import com.open.openwikipedia.common.model.Table;
import com.open.openwikipedia.common.model.dto.ArticleDTO;
import com.open.openwikipedia.common.model.dto.ArticleResponse;
import com.open.openwikipedia.common.repository.ArticleRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    ArticleMapper mapper;

    public Article saveArticle(ArticleDTO articleDTO) throws IllegalArgumentException {
        String title = articleDTO.getTitle();
        if (articleRepository.findByTitle(title).isPresent()) {
            throw new IllegalArgumentException("Article with the same title already exists.");
        }
        Article article = new Article(articleDTO.getTitle(), articleDTO.getUrl());
        article = scrapeFromWikipedia(article);
        return articleRepository.save(article);
    }

    private Article scrapeFromWikipedia(Article article) {
        String url = article.getUrl();
        Table infoTable = new Table();
        List<Paragraph> paragraphs = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Element content = doc.getElementById("mw-content-text");
            infoTable = scrapeInfoboxTable(content);
            paragraphs = scrapeParagraphs(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        article.setParagraphs(paragraphs);
        article.setInfoTable(infoTable);
        return article;
    }


    private List<Paragraph> scrapeParagraphs(Element content) {
        List<Paragraph> paragraphs = new ArrayList<>();
        if (content != null) {
            Elements elements = content.select("h2, h3, h4, h5, h6, p, ul, ol, img, table:not(.infobox):not(.vevent)");
            Paragraph currentParagraph = null;
    
            Element firstElement = elements.first();
            if (firstElement != null && firstElement.tagName().equals("p")) {
                currentParagraph = new Paragraph(null, firstElement.text(), new ArrayList<>(), new ArrayList<>(), 2);
                paragraphs.add(currentParagraph);
                elements.remove(0);
            }
    
            for (Element element : elements) {
                if (element.tagName().startsWith("h")) {
                    int level = Integer.parseInt(element.tagName().substring(1));
                    String title = element.text();
                    title = title.replace("[edit]", "").replace("[when?]", "");
                    currentParagraph = new Paragraph(title, "", new ArrayList<>(), new ArrayList<>(), level);
                    paragraphs.add(currentParagraph);
                } else if (element.tagName().equals("p") && currentParagraph != null) {
                    currentParagraph.setText(currentParagraph.getText() + element.text());
                } else if ((element.tagName().equals("ul") || element.tagName().equals("ol"))
                        && currentParagraph != null) {
                    Elements listItems = element.select("li");
                    for (Element listItem : listItems) {
                        currentParagraph.setText(currentParagraph.getText() + listItem.text() + "\n");
                    }
                } else if (element.tagName().equals("img") && currentParagraph != null) {
                    currentParagraph.getImages().add(element.attr("src"));
                } else if (element.tagName().equals("table") && currentParagraph != null) {
                    
                    boolean isNested = isNestedTable(element);
                    if (!isNested) {
                        Table table = parseTable(element);
                        currentParagraph.getTables().add(table);
                    }
                }
            }
        }
        return paragraphs;
    }
    
    private boolean isNestedTable(Element tableElement) {
        Element parentElement = tableElement.parent();
        if (parentElement != null) {
            String parentTagName = parentElement.tagName();
            return parentTagName.equalsIgnoreCase("td");
        }
        return false;
    }
    
    private Table scrapeInfoboxTable(Element content) {
        if (content != null) {
            Element tableElement = content.selectFirst("table.infobox.vevent, table.infobox");
            return parseTable(tableElement);
        }
        return null;
    }

    private Table parseTable(Element tableElement) {
        if (tableElement != null) {
            String caption = null;
            Element captionElement = tableElement.selectFirst("caption");
            if (captionElement != null) {
                caption = captionElement.text();
            }

            Map<String, List<Object>> rows = new LinkedHashMap<>();
            Elements rowElements = tableElement.select("tr");
            for (Element rowElement : rowElements) {
                Elements thElements = rowElement.select("th");
                Elements tdElements = rowElement.select("td");
                if (!thElements.isEmpty() && !tdElements.isEmpty()) {
                    String key = thElements.text().replace(".", "_");
                    List<Object> values = new ArrayList<>();
                    for (Element tdElement : tdElements) {
                        if (tdElement.select("table").isEmpty()) {
                            String cellText = tdElement.text();
                            values.add(cellText);
                            Elements nestedElements = tdElement.select("a, span");
                            for (Element nestedElement : nestedElements) {
                                String nestedText = nestedElement.text();
                                if (!cellText.contains(nestedText)) { // avoid duplicate text
                                    values.add(nestedText);
                                }
                            }
                        } else {
                            Table nestedTable = parseTable(tdElement.selectFirst("table"));
                            values.add(nestedTable);
                        }
                    }
                    rows.put(key, values);
                }
            }
            return new Table(caption, rows);
        }
        return null;
    }

    public List<ArticleResponse> findAllArticles(String titleFilter) {
        List<Article> articles;
        if (titleFilter == null || titleFilter.isEmpty()) {
            articles = articleRepository.findAll();
        } else {
            articles = articleRepository.findByTitleContainingIgnoreCase(titleFilter);
        }
        List<ArticleResponse> response = new ArrayList<>();
        for (Article article : articles) {
            response.add(mapper.toArticleResponse(article));
        }
        return response;
    }

    public ArticleResponse findArticle(String title) throws NoSuchElementException, CustomException {
        if (title == null || title.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "The title can't be blank!");
        }
        Article article = articleRepository.findByTitle(title).get();
        return mapper.toArticleResponse(article);
    }

    public List<Article> getArticlesWithHighestCounter(int n) {
        return articleRepository.findTopNByOrderByCounterDesc(n);
    }

    public ArticleResponse updateArticle(String title_, ArticleResponse articleIn)
            throws NoSuchElementException, IllegalArgumentException {
        Article article = articleRepository.findByTitle(title_).get();
        String title = articleIn.getTitle();
        Table infoTable = articleIn.getInfoTable();
        List<Paragraph> paragraphs = articleIn.getParagraphs();
        article.setTitle(title);
        article.setInfoTable(infoTable);
        article.setParagraphs(paragraphs);
        articleRepository.save(article);
        return mapper.toArticleResponse(article);
    }

    public void deleteArticle(String title) throws NoSuchElementException, IllegalArgumentException {
        Article article = articleRepository.findByTitle(title).get();
        String id = article.getId();
        articleRepository.deleteById(id);
    }

    public void updateCounter(String title) throws NoSuchElementException {
        Article article = articleRepository.findByTitle(title).get();
        Long counter = article.getCounter();
        if (counter == null) {
            counter = 0L;
        }
        article.setCounter(++counter);
        articleRepository.save(article);
    }
}
