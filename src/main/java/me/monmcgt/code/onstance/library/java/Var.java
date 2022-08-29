package me.monmcgt.code.onstance.library.java;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

public class Var {
    public static final Gson GSON;
    public static final JsonParser JSON_PARSER;

    static {
        GSON = new Gson();
        JSON_PARSER = new JsonParser();
    }
}
