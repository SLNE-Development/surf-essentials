package dev.slne.surf.essentials.commands.tp;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class TeleportToTopCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"tptop"};
    }

    @Override
    public String usage() {
        return "tptop [<player]";
    }

    @Override
    public String description() {
        return "Teleport players to the highest block at their position";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TELEPORT_TOP_SELF_PERMISSION));
        literal.executes(context -> tptop(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException().getGameProfile())));

        literal.then(Commands.argument("player", GameProfileArgument.gameProfile())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TELEPORT_TOP_OTHER_PERMISSION))
                .executes(context -> tptop(context.getSource(), GameProfileArgument.getGameProfiles(context, "player"))));
    }

    private int tptop(CommandSourceStack source, Collection<GameProfile> gameProfiles) throws CommandSyntaxException{
        if (gameProfiles.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();

        GameProfile gameProfile = gameProfiles.iterator().next();
        UUID uuid = gameProfile.getId();
        ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
        Location location;

        if (player != null) {
            location = player.getBukkitEntity().getLocation();
            location.setY((location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ())) + 1);
            location.setX(location.getBlockX() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);


            PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                    location, PlayerTeleportEvent.TeleportCause.COMMAND);
            if (playerTeleportEvent.isCancelled()) return 0;
            EssentialsUtil.callEvent(playerTeleportEvent);

            player.teleportTo(location.x(), location.getY() + 1, location.z());
        }else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (!offlinePlayer.hasPlayedBefore()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            }

            if (source.isPlayer()) {
                EssentialsUtil.sendInfo(source, "Teleportiere Spieler...");
            }

            try {
                location = EssentialsUtil.getLocation(gameProfile);
                Objects.requireNonNull(location).setY((location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ())) + 1);
                location.setX(location.getBlockX() + 0.5);
                location.setZ(location.getBlockZ() + 0.5);
                EssentialsUtil.setLocation(uuid, location);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return 0;
            }
        }

        if (source.isPlayer()){
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

            if (player != null){
                builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
            }else {
                builder.append(Component.text(gameProfile.getName(), Colors.TERTIARY));
            }

            builder.append(Component.text(" wurde zum höchsten Block teleportiert.", Colors.SUCCESS)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                            EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), Colors.INFO))));

            EssentialsUtil.sendSuccess(source, builder.build());
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Teleported " + gameProfile.getName() +
                    " to the highest block. (%s %s %s)".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                            EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ()))), false);
        }

        return 1;
    }
}
