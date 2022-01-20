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
package com.pvpin.pvpincore.modules.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.boot.PVPINLoadOnEnable;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
@PVPINLoadOnEnable
public class ConfigManager {
    static {
        PVPINCore.getCoreInstance().saveResource("config/Config.yml", false);
        PVPINCore.getCoreInstance().saveResource("config/TrustedPlayers.json", false);
        try {
            refreshConfiguration();
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

    public static String DEFAULT_LOCALE;
    public static int PLUGIN_CAPACITY;
    public static int PLUGIN_COMMAND_CAPACITY;
    public static int PLUGIN_LISTENER_CAPACITY_LOWEST;
    public static int PLUGIN_LISTENER_CAPACITY_LOW;
    public static int PLUGIN_LISTENER_CAPACITY_NORMAL;
    public static int PLUGIN_LISTENER_CAPACITY_HIGH;
    public static int PLUGIN_LISTENER_CAPACITY_HIGHEST;
    public static int PLUGIN_LISTENER_CAPACITY_MONITOR;
    public static int PLUGIN_SCHEDULER_CAPACITY;
    public static List<UUID> TRUSTED_PLAYERS;

    public static void refreshConfiguration() throws IOException {
        File configFile = new File(new File(PVPINCore.getCoreInstance().getDataFolder(), "config"), "Config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        File trustedPlayers = new File(new File(PVPINCore.getCoreInstance().getDataFolder(), "config"), "TrustedPlayers.json");
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray;
        jsonArray = new JsonParser().parse(
                new JsonReader(new FileReader(
                        new File(new File(PVPINCore.getCoreInstance().getDataFolder(), "config"), "TrustedPlayers.json"), StandardCharsets.UTF_8
                ))
        ).getAsJsonArray();
        TRUSTED_PLAYERS = (List<UUID>) gson.fromJson(jsonArray, List.class)
                .stream().map(str -> UUID.fromString((String) str)).collect(Collectors.toList());

        DEFAULT_LOCALE = config.getString("defaultLocale");
        PLUGIN_CAPACITY = calcCapacity(config.getInt("pluginCapacity"));
        PLUGIN_COMMAND_CAPACITY = calcCapacity(config.getInt("pluginCommandCapacity"));
        PLUGIN_LISTENER_CAPACITY_LOWEST = calcCapacity(config.getInt("pluginListenerCapacity_Lowest"));
        PLUGIN_LISTENER_CAPACITY_LOW = calcCapacity(config.getInt("pluginListenerCapacity_Low"));
        PLUGIN_LISTENER_CAPACITY_NORMAL = calcCapacity(config.getInt("pluginListenerCapacity_Normal"));
        PLUGIN_LISTENER_CAPACITY_HIGH = calcCapacity(config.getInt("pluginListenerCapacity_High"));
        PLUGIN_LISTENER_CAPACITY_HIGHEST = calcCapacity(config.getInt("pluginListenerCapacity_Highest"));
        PLUGIN_LISTENER_CAPACITY_MONITOR = calcCapacity(config.getInt("pluginListenerCapacity_Monitor"));
        PLUGIN_SCHEDULER_CAPACITY = calcCapacity(config.getInt("pluginSchedulerCapacity"));
    }

    protected static int calcCapacity(int i) {
        var initial = i / 0.75 - 1;
        var time = Math.log10(initial) / Math.log10(2);
        var closestTime = Math.ceil(time);
        var result = Math.pow(2, closestTime);
        return (int) result;
    }
}
