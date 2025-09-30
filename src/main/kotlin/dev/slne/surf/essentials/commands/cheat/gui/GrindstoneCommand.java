package dev.slne.surf.essentials.commands.cheat.gui;

import dev.slne.surf.essentials.utils.permission.Permissions;

public class GrindstoneCommand extends GuiCommand {
    GrindstoneCommand() {
        super("grindstone", "Der Schleifstein", Permissions.GRINDSTONE_SELF_PERMISSION, Permissions.GRINDSTONE_OTHER_PERMISSION);
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openGrindstone(player.getLocation(), true);
    }
}
