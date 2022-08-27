package dev.slne.surf.essentials.commands.cheat;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HealCommand extends EssentialsCommand {
    public HealCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Check if sender is player
        if (!(sender instanceof Player)) {sender.sendMessage(Component.text("You must be a player to execute this command!").color(SurfColors.ERROR)); return true;}
        //Player declaration
        Player player = (Player) sender;
        //if player provided to many args
        if (args.length > 0){player.sendMessage(SurfApi.getPrefix().append(Component.text("Du darfst keine Argumente angeben!" )).color(SurfColors.ERROR)); return true;}
        //Effect to Heal player
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(maxHealth);
        //TODO: Cool Sound

        SurfApi.getUser(player).thenAcceptAsync((user) -> {
            if (user == null) return;
            user.playSound(Sound.BLOCK_AMETHYST_CLUSTER_PLACE, 0.25f, 1);
        });

        //Success Message
        player.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Du wurdest geheilt!", SurfColors.SUCCESS)));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
