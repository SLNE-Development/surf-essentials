package dev.slne.surf.essentialsold.commands.minecraft;

/**
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
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
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class PlaySoundCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"playsound"};
    }

    @Override
    public String usage() {
        return "/playsound <sound> [<category>] [<targets>] [<position>] [<volume>] [<pitch>] [<minVolume>]";
    }

    @Override
    public String description() {
        return "Play sounds";
    }

    @Override
    public void literal(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.PLAY_SOUND_PERMISSION));

        final var builder = Commands.argument("sound", ResourceLocationArgument.id())
                .suggests(SuggestionProviders.AVAILABLE_SOUNDS)
                .executes(context -> playSound(
                        context.getSource(),
                        SoundSource.MASTER,
                        ResourceLocationArgument.getId(context, "sound"),
                        Collections.singleton(context.getSource().getPlayerOrException()),
                        context.getSource().getPosition(),
                        1.0F,
                        1.0F,
                        0.0F
                ));

        for (SoundSource soundSource : SoundSource.values()) {
            builder.then(source(soundSource));
        }

        literal.then(builder);
    }

    private ArgumentBuilder<CommandSourceStack, ?> source(@NotNull SoundSource soundSource){
        return Commands.literal(soundSource.getName())
                .executes(context -> playSound(
                        context.getSource(),
                        soundSource,
                        ResourceLocationArgument.getId(context, "sound"),
                        Collections.singleton(context.getSource().getPlayerOrException()),
                        context.getSource().getPosition(),
                        1.0F,
                        1.0F,
                        0.0F
                ))

                .then(Commands.argument("targets", EntityArgument.players())
                        .executes(context -> playSound(
                                context.getSource(),
                                soundSource,
                                ResourceLocationArgument.getId(context, "sound"),
                                EntityArgument.getPlayers(context, "targets"),
                                context.getSource().getPosition(),
                                1.0F,
                                1.0F,
                                0.0F
                        ))

                        .then(Commands.argument("position", Vec3Argument.vec3())
                                .executes(context -> playSound(
                                        context.getSource(),
                                        soundSource,
                                        ResourceLocationArgument.getId(context, "sound"),
                                        EntityArgument.getPlayers(context, "targets"),
                                        Vec3Argument.getVec3(context, "position"),
                                        1.0F,
                                        1.0F,
                                        0.0F
                                ))

                                .then(Commands.argument("volume", FloatArgumentType.floatArg(0.0F))
                                        .executes(context -> playSound(
                                                context.getSource(),
                                                soundSource,
                                                ResourceLocationArgument.getId(context, "sound"),
                                                EntityArgument.getPlayers(context, "targets"),
                                                Vec3Argument.getVec3(context, "position"),
                                                FloatArgumentType.getFloat(context, "volume"),
                                                1.0F,
                                                0.0F
                                        ))

                                        .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
                                                .executes(context -> playSound(
                                                        context.getSource(),
                                                        soundSource,
                                                        ResourceLocationArgument.getId(context, "sound"),
                                                        EntityArgument.getPlayers(context, "targets"),
                                                        Vec3Argument.getVec3(context, "position"),
                                                        FloatArgumentType.getFloat(context, "volume"),
                                                        FloatArgumentType.getFloat(context, "pitch"),
                                                        0.0F
                                                ))

                                                .then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
                                                        .executes(context -> playSound(
                                                                context.getSource(),
                                                                soundSource,
                                                                ResourceLocationArgument.getId(context, "sound"),
                                                                EntityArgument.getPlayers(context, "targets"),
                                                                Vec3Argument.getVec3(context, "position"),
                                                                FloatArgumentType.getFloat(context, "volume"),
                                                                FloatArgumentType.getFloat(context, "pitch"),
                                                                FloatArgumentType.getFloat(context, "minVolume")
                                                        )))))));
    }

    private int playSound(CommandSourceStack source, SoundSource soundSource, ResourceLocation sound, Collection<ServerPlayer> targetsUnchecked, Vec3 pos, float volume, float pitch, float minVolume) throws CommandSyntaxException {
        final var targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        final var holder = Holder.direct(SoundEvent.createVariableRangeEvent(sound));
        final double maxDistanceSquared = Mth.square(holder.value().getRange(volume));
        final long randomSeed = source.getLevel().getRandom().nextLong();
        int playCount = 0;

        for (ServerPlayer target : targets) {
            double distanceSquared = pos.distanceToSqr(target.position());
            Vec3 soundPos = pos;
            float soundVolume = volume;

            if (distanceSquared > maxDistanceSquared) {
                if (minVolume > 0.0F) {
                    final double distance = Math.sqrt(distanceSquared);
                    soundPos = new Vec3(
                            target.getX() + (pos.x - target.getX()) / distance * 2.0,
                            target.getY() + (pos.y - target.getY()) / distance * 2.0,
                            target.getZ() + (pos.z - target.getZ()) / distance * 2.0
                    );
                    soundVolume = minVolume;
                } else {
                    continue; // Skip playing the sound for this target
                }
            }

            EssentialsUtil.sendPackets(target, new ClientboundSoundPacket(
                    holder,
                    soundSource,
                    soundPos.x(),
                    soundPos.y(),
                    soundPos.z(),
                    soundVolume,
                    pitch,
                    randomSeed
            ));

            playCount++;
        }

        if (playCount == 0) throw ERROR_TOO_FAR.create();

        if (targets.size() == 1) {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Der Klang ", Colors.SUCCESS)
                    .append(Component.text(sound.toString(), Colors.VARIABLE_VALUE)
                            .hoverEvent(showSoundInfo(soundSource, playCount, source.getLevel(), pos, volume, pitch, minVolume)))
                    .append(Component.text(" wurde für ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" gespielt.")));
        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Der Klang ", Colors.SUCCESS)
                    .append(Component.text(sound.toString(), Colors.VARIABLE_VALUE)
                            .hoverEvent(showSoundInfo(soundSource, playCount, source.getLevel(), pos, volume, pitch, minVolume)))
                    .append(Component.text(" wurde für ", Colors.SUCCESS))
                    .append(Component.text(playCount, Colors.VARIABLE_VALUE))
                    .append(Component.text(" Spieler gespielt.", Colors.SUCCESS)));
        }

        return playCount;
    }

    @Contract("_, _, _, _, _, _, _ -> new")
    private @NotNull HoverEvent<Component> showSoundInfo(@NotNull SoundSource soundSource, int listeners, @NotNull ServerLevel level, @NotNull Vec3 pos, float volume, float pitch, float minVolume) {
        return HoverEvent.showText(Component.text("Sound source: ", Colors.VARIABLE_KEY)
                .append(Component.text(soundSource.getName(), Colors.VARIABLE_VALUE))
                .appendNewline()
                .append(Component.text("Zuhörer: ", Colors.VARIABLE_KEY)
                        .append(Component.text(listeners, Colors.VARIABLE_VALUE)))
                .appendNewline()
                .append(Component.text("Position: ", Colors.VARIABLE_KEY)
                        .append(EssentialsUtil.formatLocation(Colors.VARIABLE_VALUE, new Location(level.getWorld(), pos.x(), pos.y(), pos.z()), true)))
                .appendNewline()
                .append(Component.text("Lautstärke: ", Colors.VARIABLE_KEY)
                        .append(Component.text("%sf".formatted(volume), Colors.VARIABLE_VALUE)))
                .appendNewline()
                .append(Component.text("Pitch: ", Colors.VARIABLE_KEY)
                        .append(Component.text("%sf".formatted(pitch), Colors.VARIABLE_VALUE)))
                .appendNewline()
                .append(Component.text("Mindestlautstärke: ", Colors.VARIABLE_KEY)
                        .append(Component.text("%sf".formatted(minVolume), Colors.VARIABLE_VALUE))));
    }

    private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType(PaperAdventure.asVanilla(Component.translatable("commands.playsound.failed", Colors.ERROR)));
}
 */
