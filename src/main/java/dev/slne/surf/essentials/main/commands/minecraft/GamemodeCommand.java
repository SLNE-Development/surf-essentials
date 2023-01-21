package dev.slne.surf.essentials.main.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Collections;

@PermissionTag(name = Permissions.GAMEMODE_PERMISSION, desc = "This is the permission for the 'gamemode' command")
public class GamemodeCommand {

    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("gamemode", GamemodeCommand::literal).setDescription("Change the gamemode of players")
                .setUsage("/gamemode <gamemode> [<players>]");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GAMEMODE_PERMISSION));

        literal.then(Commands.argument("gamemode", GameModeArgument.gameMode())
                .executes(context -> setMode(Collections.singleton(context.getSource().getPlayerOrException()),
                        GameModeArgument.getGameMode(context, "gamemode")))
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> setMode(EntityArgument.getPlayers(context, "players"),
                                GameModeArgument.getGameMode(context, "gamemode")))));
    }

    private static int setMode(Collection<ServerPlayer> targets, GameType gameMode) {
        int successfulChanges = 0;

        if (targets.size() == 1){
            targets.iterator().next().setGameMode(gameMode);
            SurfApi.getUser(targets.iterator().next().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(net.kyori.adventure.text.Component.text("Dein Gamemode wurde auf ", SurfColors.SUCCESS))
                    .append(PaperAdventure.asAdventure(gameMode.getLongDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.SUCCESS))));
            logSingleChange(targets.iterator().next(), gameMode);

        }else{
            for (ServerPlayer target : targets) {
                target.setGameMode(gameMode);
                SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Dein Gamemode wurde auf ", SurfColors.SUCCESS))
                        .append(PaperAdventure.asAdventure(gameMode.getLongDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.SUCCESS))));
                ++successfulChanges;
            }
            logMultiChange(gameMode, successfulChanges);
        }
        return successfulChanges;
    }

    private static void logSingleChange(ServerPlayer player, GameType gameType){
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(player.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" hat in den Gamemode ", SurfColors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getShortDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gewechselt!", SurfColors.INFO)), "surf.announce.gamemode");

        SurfEssentials.logger().info(net.kyori.adventure.text.Component.text("Set ", SurfColors.INFO)
                .append(player.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("´s game mode to ", SurfColors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getLongDisplayName()).colorIfAbsent(SurfColors.TERTIARY)));
    }

    private static void logMultiChange(GameType gameType, int amount){
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(net.kyori.adventure.text.Component.text("Der Gamemode von ", SurfColors.INFO))
                        .append(net.kyori.adventure.text.Component.text(amount, SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" Spielern wurde auf ", SurfColors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getShortDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", SurfColors.INFO)), "surf.announce.gamemode");

        SurfEssentials.logger().info(net.kyori.adventure.text.Component.text("Set the game mode for ", SurfColors.INFO)
                .append(net.kyori.adventure.text.Component.text(amount, SurfColors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" players to ", SurfColors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getLongDisplayName()).colorIfAbsent(SurfColors.TERTIARY)));
    }
}
