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
package com.pvpin.pvpincore.impl.command;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.js.plugin.AbstractJSPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.graalvm.polyglot.Value;

import java.util.List;

/**
 * This class is used to extend Command Executor for JavaScript plugins.
 *
 * @author William_Shi
 */
public class JSCommand implements TabExecutor {

    protected String cmdName;
    protected Value cmdCallback;
    protected Value tabCallback;
    protected AbstractJSPlugin plugin;

    protected JSCommand(String cmdName, Value cmdCallback, Value tabCallback) {
        this.cmdName = cmdName;
        this.cmdCallback = cmdCallback;
        this.tabCallback = tabCallback;
        this.plugin = PVPINCore.getScriptManagerInstance().getPluginByName(
                cmdCallback.getContext().getPolyglotBindings().getMember("name").asString()
        );
        plugin.isValid();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals(this.cmdName) && cmdCallback.canExecute()) {
            cmdCallback.execute(sender, cmd, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equals(this.cmdName) && tabCallback.canExecute()) {
            Value result = tabCallback.execute(sender, cmd, args);
            return result.as(List.class);
        }
        return null;
    }
}
