package com.handzap.assignment.scrapper.controller;

import com.handzap.assignment.scrapper.searchcriteria.SiteScrapeCriteria;
import com.handzap.assignment.scrapper.service.WebSiteDataScrapperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@RestController
@RequestMapping("v1/site")
public class WebSiteDataScrapeController {
    @Resource
    WebSiteDataScrapperService webSiteDataScrapperService;

    @PostMapping
    ResponseEntity<?> scrapeSite(@RequestBody SiteScrapeCriteria siteScrapeCriteria) {
        webSiteDataScrapperService.scrape(siteScrapeCriteria);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    ResponseEntity<String> test() {
        return ResponseEntity.ok("up");
    }

}
