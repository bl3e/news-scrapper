package com.handzap.assignment.scrapper.searchcriteria;

public class SearchFieldWithRange {
    String fieldName;
    Object fromValue;
    Object toValue;


    public SearchFieldWithRange(String fieldName, Object fromValue, Object toValue) {
        this.fieldName = fieldName;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFromValue() {
        return fromValue;
    }

    public Object getToValue() {
        return toValue;
    }

}
