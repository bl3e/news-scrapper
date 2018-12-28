package com.handzap.assignment.scrapper.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ZonedDateTimeDeSerializer extends StdDeserializer<ZonedDateTime> {

    public ZonedDateTimeDeSerializer() {
        this(null);
    }

    protected ZonedDateTimeDeSerializer(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String date = parser.getText();
        return zonedDateTimeOf(date);
    }

    public static ZonedDateTime zonedDateTimeOf(String date) {
        if (date == null) {
            return null;
        }
        String normalisedDate = date;
        if (date.length() == 10) { //if time zone is missing ,default timezone for dates will be +5:30
            normalisedDate = date.concat("T00:00:00+05:30");
        }else if(date.length()==19){
            normalisedDate=date.concat("+05:30");
        }
        return ZonedDateTime.parse(normalisedDate, DateTimeFormatter.ISO_DATE_TIME);
    }
}
