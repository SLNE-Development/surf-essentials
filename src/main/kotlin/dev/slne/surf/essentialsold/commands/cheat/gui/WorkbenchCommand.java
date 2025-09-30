package dev.slne.surf.essentialsold.commands.cheat.gui;

import dev.slne.surf.essentialsold.utils.permission.Permissions;

public class WorkbenchCommand extends GuiCommand {
    WorkbenchCommand() {
        super("workbench", "Die Werkbank", Permissions.WORKBENCH_SELF_PERMISSION, Permissions.WORKBENCH_OTHER_PERMISSION, "crafting", "craftingtable");
    }

    @Override
    public void open(org.bukkit.entity.Player player) {
        player.openWorkbench(player.getLocation(), true);
    }
}
