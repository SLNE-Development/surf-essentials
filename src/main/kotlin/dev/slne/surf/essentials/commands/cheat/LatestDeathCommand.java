package dev.slne.surf.essentials.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.Objects;

public class LatestDeathCommand extends EssentialsCommand {
    public LatestDeathCommand() {
        super("latestdeath", "death [<player>]", "Get the latest death of a player", "death", "deathlocation");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.DEATH_LOCATION_PERMISSION_SELF, Permissions.DEATH_LOCATION_PERMISSION_OTHER));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> getLocation(getPlayerOrException(sender), getPlayerOrException(sender)));
        then(offlinePlayerArgument("player")
                .withPermission(Permissions.DEATH_LOCATION_PERMISSION_OTHER)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> getLocation(sender.getCallee(), Objects.requireNonNull(args.getUnchecked("player")))));
    }


    private int getLocation(CommandSender source, OfflinePlayer offlinePlayer) throws WrapperCommandSyntaxException {
        val player = offlinePlayer.getPlayer();
        val dataFile = EssentialsUtil.getPlayerFile(offlinePlayer.getUniqueId());

        if (dataFile.isEmpty() && player == null) throw Exceptions.NO_PLAYERS_FOUND;

        final int x, y, z;
        final String dimension;

        if (player != null) {
            final Location deathLocation = player.getLastDeathLocation();
            if (deathLocation == null) throw Exceptions.ERROR_PLAYER_HAS_NOT_DIED_YET.create(offlinePlayer);

            x = deathLocation.getBlockX();
            y = deathLocation.getBlockY();
            z = deathLocation.getBlockZ();
            dimension = deathLocation.getWorld().getKey().asString();
        } else {
            try {
                final CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.get().toPath(), BinaryTagIO.Compression.GZIP);
                final CompoundBinaryTag deathTag = rawTag.getCompound("LastDeathLocation");
                final int[] pos = deathTag.getIntArray("pos");
                final String dimensionTag = deathTag.getString("dimension");

                if (pos.length == 0 || dimensionTag.isEmpty())
                    throw Exceptions.ERROR_PLAYER_HAS_NOT_DIED_YET.create(offlinePlayer);

                x = pos[0];
                y = pos[1];
                z = pos[2];
                dimension = dimensionTag;
            } catch (IOException e) {
                throw Exceptions.ERROR_READING_FILE;
            }
        }

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getOfflineDisplayName(offlinePlayer)
                .append(Component.text(" ist bei ", Colors.INFO))
                .append(EssentialsUtil.formatLocationWithoutSpacer(x, y, z)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard("%s %s %s".formatted(x, y, z))))
                .append(Component.text(" in ", Colors.INFO))
                .append(Component.text(dimension, Colors.VARIABLE_KEY))
                .append(Component.text(" gestorben", Colors.INFO)));

        return 1;
    }
}
