package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

import javax.annotation.Nullable;
import java.util.Collections;

public class SpectateCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"spectate"};
    }

    @Override
    public String usage() {
        return "/spectate <target> [<player>]";
    }

    @Override
    public String description() {
        return "Spectate other players";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.SPECTATE_LEAVE_PERMISSION, Permissions.SPECTATE_OTHER_PERMISSION));

        literal.executes(context -> spectate(context.getSource(), null, context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("target", EntityArgument.entity())
                .executes(context -> spectate(context.getSource(), EntityArgument.getEntity(context, "target"), context.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SPECTATE_OTHER_PERMISSION))
                        .executes(context -> spectate(context.getSource(), EntityArgument.getEntity(context, "target"), EntityArgument.getPlayer(context, "player")))));
    }

    private int spectate(CommandSourceStack source, @Nullable Entity entityUnchecked, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        Entity entity = EssentialsUtil.checkEntitySuggestion(source, Collections.singleton(entityUnchecked)).iterator().next();
        ServerPlayer player = EssentialsUtil.checkPlayerSuggestion(source, Collections.singleton(playerUnchecked)).iterator().next();

        if (player == entity) throw ERROR_SELF.create();
        if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
            player.setGameMode(GameType.SPECTATOR);
        }

        player.setCamera(entity);

        if (entity != null) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text(" beobachtet nun ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(entity))
                    .append(Component.text(".", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text(" beobachtet nun niemanden mehr.", Colors.SUCCESS)));
        }
        return 1;
    }

    private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("commands.spectate.self"));
}
