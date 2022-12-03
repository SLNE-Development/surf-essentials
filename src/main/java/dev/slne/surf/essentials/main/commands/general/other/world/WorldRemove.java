package dev.slne.surf.essentials.main.commands.general.other.world;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;

import static org.bukkit.Bukkit.getServer;

public class WorldRemove {
    public static void remove(Player player, String[] args){
        if (args.length == 1){//check if a world is given
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du musst eine Welt zum löschen angeben!", SurfColors.ERROR))));
            return;
        }

        File file = new File(getServer().getWorldContainer(), args[1]);//gets the directory form the arg

        if (!file.exists()) {//check if the world exist
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Welt ", SurfColors.INFO))
                    .append(Component.text(args[1], SurfColors.TERTIARY))
                    .append(Component.text(" existiert nicht!", SurfColors.INFO))));
            return;
        }
        //Verifies that the world is not a main world
        if (file.getName().equals(Bukkit.getWorlds().get(0).getName()) || file.getName().equals(Bukkit.getWorlds().get(1).getName()) || file.getName().equals(Bukkit.getWorlds().get(2).getName())){
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du kannst die Welt ", SurfColors.INFO))
                    .append(Component.text(args[1], SurfColors.TERTIARY))
                    .append(Component.text(" nicht löschen!", SurfColors.INFO))));
            return;
        }
        //converts all args into a String
        String allArgs = "world ";
        for (int i = 0; i < args.length; i++) allArgs += args[i] + " ";

        if (!allArgs.contains("--confirm")) {//check if the string ends with the flag
            String finalAllArgs = allArgs;
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Bist du sicher, dass du die Welt löschen willst?", SurfColors.INFO))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix())
                    .append(Component.text("Um die Welt zu löschen füge bitte deinem Command ", SurfColors.INFO))
                    .append(Component.text("--confirm", SurfColors.RED))
                    .append(Component.text(" hinzu oder klicke ", SurfColors.INFO))
                    .append(Component.text("HIER", SurfColors.TERTIARY)
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke zum bestätigen", SurfColors.DARK_GRAY)))
                            .clickEvent(ClickEvent.suggestCommand("/%s --confirm".formatted(finalAllArgs))))));
            return;
        }

        World world = Bukkit.getWorld(args[1]);//the world to unload

        if (world != null) {//check if the world is loaded
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Entlade Welt ", SurfColors.INFO))
                    .append(Component.text(args[1], SurfColors.TERTIARY))
                    .append(Component.text("...", SurfColors.INFO))));

            if (world.getPlayers().size() != 0) {
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
            Bukkit.unloadWorld(world, false);//unloads the world
        }
        //message to the sender
        SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Lösche Welt Dateien von ", SurfColors.INFO))
                .append(Component.text(args[1], SurfColors.TERTIARY))
                .append(Component.text("...", SurfColors.INFO))));

        SurfEssentials.getInstance().getServer().getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), () -> {
            //delete world files
            try {
                FileUtils.deleteDirectory(file);
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Die Welt wurde erfolgreich gelöscht!", SurfColors.SUCCESS))));

            }catch (Exception e){
                e.printStackTrace();
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Da hat etwas nicht geklappt!", SurfColors.ERROR))
                        .append(Component.newline())
                        .append(SurfApi.getPrefix())
                        .append(Component.text("Bitte gucke in die Konsole für mehr Informationen!", SurfColors.INFO))));
            }
        });
    }
}
