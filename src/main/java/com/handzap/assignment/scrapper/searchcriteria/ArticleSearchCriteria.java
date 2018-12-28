package com.handzap.assignment.scrapper.searchcriteria;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.handzap.assignment.scrapper.config.ZonedDateTimeDeSerializer;
import com.handzap.assignment.scrapper.config.ZonedDateTimeSerializer;

import java.time.ZonedDateTime;
import java.util.*;

public class ArticleSearchCriteria {
    static final Set<String> DEFAULT_LIKE_FIELDS = new HashSet<>(Arrays.asList("description", "title"));

    String author;
    String city;
    String title;
    String description;
    String tag;
    String category;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeSerializer.class)
    ZonedDateTime publishDateTimeFrom;

    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    @JsonDeserialize(using = ZonedDateTimeDeSerializer.class)
    ZonedDateTime publishDateTimeTo;

    List<String> customLikeFields;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ZonedDateTime getPublishDateTimeFrom() {
        return publishDateTimeFrom;
    }

    public void setPublishDateTimeFrom(ZonedDateTime publishDateTimeFrom) {
        this.publishDateTimeFrom = publishDateTimeFrom;
    }

    public ZonedDateTime getPublishDateTimeTo() {
        return publishDateTimeTo;
    }

    public void setPublishDateTimeTo(ZonedDateTime publishDateTimeTo) {
        this.publishDateTimeTo = publishDateTimeTo;
    }

    public List<String> getCustomLikeFields() {
        return customLikeFields;
    }

    public void setCustomLikeFields(List<String> customLikeFields) {
        this.customLikeFields = customLikeFields;
    }

    @JsonIgnore
    public Set<SearchField> getSearchFields() {
        Set<SearchField> searchFieldSet = new LinkedHashSet<>();
        addToSearchFieldSet(searchFieldSet, "author", author);
        addToSearchFieldSet(searchFieldSet, "title", title);
        addToSearchFieldSet(searchFieldSet, "tags", tag);
        addToSearchFieldSet(searchFieldSet, "city", city);
        addToSearchFieldSet(searchFieldSet, "category", category);
        addToSearchFieldSet(searchFieldSet, "description", description);
        return searchFieldSet;
    }

    @JsonIgnore
    public Set<SearchFieldWithRange> getSearchFieldsWithRange() {
        Set<SearchFieldWithRange> rangeFieldSet = new HashSet<>();
        //use lexographically comparable ISO date format ,elastic search doesn't have a date field ,nor does JSON
        final String fromDate = ZonedDateTimeSerializer.stringOf(publishDateTimeFrom);
        final String toDate = ZonedDateTimeSerializer.stringOf(publishDateTimeTo);

        rangeFieldSet.add(new SearchFieldWithRange("publishDate", fromDate, toDate));
        return rangeFieldSet;
    }

    private void addToSearchFieldSet(Set<SearchField> fieldNameToValueMap, String fieldName, String fieldValue) {
        if (fieldValue != null) {
            fieldNameToValueMap.add(new SearchField(fieldName, fieldValue, isFuzzyField(fieldName)));
        }
    }

    private boolean isFuzzyField(String field) {
        return (customLikeFields != null && customLikeFields.contains(field)) || DEFAULT_LIKE_FIELDS.contains(field);
    }

    @JsonIgnore
    public boolean isValid() {
        return !(getSearchFields().isEmpty() && publishDateTimeFrom == null && publishDateTimeTo == null);
    }
}
