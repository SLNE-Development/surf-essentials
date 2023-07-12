package dev.slne.surf.essentials.commands.cheat.gui;

import dev.slne.surf.essentials.utils.permission.Permissions;

public class SmithingTableCommand extends GuiCommand {
    SmithingTableCommand() {
        super("smithingtable", "Der Schmiedetisch", Permissions.SMITHING_TABLE_SELF_PERMISSION, Permissions.SMITHING_TABLE_OTHER_PERMISSION, "smithing");
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openSmithingTable(player.getLocation(), true);
    }
}
