package dev.slne.surf.essentialsold.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.commands.general.other.troll.TrollEntity;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FollowingBlockTroll extends Troll {
    private static final Map<UUID, TrollEntity<FallingBlockEntity>> PLAYER_ENTITIES = new HashMap<>();

    @Override
    public String name() {
        return "followBlock";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_FAKE_BLOCK_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .then(Commands.argument("block", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.BLOCK))
                        .executes(context -> followBlock(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceArgument.getResource(context, "block", Registries.BLOCK), 60))
                        .then(Commands.argument("time", IntegerArgumentType.integer(1, 3600))
                                .executes(context -> followBlock(context.getSource(), EntityArgument.getPlayer(context, "player"), ResourceArgument.getResource(context, "block", Registries.BLOCK), IntegerArgumentType.getInteger(context, "time")))));
    }

    @SuppressWarnings("SameReturnValue")
    private int followBlock(CommandSourceStack source, ServerPlayer target, Holder.Reference<Block> blockReference, int timeInSeconds) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(source, target);

        if (getAndToggleTroll(target.getBukkitEntity())) {
            stopTroll(target.getBukkitEntity());
            EssentialsUtil.sendSourceSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" wird nun nicht mehr von einem Block verfolgt!", Colors.SUCCESS)));

            final var trollPacket = PLAYER_ENTITIES.get(target.getUUID());
            if (trollPacket == null) return 1;
            trollPacket.setTrollActive(false);
            EssentialsUtil.sendPackets(target.getBukkitEntity(), new ClientboundRemoveEntitiesPacket(trollPacket.getEntity().getId()));

            return 1;
        }

        final var blockEntity = EssentialsUtil.spawnFakeFallingBlock(
                target.getBukkitEntity(),
                blockReference.value(),
                target.getBukkitEntity().getLocation().clone().add(5, 0, 0),
                Duration.ofSeconds(timeInSeconds)
        );

        PLAYER_ENTITIES.put(target.getUUID(), new TrollEntity<>(blockEntity, true));

        EssentialsUtil.sendSourceSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" wird nun von ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(blockEntity.getBukkitEntity()))
                .append(Component.text(" verfolgt!", Colors.SUCCESS)));

        return 1;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) throws CommandSyntaxException {
        final var trollEntity = PLAYER_ENTITIES.get(event.getPlayer().getUniqueId());

        if (trollEntity == null) return;
        if (!trollEntity.isTrollActive()){
            EssentialsUtil.sendPackets(event.getPlayer(), new ClientboundRemoveEntitiesPacket(trollEntity.getEntity().getId()));
        }

        final var entity = trollEntity.getEntity();
        final var toEvent = event.getTo();
        final var to = toEvent.clone().add(5, 0, 0);
        final var from = event.getFrom().clone().add(5, 0, 0);

        entity.setDeltaMovement((to.x() - from.x()), (to.y() - from.y()), (to.z() - from.z()));

        if (toEvent.distance(entity.getBukkitEntity().getLocation()) > 8){
            entity.teleportTo(to.x(), to.y(), to.z());
            EssentialsUtil.sendPackets(event.getPlayer(), new ClientboundTeleportEntityPacket(entity));
            return;
        }

        EssentialsUtil.sendPackets(event.getPlayer(), new ClientboundSetEntityMotionPacket(entity));
    }
}
 */
