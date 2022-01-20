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

import com.pvpin.pvpincore.modules.config.ConfigManager;

/**
 * @author William_Shi
 */
public class I18N {

    public static String translateByDefault(String key) {
        return translate(ConfigManager.DEFAULT_LOCALE, key);
    }

    public static String translate(String locale, String key) {
        StringBuilder formatted = new StringBuilder();
        if (locale.contains("_")) {
            formatted.append(locale.split("_")[0].toLowerCase());
            formatted.append("_");
            formatted.append(locale.split("_")[1].toUpperCase());
        } else if (locale.length() == 4) {
            formatted.append(locale.substring(0, 2).toLowerCase());
            formatted.append("_");
            formatted.append(locale.substring(2).toUpperCase());
        } else {
            throw new RuntimeException();
        }
        return LangManager.LANG_MAP.containsKey(formatted.toString()) ?
                LangManager.LANG_MAP.get(formatted.toString()).get(key) :
                LangManager.LANG_MAP.get(ConfigManager.DEFAULT_LOCALE).get(key);
    }

}
