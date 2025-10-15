package dev.slne.surf.essentialsold.commands.tp;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.Rotation;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.entity.TeleportFlag.Relative;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TeleportCommand extends EssentialsCommand { // TODO test
    private static final String ARG_ENTITY_DESTINATION = "destination";
    private static final String ARG_TARGETS = "targets";
    private static final String ARG_LOCATION_DESTINATION = "location";
    private static final String ARG_ROTATION = "rotation";
    private static final String ARG_FACING_LOCATION = "facingLocation";
    private static final String ARG_FACING_ENTITY = "facingEntity";
    private static final String ARG_FACING_ANCHOR = "facingAnchor";
    private static final String ARG_CHECK_FOR_BLOCKS = "checkForBlocks"; // TODO maybe add this (checks if the target would suffocate in a block)
    private static final String LITERAL_FACING = "facing";
    private static final String LITERAL_ENTITY = "entity";

    public TeleportCommand() {
        super("teleport", "tp <Location | Entities [<toLocation | to Entity>]>", "Teleports you to a location or entity", "tp");

        withPermission(Permissions.TELEPORT_PERMISSION);

        then(entityArgument(ARG_ENTITY_DESTINATION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToEntity(
                        sender.getCallee(),
                        Collections.singleton(getEntityOrException(sender)),
                        Objects.requireNonNull(args.getUnchecked(ARG_ENTITY_DESTINATION))
                )));

        then(entitiesArgument(ARG_TARGETS)
                .then(locationArgument(ARG_LOCATION_DESTINATION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToLoc(
                                sender.getCallee(),
                                args.getUnchecked(ARG_TARGETS),
                                args.getUnchecked(ARG_LOCATION_DESTINATION),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()

                        ))
                        .then(rotationArgument(ARG_ROTATION)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToLoc(
                                        sender.getCallee(),
                                        args.getUnchecked(ARG_TARGETS),
                                        args.getUnchecked(ARG_LOCATION_DESTINATION),
                                        Optional.ofNullable(args.getUnchecked(ARG_ROTATION)),
                                        Optional.empty(),
                                        Optional.empty()
                                )))
                        .then(literal(LITERAL_FACING)
                                .then(locationArgument(ARG_FACING_LOCATION)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToLoc(
                                                sender.getCallee(),
                                                args.getUnchecked(ARG_TARGETS),
                                                args.getUnchecked(ARG_LOCATION_DESTINATION),
                                                Optional.ofNullable(args.getUnchecked(ARG_ROTATION)),
                                                Optional.ofNullable(args.getUnchecked(ARG_FACING_LOCATION)),
                                                Optional.empty()

                                        ))
                                )
                                .then(literal(LITERAL_ENTITY)
                                        .then(entityArgument(ARG_FACING_ENTITY)
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToLoc(
                                                        sender.getCallee(),
                                                        args.getUnchecked(ARG_TARGETS),
                                                        args.getUnchecked(ARG_LOCATION_DESTINATION),
                                                        Optional.ofNullable(args.getUnchecked(ARG_ROTATION)),
                                                        Optional.ofNullable(args.get(ARG_FACING_ENTITY) instanceof Entity entity ? entity.getLocation() : null),
                                                        Optional.empty()
                                                ))
                                                .then(lookAnchorArgument(ARG_FACING_ANCHOR)
                                                        .executes((ResultingCommandExecutor) (sender, args) -> teleportToLoc(
                                                                sender,
                                                                args.getUnchecked(ARG_TARGETS),
                                                                args.getUnchecked(ARG_LOCATION_DESTINATION),
                                                                Optional.ofNullable(args.getUnchecked(ARG_ROTATION)),
                                                                Optional.ofNullable(args.get(ARG_FACING_ENTITY) instanceof Entity entity ? entity.getLocation() : null),
                                                                Optional.ofNullable(args.getUnchecked(ARG_FACING_ANCHOR))
                                                        ))
                                                )
                                        )
                                )
                        )

                )
                .then(entityArgument(ARG_ENTITY_DESTINATION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToEntity(
                                sender.getCallee(),
                                args.getUnchecked(ARG_TARGETS),
                                Objects.requireNonNull(args.getUnchecked(ARG_ENTITY_DESTINATION))
                        ))
                )
        );

        then(locationArgument(ARG_LOCATION_DESTINATION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> teleportToLoc(
                        sender.getCallee(),
                        Collections.singleton(getEntityOrException(sender)),
                        args.getUnchecked(ARG_LOCATION_DESTINATION),
                        Optional.of(new Rotation(sender.getLocation().getYaw(), sender.getLocation().getPitch())),
                        Optional.empty(),
                        Optional.empty()
                ))
        );
    }

    private int teleportToEntity(CommandSender sender, Collection<Entity> targets, Entity destination) throws WrapperCommandSyntaxException {
        val destinationLocation = destination.getLocation();

        if (!isLoaded(destinationLocation)) {
            EssentialsUtil.sendInfo(sender, "Teleportiere...");
        }

        performTeleport(targets, destinationLocation, Optional.empty(), Optional.empty()).thenAccept(entities -> {
            boolean single = entities.size() == 1;
            EssentialsUtil.sendSuccess(sender, (single ? EssentialsUtil.getDisplayName(entities.iterator().next()) : Component.text(entities.size(), Colors.VARIABLE_VALUE)
                    .append(Component.text(" Entities", Colors.SUCCESS)))
                    .append(Component.text(" wurde%s zu ".formatted(single ? "" : "n"), Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(destination))
                    .append(Component.text(" teleportiert", Colors.SUCCESS)));
        });

        return 1;
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnstableApiUsage"})
    private int teleportToLoc(CommandSender sender, Collection<Entity> targets, Location destination, Optional<Rotation> rotation, Optional<Location> facingLocation, Optional<LookAnchor> lookAnchor) throws WrapperCommandSyntaxException {
        if (!EssentialsUtil.isInSpawnableBounds(destination)) throw Exceptions.ERROR_OUT_OF_WORLD;

        AtomicInteger success = new AtomicInteger();
        rotation.ifPresent(r -> {
            destination.setYaw(r.getNormalizedYaw());
            destination.setPitch(r.getNormalizedPitch());
        });

        if (!isLoaded(destination)) {
            EssentialsUtil.sendInfo(sender, "Teleportiere...");
        }

        performTeleport(
                targets,
                destination,
                facingLocation,
                lookAnchor,
                Relative.VELOCITY_X,
                Relative.VELOCITY_Y,
                Relative.VELOCITY_Z,
                Relative.VELOCITY_ROTATION

        ).thenAccept(entities -> {
            boolean single = entities.size() == 1;
            EssentialsUtil.sendSuccess(sender, (single ? EssentialsUtil.getDisplayName(entities.iterator().next()) : Component.text(entities.size(), Colors.VARIABLE_VALUE)
                    .append(Component.text(" Entities", Colors.SUCCESS)))
                    .append(Component.text(" wurde%s zu ".formatted(single ? "" : "n"), Colors.SUCCESS))
                    .append(EssentialsUtil.formatLocationWithoutSpacer(destination))
                    .append(Component.text(" teleportiert", Colors.SUCCESS)));
        });

        return success.get();
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnstableApiUsage"})
    private <E extends Entity> CompletableFuture<Collection<E>> performTeleport(Collection<E> targets, Location location, Optional<Location> facingLocation, Optional<LookAnchor> lookAnchor, TeleportFlag.Relative... movementFlags) throws WrapperCommandSyntaxException {
        AtomicReference<Collection<E>> otherFuture = new AtomicReference<>(new ArrayList<>());
        CompletableFuture<Void> combinedFuture = CompletableFuture.completedFuture(null);

        for (E target : targets) {
            combinedFuture = combinedFuture.thenComposeAsync(ignored -> performTeleport(
                    target,
                    location,
                    facingLocation,
                    lookAnchor,
                    movementFlags
            )).thenAccept(e -> {
                if (e != null) otherFuture.get().add(e);
            });
        }

        return combinedFuture.thenApply(ignored -> otherFuture.get());
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnstableApiUsage"})
    private <E extends Entity> CompletableFuture<E> performTeleport(E target, Location location, Optional<Location> facingLocation, Optional<LookAnchor> lookAnchor, TeleportFlag.Relative... movementFlags) {
        CompletableFuture<E> future = new CompletableFuture<>();

        EssentialsUtil.teleportAsync(
                        target,
                        location,
                        PlayerTeleportEvent.TeleportCause.COMMAND,
                        movementFlags
                )
                .thenApply(integer -> {
                    lookAt(target, facingLocation, lookAnchor);
                    return integer;
                })
                .thenAccept(future::complete)
                .exceptionally(ex -> {
                    future.completeExceptionally(ex);
                    return null;
                });

        return future;
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnstableApiUsage"})
    private void lookAt(Entity target, Optional<Location> location, Optional<LookAnchor> lookAnchor) {
        if (location.isPresent()) {
            if (target instanceof Player player) {
                Bukkit.getScheduler().runTask(SurfEssentials.getInstance(), () -> {
                    Location location1 = location.get();
                    player.lookAt(location1.x(), location1.y(), location1.z(), lookAnchor.orElse(LookAnchor.EYES));
                });
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isLoaded(Location location) {
        return location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }
}

