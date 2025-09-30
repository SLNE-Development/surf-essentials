package dev.slne.surf.essentials.commands.tp;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.SafeLocationFinder;
import dev.slne.surf.essentials.utils.brigadier.BrigadierMessage;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class RandomTeleportCommand extends EssentialsCommand { // TODO test

    public RandomTeleportCommand() {
        super("wild", "wild [<maxRadius>]", "Teleports you to a random location", "rtp", "tpr", "teleportrandom");

        withPermission(Permissions.TELEPORT_RANDOM_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportRandom(getPlayerOrException(sender), 5000))
                .then(integerArgument("maxRadius", 2, 20000)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportRandom(getPlayerOrException(sender), args.getUnchecked("maxRadius"))));
    }

    private int teleportRandom(Player player, Integer maxRadius) {
        EssentialsUtil.sendInfo(player, "Suche Ort...");

        val start = System.currentTimeMillis();
        val locationFinder = new SafeLocationFinder(player.getLocation(), maxRadius);

        locationFinder.findSafeLocationAsync().thenAcceptAsync(optionalLocation -> {
            val end = System.currentTimeMillis();

            if (optionalLocation.isEmpty()) {
                player.sendMessage(new BrigadierMessage(Exceptions.NO_SAFE_LOCATION_FOUND.getRawMessage()).asComponent());
                return;
            }

            EssentialsUtil.sendInfo(player, "Ort gefunden (%s ms)! Teleportiere...".formatted(end - start));

            val startTeleport = System.currentTimeMillis();
            val location = optionalLocation.get();

            location.add(0.5, 0, 0.5);


            EssentialsUtil.teleportLazy(player, location).thenAccept(__ -> {
                val endTeleport = System.currentTimeMillis();

                EssentialsUtil.sendSuccess(player, Component.text("Du wurdest zu ", Colors.SUCCESS)
                        .append(EssentialsUtil.formatLocationWithoutSpacer(location))
                        .append(Component.text(" teleportiert! (%s ms)".formatted(endTeleport - startTeleport), Colors.SUCCESS)));
            });
        });

        return 1;
    }
}
