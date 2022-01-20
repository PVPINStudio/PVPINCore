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
import com.pvpin.pvpincore.modules.i18n.I18N;
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
public class SubCmdJSLoadFile {
    protected static void sendHelp(CommandSender sender) {
        sender.sendMessage("========PVPINCore-LOADFILE========");
        sender.sendMessage(I18N.translateByDefault("cmd.load"));
        sender.sendMessage("================================");
    }

    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + I18N.translateByDefault("cmd.deny"));
            return true;
        }
        if (args.length == 1) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "loadfile": {
                if (args.length != 2) {
                    sendHelp(sender);
                    return true;
                }
                Bukkit.getScheduler().runTaskAsynchronously(PVPINCore.getCoreInstance(), () -> {
                    PVPINCore.getScriptManagerInstance().enablePlugin(
                            new File(
                                    new File(PVPINCore.getCoreInstance().getDataFolder(), "js"),
                                    args[1]
                            )
                    );
                });
                return true;
            }
            default: {
                sendHelp(sender);
                return true;
            }
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> second = List.of("loadfile");
        if (args.length == 1) {
            return second.stream().filter(
                    action -> action.startsWith(args[0].toLowerCase())
            ).collect(Collectors.toList());
        }
        switch (args[0].toLowerCase()) {
            case "loadfile": {
                List<String> result = new ArrayList();
                Arrays.stream(Objects.requireNonNull(
                        new File(PVPINCore.getCoreInstance().getDataFolder(), "js")
                                .listFiles(((dir, name) -> name.endsWith(".js")))
                )).forEach(file -> {
                    result.add(file.getName());
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
