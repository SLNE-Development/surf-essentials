package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.Objects;

public class OpCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("op", OpCommand::literal).setUsage("/op <player>")
                .setDescription("Makes the player a Serveroperator");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.OP_PERMISSION));
        literal.then(Commands.argument("players", GameProfileArgument.gameProfile())
                .suggests((context, builder) -> {
                    PlayerList playerList = context.getSource().getServer().getPlayerList();
                    return SharedSuggestionProvider.suggest(playerList.getPlayers().stream().filter(serverPlayer ->
                            !playerList.isOp(serverPlayer.getGameProfile())).map(serverPlayer -> serverPlayer.getGameProfile().getName()), builder);
                })
                .executes(context -> op(context.getSource(), GameProfileArgument.getGameProfiles(context, "players"))));
    }

    private static int op(CommandSourceStack source, Collection<GameProfile> players) throws CommandSyntaxException{
        PlayerList playerList = source.getServer().getPlayerList();
        int i = 0;

        for (GameProfile gameProfile : players) {
            if (!playerList.isOp(gameProfile)) {
                playerList.op(gameProfile);
                ++i;
                if (source.isPlayer()){
                    Bukkit.broadcast(SurfApi.getPrefix()
                            .append(Objects.requireNonNull(Bukkit.getPlayer(gameProfile.getId())).displayName())
                            .append(net.kyori.adventure.text.Component.text(" wurde durch ", SurfColors.INFO))
                            .append(source.getPlayerOrException().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" zum Operator", SurfColors.INFO)), "surf.essentials.announce.op");
                }else {
                    source.sendSuccess(Component.translatable("commands.op.success", gameProfile.getName()), true);
                }
            }
        }

        if (i == 0) {
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Es hat sich nicht geändert! Die Spieler sind schon Operatoren");
            }else {
                throw ERROR_ALREADY_OP.create();
            }
        }
        return i;
    }


    private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(Component.translatable("commands.op.failed"));
}
