package dev.slne.surf.essentials.main.commands.general;

import aetherial.spigot.plugin.annotation.command.CommandTag;
import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@CommandTag(name = "msg",
        usage = "/msg <player> <msg-message>",
        desc = "Sends a private message to the specified player",
        aliases = {"tell", "whisper"},
        permission = "surf.command.msg")
@PermissionTag(name = Permissions.MSG_PERMISSION, desc = "This is the permission for the 'msg' command") // only when the command gets converted to brigadier
public class MsgCommand extends EssentialsCommand {
    public MsgCommand(PluginCommand command) {
        super(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0){
                SurfApi.getUser(player).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst einen Spieler angeben!", SurfColors.ERROR)));
                });
                return true;
            }else if (Bukkit.getPlayerExact(args[0]) == null) {
                SurfApi.getUser(player).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst einen gültigen Spieler angeben!", SurfColors.ERROR)));
                });
                return true;

            }else  if (args.length == 1){
                SurfApi.getUser(player).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Du musst eine Nachricht angeben!", SurfColors.ERROR)));
                });
                return true;
            }else {
                Player targetPlayer = Bukkit.getPlayerExact(args[0]);

                String message = "";
                for (int i = 1; i < args.length; i++){
                    String arg = args[i] + " ";
                    message = message + arg;
                }
                String finalMessage = message;

                SurfApi.getUser(targetPlayer).thenAcceptAsync((surfUser) -> {
                    if (surfUser == null) return;
                    surfUser.sendMessage(Component.text(">> ", SurfColors.DARK_GRAY)
                            .append(Component.text("PM ", SurfColors.RED))
                            .append(Component.text("| ", SurfColors.DARK_GRAY))
                            .append(player.displayName())
                            .append(Component.text(" -> ", SurfColors.DARK_GRAY))
                            .append(Component.text("Dir ", TextColor.fromHexString("#F39C12")))
                            .append(Component.text(">> ", SurfColors.DARK_GRAY))
                            .append(Component.text(finalMessage, SurfColors.GRAY))
                            .clickEvent(ClickEvent.suggestCommand(String.format("/msg %s", player.getName())))
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke um zu Antworten", SurfColors.GRAY))));

                    surfUser.playSound(Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1);
                });

                SurfApi.getUser(player).thenAcceptAsync((user) -> {
                    if (user == null) return;
                    user.sendMessage(Component.text(">> ", SurfColors.DARK_GRAY)
                            .append(Component.text("PM ", SurfColors.RED))
                            .append(Component.text("| ", SurfColors.DARK_GRAY))
                            .append(Component.text("Du", TextColor.fromHexString("#F39C12")))
                            .append(Component.text(" -> ", SurfColors.DARK_GRAY))
                            .append(targetPlayer.displayName())
                            .append(Component.text(" >> ", SurfColors.DARK_GRAY))
                            .append(Component.text(finalMessage, SurfColors.GRAY))
                            .clickEvent(ClickEvent.suggestCommand(String.format("/msg %s", player.getName())))
                            .hoverEvent(HoverEvent.showText(Component.text("Klicke um zu Antworten", SurfColors.GRAY))));
                });

                ConsoleCommandSender console = SurfEssentials.getInstance().getServer().getConsoleSender();

                Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        console.sendMessage(Component.text(">>", SurfColors.DARK_GRAY)
                                .append(Component.text(" PM spy", SurfColors.RED))
                                .append(Component.text(" | ", SurfColors.DARK_GRAY))
                                .append(player.displayName())
                                .append(Component.text(" -> ", SurfColors.DARK_GRAY))
                                .append(targetPlayer.displayName())
                                .append(Component.text(" >> ", SurfColors.DARK_GRAY))
                                .append(Component.text(finalMessage, SurfColors.GRAY)));
                    }
                });
            }
        }else if (sender instanceof ConsoleCommandSender console){
            console.sendMessage(Component.text(">>", SurfColors.GRAY)
                    .append(Component.text(" PM spy", SurfColors.RED))
                    .append(Component.text(" | ", SurfColors.GRAY))
                    .append(Component.text("You must be a player to execute this command!", SurfColors.ERROR)));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
