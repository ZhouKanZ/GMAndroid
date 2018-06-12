package com.jms.cleanse.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by zhoukan on 2018/6/11.
 *
 * @desc:
 */

public class JsonHelper {

    /**
     *  jsonString 转成jsonObject
     * @param src
     * @return
     */
    public static JsonObject convertStringToJsonFormat(String src) {
        src = src.replaceAll("(\\{|,)([^:]+)", "$1\"$2\"").replaceAll("([^:,\\}]+)(\\}|,)", "\"$1\"$2");
        JsonObject jb = new Gson().fromJson(src, JsonObject.class);
        return jb;
    }

}
