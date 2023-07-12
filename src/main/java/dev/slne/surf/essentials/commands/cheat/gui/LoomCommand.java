package dev.slne.surf.essentials.commands.cheat.gui;

import dev.slne.surf.essentials.utils.permission.Permissions;

public class LoomCommand extends GuiCommand {
    LoomCommand() {
        super("loom", "Der Webstuhl", Permissions.LOOM_SELF_PERMISSION, Permissions.LOOM_OTHER_PERMISSION);
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openLoom(player.getLocation(), true);
    }
}
