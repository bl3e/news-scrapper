package com.handzap.assignment.scrapper;

import com.handzap.assignment.scrapper.model.Article;
import com.handzap.assignment.scrapper.searchcriteria.ArticleSearchCriteria;
import com.handzap.assignment.scrapper.searchcriteria.SiteScrapeCriteria;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ApplicationFunctionalTest {
    private static final RestTemplate restTemplate = new RestTemplate();
    static final String artilceAPIurl = "http://localhost:8080/v1/articles";
    static final String scrapeAPIurl = "http://localhost:8080/v1/site";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFunctionalTest.class);

    @BeforeClass
    public static void setup() throws InterruptedException {
        while (!isUp()) {
            Thread.sleep(1000);
            System.out.println("application not up ,will retry in 1000ms ");
        }
        scrapeArchive(10);
    }

    private static void scrapeArchive(int maxRecords) {
        SiteScrapeCriteria siteScrapeCriteria = new SiteScrapeCriteria();
        siteScrapeCriteria.setBaseUrl("https://www.thehindu.com/archive/web");
        siteScrapeCriteria.setMaxRecords(maxRecords);
        HttpEntity<SiteScrapeCriteria> httpEntity = new HttpEntity<>(siteScrapeCriteria);
        final ResponseEntity<Void> scrapeResponse = restTemplate.exchange(scrapeAPIurl, HttpMethod.POST, httpEntity, Void.class);
        assertThat(scrapeResponse.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }

    private static boolean isUp() {
        final String testUrl = scrapeAPIurl + "/test";
        try {
            final ResponseEntity<String> healthResponse = restTemplate.getForEntity(testUrl, String.class);
            return healthResponse.getStatusCode().is2xxSuccessful();
        } catch (RestClientException e) {
            LOGGER.error("application not up ? ", e);
        }
        return false;
    }

    //1 Find all authors
    @Test
    public void findAllAuthors() {
        final HttpEntity<?> httpEntity = getHttpEntity();
        final String authorAPI = artilceAPIurl + "/authors";
        final URI uri = getUri(authorAPI, Collections.emptyMap());
        ResponseEntity<List<String>> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<String>>() {
        });
        final List<String> authorList = responseEntity.getBody();
        assertThat(authorList, hasItems("Staff Reporter", "Jon Boone", "Tara Parker Pope", "Bageshree S.", "The Hindu"));
    }


    @Test
    public void searchByAuthor() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("author", "John Ashbourne");

        final URI uri = getUri(artilceAPIurl, queryMap);
        final List<Article> articleList = getArticleList(uri);
        assertThat(articleList.size(), greaterThan(0));
        List<String> authors = articleList.stream().map(article -> article.getAuthor()).collect(Collectors.toList());
        assertThat(authors, hasItem("John Ashbourne"));
        List<String> titles = articleList.stream().map(article -> article.getTitle()).collect(Collectors.toList());
        assertThat(titles, hasItem("Why triage will not be enough"));
    }

    @Test
    public void searchByTitleAndDescription() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("title", "Homilies for swine flu");
        queryMap.put("description", "A clove of garlic a day keeps swine");
        final URI uri = getUri(artilceAPIurl, queryMap);
        final List<Article> articleList = getArticleList(uri);
        assertThat(articleList.size(), greaterThan(0));
        List<String> titles = articleList.stream().map(article -> article.getTitle()).collect(Collectors.toList());
        assertThat(titles, hasItem("Homilies for swine flu spread faster than virus"));
        List<String> descriptions = articleList.stream().map(article -> article.getDescription()).collect(Collectors.toList());
        String description = "A clove of garlic a day keeps swine flu away. If you fear that this remedy is more" +
                " likely to keep a whole lot of people away, you could replace garlic with tulsi leaves, " +
                "pepper or any other herb or a";
        assertThat(descriptions, hasItem(description));
    }

    @Test
    public void searchByDate() {
        final List<Article> articleList = getArticlesByDateRange("2009-08-15", "2009-08-16");
        assertThat(articleList.size(), greaterThan(9));
    }

    @Test
    public void searchByDateWithTime() {
        final List<Article> articleList = getArticlesByDateRange("2009-08-15T18:32:09", "2009-08-15T18:35:09");
        assertThat(articleList.size(), is(1));
    }

    private List<Article> getArticlesByDateRange(String fromDate, String toDate) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("fromDate", fromDate);
        queryMap.put("toDate", toDate);
        final URI uri = getUri(artilceAPIurl, queryMap);
        return getArticleList(uri);
    }

    private List<Article> getArticleList(URI uri) {
        return getArticleList(uri, new ArticleSearchCriteria());
    }

    @Test
    public void searchByTag() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("tag", "Metro Rail");
        final URI uri = getUri(artilceAPIurl, queryMap);
        final List<Article> articleList = getArticleList(uri);
        assertThat(articleList.size(), is(1));
        assertThat(articleList.get(0).getTags(), hasItem("Bangalore Metro Rail Corporation Ltd"));
    }

    @Test
    public void searchByCity() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("city", "Bangalore");
        final URI uri = getUri(artilceAPIurl, queryMap);
        final List<Article> articleList = getArticleList(uri);
        assertThat(articleList.size(), is(5));

        for (Article article : articleList) {
            assertThat(article.getCity(), is("Bangalore"));
        }
    }

    @Test
    public void searchByCategory() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("category", "Bengaluru");
        final URI uri = getUri(artilceAPIurl, queryMap);
        final List<Article> articleList = getArticleList(uri);
        assertThat(articleList.size(), is(5));

        for (Article article : articleList) {
            assertThat(article.getCategory(), is("Bengaluru"));
        }
    }

    private List<Article> getArticleList(URI uri, ArticleSearchCriteria articleSearchCriteria) {
        ParameterizedTypeReference<List<Article>> typeReference = new ParameterizedTypeReference<List<Article>>() {
        };
        ResponseEntity<List<Article>> responseEntity = restTemplate
                .exchange(uri, HttpMethod.GET, getHttpEntity(articleSearchCriteria), typeReference);
        return responseEntity.getBody();
    }

    private URI getUri(String url) {
        return getUri(url, Collections.EMPTY_MAP);
    }

    private URI getUri(String url, Map<String, String> queryMap) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        queryMap.entrySet().forEach(entry -> multiValueMap.add(entry.getKey(), entry.getValue()));
        return UriComponentsBuilder.fromHttpUrl(url).queryParams(multiValueMap).build().encode()
                .toUri();
    }

    private HttpEntity<?> getHttpEntity() {
        return getHttpEntity(null);
    }

    private HttpEntity<?> getHttpEntity(ArticleSearchCriteria articleSearchCriteria) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        return new HttpEntity<>(articleSearchCriteria, headers);
    }
}