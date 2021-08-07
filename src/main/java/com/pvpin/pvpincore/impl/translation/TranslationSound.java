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

import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Sound;

import static com.pvpin.pvpincore.impl.translation.TranslationManager.*;

/**
 * @author William_Shi
 */
public class TranslationSound {

    protected static String getMojangKey(Sound sound) {
        String str = sound.toString().toLowerCase();
        return "subtitles." + str.replaceAll("_", ".");
        // AMBIENT_CAVE -> subtitles.ambient.cave
    }

    /**
     * @param locale locale type
     * @param sound  sound to be translated
     * @return localized name
     */
    public static String getLocalizedName(String locale, Sound sound) {
        String str = null;
        switch (locale) {
            case "zh_cn": {
                str = getZH_CNName(getMojangKey(sound));
                break;
            }
            case "zh_tw": {
                str = getZH_TWName(getMojangKey(sound));
                break;
            }
            case "en_us": {
                str = getEN_USName(getMojangKey(sound));
                break;
            }
        }
        ;
        return str;
    }

    public static TranslatableComponent getTextComponent(Sound sound) {
        return new TranslatableComponent(getMojangKey(sound));
    }

}
