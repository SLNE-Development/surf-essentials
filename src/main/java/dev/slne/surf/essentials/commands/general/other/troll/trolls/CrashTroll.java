package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class CrashTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> crashTroll(LiteralArgumentBuilder<CommandSourceStack> literal){
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    EssentialsUtil.sendSystemMessage(context.getSource(), Component.text("Bist du sicher, dass du das Spiel crashen möchtest?", Colors.INFO)
                            .append(Component.newline())
                            .append(EssentialsUtil.getPrefix()
                                    .append(Component.text("Klicke zum Bestätigen ", Colors.INFO)
                                            .append(Component.text("Hier", Colors.SECONDARY)
                                                    .clickEvent(ClickEvent.suggestCommand("/troll crash %s confirm"
                                                            .formatted(EntityArgument.getPlayer(context, "player").getName().getString())))))));
                    return 1;
                })
                .then(Commands.literal("confirm")
                        .executes(context -> crash(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int crash(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(source, player);
        var connection = player.connection;

        var explosion = new ClientboundExplodePacket(
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Double.MAX_VALUE,
                Float.MAX_VALUE,
                Collections.emptyList(),
                new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE)
        );

        Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), bukkitTask -> {
            for (int i = 0; i < 100; i++) {
                connection.send(explosion);
                connection.send(getParticlePacket(ParticleTypes.EXPLOSION, player));
                connection.send(getParticlePacket(ParticleTypes.EXPLOSION_EMITTER, player));
                connection.send(getParticlePacket(ParticleTypes.TOTEM_OF_UNDYING, player));
                connection.send(getParticlePacket(ParticleTypes.LARGE_SMOKE, player));
                connection.send(getParticlePacket(ParticleTypes.SMOKE, player));
                connection.send(getParticlePacket(ParticleTypes.DRAGON_BREATH, player));
                connection.send(getParticlePacket(ParticleTypes.CLOUD, player));
                connection.send(getParticlePacket(ParticleTypes.CRIT, player));
                connection.send(getParticlePacket(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player));
                connection.send(getParticlePacket(ParticleTypes.SCRAPE, player));
            }
        });

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, (Component.text("Bei ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(player))
                    .append(Component.text(" crasht nun das Spiel!", Colors.SUCCESS)));
        }else{
            EssentialsUtil.sendSystemMessage(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text("´s game is no crashing", Colors.GREEN)));
        }
        return 1;
    }

    @Contract("_, _ -> new")
    private static<T extends ParticleOptions> @NotNull ClientboundLevelParticlesPacket getParticlePacket(T option, ServerPlayer player){
        return new ClientboundLevelParticlesPacket(
                option,
                true,
                player.getX(),
                player.getY(),
                player.getZ(),
                0,
                1,
                0,
                100_000,
                Integer.MAX_VALUE
        );
    }
}
