/*
 * The MIT License
 * Copyright © ${year} PVPINStudio
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
package com.pvpin.pvpincore.modules.command;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.PVPINScriptManager;
import com.pvpin.pvpincore.modules.js.JSPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
public class SubCmdJS {

    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "抱歉，该指令暂时仅供后台使用。");
        }
        if (args.length == 1) {
            sender.sendMessage("========PVPINCore-JS指令========");
            sender.sendMessage("/pvpincore js reload -- 重载所有JavaScript插件");
            sender.sendMessage("/pvpincore js enable <文件名> -- 加载指定.js文件");
            sender.sendMessage("/pvpincore js disable <插件名> -- 卸载指定插件");
            sender.sendMessage("================================");
            return true;
        }
        switch (args[1].toLowerCase()) {
            case "rl":
                // Command aliases.
            case "reload": {
                if (args.length != 2) {
                    sender.sendMessage("========PVPINCore-JS指令========");
                    sender.sendMessage("/pvpincore js reload -- 重载所有JavaScript插件");
                    sender.sendMessage("/pvpincore js enable <文件名> -- 加载指定.js文件");
                    sender.sendMessage("/pvpincore js disable <插件名> -- 卸载指定插件");
                    sender.sendMessage("================================");
                    return true;
                }
                PVPINCore.getScriptManagerInstance().onReload();
                return true;
            }
            case "enable": {
                if (args.length != 3) {
                    sender.sendMessage("========PVPINCore-JS指令========");
                    sender.sendMessage("/pvpincore js reload -- 重载所有JavaScript插件");
                    sender.sendMessage("/pvpincore js enable <文件名> -- 加载指定.js文件");
                    sender.sendMessage("/pvpincore js disable <插件名> -- 卸载指定插件");
                    sender.sendMessage("================================");
                    return true;
                }
                PVPINCore.getScriptManagerInstance().enablePlugin(
                        new File(
                                new File(PVPINCore.getCoreInstance().getDataFolder(), "js"),
                                args[2]
                        )
                );
                return true;
            }
            case "disable": {
                if (args.length != 3) {
                    sender.sendMessage("========PVPINCore-JS指令========");
                    sender.sendMessage("/pvpincore js reload -- 重载所有JavaScript插件");
                    sender.sendMessage("/pvpincore js enable <文件名> -- 加载指定.js文件");
                    sender.sendMessage("/pvpincore js disable <插件名> -- 卸载指定插件");
                    sender.sendMessage("================================");
                    return true;
                }
                PVPINCore.getScriptManagerInstance().disablePlugin(args[2]);
                return true;
            }
            default: {
                sender.sendMessage("========PVPINCore-JS指令========");
                sender.sendMessage("/pvpincore js reload -- 重载所有JavaScript插件");
                sender.sendMessage("/pvpincore js enable <文件名> -- 加载指定.js文件");
                sender.sendMessage("/pvpincore js disable <插件名> -- 卸载指定插件");
                sender.sendMessage("================================");
                return true;
            }
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> second = List.of("reload", "enable", "disable");
        if (args.length == 2) {
            return second.stream().filter(
                    action -> action.startsWith(args[1].toLowerCase())
            ).collect(Collectors.toList());
        }
        switch (args[1]) {
            case "enable": {
                List<String> result = new ArrayList();
                Arrays.stream(Objects.requireNonNull(
                        new File(PVPINCore.getCoreInstance().getDataFolder(), "js")
                                .listFiles(((dir, name) -> name.endsWith(".js")))
                )).forEach(file -> {
                    result.add(file.getName());
                });
                return result.stream().filter(
                        action -> action.toLowerCase().startsWith(args[2].toLowerCase())
                ).collect(Collectors.toList());
            }
            case "disable": {
                List<String> result = new ArrayList();
                PVPINCore.getScriptManagerInstance().getAllPlugins().forEach(action -> {
                    result.add(((JSPlugin) action).getName());
                });
                return result.stream().filter(
                        action -> action.toLowerCase().startsWith(args[2].toLowerCase())
                ).collect(Collectors.toList());
            }
            default: {
                return null;
            }
        }
    }
}
