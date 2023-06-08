package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;

import java.util.Collection;

public class OpCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"op"};
    }

    @Override
    public String usage() {
        return "/op <player>";
    }

    @Override
    public String description() {
        return "Makes the player a server operator";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.OP_PERMISSION));
        literal.then(Commands.argument("players", GameProfileArgument.gameProfile())
                .suggests((context, builder) -> {
                    PlayerList playerList = context.getSource().getServer().getPlayerList();
                    return SharedSuggestionProvider.suggest(playerList.getPlayers().stream().filter(serverPlayer ->
                            !playerList.isOp(serverPlayer.getGameProfile())).map(serverPlayer -> serverPlayer.getGameProfile().getName()), builder);
                })
                .executes(context -> op(context.getSource(), GameProfileArgument.getGameProfiles(context, "players"))));
    }

    private static int op(CommandSourceStack source, Collection<GameProfile> players) throws CommandSyntaxException {
        PlayerList playerList = source.getServer().getPlayerList();
        int successful = 0;

        for (GameProfile gameProfile : players) {
            if (!playerList.isOp(gameProfile)) {
                playerList.op(gameProfile);
                ++successful;

                Bukkit.broadcast(EssentialsUtil.getPrefix()
                        .append(EssentialsUtil.getDisplayName(gameProfile))
                        .append(net.kyori.adventure.text.Component.text(" wurde durch ", Colors.INFO))
                        .append(source.getPlayerOrException().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" zum Operator", Colors.INFO)), "surf.essentials.announce.op");

            }
        }

        if (successful == 0) {
            if (source.isPlayer()) {
                EssentialsUtil.sendError(source, "Es hat sich nicht geändert! Die Spieler sind bereits Operatoren");
            } else throw ERROR_ALREADY_OP.create();
        }
        return successful;
    }

    private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(Component.translatable("commands.op.failed"));
}
