package dev.slne.surf.essentials.commands.cheat.gui;

import dev.slne.surf.essentials.utils.permission.Permissions;

public class StonecutterCommand extends GuiCommand {
    StonecutterCommand() {
        super("stonecutter", "Die Steinsäge", Permissions.STONECUTTER_SELF_PERMISSION, Permissions.STONECUTTER_OTHER_PERMISSION);
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openStonecutter(player.getLocation(), true);
    }
}
