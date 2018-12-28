package com.handzap.assignment.scrapper.dataextractor;

import com.handzap.assignment.scrapper.model.Article;
import org.jsoup.nodes.Document;

public interface DataExtractor {
    void extract(Document articleMetadata, Article article);
}
