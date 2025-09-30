package dev.slne.surf.essentialsold.commands.cheat.gui;

import dev.slne.surf.essentialsold.utils.permission.Permissions;

public class CartographyTableCommand extends GuiCommand {
    CartographyTableCommand() {
        super("cartographytable", "Der Karten Tisch", Permissions.CARTOGRAPHY_TABLE_SELF_PERMISSION, Permissions.CARTOGRAPHY_TABLE_OTHER_PERMISSION);
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openCartographyTable(player.getLocation(), true);
    }
}
