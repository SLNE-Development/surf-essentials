package dev.slne.surf.essentials.main.commands.general.other.poll;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.slne.surf.essentials.main.utils.EssentialsUtil.sortedSuggestions;

public class PollManager extends EssentialsCommand {
    public PollManager(PluginCommand command) {
        super(command);
        command.setPermission("surf.essentials.commands.poll.create");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
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

            if (args[0].equalsIgnoreCase("create")){
                PollCreateCommand.create(player, args);
            }

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();//all completion are added to the list
        List<String> completions = new ArrayList<>();//the final completion list
        String currentarg = args[args.length - 1];//the current argument

        if (args.length > 1){
            if (args[0].equalsIgnoreCase("quick")){
                list.add("15");
                list.add("900");
                sortedSuggestions(list, currentarg, completions);
            }
        }
        return completions;
    }

}
