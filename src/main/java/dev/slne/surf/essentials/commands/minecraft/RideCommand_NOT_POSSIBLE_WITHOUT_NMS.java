package dev.slne.surf.essentials.commands.minecraft;

// TODO: Maybe it is possible without nms
/**
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class RideCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"ride"};
    }

    @Override
    public String usage() {
        return "/ride <target> <dismount | mount <vehicle>>";
    }

    @Override
    public String description() {
        return "Ride entities";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(commandSourceStack -> commandSourceStack.hasPermission(2, Permissions.RIDE_PERMISSION));

        literal.then(Commands.argument("target", EntityArgument.entity())
                .then(Commands.literal("mount")
                        .then(Commands.argument("vehicle", EntityArgument.entity())
                                .executes(context -> mount(context.getSource(), EntityArgument.getEntity(context, "target"), EntityArgument.getEntity(context, "vehicle")))))

                .then(Commands.literal("dismount")
                        .executes(context -> dismount(context.getSource(), EntityArgument.getEntity(context, "target")))));
    }

    private int mount(CommandSourceStack source, Entity rider, Entity vehicle) throws CommandSyntaxException {
        Entity entity = vehicle.getVehicle();

        if (entity != null) throw ERROR_ALREADY_RIDING.create(rider.getDisplayName(), entity.getDisplayName());
        if (vehicle.getType() == EntityType.PLAYER) throw ERROR_MOUNTING_PLAYER.create();
        if (rider.getSelfAndPassengers().anyMatch(passenger -> passenger == vehicle))
            throw ERROR_MOUNTING_LOOP.create();

        if (rider.level() != vehicle.level()) throw ERROR_WRONG_DIMENSION.create();

        if (!rider.startRiding(vehicle, true))
            throw ERROR_MOUNT_FAILED.create(rider.getDisplayName(), vehicle.getDisplayName());

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(rider)
                .append(net.kyori.adventure.text.Component.text(" reitet nun ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(vehicle))
                .append(net.kyori.adventure.text.Component.text(".", Colors.SUCCESS)));

        return 1;
    }

    private static int dismount(CommandSourceStack source, Entity rider) throws CommandSyntaxException {
        Entity entity = rider.getVehicle();
        if (entity == null) throw ERROR_NOT_RIDING.create(rider.getDisplayName());

        rider.stopRiding();

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(rider)
                .append(net.kyori.adventure.text.Component.text(" reitet nicht mehr ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(entity))
                .append(net.kyori.adventure.text.Component.text(".", Colors.TERTIARY)));
        return 1;

    }

    private static final DynamicCommandExceptionType ERROR_NOT_RIDING = new DynamicCommandExceptionType((entity) ->
            Component.translatable("commands.ride.not_riding", entity));
    private static final Dynamic2CommandExceptionType ERROR_ALREADY_RIDING = new Dynamic2CommandExceptionType((rider, vehicle) ->
            Component.translatable("commands.ride.already_riding", rider, vehicle));
    private static final Dynamic2CommandExceptionType ERROR_MOUNT_FAILED = new Dynamic2CommandExceptionType((rider, vehicle) ->
            Component.translatable("commands.ride.mount.failure.generic", rider, vehicle));
    private static final SimpleCommandExceptionType ERROR_MOUNTING_PLAYER = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.cant_ride_players"));
    private static final SimpleCommandExceptionType ERROR_MOUNTING_LOOP = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.loop"));
    private static final SimpleCommandExceptionType ERROR_WRONG_DIMENSION = new SimpleCommandExceptionType(Component.translatable("commands.ride.mount.failure.wrong_dimension"));
}
 */
