package com.handzap.assignment.scrapper.searchcriteria;

public class SearchField {
    String fieldName;
    String fieldValue;
    boolean isLikeMatch;

    public SearchField(String fieldName,String fieldValue,boolean isLikeMatch) {
       this.fieldName=fieldName;
        this.fieldValue = fieldValue;
        this.isLikeMatch = isLikeMatch;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public boolean isLikeMatch() {
        return isLikeMatch;
    }
}
