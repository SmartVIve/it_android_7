package com.example.smart.test1.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Smart on 2017-12-15.
 */

public class ClassPathResourceUtil {
    public static Boolean isPhonenum(String num){
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[0-3,5-9])|(18[0-9])|(14[1,4-9])|(16[6])|(17[0,3,5-8])|(19[8-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(num);
        return matcher.matches();
    }
}
