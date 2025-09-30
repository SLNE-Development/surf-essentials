package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InfinityCommand extends EssentialsCommand {
    @Getter
    private static final List<Player> playersInInfinity = new ArrayList<>();

    public InfinityCommand() {
        super("infinity", "infinity", "Never run out of the Item you are currently holding");

        withPermission(Permissions.INFINITY_PERMISSION);

        executesNative((sender, args) -> {
            val player = getPlayerOrException(sender);
            if (playersInInfinity.contains(player)){
                playersInInfinity.remove(player);
                EssentialsUtil.sendSuccess(player, "Du hast nun nicht mehr unbegrenzt Items");

                return 1;
            }

            playersInInfinity.add(player);
            EssentialsUtil.sendSuccess(player, "Du hast nun unbegrenzt Items");

            return 1;
        });
    }
}
