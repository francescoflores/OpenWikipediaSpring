package com.open.openwikipedia.articleManagement.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.open.openwikipedia.articleManagement.service.ArticleService;
import com.open.openwikipedia.common.Exceptions.CustomException;
import com.open.openwikipedia.common.model.Article;
import com.open.openwikipedia.common.model.dto.ArticleDTO;
import com.open.openwikipedia.common.model.dto.ArticleResponse;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody ArticleDTO articleDTO) {
        try {
            Article savedArticle = articleService.saveArticle(articleDTO);
            return ResponseEntity.ok(savedArticle);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "article's title is already present!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getArticles(@RequestParam(required = false) String title) {
        List<ArticleResponse> response = articleService.findAllArticles(title);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{title}")
    public ResponseEntity<?> getArticle(@PathVariable String title) {
        try {
            return ResponseEntity.ok(articleService.findArticle(title));
        } catch (CustomException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "input '" + title + "' can't be blank or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{title}")
    public ResponseEntity<?> updateArticle(@PathVariable String title, @RequestBody ArticleResponse request) {
        try {
            ArticleResponse response = articleService.updateArticle(title, request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("success", response));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "article not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", "request argument must be a string"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e));
        }
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<?> deleteArticle(@PathVariable String title) {
        try {
            articleService.deleteArticle(title);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("success", "article deleted"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "article not found"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", "request argument must be a string"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e));
        }
    }

    @PostMapping("/{title}/counter")
    public ResponseEntity<?> increaseCounter(@PathVariable String title) {
        try {
            articleService.updateCounter(title);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("success", true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body(Map.of("error", e));
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<?>> getArticlesofTheDay(@RequestParam(required = false) String title) {
        List<Article> response = articleService.getArticlesWithHighestCounter(1);
        return ResponseEntity.ok(response);
    }
}
