package dev.slne.surf.essentials.commands.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class TeleportCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"tp", "teleport"};
    }

    @Override
    public String usage() {
        return "/tp <Location | Entities [<toLocation | to Entity>]>";
    }

    @Override
    public String description() {
        return "Teleport the targets to location/targets";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TELEPORT_PERMISSION));

        literal.then(Commands.argument("toLocation", Vec3Argument.vec3(true))
                .executes(context -> teleportEntityToLocation(context.getSource(), context.getSource().getEntityOrException(), Vec3Argument.getVec3(context, "toLocation"))));

        literal.then(Commands.argument("entity", EntityArgument.entity())
                .executes(context -> teleportToEntity(context.getSource(), EntityArgument.getEntity(context, "entity")))

                .then(Commands.argument("toEntity", EntityArgument.entity())
                        .executes(context -> teleportEntityToEntity(context.getSource(), EntityArgument.getEntity(context, "entity"), EntityArgument.getEntity(context, "toEntity"))))

                .then(Commands.argument("toLocation", Vec3Argument.vec3(true))
                        .executes(context -> teleportEntityToLocation(context.getSource(), EntityArgument.getEntity(context, "entity"), Vec3Argument.getVec3(context, "toLocation")))));

        literal.then(Commands.argument("fromEntities", EntityArgument.entities())
                .then(Commands.argument("toEntity", EntityArgument.entity())
                        .executes(context -> teleportEntitiesToEntity(context.getSource(), EntityArgument.getEntities(context, "fromEntities"), EntityArgument.getEntity(context, "toEntity"))))
                .then(Commands.argument("toLocation", Vec3Argument.vec3(true))
                        .executes(context -> teleportEntitiesToLocation(context.getSource(), EntityArgument.getEntities(context, "fromEntities"), Vec3Argument.getVec3(context, "toLocation")))));
    }

    private int teleportToEntity(CommandSourceStack source, Entity entity) throws CommandSyntaxException {
        canSourceSeeEntity(source, entity);
        ServerPlayer sender = source.getPlayerOrException();
        Location targetLocation = entity.getBukkitEntity().getLocation();
        PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(sender.getBukkitEntity(), sender.getBukkitEntity().getLocation(),
                targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
        if (playerTeleportEvent.isCancelled()) return 0;

        if (isLoaded(targetLocation)) {
            EssentialsUtil.callEvent(playerTeleportEvent);

            sender.getBukkitEntity().teleport(targetLocation);
            EssentialsUtil.sendSuccess(source, teleportToEntity$adventure(entity));

        } else {
            EssentialsUtil.callEvent(playerTeleportEvent);
            waiting$adventure(source);

            sender.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(__ -> EssentialsUtil.sendSuccess(source, teleportToEntity$adventure(entity)));
        }

        return 1;
    }

    private int teleportEntityToEntity(CommandSourceStack source, Entity fromEntity, Entity toEntity) throws CommandSyntaxException {
        canSourceSeeEntity(source, fromEntity);
        canSourceSeeEntity(source, toEntity);
        Location targetLocation = toEntity.getBukkitEntity().getLocation();
        PlayerTeleportEvent playerTeleportEvent = null;
        if (fromEntity instanceof ServerPlayer player) {
            playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                    targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
            if (playerTeleportEvent.isCancelled()) return 0;
        }

        if (isLoaded(targetLocation)) {
            if (playerTeleportEvent != null) {
                EssentialsUtil.callEvent(playerTeleportEvent);
            }

            fromEntity.getBukkitEntity().teleport(targetLocation);
            EssentialsUtil.sendSuccess(source, teleportEntityToEntity$adventure(fromEntity, toEntity));

        } else {
            waiting$adventure(source);
            fromEntity.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(__ -> EssentialsUtil.sendSuccess(source, teleportEntityToEntity$adventure(fromEntity, toEntity)));
        }
        return 1;
    }

    private int teleportEntityToLocation(CommandSourceStack source, Entity entity, Vec3 vec3) throws CommandSyntaxException {
        canSourceSeeEntity(source, entity);
        var targetLocation = new Location(source.getLevel().getWorld(), vec3.x(), vec3.y(), vec3.z());
        PlayerTeleportEvent playerTeleportEvent = null;
        if (entity instanceof ServerPlayer player) {
            playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                    targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
            if (playerTeleportEvent.isCancelled()) return 0;
        }

        if (isLoaded(targetLocation)) {
            if (playerTeleportEvent != null) {
                EssentialsUtil.callEvent(playerTeleportEvent);
            }
            entity.getBukkitEntity().teleport(targetLocation);
            EssentialsUtil.sendSuccess(source, teleportEntityToLocation$adventure(entity, targetLocation));

        } else {
            waiting$adventure(source);

            entity.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(__ -> EssentialsUtil.sendSuccess(source, teleportEntityToLocation$adventure(entity, targetLocation)));
        }
        return 1;
    }

    private int teleportEntitiesToEntity(CommandSourceStack source, Collection<? extends Entity> entitiesUnchecked, Entity toEntity) throws CommandSyntaxException {
        var entities = EssentialsUtil.checkEntitySuggestion(source, entitiesUnchecked);
        canSourceSeeEntity(source, toEntity);

        var targetLocation = toEntity.getBukkitEntity().getLocation();
        var successfulTeleports = new AtomicInteger();

        if (isLoaded(targetLocation)) {
            teleportEntities(entities, targetLocation, successfulTeleports);
            EssentialsUtil.sendSuccess(source, teleportEntitiesToEntity$adventure(successfulTeleports.get(), toEntity));

        } else {
            waiting$adventure(source);

            entities.iterator().next().getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(__ -> {
                for (Entity entity : entities) {
                    entity.getBukkitEntity().teleport(targetLocation);
                    successfulTeleports.getAndIncrement();
                }

                EssentialsUtil.sendSuccess(source, teleportEntitiesToEntity$adventure(successfulTeleports.get(), toEntity));

            });
        }
        return successfulTeleports.get();
    }

    private int teleportEntitiesToLocation(CommandSourceStack source, Collection<? extends Entity> entitiesUnchecked, Vec3 vec3) throws CommandSyntaxException {
        var entities = EssentialsUtil.checkEntitySuggestion(source, entitiesUnchecked);
        var targetLocation = new Location(source.getLevel().getWorld(), vec3.x(), vec3.y(), vec3.z());
        var successfulTeleports = new AtomicInteger();

        if (isLoaded(targetLocation)) {
            teleportEntities(entities, targetLocation, successfulTeleports);
            EssentialsUtil.sendSuccess(source, teleportEntitiesToLocation$adventure(successfulTeleports.get(), targetLocation));

        } else {
            waiting$adventure(source);

            entities.iterator().next().getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(__ -> {
                for (Entity entity : entities) {
                    entity.getBukkitEntity().teleport(targetLocation);
                    successfulTeleports.getAndIncrement();
                }
                EssentialsUtil.sendSuccess(source, teleportEntitiesToLocation$adventure(successfulTeleports.get(), targetLocation));

            });
        }
        return successfulTeleports.get();
    }


    private boolean isLoaded(Location location) {
        return location.isChunkLoaded();
    }

    private void canSourceSeeEntity(CommandSourceStack source, Entity entity) throws CommandSyntaxException {
        if (source.isPlayer()) {
            if (entity instanceof ServerPlayer player) {
                if (!EssentialsUtil.isVanished(player.getBukkitEntity())) return;
                if (source.getPlayerOrException().getBukkitEntity().canSee(player.getBukkitEntity())) return;
            } else {
                if (source.getPlayerOrException().getBukkitEntity().canSee(entity.getBukkitEntity())) return;
            }
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
    }

    private void waiting$adventure(CommandSourceStack source) {
        if (source.isPlayer()) {
            EssentialsUtil.sendInfo(source, "Teleportiere...");
        }
    }

    private Component teleportToEntity$adventure(Entity entity) {
        var builder = Component.text();
        builder.append(Component.text("Du hast dich zu ", Colors.SUCCESS));

        if (entity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        } else {
            builder.append(PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private Component teleportEntityToEntity$adventure(Entity fromEntity, Entity toEntity) {
        var builder = Component.text();

        if (fromEntity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        } else {
            builder.append(PaperAdventure.asAdventure(fromEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        builder.append(Component.text(" wurde zu ", Colors.SUCCESS));

        if (toEntity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(player.getX()),
                            EssentialsUtil.makeDoubleReadable(player.getY()), EssentialsUtil.makeDoubleReadable(player.getZ())), Colors.INFO))));
        } else {
            builder.append(PaperAdventure.asAdventure(toEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(toEntity.getX()),
                            EssentialsUtil.makeDoubleReadable(toEntity.getY()), EssentialsUtil.makeDoubleReadable(toEntity.getZ())), Colors.INFO))));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private Component teleportEntityToLocation$adventure(Entity entity, Location location) {
        var builder = Component.text();

        if (entity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        } else {
            builder.append(PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" wurde zu ", Colors.SUCCESS)
                .append(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                        EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), Colors.TERTIARY))
                .append(Component.text(" teleportiert!", Colors.SUCCESS))).build();
    }

    private Component teleportEntitiesToEntity$adventure(int successfulTeleports, Entity toEntity) {
        var builder = Component.text();

        builder.append(Component.text(successfulTeleports, Colors.TERTIARY)
                .append(Component.text(" Entities wurden zu ", Colors.SUCCESS)));

        if (toEntity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        } else {
            builder.append(PaperAdventure.asAdventure(toEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private Component teleportEntitiesToLocation$adventure(int successfulTeleports, Location location) {
        var builder = Component.text();

        builder.append(Component.text(successfulTeleports, Colors.TERTIARY)
                .append(Component.text(" Entities wurden zu ", Colors.SUCCESS)));

        builder.append(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), Colors.TERTIARY));

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private <T extends Entity, S extends Location, M extends AtomicInteger> void teleportEntities(Collection<T> entities, S targetLocation, M atomicInteger) {
        for (Entity entity : entities) {
            if (entity instanceof ServerPlayer player) {
                var playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(), targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
                if (playerTeleportEvent.isCancelled()) continue;
                EssentialsUtil.callEvent(playerTeleportEvent);
            }
            entity.getBukkitEntity().teleport(targetLocation);
            atomicInteger.getAndIncrement();
        }
    }
}
