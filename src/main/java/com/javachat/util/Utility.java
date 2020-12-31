package com.javachat.util;

import java.text.DateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Utility {
    @Value("${chat.app.locale}")
    private String appLocale;
    private ChronoUnit unit = ChronoUnit.MINUTES;
    
    public boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }  

    public ZonedDateTime getCurrentLocalTime(){
        Instant instantNow = Instant.now();
        ZonedDateTime now = ZonedDateTime.ofInstant(instantNow, ZoneId.of(appLocale));
        return now;
    }

    public ZonedDateTime getCurrentSystemLocalTime(){
        Instant instantNow = Instant.now();
        ZonedDateTime now = ZonedDateTime.ofInstant(instantNow, ZoneId.systemDefault());
        return now;
    }

    public long zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2){
        return unit.between(d1, d2);
    }

    public String formatDate(ZonedDateTime d) {
        if(d != null){
            // Locale is US time
            DateFormat sdf = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String strDate = sdf.format(d);
            return strDate;
        } else {
            return "";
        }
    } 

    public boolean isEmpty(String str){
        if(str != null && !str.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public <T> T copy(T anObject, Class<T> classInfo) {
        Gson gson = new GsonBuilder().create();
        String text = gson.toJson(anObject);
        T newObject = gson.fromJson(text, classInfo);
        return newObject;
    }
}