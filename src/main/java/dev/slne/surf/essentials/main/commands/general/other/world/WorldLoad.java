package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;

import static org.bukkit.Bukkit.getServer;

public class WorldLoad {
    public static void load(Player player, String[] args) {
        if (args.length == 1) {//no world given
            SurfEssentials.somethingWentWrongAsync_DE(player, "Du musst eine Welt angeben!");
            return;
        }
        File file = new File(getServer().getWorldContainer(), args[1]);//gets the directory from the arg
        if (!file.exists()) {//check if the directory exist
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Welt ", SurfColors.INFO))
                    .append(Component.text(args[1], SurfColors.TERTIARY))
                    .append(Component.text(" existiert nicht!", SurfColors.INFO))));
            return;
        }
        if (Bukkit.getWorld(args[1]) != null){//check if the world is already loaded
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Welt ", SurfColors.INFO)
                            .append(Component.text(args[1], SurfColors.TERTIARY))
                            .append(Component.text(" ist bereits geladen!", SurfColors.INFO))
                            .append(Component.newline()
                                    .append(SurfApi.getPrefix())
                                    .append(Component.text("Klicke um dich zu teleportieren", SurfColors.DEBUG)
                                            .decorate(TextDecoration.ITALIC))))
                    .hoverEvent(HoverEvent.showText(Component.text("Klicke zum teleportieren", SurfColors.DARK_GRAY)))
                    .clickEvent(ClickEvent.suggestCommand("/world join %s".formatted(args[1])))));
            return;
        }
        //sends message to the sender
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Lade Welt ", SurfColors.INFO))
                .append(Component.text(args[1], SurfColors.TERTIARY))
                .append(Component.text("...", SurfColors.INFO))));

        Bukkit.createWorld(new WorldCreator(args[1]));//loads the world
        //success to the sender
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Die Welt ", SurfColors.INFO)
                        .append(Component.text(args[1], SurfColors.TERTIARY)
                                .append(Component.text(" wurde erfolgreich geladen!", SurfColors.INFO))))
                .hoverEvent(HoverEvent.showText(Component.text("Klicke um dich zu teleportieren", SurfColors.DARK_GRAY)))
                .clickEvent(ClickEvent.suggestCommand("/world change %s".formatted(args[1])))));
    }
}
