package com.handzap.assignment.scrapper.dataextractor;

import com.handzap.assignment.scrapper.model.Article;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class CategoryExtractor implements DataExtractor {
    @Override
    public void extract(Document articleDocument, Article article) {
        final Elements metaTags = articleDocument.getElementsByTag("meta");
        for(Element metaTag:metaTags) {
            final String propertyValue = metaTag.attr("property");
            if("article:section".equals(propertyValue)){
                final String category = metaTag.attr("content");
                article.setCategory(category);
                return;
            }
        }
    }
}
