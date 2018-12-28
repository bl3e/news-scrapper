package com.handzap.assignment.scrapper.repository;

import com.handzap.assignment.scrapper.model.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by kumarrak on 26/12/18.
 */

@Repository
public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

}