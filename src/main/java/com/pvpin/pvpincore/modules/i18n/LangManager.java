/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pvpin.pvpincore.modules.i18n;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class LangManager {
    protected static final Map<String, Map<String, String>> LANG_MAP = new HashMap<>(16);

    static {
        registerDefaultLang("en_US");
        registerDefaultLang("zh_CN");
        registerDefaultLang("zh_TW");
    }

    public static void registerLang(String locale, Map<String, String> map) {
        if (LANG_MAP.containsKey(locale)) {
            throw new RuntimeException();
        }
        LANG_MAP.put(locale, map);
    }

    protected static void registerDefaultLang(String locale) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObj;
        jsonObj = new JsonParser().parse(
                new JsonReader(new InputStreamReader(
                        PVPINCore.getCoreInstance().getResource("locale/" + locale + ".json"), StandardCharsets.UTF_8
                ))
        ).getAsJsonObject();
        Map map = gson.fromJson(jsonObj, Map.class);
        LANG_MAP.put(locale, map);
    }
}
