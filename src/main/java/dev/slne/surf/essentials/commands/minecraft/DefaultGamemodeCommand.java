package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

@DefaultQualifier(NotNull.class)
public class DefaultGamemodeCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("defaultgamemode", DefaultGamemodeCommand::literal).setUsage("/defaultgamemode [<gamemode>]")
                .setDescription("Set or query the default game mode");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.DEFAULT_GAMEMODE_PERMISSION));

        literal.executes(context -> getGameMode(context.getSource()));
        literal.then(Commands.argument("gamemode", GameModeArgument.gameMode())
                .executes(context -> setGameMode(context.getSource(), GameModeArgument.getGameMode(context, "gamemode"))));
    }

    private static int setGameMode(CommandSourceStack source, GameType newGameMode) throws CommandSyntaxException{
        MinecraftServer server = source.getServer();
        int playersUpdated = 0;

        if (server.getDefaultGameType() == newGameMode){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Der Spielmodus ist gleich!");
            }else {
                source.sendFailure(Component.literal("The game mode is identical!"));
            }
        }else {
            server.setDefaultGameType(newGameMode);
            @Nullable GameType forcedGameType = server.getForcedGameType();
            if (forcedGameType != null) {
                for(Iterator<ServerPlayer> serverPlayerList = server.getPlayerList().getPlayers().iterator(); serverPlayerList.hasNext(); ++playersUpdated) {
                    ServerPlayer serverPlayer = serverPlayerList.next();
                    PlayerGameModeChangeEvent event = serverPlayer.setGameMode(forcedGameType, PlayerGameModeChangeEvent.Cause.DEFAULT_GAMEMODE,
                            net.kyori.adventure.text.Component.empty());
                    if (event.isCancelled()) {
                        source.sendSuccess(PaperAdventure.asVanilla(event.cancelMessage()), false);
                    }
                }
            }
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Der standard Spielmodus wurde auf ", Colors.SUCCESS)
                        .append(net.kyori.adventure.text.Component.text(newGameMode.getLongDisplayName().getString(), Colors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" gesetzt!", Colors.SUCCESS)));
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.defaultgamemode.success",
                        newGameMode.getLongDisplayName()), false);
            }
        }
        return playersUpdated;
    }

    private static int getGameMode(CommandSourceStack source) throws CommandSyntaxException{
        GameType gameType = source.getServer().getDefaultGameType();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, net.kyori.adventure.text.Component.text("Der standard Spielmodus ist ", Colors.INFO)
                    .append(net.kyori.adventure.text.Component.text(gameType.getLongDisplayName().getString(), Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text("!", Colors.INFO)));
        }else {
            source.sendSuccess(Component.literal("The default game mode is ")
                            .withStyle(ChatFormatting.GRAY)
                    .append(gameType.getLongDisplayName()), false);
        }
        return 1;
    }

}
