package com.handzap.assignment.scrapper.dataextractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handzap.assignment.scrapper.model.Article;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Component
public class ArticleMetaDataExtractor implements DataExtractor {
    final ObjectMapper objectMapper = new ObjectMapper();
    private final String DEFAULT_MISSING_VALUE = "";

    public void extract(Document articleDocument, Article article) {
        try {
            final String articleMetadata = articleDocument.getElementsByTag("script").get(0).data();
            final JsonNode articleJsonNode = objectMapper.readTree(articleMetadata);
            article.setAuthor(getValue(articleJsonNode.get("author").get("name")));
            article.setArticleLink(getValue(articleJsonNode.get("mainEntityOfPage").get("@id")));
            article.setTitle(getValue(articleJsonNode.get("headline")));
            article.setDescription(getValue(articleJsonNode.get("description")));
            final String keywordString = getValue(articleJsonNode.get("keywords"));//csv
            article.setTags(Arrays.asList(keywordString.split(",")));
            String dateModified = getValue(articleJsonNode.get("dateModified"));
            article.setModifiedDate(ZonedDateTime.parse(dateModified));
            final String datePublished = getValue(articleJsonNode.get("datePublished"));
            article.setPublishDate(ZonedDateTime.parse(datePublished));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getValue(JsonNode jsonNode) {
        return jsonNode == null ? DEFAULT_MISSING_VALUE : jsonNode.asText();
    }
}
