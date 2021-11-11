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
package com.pvpin.pvpincore.modules.jsloader.command;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.plugin.StringJSPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
public class SubCmdJSReset {
    protected static void sendHelp(CommandSender sender) {
        sender.sendMessage("========PVPINCore-RESET指令========");
        sender.sendMessage("/pvpincore reset <玩家名> -- 卸载指定玩家执行的所有JavaScript");
        sender.sendMessage("/pvpincore resetall -- 卸载所有JavaScript插件并重新加载所有.js文件");
        sender.sendMessage("================================");
    }

    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "抱歉，该指令暂时仅供后台使用。");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "resetall": {
                PVPINCore.getScriptManagerInstance().onReload();
                return true;
            }
            case "reset": {
                if (args.length == 1) {
                    sendHelp(sender);
                    return true;
                }
                OfflinePlayer pl = Bukkit.getOfflinePlayer(args[1]);
                if (pl.isOnline()) {
                    PVPINCore.getScriptManagerInstance().getAllPlugins().stream()
                            .filter(plugin ->
                                    plugin instanceof StringJSPlugin
                            ).filter(plugin ->
                                    ((StringJSPlugin) plugin).getPlayer() == pl.getUniqueId()
                            ).collect(Collectors.toList())
                            .forEach(plugin ->
                                    PVPINCore.getScriptManagerInstance().disablePlugin(plugin.getName())
                            );
                } else {
                    sendHelp(sender);
                }
                return true;
            }
            default: {
                sendHelp(sender);
                return true;
            }
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            return List.of("reset", "resetall");
        }
        switch (args[0].toLowerCase()) {
            case "resetall": {
                return List.of();
            }
            case "reset": {
                return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName)
                        .filter(
                                action -> action.startsWith(args[1].toLowerCase())
                        ).collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
