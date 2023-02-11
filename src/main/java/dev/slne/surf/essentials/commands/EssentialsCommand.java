package dev.slne.surf.essentials.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

public abstract class EssentialsCommand implements CommandExecutor, TabCompleter {
    public EssentialsCommand(PluginCommand command) {
        command.setExecutor(this);
        command.setTabCompleter(this);
    }
}
