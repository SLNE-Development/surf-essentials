package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PollManager extends EssentialsCommand {
    public PollManager(PluginCommand command) {
        super(command);
        command.setPermission("surf.essentials.commands.poll.make");
        command.permissionMessage(SurfEssentials.NO_PERMISSION());
        command.setDescription("create a poll");
        command.setUsage("/poll create|delete|quick|analyze");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length == 0){
                if (!PollUtil.isPoll()) {
                    SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Aktuell läuft keine Abstimmung!", SurfColors.INFO))));
                } else {
                    SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Aktuell läuft eine Abstimmung!", SurfColors.INFO))));
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("quick")){

            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

}
