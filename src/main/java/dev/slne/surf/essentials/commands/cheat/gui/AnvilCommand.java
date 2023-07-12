package dev.slne.surf.essentials.commands.cheat.gui;

import dev.slne.surf.essentials.utils.permission.Permissions;
import org.bukkit.entity.Player;

public class AnvilCommand extends GuiCommand {
    AnvilCommand() {
        super("anvil", "Der Amboss", Permissions.ANVIL_SELF_PERMISSION, Permissions.ANVIL_OTHER_PERMISSION);
    }

    @Override
    public void open(Player player) {
        player.openAnvil(player.getLocation(), true);
    }
}
