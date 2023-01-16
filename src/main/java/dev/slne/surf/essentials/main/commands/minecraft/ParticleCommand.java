package dev.slne.surf.essentials.main.commands.minecraft;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Objects;

@DefaultQualifier(NotNull.class)
public class ParticleCommand {
    @Nullable public static String PERMISSION;
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("particles", ParticleCommand::literal).setUsage("/particle <name> [<position>] [<delta>] [<speed>] [<amount>] [<force | normal>] [<viewers>]")
                .setDescription("Creates particles");
    }
    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        literal.then(Commands.argument("name", ParticleArgument.particle(EssentialsUtil.buildContext()))
                .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                        context.getSource().getPosition(), Vec3.ZERO, 0.0F, 0, false, context.getSource().getServer().getPlayerList().getPlayers()))

                .then(Commands.argument("position", Vec3Argument.vec3())
                        .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                Vec3Argument.getVec3(context, "position"), Vec3.ZERO, 0.0F, 0, false,
                                context.getSource().getServer().getPlayerList().getPlayers()))

                        .then(Commands.argument("delta", Vec3Argument.vec3(false))
                                .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                        Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"), 0.0F, 0, false,
                                        context.getSource().getServer().getPlayerList().getPlayers()))

                                .then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F))
                                        .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                FloatArgumentType.getFloat(context, "speed"), 0, false,
                                                context.getSource().getServer().getPlayerList().getPlayers()))

                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                        Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                        FloatArgumentType.getFloat(context, "speed"), IntegerArgumentType.getInteger(context, "amount"), false,
                                                        context.getSource().getServer().getPlayerList().getPlayers()))

                                                .then(Commands.literal("force")
                                                        .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                                Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                                FloatArgumentType.getFloat(context, "speed"), IntegerArgumentType.getInteger(context, "amount"), true,
                                                                context.getSource().getServer().getPlayerList().getPlayers()))
                                                        .then(Commands.argument("viewers", EntityArgument.players())
                                                                .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                                        Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                                        FloatArgumentType.getFloat(context, "speed"), IntegerArgumentType.getInteger(context, "amount"), true,
                                                                        EntityArgument.getPlayers(context, "viewers")))))

                                                .then(Commands.literal("normal")
                                                        .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                                Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                                FloatArgumentType.getFloat(context, "speed"), IntegerArgumentType.getInteger(context, "amount"), false,
                                                                context.getSource().getServer().getPlayerList().getPlayers()))
                                                        .then(Commands.argument("viewers", EntityArgument.players())
                                                                .executes(context -> showParticles(context.getSource(), ParticleArgument.getParticle(context, "name"),
                                                                        Vec3Argument.getVec3(context, "position"), Vec3Argument.getVec3(context, "delta"),
                                                                        FloatArgumentType.getFloat(context, "speed"), IntegerArgumentType.getInteger(context, "amount"), false,
                                                                        EntityArgument.getPlayers(context, "viewers"))))))))));
    }

    private static int showParticles(CommandSourceStack source, ParticleOptions parameters, Vec3 pos, Vec3 delta, float speed, int count, boolean force, Collection<ServerPlayer> viewers) throws CommandSyntaxException{
        int countParticlesShown = 0;

        for (ServerPlayer serverPlayer : viewers) {
            if (source.getLevel().sendParticles(serverPlayer, parameters, force, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed)){
                countParticlesShown++;
            }
        }

        if (countParticlesShown == 0){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, "Die Partikel wurden niemanden gezeigt!");
            }else throw ERROR_FAILED.create();
            return countParticlesShown;
        }

        if (source.isPlayer()){
            String particleName = Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(parameters.getType())).toLanguageKey();

            String positionX = new DecimalFormat("#.#").format(pos.x);
            String positionY = new DecimalFormat("#.#").format(pos.y);
            String positionZ = new DecimalFormat("#.#").format(pos.z);
            String position = positionX + " " + positionY + " " + positionZ;

            String deltaX = new DecimalFormat("#.#").format(delta.x);
            String deltaY = new DecimalFormat("#.#").format(delta.y);
            String deltaZ = new DecimalFormat("#.#").format(delta.z);
            String deltaString = deltaX + " " + deltaY + " " + deltaZ;

            String forceString = (force) ? "Ja" : "Nein";

            EssentialsUtil.sendSuccess(source, Component.text("Partikel ", SurfColors.SUCCESS)
                    .append(Component.text(particleName, SurfColors.TERTIARY)
                            .hoverEvent(HoverEvent.showText(Component.text("Partikel: ", SurfColors.TERTIARY)
                                    .append(Component.text(particleName, TextColor.fromCSSHexString("#f2b179")))
                                    .append(Component.newline())
                                    .append(Component.text("Position: ", SurfColors.TERTIARY)
                                            .append(Component.text(position, TextColor.fromCSSHexString("#6699cc"))))
                                    .append(Component.newline())
                                    .append(Component.text("Delta: ", SurfColors.TERTIARY)
                                            .append(Component.text(deltaString, TextColor.fromCSSHexString("#ff9900"))))
                                    .append(Component.newline())
                                    .append(Component.text("Anzahl: ", SurfColors.TERTIARY)
                                            .append(Component.text(count, TextColor.fromCSSHexString("#339933"))))
                                    .append(Component.newline())
                                    .append(Component.text("Geschwindigkeit: ", SurfColors.TERTIARY)
                                            .append(Component.text(speed, TextColor.fromCSSHexString("#00cc00"))))
                                    .append(Component.newline())
                                    .append(Component.text("Erzwungen: ", SurfColors.TERTIARY)
                                            .append(Component.text(forceString, (force) ? TextColor.fromHexString("#009933") : TextColor.fromCSSHexString("#cc0000"))))
                                    .append(Component.newline())
                                    .append(Component.text("Betrachter: ", SurfColors.TERTIARY)
                                            .append(Component.text(viewers.size(), TextColor.fromCSSHexString("#ffcc00")))))))
                    .append(Component.text(" wird ", SurfColors.SUCCESS))
                    .append(Component.text(viewers.size(), SurfColors.TERTIARY))
                    .append(Component.text((viewers.size() == 1) ? " Spieler" : " Spielern", SurfColors.SUCCESS))
                    .append(Component.text(" gezeigt!", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.particle.success",
                    Objects.requireNonNull(BuiltInRegistries.PARTICLE_TYPE.getKey(parameters.getType())).toString()), true);
        }
        return countParticlesShown;
    }

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("commands.particle.failed"));
}

