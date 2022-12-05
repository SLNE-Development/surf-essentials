package dev.slne.surf.essentials.main.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.slne.surf.api.utils.message.SurfColors.ERROR;
import static dev.slne.surf.api.utils.message.SurfColors.SUCCESS;

public class SuicideCommand extends EssentialsCommand {
    public SuicideCommand(PluginCommand command) {
        super(command);
        command.setUsage("/suicide");
        command.setDescription("Makes you commit suicide.");
        command.setPermission("surf.essentials.commands.suicide");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            player.setHealth(0);
            SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du hast Selbstmord begangen!", SUCCESS))));
            return true;

        }else if (sender instanceof ConsoleCommandSender){
            SurfEssentials.getInstance().getComponentLogger().info(Component.text("You must be a player to execute this command!", ERROR));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
