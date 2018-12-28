package com.handzap.assignment.scrapper.repository;

import com.handzap.assignment.scrapper.model.Article;
import com.handzap.assignment.scrapper.searchcriteria.ArticleSearchCriteria;
import com.handzap.assignment.scrapper.searchcriteria.SearchField;
import com.handzap.assignment.scrapper.searchcriteria.SearchFieldWithRange;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Repository
public class ArticleESRepository {
    @Resource
    ElasticsearchTemplate elasticsearchTemplate;

    public List<String> findAllAuthors() {
        String[] includes = new String[]{"author"};
        final NativeSearchQuery searchQuery0 = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSearchType(SearchType.DEFAULT)
                .withIndices("news").withTypes("article")
                .withSourceFilter(new FetchSourceFilter(includes, null))
                .addAggregation(AggregationBuilders.terms("distinct_author").field("author.fullname"))
                .build();
        final Aggregations aggregations1 = elasticsearchTemplate.query(searchQuery0, response -> response.getAggregations());
        aggregations1.get("distinct_author");
        final List<? extends Terms.Bucket> distinct_authorBuckets = ((Terms) aggregations1.get("distinct_author")).getBuckets();
        final List<String> authorList = distinct_authorBuckets.stream().map(bucket ->
                (String) bucket.getKey()).collect(Collectors.toList());
        return authorList;
    }

    public List<Article> find(ArticleSearchCriteria articleSearchCriteria) {
        Criteria criteria = null;
        final Set<SearchField> searchFieldSet = articleSearchCriteria.getSearchFields();
        for (SearchField searchField : searchFieldSet) {
            final Criteria fieldCriteria = getCriteria(searchField);
            criteria = getAppendedCriteria(criteria, fieldCriteria);

        }
        final Set<SearchFieldWithRange> searchFieldsWithRangeSet = articleSearchCriteria.getSearchFieldsWithRange();
        for (SearchFieldWithRange searchFieldWithRange : searchFieldsWithRangeSet) {
            final Criteria fieldCriteria = getCriteria(searchFieldWithRange);
            criteria = getAppendedCriteria(criteria, fieldCriteria);
        }
        return elasticsearchTemplate.queryForList(new CriteriaQuery(criteria), Article.class);
    }

    private Criteria getAppendedCriteria(Criteria criteria, Criteria fieldCriteria) {
        if (fieldCriteria == null) {
            return criteria;
        }
        if (criteria == null) {
            criteria = fieldCriteria;
        } else {
            criteria = criteria.and(fieldCriteria);
        }
        return criteria;
    }

    private Criteria getCriteria(SearchField searchField) {
        Criteria fieldCriteria = new Criteria(searchField.getFieldName());
        final String fieldValue = searchField.getFieldValue();
        return fieldCriteria.is(fieldValue);
    }

    private Criteria getCriteria(SearchFieldWithRange searchFieldWithRange) {
        final Object fromValue = searchFieldWithRange.getFromValue();
        final Object toValue = searchFieldWithRange.getToValue();
        if (toValue == null && fromValue == null) {
            return null;
        }
        if (toValue == null) {
            return new Criteria(searchFieldWithRange.getFieldName()).greaterThanEqual(fromValue);
        }

        if (fromValue == null) {
            return new Criteria(searchFieldWithRange.getFieldName()).lessThanEqual(toValue);
        }

        return new Criteria(searchFieldWithRange.getFieldName()).between(fromValue, toValue);
    }
}
