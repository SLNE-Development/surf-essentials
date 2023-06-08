package dev.slne.surf.essentials.commands.tp;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class TeleportOffline {
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("argument.player.toomany"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("argument.entity.notfound.player"));

    public TeleportOffline() {
        SurfEssentials.registerPluginBrigadierCommand("tpoff", this::literal);
    }

    private void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.OFFLINE_TELEPORT_PERMISSION));

        literal.then(Commands.argument("player", GameProfileArgument.gameProfile())
                .executes(context -> teleportToPlayer(context, GameProfileArgument.getGameProfiles(context, "player")))
                .then(Commands.argument("location", Vec3Argument.vec3())
                        .executes(context -> teleportPlayerToLocation(context, GameProfileArgument.getGameProfiles(context, "player"),
                                Vec3Argument.getVec3(context, "location")))));
    }

    private int teleportToPlayer(CommandContext<CommandSourceStack> context, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
        if (gameProfiles.size() > 1) {
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(new StringReader(context.getInput()));
        }

        CommandSourceStack source = context.getSource();
        ServerPlayer player = context.getSource().getPlayerOrException();
        GameProfile profile = gameProfiles.iterator().next();
        UUID uuid = profile.getId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            throw NO_PLAYERS_FOUND.create();
        }

        if (offlinePlayer.isOnline()) {
            player.getBukkitEntity().teleport(Objects.requireNonNull(offlinePlayer.getPlayer()).getLocation());

        }else {

            EssentialsUtil.sendInfo(player, "Spieler daten laden...");

            try {
                player.getBukkitEntity().teleportAsync(EssentialsUtil.getLocation(profile), PlayerTeleportEvent.TeleportCause.COMMAND);
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        EssentialsUtil.sendSuccess(source, Component.text("Du hast dich zu ", Colors.SUCCESS)
                .append(Component.text(profile.getName(), Colors.TERTIARY))
                .append(Component.text(" teleportiert!", Colors.SUCCESS)));

        return 1;
    }

    private int teleportPlayerToLocation(CommandContext<CommandSourceStack> context, Collection<GameProfile> gameProfiles, Vec3 newLocation) throws CommandSyntaxException {
        if (gameProfiles.size() > 1) {
            throw ERROR_NOT_SINGLE_PLAYER.createWithContext(new StringReader(context.getInput()));
        }

        CommandSourceStack source = context.getSource();
        ServerPlayer player = context.getSource().getPlayerOrException();
        GameProfile profile = gameProfiles.iterator().next();
        UUID uuid = profile.getId();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);


        if (!offlinePlayer.hasPlayedBefore()) {
            throw NO_PLAYERS_FOUND.create();
        }

        if (offlinePlayer.isOnline()) {
            Objects.requireNonNull(offlinePlayer.getPlayer()).teleportAsync(new Location(offlinePlayer.getPlayer().getWorld(), newLocation.x(), newLocation.y(), newLocation.z()));

        } else {

            EssentialsUtil.sendInfo(player, "Teleportiere Spieler...");

            try(final var level = player.level()) {
                EssentialsUtil.setLocation(uuid, new Location(level.getWorld(), newLocation.x(), newLocation.y(), newLocation.z()));
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        double posX = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.x()));
        double posY = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.y()));
        double posZ = Double.parseDouble(new DecimalFormat("#.##").format(newLocation.z()));

        EssentialsUtil.sendSuccess(source, Component.text("Der Spieler ", Colors.SUCCESS)
                .append(Component.text(profile.getName(), Colors.TERTIARY))
                .append(Component.text(" wurde zu ", Colors.SUCCESS))
                .append(Component.text("%s %s %s".formatted(posX, posY, posZ), Colors.TERTIARY))
                .append(Component.text(" teleportiert!", Colors.SUCCESS)));

        return 1;
    }
}
