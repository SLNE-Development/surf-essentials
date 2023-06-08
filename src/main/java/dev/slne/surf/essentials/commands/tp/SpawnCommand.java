package dev.slne.surf.essentials.commands.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.World;

import java.util.Collection;

public class SpawnCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"spawn", "spawntp", "tpspawn"};
    }

    @Override
    public String usage() {
        return "/spawn [<players>]";
    }

    @Override
    public String description() {
        return "Teleports the targets to the overworld-spawn.";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.TELEPORT_SPAWN_SELF, Permissions.TELEPORT_SPAWN_OTHER));
        literal.executes(context -> tpSingle(context.getSource()));

        literal.then(Commands.argument("players", EntityArgument.players())
                .requires(EssentialsUtil.checkPermissions(Permissions.TELEPORT_SPAWN_OTHER))
                .executes(context -> tpMulti(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    public int tpSingle(CommandSourceStack source) throws CommandSyntaxException {
        source.getPlayerOrException().getBukkitEntity().teleport(source.getServer().overworld().getWorld().getSpawnLocation());
        EssentialsUtil.sendSuccess(source, "Du wurdest zum Spawn teleportiert");
        return 1;
    }

    public int tpMulti(CommandSourceStack sourceStack, Collection<ServerPlayer> playersUnchecked) throws CommandSyntaxException {
        var players = EssentialsUtil.checkPlayerSuggestion(sourceStack, playersUnchecked);
        World overworld = sourceStack.getServer().overworld().getWorld();
        int successfullyTeleported = 0;

        for (ServerPlayer player : players) {
            player.getBukkitEntity().teleport(overworld.getSpawnLocation());
            EssentialsUtil.sendSuccess(player, "Du wurdest zum Spawn teleportiert");
            successfullyTeleported++;
        }

        boolean isSelf = sourceStack.isPlayer() && players.iterator().next().getUUID() == sourceStack.getPlayerOrException().getUUID();
        if (successfullyTeleported == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(sourceStack, players.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" wurde zum Spawn teleportiert", Colors.SUCCESS)));
        } else if (successfullyTeleported != 1) {
            EssentialsUtil.sendSuccess(sourceStack, Component.text("Es wurden ", Colors.SUCCESS)
                    .append(Component.text(successfullyTeleported, Colors.TERTIARY))
                    .append(Component.text(" Spieler zum Spawn teleportiert", Colors.SUCCESS)));
        }

        return 1;
    }
}
