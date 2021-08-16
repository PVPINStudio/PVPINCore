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
package com.pvpin.pvpincore.api;

import com.pvpin.pvpincore.impl.translation.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * This class is used to manage methods related to translation.<p>
 * Currently, three languages are supported.<p>
 * They are zh_cn, zh_tw, and en_us.<p>
 * Only entity types, materials (block and item) and potion effect types are supported.<p>
 * Returns the official translations.
 *
 * @author William_Shi
 */
public class PVPINTranslation {

    /**
     * This method is used to get material translations.
     *
     * @param locale target locale
     * @param mat    material to be translated
     * @return the official translation of the material
     */
    public static String getLocalizedName(String locale, Material mat) {
        return TranslationMaterial.getLocalizedName(locale, mat);
    }

    /**
     * This method is used to get material translations.
     *
     * @param player target locale (of the player)
     * @param mat    material to be translated
     * @return the official translation of the material
     */
    public static String getLocalizedName(Player player, Material mat) {
        return getLocalizedName(player.getLocale(), mat);
    }

    /**
     * This method is used to get enchantment translations.
     *
     * @param locale target locale
     * @param ench   enchantment to be translated
     * @return the official translation of the enchantment
     */
    public static String getLocalizedName(String locale, Enchantment ench) {
        return TranslationEnchantment.getLocalizedName(locale, ench);
    }

    /**
     * This method is used to get enchantment translations.
     *
     * @param player target locale (of the player)
     * @param ench   enchantment to be translated
     * @return the official translation of the enchantment
     */
    public static String getLocalizedName(Player player, Enchantment ench) {
        return getLocalizedName(player.getLocale(), ench);
    }

    /**
     * This method is used to get entity type translations.
     *
     * @param locale target locale
     * @param type   entity type to be translated
     * @return the official translation of the entity type
     */
    public static String getLocalizedName(String locale, EntityType type) {
        return TranslationEntityType.getLocalizedName(locale, type);
    }

    /**
     * This method is used to get entity type translations.
     *
     * @param player target locale (of the player)
     * @param type   entity type to be translated
     * @return the official translation of the entity type
     */
    public static String getLocalizedName(Player player, EntityType type) {
        return getLocalizedName(player.getLocale(), type);
    }

    /**
     * This method is used to get potion effect type translations.
     *
     * @param locale target locale
     * @param type   potion effect type to be translated
     * @return the official translation of the  potion effect type
     */
    public static String getLocalizedName(String locale, PotionEffectType type) {
        return TranslationPotionEffectType.getLocalizedName(locale, type);
    }

    /**
     * This method is used to get potion effect type translations.
     *
     * @param player target locale (of the player)
     * @param type   potion effect type to be translated
     * @return the official translation of the  potion effect type
     */
    public static String getLocalizedName(Player player, PotionEffectType type) {
        return getLocalizedName(player.getLocale(), type);
    }

    /**
     * This method is used to get potion effect type translations.
     *
     * @param locale target locale
     * @param sound  sound type to be translated
     * @return the official translation of the  potion effect type
     */
    public static String getLocalizedName(String locale, Sound sound) {
        return TranslationSound.getLocalizedName(locale, sound);
    }

    /**
     * This method is used to get potion effect type translations.
     *
     * @param player target locale (of the player)
     * @param sound  sound type to be translated
     * @return the official translation of the  potion effect type
     */
    public static String getLocalizedName(Player player, Sound sound) {
        return getLocalizedName(player.getLocale(), sound);
    }

}
