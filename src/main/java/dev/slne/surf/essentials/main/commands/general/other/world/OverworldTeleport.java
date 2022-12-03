package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static dev.slne.surf.api.utils.message.SurfColors.*;

public class OverworldTeleport {//only to make other commands work properly
    public static void tp(Player player, String[] args) {
        if (args.length == 1){
            player.sendMessage("Du musst eine Welt angeben!");
            return;
        }
        if (Bukkit.getWorld(args[1]) == null){
            player.sendMessage("Die welt existiert nicht");
            return;
        }
        Bukkit.getWorld(args[1]).getPlayers().forEach(player1 -> {
            World world = Bukkit.getWorlds().get(0);
            double x = player1.getLocation().getX();
            double z = player1.getLocation().getZ();
            player1.teleportAsync(new Location(world, x, world.getHighestBlockYAt((int) x, (int) z), z, player1.getLocation().getYaw(), player1.getLocation().getPitch()));
            SurfApi.getUser(player1).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du wirst in die ", INFO))
                    .append(Component.text("overworld", TERTIARY))
                    .append(Component.text(" teleportiert, da die andere Welt nun schließt!", INFO))));
        });
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Alle Spieler aus dieser Welt wurden in die ", SUCCESS))
                .append(Component.text("overworld", TERTIARY))
                .append(Component.text(" teleportiert.", SUCCESS))));
    }
}
