package dev.slne.surf.essentials.main.commands.tp;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.commands.tp.messages.Messages_DE;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.nbt.NBTLocationReader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class TeleportCommand extends EssentialsCommand {
    public TeleportCommand(PluginCommand command) {
        super(command);
    }

    Messages_DE messages_DE = new Messages_DE();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            switch (args.length) {
                case 0:
                    /* Too few arguments for teleport */
                    messages_DE.somethingWentWrongAsync_DE(player, "Du musst Argumente angeben!");
                    break;
                case 1:
                    /* teleport sender to offline player */
                    if (Bukkit.getPlayerExact(args[0]) == null) {
                        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[0]);
                        teleportToOfflineAsync(player, offlineTarget);
                        break;
                    }
                    /* get online target */
                    Player playerArg0 = Bukkit.getPlayerExact(args[0]);
                    /* teleport sender to target */
                    player.teleportAsync(Objects.requireNonNull(playerArg0).getLocation());
                    messages_DE.teleportMessage_DE(player, playerArg0);
                    break;

                case 2:
                    /* all targets are offline */
                    if ((Bukkit.getPlayerExact(args[0]) == null) && (Bukkit.getPlayerExact(args[1]) == null)) {
                        messages_DE.somethingWentWrongAsync_DE(player, "Es muss mindestens ein Spieler online sein!");
                        break;
                    }
                    /* second target is offline */
                    if ((Bukkit.getPlayerExact(args[1]) == null) && (Bukkit.getPlayerExact(args[0]) != null)) {
                        /* teleport first target to second target */
                        teleportToOfflineAsync(Bukkit.getPlayerExact(args[0]), Bukkit.getOfflinePlayer(args[1]));
                        break;
                    }
                    /* second target */
                    Player playerArg1 = Bukkit.getPlayerExact(args[1]);
                    /* first target is offline */
                    if (Bukkit.getPlayerExact(args[0]) == null) {
                        /* teleport first target to second target */
                        teleportOfflineToTargetAsync(player, Bukkit.getOfflinePlayer(args[0]), Objects.requireNonNull(playerArg1).getLocation());
                        break;
                    }
                    /* second target */
                    playerArg0 = Bukkit.getPlayerExact(args[0]);
                    /* normal online teleportation */
                    teleportAsync(player, playerArg0, Objects.requireNonNull(playerArg1).getLocation());
                    break;

                case 3:
                    try {
                        /* new Location from sender input */
                        final double x = args[0].startsWith("~") ? player.getLocation().getX() + (args[0].length() > 1 ? Double.parseDouble(args[0].substring(1)) : 0) : Double.parseDouble(args[0]);
                        final double y = args[1].startsWith("~") ? player.getLocation().getY() + (args[1].length() > 1 ? Double.parseDouble(args[1].substring(1)) : 0) : Double.parseDouble(args[1]);
                        final double z = args[2].startsWith("~") ? player.getLocation().getZ() + (args[2].length() > 1 ? Double.parseDouble(args[2].substring(1)) : 0) : Double.parseDouble(args[2]);
                        Location location = new Location(player.getWorld(), x, y, z);

                        /* teleport player to location */
                        teleportAsync(player, location);
                        break;
                    } catch (Exception e) {
                        /* no location given */
                        if (!EssentialsUtil.isInt(args[0]) || !EssentialsUtil.isInt(args[1]) || !EssentialsUtil.isInt(args[2])) {
                            /* message to sender */
                            messages_DE.somethingWentWrongAsync_DE(player, "Du musst einen gültigen Ort angeben!");
                            break;
                        }
                        Location location = new Location(player.getWorld(), Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
                        teleportAsync(player, location);
                    }
                    break;

                default:
                    /* normal online teleport */
                    if (Bukkit.getPlayerExact(args[0]) != null) {
                        /* get online player */
                        playerArg0 = Bukkit.getPlayerExact(args[0]);
                        try {
                            final double x2 = args[0].startsWith("~") ? playerArg0.getLocation().getX() + (args[1].length() > 1 ? Double.parseDouble(args[1].substring(1)) : 0) : Double.parseDouble(args[1]);
                            final double y2 = args[1].startsWith("~") ? playerArg0.getLocation().getY() + (args[2].length() > 1 ? Double.parseDouble(args[2].substring(1)) : 0) : Double.parseDouble(args[2]);
                            final double z2 = args[2].startsWith("~") ? playerArg0.getLocation().getZ() + (args[3].length() > 1 ? Double.parseDouble(args[3].substring(1)) : 0) : Double.parseDouble(args[3]);

                            Location location2 = new Location(playerArg0.getWorld(), x2, y2, z2);
                            teleportAsync(player, playerArg0, location2);
                            break;
                        } catch (Exception e) {
                            /* wrong input */
                            if (EssentialsUtil.isInt(args[0]) || !EssentialsUtil.isInt(args[1]) || !EssentialsUtil.isInt(args[2]) || !EssentialsUtil.isInt(args[3])) {
                                /* message to sender */
                                messages_DE.somethingWentWrongAsync_DE(player, "/tp <Spieler> <x> <y> <z>");
                                break;
                            }
                            /* new Location from sender input */
                            Location location = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));

                            teleportAsync(player, playerArg0, location);
                        }
                    }

                    /* wrong input */
                    if (EssentialsUtil.isInt(args[0]) || !EssentialsUtil.isInt(args[1]) || !EssentialsUtil.isInt(args[2]) || !EssentialsUtil.isInt(args[3])) {
                        /* message to sender */
                        messages_DE.somethingWentWrongAsync_DE(player, "/tp <Spieler> <x> <y> <z>");
                        break;
                    }
                    /* new Location from sender input */
                    Location location = new Location(player.getWorld(), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                    /* teleport offline target to location */
                    teleportOfflineToTargetAsync(player, Bukkit.getOfflinePlayer(args[0]), location);
                    break;
            }

            //TODO: make the command executable via the console
        }else if (sender instanceof ConsoleCommandSender){

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }


    /**
     *
     * Teleport online player to location.
     *
     * @param sender  the sender
     * @param location  the location
     */
    public void teleportAsync(Player sender, Location location){

        Objects.requireNonNull(sender).teleportAsync(Objects.requireNonNull(location));
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(sender.teamDisplayName()
                        .decorate(TextDecoration.ITALIC))
                .append(Component.text(" "))
                .append(sender.teamDisplayName())
                .append(Component.text(" hat sich zu ", SurfColors.GRAY))
                .append(Component.text(" %s %s %s".formatted(Math.round(location.getX()), Math.round(location.getY()), Math.round(location.getZ())), SurfColors.GOLD))
                .append(Component.text(" teleportiert!", SurfColors.GRAY)), "surf.teleport.announce");
    }

    /**
     *
     * Teleports the player to offline player.
     *
     * @param player  the player
     * @param target  the target player
     */
    public void teleportToOfflineAsync(Player player, OfflinePlayer target){
        NBTLocationReader.getLocationAsync(target.getName(), new NBTLocationReader.NBTCallback<Location>() {
            /**
             *
             * On success player teleported to offline player location.
             *
             * @param data  the location of offline Player
             */
            @Override
            public void onSuccess(Location data) {
                Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> {
                    player.sendMessage(SurfApi.getPrefix()
                            .append(EssentialsUtil.gradientify("Teleportiere...", "#F9655B", "#EE821A")));

                    player.teleportAsync(data);

                    Bukkit.broadcast(SurfApi.getPrefix()
                            .append(player.teamDisplayName()
                                    .decorate(TextDecoration.ITALIC))
                            .append(Component.text(" hat sich zu ", SurfColors.GRAY))
                            .append(Component.text(Objects.requireNonNull(target.getName())))
                            .append(Component.text(" teleportiert!", SurfColors.GRAY)), "surf.teleport.announce");
                });
            }
            @Override
            /**
             *
             * If the teleport fails, a message is sent to the player.
             *
             * @param message  the message
             */
            public void onFail(Component message) {
                player.sendMessage(message);
            }
        }, Messages_DE.playerWasNeverOnlineMessage_DE(target), Messages_DE.failedOfflineTeleportMessage_DE());
    }

    /**
     *
     * Teleports an offline player to a location.
     *
     * @param sender  the sender
     * @param target  the offline player
     * @param location  the location
     */
    public void teleportOfflineToTargetAsync(Player sender, OfflinePlayer target, Location location){
        NBTLocationReader.setLocationAsync(target.getName(), location, new NBTLocationReader.NBTCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> {
                    messages_DE.teleportTargetPlayerMessage_DE(sender, target, location);
                });
            }

            /**
             *
             * If the teleport fails, a message is sent to the player.
             *
             * @param message  the message
             */
            @Override
            public void onFail(Component message) {
                sender.sendMessage(message);
            }
        }, Messages_DE.failedOfflineTeleportMessage_DE());
    }

    /**
     *
     * Teleports a player to a location async.
     *
     * @param sender  the sender
     * @param targetPlayer  the target player
     * @param location  the location
     */
    public void teleportAsync(Player sender, Player targetPlayer, Location location){
        Objects.requireNonNull(targetPlayer).teleportAsync(Objects.requireNonNull(location));
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(sender.teamDisplayName()
                        .decorate(TextDecoration.ITALIC))
                .append(Component.text(" "))
                .append(targetPlayer.teamDisplayName())
                .append(Component.text(" wurde zu ", SurfColors.GRAY))
                .append(Component.text(" %s %s %s".formatted(Math.round(location.getX()), Math.round(location.getY()), Math.round(location.getZ())), SurfColors.GOLD))
                .append(Component.text(" teleportiert!", SurfColors.GRAY)), "surf.teleport.announce");
    }
}
