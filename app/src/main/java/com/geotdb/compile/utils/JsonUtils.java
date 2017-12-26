package com.geotdb.compile.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtils {
    //判定是否是json格式
    public static boolean isGoodJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            System.out.println("bad json: " + json);
            return false;
        }
    }
}
