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
package com.pvpin.pvpincore.api;

import com.pvpin.pvpincore.impl.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This class is used to manage methods related to commands.
 *
 * @author William_Shi
 */
public class PVPINCommand {

    /**
     * This method is used to register a new command.<p>
     * After registration, you can access the command with Bukkit#getPluginCommand.<p>
     * Command Executor needs to be bound manually.
     *
     * @param cmd    name of the command
     * @param plugin instance of the java plugin, under whose namespace will the command be registered
     */
    public static void registerNewCmd(String cmd, JavaPlugin plugin) {
        CommandManager.registerNewCmd(cmd, plugin);
    }

}
