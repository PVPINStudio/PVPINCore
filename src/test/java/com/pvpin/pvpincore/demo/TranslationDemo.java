package com.pvpin.pvpincore.demo;

import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.api.PVPINTranslation;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

/**
 * @author William_Shi
 */
public class TranslationDemo {
    static {

        System.out.println(PVPINTranslation.getLocalizedName("zh_cn", Material.DIAMOND));
        System.out.println(PVPINTranslation.getLocalizedName("zh_cn", EntityType.BEE));
        System.out.println(PVPINTranslation.getLocalizedName("zh_cn", PotionEffectType.DOLPHINS_GRACE));
        System.out.println(PVPINTranslation.getLocalizedName("zh_cn", Sound.AMBIENT_CAVE));
    }
}
