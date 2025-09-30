package dev.slne.surf.essentials.commands.minecraft;

/**
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"damage"};
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(commandSourceStack -> commandSourceStack.hasPermission(2, Permissions.DAMAGE_PERMISSION));

        literal.then(Commands.argument("target", EntityArgument.entity())
                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0F))
                        .executes(context -> damage(context.getSource(), EntityArgument.getTileState(context, "target"),
                                FloatArgumentType.getFloat(context, "amount"), context.getSource().getLevel().damageSources().generic()))

                        .then(Commands.argument("damageType", ResourceArgument.resource(this.commandBuildContext, Registries.DAMAGE_TYPE))
                                .executes(context -> damage(context.getSource(), EntityArgument.getTileState(context, "target"),
                                        FloatArgumentType.getFloat(context, "amount"),
                                        new DamageSource(ResourceArgument.getResource(context, "damageType", Registries.DAMAGE_TYPE))))

                                .then(Commands.literal("at")
                                        .then(Commands.argument("location", Vec3Argument.vec3())
                                                .executes(context -> damage(context.getSource(), EntityArgument.getTileState(context, "target"),
                                                        FloatArgumentType.getFloat(context, "amount"),
                                                        new DamageSource(ResourceArgument.getResource(context, "damageType", Registries.DAMAGE_TYPE),
                                                                Vec3Argument.getVec3(context, "location"))))))

                                .then(Commands.literal("by")
                                        .then(Commands.argument("entity", EntityArgument.entity())
                                                .executes(context -> damage(context.getSource(), EntityArgument.getTileState(context, "target"),
                                                        FloatArgumentType.getFloat(context, "amount"),
                                                        new DamageSource(ResourceArgument.getResource(context, "damageType", Registries.DAMAGE_TYPE),
                                                                EntityArgument.getTileState(context, "entity"))))
                                                .then(Commands.literal("from")
                                                        .then(Commands.argument("cause", EntityArgument.entity())
                                                                .executes(context -> damage(context.getSource(), EntityArgument.getTileState(context, "target"),
                                                                        FloatArgumentType.getFloat(context, "amount"),
                                                                        new DamageSource(ResourceArgument.getResource(context, "damageType", Registries.DAMAGE_TYPE),
                                                                                EntityArgument.getTileState(context, "entity"), EntityArgument.getTileState(context, "cause")))))))))));
    }

    private static int damage(CommandSourceStack source, Entity target, float amount, DamageSource damageSource) throws CommandSyntaxException {
        if (target.hurt(damageSource, amount)) {

            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(net.kyori.adventure.text.Component.text(" wurde ", Colors.SUCCESS))
                    .append(net.kyori.adventure.text.Component.text(amount, Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" verletzt.", Colors.SUCCESS)));

            return 1;
        } else throw ERROR_INVULNERABLE.create();
    }

    public static final SimpleCommandExceptionType ERROR_INVULNERABLE = new SimpleCommandExceptionType(Component.translatable("commands.damage.invulnerable"));
}
 */
