package com.handzap.assignment.scrapper.searchcriteria;

public class SiteScrapeCriteria {

    private String baseUrl;
    private String fromYear;
    private String toYear;
    private String fromMonth;
    private String toMonth;
    private String fromDay;
    private String toDay;
    private int maxRecords;


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getFromYear() {
        return fromYear;
    }

    public String getFromMonth() {
        return fromMonth;
    }

    public String getToMonth() {
        return toMonth;
    }

    public String getFromDay() {
        return fromDay;
    }

    public String getToDay() {
        return toDay;
    }

    public String getToYear() {
        return toYear;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setFromYear(String fromYear) {
        this.fromYear = fromYear;
    }

    public void setFromMonth(String fromMonth) {
        this.fromMonth = fromMonth;
    }

    public void setToMonth(String toMonth) {
        this.toMonth = toMonth;
    }

    public void setFromDay(String fromDay) {
        this.fromDay = fromDay;
    }

    public void setToDay(String toDay) {
        this.toDay = toDay;
    }

    public void setToYear(String toYear) {
        this.toYear = toYear;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }


}
