package dev.slne.surf.essentials.main.commands.tp;

import aetherial.spigot.plugin.annotation.command.CommandTag;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandTag(name = "tpall",
        usage = "/tpall",
        desc = "Teleports all online players to you.",
        permission = "surf.command.tpall")
public class TeleportAll extends EssentialsCommand {
    public TeleportAll(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is player
        if (!(sender instanceof Player player)){sender.sendMessage(SurfApi.getPrefix()
                .append(Component.text("You must be a player to execute this command!", SurfColors.ERROR))); return true;}
        //Check args length
        if (args.length > 1){player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du darfst keine Argumente angeben!", SurfColors.ERROR))); return true;}
        //Ask for confirm
        if (args.length == 0){
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Bist du sicher, dass du alle online Spieler zu dir Teleportieren willst?", SurfColors.WARNING))
                    .append(Component.newline())
                    .append(SurfApi.getPrefix()
                    .append(Component.text("Ja (Klicke)", SurfColors.GREEN))
                            .hoverEvent(Component.text("Klicke um zu Bestätigen!", SurfColors.INFO))
                            .clickEvent(ClickEvent.runCommand("/tpall confirm")))
                    .append(Component.text(" | ", SurfColors.GRAY))
                    .append(Component.text("Nein (Klicke)", SurfColors.RED)
                            .hoverEvent(Component.text("Klicke zum Abbrechen!", SurfColors.INFO))
                            .clickEvent(ClickEvent.runCommand("/tpall cancel"))));
            return true;

            //check if player confirm command
        } else if (args[0].equalsIgnoreCase("confirm")){
            //Teleports all online players to sender
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.teleport(player.getLocation());
            }
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Es wurden alle online Spieler zu dir Teleportiert!", SurfColors.SUCCESS)));
            return true;

        //check if player cancels command
        }else if (args[0].equalsIgnoreCase("cancel")){
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Der Vorgang wurde abgebrochen!", SurfColors.ERROR)));
            return true;

        //If arg 0 is not "confirm" or "cancel"
        }else {
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du darfst keine Argumente angeben!", SurfColors.ERROR)));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
