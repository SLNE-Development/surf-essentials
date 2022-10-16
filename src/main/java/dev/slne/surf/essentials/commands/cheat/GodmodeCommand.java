package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GodmodeCommand extends EssentialsCommand {
    public GodmodeCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)){sender.sendMessage(SurfApi.getPrefix().append(Component.text("You must be a player to execute this command!", SurfColors.ERROR))); return true;}
        Player player = (Player)sender;
        if (args.length > 0) {player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst keine Argumente angeben!", SurfColors.ERROR))); return true;}


        boolean isInvulnerable = player.isInvulnerable();
        player.setInvulnerable(!isInvulnerable);

        if (isInvulnerable){
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Godmode verlassen!", SurfColors.SUCCESS)));
        }else {
            player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast den Godmode betreten!", SurfColors.SUCCESS)));

        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
