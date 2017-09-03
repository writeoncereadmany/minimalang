package com.writeoncereadmany.minimalang.util;

import com.writeoncereadmany.minimalang.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringUtils {

    Pattern SINGLE_QUOTED = Pattern.compile("^\"(.*)\"$");

    static String stripSurroundingQuotes(String string) {
        Matcher matcher = SINGLE_QUOTED.matcher(string);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            throw new ParseException("Cannot strip quotes from string");
        }
    }
}
