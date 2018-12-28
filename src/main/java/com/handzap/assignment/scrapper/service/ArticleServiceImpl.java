package com.handzap.assignment.scrapper.service;

import com.handzap.assignment.scrapper.model.Article;
import com.handzap.assignment.scrapper.repository.ArticleESRepository;
import com.handzap.assignment.scrapper.repository.ArticleRepository;
import com.handzap.assignment.scrapper.searchcriteria.ArticleSearchCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Created by kumarrak on 27/12/18.
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    private final String KEY_SEPARATOR = "|";
    @Resource
    private ArticleRepository articleRepository;

    @Resource
    ArticleESRepository articleESRepository;

    @Override
    public void save(Article article) {
        article.setKey(buildKey(article.getAuthor(), article.getTitle()));
        articleRepository.save(article);

    }

    public Article get(String author, String title) {
        final String id = buildKey(author, title);
        final Optional<Article> optionalArticle = articleRepository.findById(id);
        if (!optionalArticle.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Article Not Found for author " + author + "title " + title);
        }
        return optionalArticle.get();
    }

    public List<Article> search(ArticleSearchCriteria articleSearchCriteria) {
        return articleESRepository.find(articleSearchCriteria);
    }

    @Override
    public List<String> findAllAuthors() {
        return articleESRepository.findAllAuthors();
    }

    private String buildKey(String author, String title) {
        StringJoiner stringJoiner = new StringJoiner(KEY_SEPARATOR);
        return stringJoiner.add(author).add(title).toString();
    }
}
