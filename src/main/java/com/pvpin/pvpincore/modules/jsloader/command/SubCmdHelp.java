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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @author William_Shi
 */
public class SubCmdHelp {
    public static boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        net.md_5.bungee.api.chat.BaseComponent component = new net.md_5.bungee.api.chat.TextComponent();
        net.md_5.bungee.api.chat.BaseComponent eval = new net.md_5.bungee.api.chat.TextComponent("☞ /pvpincore eval\n");
        eval.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/pvpincore eval"));
        net.md_5.bungee.api.chat.BaseComponent loadfile = new net.md_5.bungee.api.chat.TextComponent("☞ /pvpincore loadfile\n");
        loadfile.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/pvpincore loadfile"));
        net.md_5.bungee.api.chat.BaseComponent disable = new net.md_5.bungee.api.chat.TextComponent("☞ /pvpincore disable\n");
        disable.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/pvpincore disable"));
        net.md_5.bungee.api.chat.BaseComponent reset = new net.md_5.bungee.api.chat.TextComponent("☞ /pvpincore reset"); // No \n at the end of the commands.
        reset.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/pvpincore reset"));
        component.setExtra(List.of(
                eval, loadfile, disable, reset
        ));
        sender.sendMessage("========PVPINCore-JS========");
        sender.spigot().sendMessage(component);
        sender.sendMessage("================================");
        return true;
    }

    public static List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return List.of();
    }
}
