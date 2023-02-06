package dev.slne.surf.essentials.main.commands.tp;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import dev.slne.surf.essentials.main.utils.brigadier.BrigadierCommand;
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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@PermissionTag(name = Permissions.TELEPORT_TOP_SELF_PERMISSION, desc = "Allows you to teleport you to the highest block at your location")
@PermissionTag(name = Permissions.TELEPORT_TOP_OTHER_PERMISSION, desc = "Allows you to teleport others to the highest block at their location")
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

        if (player != null){
            location = player.getBukkitEntity().getLocation();
            location.setY(player.getLevel().getWorld().getHighestBlockYAt((int) location.x(), (int) location.z()));

            player.teleportTo(location.x(), location.getY() + 1, location.z());
        }else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

            if (!offlinePlayer.hasPlayedBefore()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            }

            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAccept(user -> user.sendMessage(Component.text("Teleportiere Spieler...", SurfColors.INFO)));
            }

            try {
                location = EssentialsUtil.getLocation(gameProfile);
                EssentialsUtil.setLocation(uuid, new Location(location.getWorld(), location.x(), location.getWorld().getHighestBlockYAt((int) location.x(), (int) location.y()) + 1, location.z()));
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return 0;
            }
        }

        if (source.isPlayer()){
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

            if (player != null){
                builder.append(player.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY));
            }else {
                builder.append(Component.text(gameProfile.getName(), SurfColors.TERTIARY));
            }

            builder.append(Component.text(" wurde zum höchsten Block teleportiert.", SurfColors.SUCCESS)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                            EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), SurfColors.INFO))));

            EssentialsUtil.sendSuccess(source, builder.build());
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Teleported " + gameProfile.getName() +
                    " to the highest block. (%s %s %s)".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                            EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ()))), false);
        }

        return 1;
    }
}
