package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

import static net.kyori.adventure.nbt.BinaryTagIO.Compression.GZIP;

public class GamemodeCommand {

    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("gamemode", GamemodeCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GAMEMODE_CREATIVE_SELF_PERMISSION) ||
                sourceStack.hasPermission(2, Permissions.GAMEMODE_SPECTATOR_SELF_PERMISSION) ||
                sourceStack.hasPermission(2, Permissions.GAMEMODE_SURVIVAL_SELF_PERMISSION) ||
                sourceStack.hasPermission(2, Permissions.GAMEMODE_ADVENTURE_SELF_PERMISSION));


        registerGameModes(literal, GameType.SURVIVAL, Permissions.GAMEMODE_SURVIVAL_SELF_PERMISSION, Permissions.GAMEMODE_SURVIVAL_OTHER_PERMISSION,
                Permissions.GAMEMODE_SURVIVAL_OTHER_OFFLINE_PERMISSION);

        registerGameModes(literal, GameType.CREATIVE, Permissions.GAMEMODE_CREATIVE_SELF_PERMISSION, Permissions.GAMEMODE_CREATIVE_OTHER_PERMISSION,
                Permissions.GAMEMODE_CREATIVE_OTHER_OFFLINE_PERMISSION);

        registerGameModes(literal, GameType.SPECTATOR, Permissions.GAMEMODE_SPECTATOR_SELF_PERMISSION, Permissions.GAMEMODE_SPECTATOR_OTHER_PERMISSION,
                Permissions.GAMEMODE_SPECTATOR_OTHER_OFFLINE_PERMISSION);

        registerGameModes(literal, GameType.ADVENTURE, Permissions.GAMEMODE_ADVENTURE_SELF_PERMISSION, Permissions.GAMEMODE_ADVENTURE_OTHER_PERMISSION,
                Permissions.GAMEMODE_ADVENTURE_OTHER_OFFLINE_PERMISSION);
    }

    private static int setMode(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, GameType gameMode) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        if (targets.size() == 1) {
            targets.iterator().next().setGameMode(gameMode);
            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Dein Gamemode wurde auf ", Colors.SUCCESS))
                    .append(PaperAdventure.asAdventure(gameMode.getLongDisplayName()).colorIfAbsent(Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));
            logSingleChange(targets.iterator().next(), gameMode);

        } else {
            for (ServerPlayer target : targets) {
                target.setGameMode(gameMode);
                EssentialsUtil.sendSuccess(target, (net.kyori.adventure.text.Component.text("Dein Gamemode wurde auf ", Colors.SUCCESS))
                        .append(PaperAdventure.asAdventure(gameMode.getLongDisplayName()).colorIfAbsent(Colors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));
                ++successfulChanges;
            }
            logMultiChange(gameMode, successfulChanges);
        }
        return successfulChanges;
    }

    private static int setOfflineMode(CommandSourceStack source, GameType gameType, Collection<GameProfile> profiles) throws CommandSyntaxException {
        if (profiles.size() > 1) throw EntityArgument.ERROR_NOT_SINGLE_PLAYER.create();
        GameProfile gameProfile = profiles.iterator().next();
        UUID targetUUID = gameProfile.getId();

        if (source.getServer().getPlayerList().getPlayer(targetUUID) != null){
            setMode(source, Collections.singleton(source.getServer().getPlayerList().getPlayer(targetUUID)), gameType);
            return 1;
        }

        File playerData = EssentialsUtil.getPlayerFile(targetUUID);

        if (playerData == null) throw EntityArgument.NO_PLAYERS_FOUND.create();

        try {
            setModeInFile(gameType, playerData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, Component.text("Der Gamemode von ", Colors.SUCCESS)
                    .append(Component.text(gameProfile.getName(), Colors.TERTIARY))
                    .append(Component.text(" wurde auf ", Colors.SUCCESS))
                    .append(PaperAdventure.asAdventure(gameType.getLongDisplayName()).colorIfAbsent(Colors.TERTIARY))
                    .append(Component.text(" gesetzt!", Colors.SUCCESS)));
        } else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Set ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(gameProfile.getName())
                    .withStyle(ChatFormatting.GOLD)
                    .append("´s gamemode to ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(gameType.getLongDisplayName())
                    .append("!")
                    .withStyle(ChatFormatting.GRAY), false);
        }

        return 1;
    }

    private static void logSingleChange(ServerPlayer player, GameType gameType) {
        for (ServerPlayer serverPlayer : Objects.requireNonNull(player.getServer()).getPlayerList().getPlayers()) {
            if (serverPlayer.getUUID() == player.getUUID()) continue;
            if (!serverPlayer.getBukkitEntity().hasPermission("surf.announce.gamemode")) continue;
            serverPlayer.sendSystemMessage(PaperAdventure.asVanilla(EssentialsUtil.getPrefix()
                    .append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" hat in den Gamemode ", Colors.INFO))
                    .append(PaperAdventure.asAdventure(gameType.getShortDisplayName()).colorIfAbsent(Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" gewechselt!", Colors.INFO))));
        }

        SurfEssentials.logger().info(net.kyori.adventure.text.Component.text("Set ", Colors.INFO)
                .append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("´s game mode to ", Colors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getLongDisplayName()).colorIfAbsent(Colors.TERTIARY)));
    }

    private static void logMultiChange(GameType gameType, int amount) {
        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(net.kyori.adventure.text.Component.text("Der Gamemode von ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(amount, Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" Spielern wurde auf ", Colors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getShortDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.INFO)), "surf.announce.gamemode");

        SurfEssentials.logger().info(net.kyori.adventure.text.Component.text("Set the game mode for ", Colors.INFO)
                .append(net.kyori.adventure.text.Component.text(amount, Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" players to ", Colors.INFO))
                .append(PaperAdventure.asAdventure(gameType.getLongDisplayName()).colorIfAbsent(Colors.TERTIARY)));
    }

    private static void registerGameModes(LiteralArgumentBuilder<CommandSourceStack> literal, GameType gameType,
                                          String permissionSelf, String permissionOthers, String permissionOthersOffline) {

        literal.then(Commands.literal(gameType.getName().toLowerCase())
                .requires(sourceStack -> sourceStack.hasPermission(2, permissionSelf))
                .executes(context -> setMode(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException()), gameType))
                .then(Commands.argument("players", EntityArgument.players())
                        .requires(sourceStack -> sourceStack.hasPermission(2, permissionOthers))
                        .executes(context -> setMode(context.getSource(), EntityArgument.getPlayers(context, "players"), gameType)))

                .then(Commands.literal("offline")
                        .requires(sourceStack -> sourceStack.hasPermission(2, permissionOthersOffline))
                        .then(Commands.argument("offlinePlayer", GameProfileArgument.gameProfile())
                                .executes(context -> setOfflineMode(context.getSource(), gameType, GameProfileArgument.getGameProfiles(context, "offlinePlayer"))))));
    }

    private static void setModeInFile(GameType gameType, File dataFile) throws IOException{
        CompoundBinaryTag rawTag = BinaryTagIO.unlimitedReader().read(dataFile.toPath(), GZIP);
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder().put(rawTag);

        builder.put("playerGameType", IntBinaryTag.of(gameType.getId()));

        BinaryTagIO.writer().write(builder.build(), dataFile.toPath(), GZIP);
    }
}
