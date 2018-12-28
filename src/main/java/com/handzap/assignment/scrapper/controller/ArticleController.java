package com.handzap.assignment.scrapper.controller;

import com.handzap.assignment.scrapper.config.ZonedDateTimeDeSerializer;
import com.handzap.assignment.scrapper.model.Article;
import com.handzap.assignment.scrapper.searchcriteria.ArticleSearchCriteria;
import com.handzap.assignment.scrapper.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("v1/articles")
public class ArticleController {
    @Resource
    ArticleService articleService;

    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAuthors() {
        final List<String> allAuthors = articleService.findAllAuthors();
        return ResponseEntity.ok(allAuthors);
    }

    @GetMapping("{author}/{title}")
    ResponseEntity<Article> getArticleByAuthorAndTitle(@PathVariable String author, @PathVariable String title) {
        final Article article = articleService.get(author, title);
        return ResponseEntity.ok(article);
    }

    //search by individual fields in request parameter
    @GetMapping
    ResponseEntity<List<Article>> searchArticlesByParam(@ModelAttribute ArticleSearchCriteria articleSearchCriteria,
                                                        @RequestParam(required = false) String toDate,
                                                        @RequestParam(required = false) String fromDate) {
        if (fromDate != null) {
            articleSearchCriteria.setPublishDateTimeFrom(ZonedDateTimeDeSerializer.zonedDateTimeOf(fromDate));
        }
        if (toDate != null) {
            articleSearchCriteria.setPublishDateTimeTo(ZonedDateTimeDeSerializer.zonedDateTimeOf(toDate));
        }
        return getArticleListResponseEntity(articleSearchCriteria);
    }

    //search by fields in request body ,easier to handle de-serialization for ZonedDateTime
    @PostMapping("/search")
    ResponseEntity<List<Article>> searchArticles(@RequestBody ArticleSearchCriteria articleSearchCriteria) {
        return getArticleListResponseEntity(articleSearchCriteria);
    }

    private ResponseEntity<List<Article>> getArticleListResponseEntity(@RequestBody ArticleSearchCriteria articleSearchCriteria) {
        if (!articleSearchCriteria.isValid()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "too broad search,specify search parameter");

        }
        final List<Article> articles = articleService.search(articleSearchCriteria);
        return ResponseEntity.ok(articles);
    }

}
