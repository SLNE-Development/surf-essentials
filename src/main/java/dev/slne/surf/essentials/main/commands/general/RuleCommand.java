package dev.slne.surf.essentials.main.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RuleCommand extends EssentialsCommand {
    public RuleCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //If sender is instance of Player
        if (sender instanceof Player player) {
            //Check if player provided too many args
            if (args.length > 1) {
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du darfst nur einen Spieler angeben!", SurfColors.ERROR)));
                return true;
            }
            //If the player has not specify any arguments
            if (args.length == 0) {
                //Send rules to the player
                sendrules(player);
                return true;
            //Check if player specify arguments
            }else if (args.length == 1){
                //if player wants to send rules to all online players
                if (args[0].equalsIgnoreCase("online")){
                    //Ask for confirmation
                    player.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Bist du sicher, dass du die ", SurfColors.SUCCESS)
                                    .append(Component.text("Regeln", SurfColors.GOLD))
                                    .append(Component.text(" an ", SurfColors.SUCCESS))
                                    .append(Component.text("alle online Spieler", SurfColors.GOLD))
                                    .append(Component.text(" schicken möchtest?", SurfColors.SUCCESS)))
                            .append(Component.newline())
                            //Confirm
                            .append(SurfApi.getPrefix()
                                    .append(Component.text("Klicke hier um zu Bestätigen", SurfColors.DARK_GREEN)
                                            .clickEvent(ClickEvent.runCommand("/rule confirm")))));
                    return true;
                //confirm command to send rules to all online player
                }else if (args[0].equalsIgnoreCase("confirm")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        sendrules(onlinePlayer);
                    }
                    //Success message
                    player.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Es wurden erfolgreich die Reglen und Informationen an alle online Spieler gesendet!", SurfColors.SUCCESS)));
                    return true;
                }
                //Check if arg[0] is valid online Player
                if (Bukkit.getPlayerExact(args[0]) != null){
                    Player targetPlayer = Bukkit.getPlayerExact(args[0]);
                    //Send rules to target player
                    sendrules(targetPlayer);
                    //Success message
                    player.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Es wurden erfolgreich die Reglen und Informationen an ", SurfColors.SUCCESS))
                            .append(targetPlayer.displayName())
                            .append(Component.text(" gesendet!", SurfColors.SUCCESS)));
                    return true;
                }
                //If specified argument isn´t a valid online player
                else {
                    player.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Der Benutzer '", SurfColors.ERROR)
                                    .append(Component.text(args[0])
                                            .color(SurfColors.TERTIARY)))
                            .append(Component.text("' konnte nicht gefunden werden!", SurfColors.ERROR)));
                    return true;
                }
            }

        //If sender is a console
        }else if (sender instanceof ConsoleCommandSender console) {
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
            //If too many args return
            if (args.length > 1){
                console.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("You may only specify one player!")));
                return true;
            //Send rules to all online players
            }else if (args.length == 0){
                //Ask for confirmation
                console.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Are you sure you want to send the rules to all online players?", SurfColors.GOLD))
                        .append(Component.newline()
                                .append(Component.text("                 ", SurfColors.GRAY))
                                .append(SurfApi.getPrefix())
                                .append(Component.text("In order to confirm write: ", SurfColors.GREEN)
                                        .append(Component.text("rule CONFIRM", SurfColors.DARK_AQUA)))));
            //Confirm command
            }else if (args[0].equalsIgnoreCase("CONFIRM")){
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    sendrules(onlinePlayer);
                }
                //Success message
                console.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("The rules have been successfully sent to all online players!", SurfColors.SUCCESS)));
                return true;
            //If target player is null
            }else if (Bukkit.getPlayerExact(args[0]) == null){
                console.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("The specified player does not exist!", SurfColors.ERROR)));
            }else if (Bukkit.getPlayerExact(args[0]) != null){
                Player targetPlayer = Bukkit.getPlayerExact(args[0]);
                //Send rules to target player
                sendrules(targetPlayer);
                //Success message
                console.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("The rules were successfully sent to ", SurfColors.SUCCESS))
                        .append(Component.text(args[0])
                                .color(SurfColors.GOLD)));
                return true;
            }
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Returns a list of online players
        List<String> list = new ArrayList<>();
        if (!(args.length == 1)){return list;}
        list.add("online");
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
        List<String> tabComplete = new ArrayList<>();
        String currentarg = args[args.length-1];
        for (String s : list){
            if (s.startsWith(currentarg)){
                tabComplete.add(s);
            }
        }
        return tabComplete;
    }

    //The server rules
    public void sendrules(Player player){
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Alle ", SurfColors.SUCCESS))
                .append(Component.text("Regeln", SurfColors.GOLD))
                .append(Component.text(" und", SurfColors.SUCCESS))
                .append(Component.text(" Informationen", SurfColors.GOLD))
                .append(Component.text(" findest du ", SurfColors.SUCCESS))
                .append(Component.text("Hier", SurfColors.RED)
                        .decorate(TextDecoration.BOLD)
                        .hoverEvent(Component.text("Klicke um zu der Website zu kommen", SurfColors.GRAY))
                        .clickEvent(ClickEvent.openUrl("https://castcrafter.de/subserver"))));
    }

}
