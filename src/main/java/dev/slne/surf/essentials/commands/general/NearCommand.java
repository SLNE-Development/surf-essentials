package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class NearCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"near"};
    }

    @Override
    public String usage() {
        return "/near [<radius>] [<entities>]";
    }

    @Override
    public String description() {
        return "Displays the players near to you";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.NEAR_PERMISSION));
        literal.executes(context -> getNearEntities(context.getSource(), 200, false));

        literal.then(Commands.argument("distance", IntegerArgumentType.integer(1, 1000))
                .executes(context -> getNearEntities(context.getSource(), IntegerArgumentType.getInteger(context, "distance"), false))

                .then(Commands.literal("entities")
                        .executes(context -> getNearEntities(context.getSource(), IntegerArgumentType.getInteger(context, "distance"), true))));
    }

    public int getNearEntities(CommandSourceStack source, int distance, boolean entities) {
        final var pos = source.getPosition();

        final var nearbyEntities = (entities) ?
                source.getLevel()
                        .getWorld()
                        .getNearbyEntitiesByType(
                                LivingEntity.class,
                                Objects.requireNonNull(source.getBukkitLocation()),
                                distance,
                                livingEntity -> EssentialsUtil.canSourceSeeEntity(source, livingEntity)
                        )
                :
                source.getLevel()
                        .getNearbyPlayers(
                                null,
                                pos.x(),
                                pos.y(),
                                pos.z(),
                                distance,
                                entity -> EssentialsUtil.canSourceSeeEntity(source, entity)
                        )
                        .stream()
                        .map(ServerPlayer::getBukkitEntity)
                        .toList();

        EssentialsUtil.sendSourceSuccess(source, Component.text("%s in der Nähe: ".formatted((entities) ? "Entities" : "Spieler"), Colors.INFO)
                .append(Component.join(JoinConfiguration.commas(true), nearbyEntities.stream()
                        .map(livingEntity -> EssentialsUtil.getDisplayName(livingEntity)
                                .hoverEvent(HoverEvent.showText(Component.translatable(livingEntity.getType().translationKey(), Colors.INFO))))
                        .toArray(Component[]::new))));
        return distance;
    }
}
