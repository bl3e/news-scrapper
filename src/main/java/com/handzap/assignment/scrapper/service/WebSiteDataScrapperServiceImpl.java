package com.handzap.assignment.scrapper.service;

import com.handzap.assignment.scrapper.dataextractor.DataExtractor;
import com.handzap.assignment.scrapper.model.Article;
import com.handzap.assignment.scrapper.searchcriteria.SiteScrapeCriteria;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.util.List;

/**
 * Created by kumarrak on 26/12/18.
 */
@Service
public class WebSiteDataScrapperServiceImpl implements WebSiteDataScrapperService {
    @Resource
    private List<DataExtractor> dataExtractorList;

    @Resource
    private ArticleService articleService;

    @Override
    @Async
    public void scrape(SiteScrapeCriteria siteScrapeCriteria) {
        int processedCount = 0;
        final Document document = getDocument(siteScrapeCriteria.getBaseUrl());
        final Elements archiveElements = document.getElementsByClass("archiveMonthList").select("a");
        for (Element archiveElement : archiveElements) {
            final Elements dayDocumentArchiveList = getMonthArchiveList(archiveElement);

            for (Element dayDocumentArchive : dayDocumentArchiveList) {
                String dayUrl = dayDocumentArchive.getElementsByClass("ui-state-default").attr("href");
                if (!StringUtils.isEmpty(dayUrl)) {
                    final Elements dayArchiveElementList = getDayArchiveList(dayUrl);
                    for (Element dayArchiveElement : dayArchiveElementList) {
                        final Elements articleElements = dayArchiveElement.select("li");
                        for (Element articleElement : articleElements) {
                            final Article article = extractArticleFromMetadata(articleElement);
                            if (!shouldProcess(siteScrapeCriteria, processedCount)) {
                                return;
                            }
                            articleService.save(article);
                            processedCount++;
                        }
                    }
                }
            }
        }
    }

    private boolean shouldProcess(SiteScrapeCriteria siteScrapeCriteria, int numProcessed) {
        if (siteScrapeCriteria.getMaxRecords() == 0) {
            return true;
        }
        return siteScrapeCriteria.getMaxRecords() == 0 || siteScrapeCriteria.getMaxRecords() > numProcessed;

    }

    private Elements getMonthArchiveList(Element archiveElement) {
        final String href = archiveElement.attr("href");
        final Document monthDocument = getDocument(href);
        return monthDocument.select("tbody").get(0).select("td");
    }

    private Elements getDayArchiveList(String dayUrl) {
        final Document dayArchives = getDocument(dayUrl);
        dayArchives.getElementsByClass("archive-list");
        return dayArchives.getElementsByClass("archive-list");
    }

    private Article extractArticleFromMetadata(Element articleElement) {
        final String articleUrl = articleElement.select("a").attr("href");
        final Document articleDocument = getDocument(articleUrl);
        Article article = new Article();
        dataExtractorList.forEach(dataExtractor -> dataExtractor.extract(articleDocument, article));
        return article;
    }

    private Document getDocument(String url) {
        final Connection connectionToMonthArchive = Jsoup.connect(url);
        try {
            return connectionToMonthArchive.get();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "could not connect to " + url);
        }
    }


}
