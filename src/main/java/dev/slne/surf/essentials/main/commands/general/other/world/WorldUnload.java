package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;

import static org.bukkit.Bukkit.getServer;

public class WorldUnload {
    public static void unload(Player player, String[] args) {
        if (args.length == 1) {//no world specified
            EssentialsUtil.somethingWentWrongAsync_DE(player, "Du musst eine Welt angeben!");
            return;
        }
        File file = new File(getServer().getWorldContainer(), args[1]);//gets the world form the arg

        if (!file.exists()) {//check if the world exists
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Welt ", SurfColors.INFO))
                    .append(Component.text(args[1], SurfColors.TERTIARY))
                    .append(Component.text(" existiert nicht!", SurfColors.INFO))));
            return;
        }
        if (Bukkit.getWorld(args[1]) == null) {//check if the world is loaded
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Welt ", SurfColors.INFO)
                            .append(Component.text(args[1], SurfColors.TERTIARY))
                            .append(Component.text(" ist nicht geladen!", SurfColors.INFO))
                            .append(Component.newline()
                                    .append(SurfApi.getPrefix())
                                    .append(Component.text("Klicke um sie zu laden", SurfColors.DEBUG)
                                            .decorate(TextDecoration.ITALIC))))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicke zum laden", SurfColors.DARK_GRAY)))
                    .clickEvent(ClickEvent.suggestCommand("/world load %s".formatted(args[1])))));
            return;
        }

        World world = Bukkit.getWorld(args[1]);//the world from the arg
        //Verifies that the world is not a main world
        if (world == Bukkit.getWorlds().get(0) || world == Bukkit.getWorlds().get(1) || world == Bukkit.getWorlds().get(2)){
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du kannst diese Welt nicht entladen!", SurfColors.WARNING))));
            return;
        }
        //message to the sender
        SurfApi.getUser(player).thenAccept(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Entlade Welt ", SurfColors.INFO))
                .append(Component.text(args[1], SurfColors.TERTIARY))
                .append(Component.text("...", SurfColors.INFO))));

        if (world.getPlayers().size() != 0){//check that no players are in the world
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("In der Welt befinden sich noch Spieler!", SurfColors.ERROR))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.text("Bitte entferne sie oder klicke ", SurfColors.INFO))
                    .append(Component.text("Hier", SurfColors.TERTIARY)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke um alle Spieler in dieser Welt in die overworld zu teleportieren", SurfColors.DARK_GRAY)))
                            .clickEvent(ClickEvent.runCommand("/world allPlayers %s".formatted(world.getName()))))));
            return;
        }

        Bukkit.unloadWorld(world, true);//unload the world
        //success message to the sender
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Die Welt ", SurfColors.INFO)
                        .append(Component.text(args[1], SurfColors.TERTIARY)
                                .append(Component.text(" wurde erfolgreich entladen!", SurfColors.INFO))))));
    }

}
