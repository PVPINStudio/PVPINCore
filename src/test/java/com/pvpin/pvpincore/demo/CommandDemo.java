package com.pvpin.pvpincore.demo;

import com.pvpin.pvpincore.api.PVPINCommand;
import com.pvpin.pvpincore.api.PVPINLogManager;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author William_Shi
 */
public class CommandDemo implements CommandExecutor {
    static {
        PVPINCommand.registerNewCmd("javatest", JavaPlugin.getProvidingPlugin(CommandDemo.class));
        Bukkit.getPluginCommand("javatest").setExecutor(new CommandDemo());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        System.out.println("Java Command Test");
        return true;
    }
}
