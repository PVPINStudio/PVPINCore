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
package com.pvpin.pvpincore.impl.translation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.impl.nms.VersionChecker;

import static com.pvpin.pvpincore.impl.nms.VersionChecker.version;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class TranslationManager {

    protected static Map<String, String> zhcn;
    // Simplified Chinese.
    protected static Map<String, String> zhtw;
    // Traditional Chinese.
    protected static Map<String, String> enus;
    // English.

    /**
     * Get the locale file according to the server version.
     */
    static {
        if (VersionChecker.isCurrentHigherOrEquals("v1_13_R0")) {
            // json for 1.13 or higher.
            Gson gson = new GsonBuilder().create();
            JsonObject jsonObj;
            jsonObj = new JsonParser().parse(
                    new JsonReader(new InputStreamReader(
                            PVPINCore.getCoreInstance().getResource("locale/zh_cn_" + version.substring(1, 5) + ".json")
                            // Turn v1_1x_Rx into v1_1x.
                            // The locale files in the PVPINCore jar are the final versions.
                            // Such as 1_12 represents 1.12.2 and 1_16 represents 1.16.5.
                    ))
            ).getAsJsonObject();
            zhcn = gson.fromJson(jsonObj, Map.class);
            jsonObj = new JsonParser().parse(
                    new JsonReader(new InputStreamReader(
                            PVPINCore.getCoreInstance().getResource("locale/zh_tw_" + version.substring(1, 5) + ".json")

                    ))
            ).getAsJsonObject();
            zhtw = gson.fromJson(jsonObj, Map.class);
            jsonObj = new JsonParser().parse(
                    new JsonReader(new InputStreamReader(
                            PVPINCore.getCoreInstance().getResource("locale/en_us_" + version.substring(1, 5) + ".json")

                    ))
            ).getAsJsonObject();
            enus = gson.fromJson(jsonObj, Map.class);
        } else {
            Properties propCN = new Properties();
            Properties propTW = new Properties();
            Properties propEN = new Properties();
            try {
                propCN.load(
                        PVPINCore.getCoreInstance().getResource("locale/zh_cn_1_12.properties")
                );
                zhcn = (Map) propCN.clone();
                propTW.load(
                        PVPINCore.getCoreInstance().getResource("locale/zh_tw_1_12.properties")
                );
                zhtw = (Map) propTW.clone();
                propEN.load(
                        PVPINCore.getCoreInstance().getResource("locale/en_us_1_12.properties")
                );
                enus = (Map) propEN.clone();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * @param key translation key
     * @return simplified Chinese name
     */
    protected static String getZH_CNName(String key) {
        return zhcn.get(key);
    }

    /**
     * @param key translation key
     * @return traditional Chinese name
     */
    protected static String getZH_TWName(String key) {
        return zhtw.get(key);
    }

    /**
     * @param key translation key
     * @return English (US) name
     */
    protected static String getEN_USName(String key) {
        return enus.get(key);
    }

}
