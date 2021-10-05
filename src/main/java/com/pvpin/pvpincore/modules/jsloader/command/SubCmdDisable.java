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
import com.pvpin.pvpincore.modules.js.AbstractJSPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
public class SubCmdDisable {
    protected static void sendHelp(CommandSender sender) {
        sender.sendMessage("========PVPINCore-DISABLE指令========");
        sender.sendMessage("/pvpincore disable <插件名> -- 卸载指定名称的JavaScript插件");
        sender.sendMessage("================================");
    }

    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "抱歉，该指令暂时仅供后台使用。");
            return true;
        }
        if (args.length == 1) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "disable": {
                if (args.length != 2) {
                    sendHelp(sender);
                    return true;
                }
                PVPINCore.getScriptManagerInstance().disablePlugin(
                        args[1]
                );
                return true;
            }
            default: {
                sendHelp(sender);
                return true;
            }
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> second = List.of("disable");
        if (args.length == 1) {
            return second.stream().filter(
                    action -> action.startsWith(args[0].toLowerCase())
            ).collect(Collectors.toList());
        }
        switch (args[0].toLowerCase()) {
            case "disable": {
                List<String> result = new ArrayList();
                PVPINCore.getScriptManagerInstance().getAllPlugins().forEach(action -> {
                    result.add(((AbstractJSPlugin) action).getName());
                });
                return result.stream().filter(
                        action -> action.toLowerCase().startsWith(args[1].toLowerCase())
                ).collect(Collectors.toList());
            }
            default: {
                return List.of();
            }
        }
    }
}
