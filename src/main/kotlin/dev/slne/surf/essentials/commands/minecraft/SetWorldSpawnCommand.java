package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SetWorldSpawnCommand extends EssentialsCommand {
    public SetWorldSpawnCommand() {
        super("setworldspawn", "setworldspawn [<location>]", "Sets the world spawn to the given location");

        withPermission(Permissions.SET_WORLD_SPAWN_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> setWorldSpawn(
                sender.getCallee(),
                sender.getLocation(),
                0.0F
        ));
        then(locationArgument("location", LocationType.BLOCK_POSITION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setWorldSpawn(
                        sender.getCallee(),
                        Objects.requireNonNull(args.getUnchecked("location")),
                        0.0F
                ))
                .then(angleArgument("angle")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> setWorldSpawn(
                                sender.getCallee(),
                                Objects.requireNonNull(args.getUnchecked("location")),
                                args.getUnchecked("angle")
                        ))
                )
        );
    }

    private int setWorldSpawn(@NotNull CommandSender source, @NotNull Location location, Float angle) {
        val world = location.getWorld();

        location.setYaw(angle);
        world.setSpawnLocation(location);

        EssentialsUtil.sendSuccess(source, Component.text("Der Welt spawn wurde bei ", Colors.SUCCESS)
                .append(EssentialsUtil.formatLocationWithoutSpacer(location))
                .append(Component.text(" mit einem Winkel von ", Colors.SUCCESS))
                .append(Component.text(angle, Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt.", Colors.SUCCESS)));
        return 1;
    }
}
