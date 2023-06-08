package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent.Cause;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DefaultQualifier(NotNull.class)
public class DefaultGamemodeCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"defaultgamemode"};
    }

    @Override
    public String usage() {
        return "/defaultgamemode [<gamemode>]";
    }

    @Override
    public String description() {
        return "Set the default gamemode for players";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.DEFAULT_GAMEMODE_PERMISSION));

        literal.executes(context -> getGameMode(context.getSource()));
        literal.then(Commands.argument("gamemode", GameModeArgument.gameMode())
                .executes(context -> setGameMode(context.getSource(), GameModeArgument.getGameMode(context, "gamemode"))));
    }

    private static int setGameMode(CommandSourceStack source, GameType newGameMode) {
        MinecraftServer server = EssentialsUtil.getMinecraftServer();
        int playersUpdated = 0;

        server.setDefaultGameType(newGameMode);
        @Nullable GameType forcedGameType = server.getForcedGameType();
        if (forcedGameType != null) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                PlayerGameModeChangeEvent event = player.setGameMode(forcedGameType, Cause.DEFAULT_GAMEMODE, net.kyori.adventure.text.Component.empty());
                if (event.isCancelled() && event.cancelMessage() != null) {
                    EssentialsUtil.sendSuccess(source, event.cancelMessage());
                    continue;
                }
                playersUpdated++;
            }
        }

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Der standard Spielmodus wurde auf ", Colors.SUCCESS)
                .append(net.kyori.adventure.text.Component.text(newGameMode.getLongDisplayName().getString(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));
        return playersUpdated;
    }

    private static int getGameMode(CommandSourceStack source) {
        GameType gameType = EssentialsUtil.getMinecraftServer().getDefaultGameType();

        EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Der standard Spielmodus ist ", Colors.INFO)
                .append(net.kyori.adventure.text.Component.text(gameType.getLongDisplayName().getString(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("!", Colors.INFO)));

        return 1;
    }

}
