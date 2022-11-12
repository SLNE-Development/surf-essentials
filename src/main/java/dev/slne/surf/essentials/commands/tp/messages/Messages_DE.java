package dev.slne.surf.essentials.commands.tp.messages;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Messages_DE {
    SurfEssentials surf = SurfEssentials.getInstance();

    /**
     *
     * Sends a message to everyone with the given permission.
     *
     * @param player  the player
     * @param targetPlayer  the target player
     */
    public void teleportMessage_DE(Player player, Player targetPlayer){
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(player.teamDisplayName()
                        .decorate(TextDecoration.ITALIC))
                .append(Component.text(" hat sich zu ", SurfColors.GRAY))
                .append(targetPlayer.teamDisplayName())
                .append(Component.text(" teleportiert!", SurfColors.GRAY)), "surf.teleport.announce");
    }

    /**
     *
     * Sends a message to everyone with the given permission.
     *
     * @param player  the player
     * @param targetPlayer  the target player
     * @param location  the location
     */
    public void teleportTargetPlayerMessage_DE(Player player, OfflinePlayer targetPlayer, Location location){
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(player.teamDisplayName()
                        .decorate(TextDecoration.ITALIC))
                        .append(Component.text(" "))
                .append(Component.text(Objects.requireNonNull(targetPlayer.getName()), SurfColors.DARK_PURPLE))
                .append(Component.text(" wurde zu", SurfColors.GRAY))
                .append(Component.text(" %s %s %s".formatted(Math.round(location.getX()), Math.round(location.getY()), Math.round(location.getZ())), SurfColors.GOLD))
                .append(Component.text(" teleportiert!", SurfColors.GRAY)), "surf.teleport.announce");
    }

    /**
     *
     * Sends an error message to the sender.
     *
     * @param sender  the sender
     * @param error  the error
     */
    public void somethingWentWrongAsync_DE(Player sender, String error){
        SurfApi.getUser(sender).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(surf.gradientify("Es ist ein Fehler aufgetreten:", "#eb3349", "#f45c43"))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(surf.gradientify(error, "#EA98DA", "#5B6CF9"))));
    }

    /**
     *
     * a message that the player has never been on the server.
     *
     * @param player  the offline player
     */
    public static Component playerWasNeverOnlineMessage_DE(OfflinePlayer player){
        return SurfApi.getPrefix()
                .append(Component.text("Der Benutzer ", SurfColors.ERROR))
                .append(Component.text(player.getName(), SurfColors.DARK_PURPLE))
                .append(Component.text(" war noch nie auf diesem Server!", SurfColors.ERROR));
    }

    /**
     *
     * a message that the teleport has failed.
     *
     */
    public static Component failedOfflineTeleportMessage_DE(){
        return SurfApi.getPrefix()
                .append(Component.text("Der Teleport ist fehlgeschlagen!", SurfColors.ERROR));
    }
}
