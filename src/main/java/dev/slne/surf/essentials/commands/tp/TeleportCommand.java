package dev.slne.surf.essentials.commands.tp;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.PositionImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
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

            ServerLevel level = ((CraftWorld) targetLocation.getWorld()).getHandle();
            sender.teleportTo(level, targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(),targetLocation.getYaw(), targetLocation.getPitch());
            EssentialsUtil.sendSuccess(source, teleportToEntity$adventure(entity));

        }else {
            EssentialsUtil.callEvent(playerTeleportEvent);
            waiting$adventure(source);

            sender.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(aBoolean -> {
                try {
                    EssentialsUtil.sendSuccess(source, teleportToEntity$adventure(entity));
                } catch (CommandSyntaxException ignored) {}
            });
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

        if (isLoaded(targetLocation)){
            if (playerTeleportEvent != null) {
                EssentialsUtil.callEvent(playerTeleportEvent);
            }

            fromEntity.getBukkitEntity().teleport(targetLocation);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, teleportEntityToEntity$adventure(fromEntity, toEntity));
            }else {
                source.sendSuccess(teleportEntityToEntity(fromEntity, toEntity), false);
            }
        }else {
            waiting$adventure(source);

            fromEntity.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(aBoolean -> {
                if (source.isPlayer()){
                    try {
                        EssentialsUtil.sendSuccess(source, teleportEntityToEntity$adventure(fromEntity, toEntity));
                    } catch (CommandSyntaxException ignored) {}
                }else {
                    source.sendSuccess(teleportEntityToEntity(fromEntity, toEntity), false);
                }
            });
        }
        return 1;
    }

    private int teleportEntityToLocation(CommandSourceStack source, Entity entity, Vec3 vec3) throws CommandSyntaxException{
        canSourceSeeEntity(source, entity);
        Location targetLocation = new Location(source.getLevel().getWorld(), vec3.x(), vec3.y(), vec3.z());
        PlayerTeleportEvent playerTeleportEvent = null;
        if (entity instanceof ServerPlayer player) {
            playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                    targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
            if (playerTeleportEvent.isCancelled()) return 0;
        }

        if (isLoaded(targetLocation)){
            if (playerTeleportEvent != null) {
                EssentialsUtil.callEvent(playerTeleportEvent);
            }
            ServerLevel level = ((CraftWorld) targetLocation.getWorld()).getHandle();
            PositionImpl position = new PositionImpl(vec3.x(), vec3.y(), vec3.z());
            entity.teleportTo(level, position);

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, teleportEntityToLocation$adventure(entity, targetLocation));
            }else {
                source.sendSuccess(teleportEntityToLocation(entity, targetLocation), false);
            }
        }else {
            waiting$adventure(source);

            entity.getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAcceptAsync(aBoolean -> {
                if (source.isPlayer()){
                    try {
                        EssentialsUtil.sendSuccess(source, teleportEntityToLocation$adventure(entity, targetLocation));
                    } catch (CommandSyntaxException ignored) {}
                }else {
                    source.sendSuccess(teleportEntityToLocation(entity, targetLocation), false);
                }
            });
        }
        return 1;
    }

    private int teleportEntitiesToEntity(CommandSourceStack source, Collection<? extends Entity> entitiesUnchecked, Entity toEntity) throws CommandSyntaxException{
        Collection<? extends Entity> entities = EssentialsUtil.checkEntitySuggestion(source, entitiesUnchecked);
        canSourceSeeEntity(source, toEntity);

        Location targetLocation = toEntity.getBukkitEntity().getLocation();
        AtomicInteger successfulTeleports = new AtomicInteger();

        if (isLoaded(targetLocation)){
            for (Entity entity : entities) {
                if (entity instanceof ServerPlayer player) {
                    PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                            targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
                    if (playerTeleportEvent.isCancelled()) continue;
                    EssentialsUtil.callEvent(playerTeleportEvent);
                }
                ServerLevel level = ((CraftWorld) targetLocation.getWorld()).getHandle();
                PositionImpl position = new PositionImpl(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
                entity.teleportTo(level, position);
                successfulTeleports.getAndIncrement();
            }
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, teleportEntitiesToEntity$adventure(successfulTeleports.get(), toEntity));
            }else {
                source.sendSuccess(teleportEntitiesToEntity(successfulTeleports.get(), toEntity), false);
            }
        }else {
            waiting$adventure(source);

            entities.iterator().next().getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(aBoolean -> {
                for (Entity entity : entities) {
                    ServerLevel level = ((CraftWorld) targetLocation.getWorld()).getHandle();
                    PositionImpl position = new PositionImpl(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
                    entity.teleportTo(level, position);
                    successfulTeleports.getAndIncrement();
                }
                if (source.isPlayer()){
                    try {
                        EssentialsUtil.sendSuccess(source, teleportEntitiesToEntity$adventure(successfulTeleports.get(), toEntity));
                    } catch (CommandSyntaxException ignored) {}
                }else {
                    source.sendSuccess(teleportEntitiesToEntity(successfulTeleports.get(), toEntity), false);
                }
            });
        }
        return successfulTeleports.get();
    }

    private int teleportEntitiesToLocation(CommandSourceStack source, Collection<? extends Entity> entitiesUnchecked, Vec3 vec3) throws CommandSyntaxException{
        Collection<? extends Entity> entities = EssentialsUtil.checkEntitySuggestion(source, entitiesUnchecked);
        Location targetLocation = new Location(source.getLevel().getWorld(), vec3.x(), vec3.y(), vec3.z());
        AtomicInteger successfulTeleports = new AtomicInteger();

        if (isLoaded(targetLocation)){
            for (Entity entity : entities) {
                if (entity instanceof ServerPlayer player) {
                    PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(player.getBukkitEntity(), player.getBukkitEntity().getLocation(),
                            targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND);
                    if (playerTeleportEvent.isCancelled()) continue;
                    EssentialsUtil.callEvent(playerTeleportEvent);
                }
                ServerLevel level = ((CraftWorld) targetLocation.getWorld()).getHandle();
                PositionImpl position = new PositionImpl(vec3.x(), vec3.y(), vec3.z());
                entity.teleportTo(level, position);
                successfulTeleports.getAndIncrement();
            }
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, teleportEntitiesToLocation$adventure(successfulTeleports.get(), targetLocation));
            }else {
                source.sendSuccess(teleportEntitiesToLocation(successfulTeleports.get(), targetLocation), false);
            }
        }else {
            waiting$adventure(source);

            entities.iterator().next().getBukkitEntity().teleportAsync(targetLocation, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(aBoolean -> {
                for (Entity entity : entities) {
                    entity.teleportTo(vec3.x(), vec3.y(), vec3.z());
                    successfulTeleports.getAndIncrement();
                }
                if (source.isPlayer()){
                    try {
                        EssentialsUtil.sendSuccess(source, teleportEntitiesToLocation$adventure(successfulTeleports.get(), targetLocation));
                    } catch (CommandSyntaxException ignored) {}
                }else {
                    source.sendSuccess(teleportEntitiesToLocation(successfulTeleports.get(), targetLocation), false);
                }
            });
        }
        return successfulTeleports.get();
    }


    private boolean isLoaded(Location location){
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        return level.isLoaded(new BlockPos(location.getX(), location.getY(), location.getZ()));
    }

    private void canSourceSeeEntity(CommandSourceStack source, Entity entity) throws CommandSyntaxException {
        if (source.isPlayer()) {
            if (entity instanceof ServerPlayer player) {
                if (!EssentialsUtil.isVanished(player.getBukkitEntity())) return;
                if (source.getPlayerOrException().getBukkitEntity().canSee(player.getBukkitEntity())) return;
            }else {
                if (source.getPlayerOrException().getBukkitEntity().canSee(entity.getBukkitEntity())) return;
            }
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
    }

    private void waiting$adventure(CommandSourceStack source) throws CommandSyntaxException {
        if (source.isPlayer()){
            EssentialsUtil.sendInfo(source, "Teleportiere...");
        }
    }

    private Component teleportToEntity$adventure(Entity entity){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("Du hast dich zu ", Colors.SUCCESS));

        if (entity instanceof ServerPlayer player){
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        }else {
            builder.append(PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private Component teleportEntityToEntity$adventure(Entity fromEntity, Entity toEntity){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        if (fromEntity instanceof ServerPlayer player){
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        }else {
            builder.append(PaperAdventure.asAdventure(fromEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        builder.append(Component.text(" wurde zu ", Colors.SUCCESS));

        if (toEntity instanceof ServerPlayer player) {
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(player.getX()),
                            EssentialsUtil.makeDoubleReadable(player.getY()), EssentialsUtil.makeDoubleReadable(player.getZ())), Colors.INFO))));
        }else {
            builder.append(PaperAdventure.asAdventure(toEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY)
                    .hoverEvent(HoverEvent.showText(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(toEntity.getX()),
                            EssentialsUtil.makeDoubleReadable(toEntity.getY()), EssentialsUtil.makeDoubleReadable(toEntity.getZ())), Colors.INFO))));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private net.minecraft.network.chat.Component teleportEntityToEntity(Entity fromEntity, Entity toEntity){
        return net.minecraft.network.chat.Component.literal("Teleported ")
                .withStyle(ChatFormatting.GREEN)
                .append(fromEntity.getDisplayName())
                .append(net.minecraft.network.chat.Component.literal(" to ")
                        .withStyle(ChatFormatting.GREEN))
                .append(toEntity.getDisplayName());
    }

    private Component teleportEntityToLocation$adventure(Entity entity, Location location){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        if (entity instanceof ServerPlayer player){
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        }else {
            builder.append(PaperAdventure.asAdventure(entity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" wurde zu ", Colors.SUCCESS)
                .append(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                        EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), Colors.TERTIARY))
                .append(Component.text(" teleportiert!", Colors.SUCCESS))).build();
    }

    private net.minecraft.network.chat.Component teleportEntityToLocation(Entity entity, Location location){
        return net.minecraft.network.chat.Component.literal("Teleported ")
                .withStyle(ChatFormatting.GREEN)
                .append(entity.getDisplayName())
                .append(net.minecraft.network.chat.Component.literal(" to %s %s %s".formatted(location.getX(), location.getY(), location.getZ()))
                        .withStyle(ChatFormatting.GREEN));
    }

    private Component teleportEntitiesToEntity$adventure(int successfulTeleports, Entity toEntity){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        builder.append(Component.text(successfulTeleports, Colors.TERTIARY)
                .append(Component.text(" Entities wurden zu ", Colors.SUCCESS)));

        if (toEntity instanceof ServerPlayer player){
            builder.append(player.adventure$displayName.colorIfAbsent(Colors.TERTIARY));
        }else {
            builder.append(PaperAdventure.asAdventure(toEntity.getDisplayName()).colorIfAbsent(Colors.TERTIARY));
        }

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private net.minecraft.network.chat.Component teleportEntitiesToEntity(int successfulTeleports, Entity toEntity){
        return net.minecraft.network.chat.Component.literal("Teleported " + successfulTeleports + " entities to ")
                .withStyle(ChatFormatting.GREEN)
                .append(toEntity.getDisplayName());
    }

    private Component teleportEntitiesToLocation$adventure(int successfulTeleports, Location location){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        builder.append(Component.text(successfulTeleports, Colors.TERTIARY)
                .append(Component.text(" Entities wurden zu ", Colors.SUCCESS)));

        builder.append(Component.text("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())), Colors.TERTIARY));

        return builder.append(Component.text(" teleportiert!", Colors.SUCCESS)).build();
    }

    private net.minecraft.network.chat.Component teleportEntitiesToLocation(int successfulTeleports, Location location){
        return net.minecraft.network.chat.Component.literal("Teleported " + successfulTeleports + " entities to ")
                .withStyle(ChatFormatting.GREEN)
                .append(net.minecraft.network.chat.Component.literal("%s %s %s".formatted(EssentialsUtil.makeDoubleReadable(location.getX()),
                        EssentialsUtil.makeDoubleReadable(location.getY()), EssentialsUtil.makeDoubleReadable(location.getZ())))
                        .withStyle(ChatFormatting.GRAY));
    }
}
