package dev.slne.surf.essentials.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand extends EssentialsCommand {
    public InfoCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is player
        if (!(sender instanceof Player player)){sender.sendMessage(SurfApi.getPrefix()
                .append(Component.text("You must be a player to execute this command!", SurfColors.ERROR)));
            return true;
        }
        //Check args length
        if (args.length > 1){player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du darfst nur einen Spieler angeben!", SurfColors.ERROR)));
            return true;
        }
        //Check if sender provided player
        if (args.length == 0){player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du musst einen Spieler angeben!", SurfColors.ERROR)));
        return true;
        }
        //check if arg[0] is valid Player
        if (Bukkit.getPlayerExact(args[0]) == null){
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Der Spieler existiert nicht!", SurfColors.ERROR)));
            return true;
        }
        //Target Player
        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        //Info message
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Informationen über: ", SurfColors.INFO)).append(targetPlayer.displayName().color(SurfColors.GOLD))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                //Displays the UUID of the target player
                .append(Component.text("UUID: ", SurfColors.AQUA)
                        .append(Component.text(targetPlayer.getUniqueId().toString(), SurfColors.GOLD)
                                .clickEvent(ClickEvent.copyToClipboard(targetPlayer.getUniqueId().toString()))
                        .hoverEvent(Component.text("Klicke um die UUID zu kopieren!", SurfColors.INFO))))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                //Displays the playtime of the target player
                .append(Component.text("Spielzeit: ", SurfColors.AQUA)
                        .append(Component.text("Spielzeit Einfügen", SurfColors.GOLD)))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                //Generates a link to the NameMC profile of the target player
                .append(Component.text("Name MC: ", SurfColors.AQUA)
                        .append(Component.text("Klick mich", SurfColors.GREEN)
                                .clickEvent(ClickEvent.openUrl("https://de.namemc.com/profile/%s".formatted(targetPlayer.getUniqueId().toString())))))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                //Shows the health and saturation of the target player
                .append(Component.text("Health: ", SurfColors.AQUA)
                        .append(Component.text("%s".formatted(Math.round(targetPlayer.getHealth()*10/10)), SurfColors.GOLD))
                        .append(Component.text("/%s".formatted(targetPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())).color(SurfColors.GOLD)))
                .append(Component.newline())
                .append(SurfApi.getPrefix())
                .append(Component.text("Food: ", SurfColors.AQUA)
                        .append(Component.text("%s".formatted(targetPlayer.getFoodLevel()), SurfColors.GOLD))
                        .append(Component.text("/20", SurfColors.GOLD))));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Returns a list of online players
        List<String> list = new ArrayList<>();
        if (!(args.length == 1)){return list;}
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
        List<String> onlinePlayers = new ArrayList<>();
        String currentarg = args[args.length-1];
        for (String s : list){
            if (s.startsWith(currentarg)){
                onlinePlayers.add(s);
            }
        }
        return onlinePlayers;
    }
}
