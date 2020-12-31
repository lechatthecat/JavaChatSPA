package com.javachat.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Date;
import org.springframework.validation.ValidationUtils;

public class MyValidateUtil extends ValidationUtils {
    
    public static boolean isValidEmailAddress(String email) {
        if (null != email) {
            String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isValidDate(String date, String format) {
        try{
            parseDate(date, format, false);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private static Date parseDate(String maybeDate, String format, boolean lenient) {
        Date date = null;
    
        // test date string matches format structure using regex
        // - weed out illegal characters and enforce 4-digit year
        // - create the regex based on the local format string
        String reFormat = Pattern.compile("d+|M+").matcher(Matcher.quoteReplacement(format)).replaceAll("\\\\d{1,2}");
        reFormat = Pattern.compile("y+").matcher(reFormat).replaceAll("\\\\d{4}");
        if ( Pattern.compile(reFormat).matcher(maybeDate).matches() ) {
    
          // date string matches format structure, 
          // - now it can be converted to a valid date
          SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateInstance();
          sdf.applyPattern(format);
          sdf.setLenient(lenient);
          try { date = sdf.parse(maybeDate); } catch (ParseException e) { }
        } 
        return date;
      } 

}