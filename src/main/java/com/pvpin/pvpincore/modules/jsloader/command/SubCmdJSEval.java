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

import com.cryptomorin.xseries.XMaterial;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.PVPINScriptManager;
import com.pvpin.pvpincore.modules.config.ConfigManager;
import com.pvpin.pvpincore.modules.i18n.I18N;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author William_Shi
 */
public class SubCmdJSEval {
    protected static void sendHelp(CommandSender sender) {
        sender.sendMessage("========PVPINCore-EVAL========");
        sender.sendMessage(I18N.translate(((Player) sender).getLocale(), "cmd.eval.1"));
        sender.sendMessage(I18N.translate(((Player) sender).getLocale(), "cmd.eval.2"));
        sender.sendMessage("================================");
    }

    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + I18N.translate(((Player) sender).getLocale(), "cmd.deny.pl"));
            sendHelp(sender);
            return true;
        }
        if (!ConfigManager.TRUSTED_PLAYERS.contains(((Player) sender).getUniqueId())) {
            return false;
        }
        if (args.length == 1) {
            ItemStack stack = ((Player) sender).getInventory().getItemInMainHand();
            if (stack == null) {
                sendHelp(sender);
                return true;
            }
            if ((stack.getType() != XMaterial.WRITTEN_BOOK.parseMaterial()) && (stack.getType() != XMaterial.WRITABLE_BOOK.parseMaterial())) {
                sendHelp(sender);
                return true;
            }
            BookMeta meta = (BookMeta) stack.getItemMeta();
            Bukkit.getScheduler().runTaskAsynchronously(PVPINCore.getCoreInstance(), () -> {
                PVPINCore.getScriptManagerInstance().enablePlugin(((Player) sender).getUniqueId(), meta);
            });
            return true;
        } else {
            StringBuilder sb = new StringBuilder();
            Arrays.stream(args).skip(1).forEach(str -> {
                sb.append(str);
                sb.append(" ");
            });
            String src = sb.toString();
            Bukkit.getScheduler().runTaskAsynchronously(PVPINCore.getCoreInstance(), () -> {
                PVPINCore.getScriptManagerInstance().enablePlugin(((Player) sender).getUniqueId(), src);
            });
            return true;
        }
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return List.of();
    }
}
