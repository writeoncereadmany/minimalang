package com.writeoncereadmany.minimalang.util;

import com.writeoncereadmany.minimalang.ParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringUtils {

    Pattern DOUBLE_QUOTED = Pattern.compile("^\"(.*)\"$");

    static String stripSurroundingQuotes(String string) {
        Matcher matcher = DOUBLE_QUOTED.matcher(string);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            throw new ParseException("Cannot strip quotes from string");
        }
    }
}
