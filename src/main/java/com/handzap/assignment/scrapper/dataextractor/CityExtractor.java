package com.handzap.assignment.scrapper.dataextractor;

import com.handzap.assignment.scrapper.model.Article;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class CityExtractor implements DataExtractor {
    @Override
    public void extract(Document articleDocument, Article article) {
        final String cityHtml = articleDocument.getElementsByClass("blue-color ksl-time-stamp").get(0).html();
        if (!cityHtml.contains(" IST")) {
            final String city = cityHtml.replaceAll("[^A-Za-z]", "");
            article.setCity(city);
        }
    }
}
