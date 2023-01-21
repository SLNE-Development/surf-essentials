package dev.slne.surf.essentials.main.commands.general.sign;

import aetherial.spigot.plugin.annotation.command.CommandTag;
import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@CommandTag(name = "signEdit",
        usage = "/signedit <true|false>",
        desc = "Toggles whether signs can be edited by right-clicking or not.",
        aliases = {"editsign", "signtoggle", "togglesign"},
        permission = "sign.toggle")
@PermissionTag(name = Permissions.TOGGLE_SIGN_PERMISSION, desc = "This is the permission for the 'signedit' command") // only when the command get´s migrated to brigadier
public class SignToggleCommand extends EssentialsCommand {
    public SignToggleCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            editSigns = !editSigns;
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();

            if (editSigns) {
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Spieler können nun Schilder per Rechtsklick bearbeiten!", SurfColors.SUCCESS)));

                logger.info(Component.text("Players can now edit signs by right clicking!", SurfColors.SUCCESS));
                return true;
            } else {
                player.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Spieler können nun nicht mehr Schilder per Rechtsklick bearbeiten!", SurfColors.SUCCESS)));
                logger.info(Component.text("Players can now no longer edit signs by right-clicking!", SurfColors.SUCCESS));
                return true;
            }


        }else if (sender instanceof ConsoleCommandSender) {
            ComponentLogger logger = SurfEssentials.getInstance().getComponentLogger();
            editSigns = !editSigns;
            if (editSigns) {
                logger.info(Component.text("Players can now edit signs by right clicking!", SurfColors.SUCCESS));
                return true;
            } else {
                logger.info(Component.text("Players can now no longer edit signs by right-clicking!", SurfColors.SUCCESS));
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //Returns a list of true or false
        List<String> list = new ArrayList<>();
        if (!(args.length == 1)) {
            return list;
        }
        list.add("true");
        list.add("false");
        List<String> booleans = new ArrayList<>();
        String currentarg = args[args.length - 1];
        for (String s : list) {
            if (s.startsWith(currentarg)) {
                booleans.add(s);
            }
        }
        return booleans;
    }

    private static boolean editSigns;

    public static boolean canEditSigns(){
        return editSigns;
    }
}
