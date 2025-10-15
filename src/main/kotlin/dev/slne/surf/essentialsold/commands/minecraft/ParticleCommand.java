package dev.slne.surf.essentialsold.commands.minecraft;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import dev.jorel.commandapi.arguments.LocationType;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.wrappers.ParticleData;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.ParticleWrapper;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ParticleCommand extends EssentialsCommand {
    private static final double MAX_FORCE_DISTANCE = 512.0;
    private static final double MAX_DISTANCE = 32.0;

    public ParticleCommand() {
        super("bukkitParticle", "bukkitParticle <bukkitParticle> [<pos>] [<delta>] [<speed>] [<count>] [<mode>] [<player>]", "Spawns a bukkitParticle");

        withPermission(Permissions.PARTICLE_PERMISSION);

        then(particleArgument("bukkitParticle")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                        sender.getCallee(),
                        args.getUnchecked("bukkitParticle"),
                        sender.getLocation(),
                        new Location(null, 0, 0, 0),
                        0.0F,
                        0,
                        false,
                        new ArrayList<>(Bukkit.getOnlinePlayers())
                ))
                .then(locationArgument("position", LocationType.PRECISE_POSITION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                sender.getCallee(),
                                args.getUnchecked("bukkitParticle"),
                                Objects.requireNonNull(args.getUnchecked("position")),
                                new Location(null, 0, 0, 0),
                                0.0F,
                                0,
                                false,
                                new ArrayList<>(Bukkit.getOnlinePlayers())
                        ))
                        .then(locationArgument("delta", LocationType.PRECISE_POSITION)
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                        sender.getCallee(),
                                        args.getUnchecked("bukkitParticle"),
                                        Objects.requireNonNull(args.getUnchecked("position")),
                                        Objects.requireNonNull(args.getUnchecked("delta")),
                                        0.0F,
                                        0,
                                        false,
                                        new ArrayList<>(Bukkit.getOnlinePlayers())
                                ))
                                .then(floatArgument("speed", 0.0F)
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                                sender.getCallee(),
                                                args.getUnchecked("bukkitParticle"),
                                                Objects.requireNonNull(args.getUnchecked("position")),
                                                Objects.requireNonNull(args.getUnchecked("delta")),
                                                args.getUnchecked("speed"),
                                                0,
                                                false,
                                                new ArrayList<>(Bukkit.getOnlinePlayers())
                                        ))
                                        .then(integerArgument("count", 0)
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                                        sender.getCallee(),
                                                        args.getUnchecked("bukkitParticle"),
                                                        Objects.requireNonNull(args.getUnchecked("position")),
                                                        Objects.requireNonNull(args.getUnchecked("delta")),
                                                        args.getUnchecked("speed"),
                                                        args.getUnchecked("count"),
                                                        false,
                                                        new ArrayList<>(Bukkit.getOnlinePlayers())
                                                ))
                                                .then(booleanArgument("force")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                                                sender.getCallee(),
                                                                args.getUnchecked("bukkitParticle"),
                                                                Objects.requireNonNull(args.getUnchecked("position")),
                                                                Objects.requireNonNull(args.getUnchecked("delta")),
                                                                args.getUnchecked("speed"),
                                                                args.getUnchecked("count"),
                                                                Boolean.TRUE.equals(args.getUnchecked("force")),
                                                                new ArrayList<>(Bukkit.getOnlinePlayers())
                                                        ))
                                                        .then(playersArgument("viewers")
                                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> showParticles(
                                                                        sender.getCallee(),
                                                                        args.getUnchecked("bukkitParticle"),
                                                                        Objects.requireNonNull(args.getUnchecked("position")),
                                                                        Objects.requireNonNull(args.getUnchecked("delta")),
                                                                        args.getUnchecked("speed"),
                                                                        args.getUnchecked("count"),
                                                                        Boolean.TRUE.equals(args.getUnchecked("force")),
                                                                        args.getUnchecked("viewers")
                                                                ))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private int showParticles(CommandSender source, ParticleData<?> parameters, Location pos, Location delta, Float speed, Integer count, boolean force, Collection<Player> viewersUnchecked) throws WrapperCommandSyntaxException {
        val viewers = EssentialsUtil.checkPlayerSuggestion(source, new ArrayList<>(viewersUnchecked));
        val playerManager = PacketEvents.getAPI().getPlayerManager();
        val particleWrapper = new ParticleWrapper<>(parameters);


        val particle = particleWrapper.getPacketEventsParticle();
        val particlePacket = new WrapperPlayServerParticle(
                particle,
                force,
                new Vector3d(pos.x(), pos.y(), pos.z()),
                new Vector3f((float) delta.x(), (float) delta.y(), (float) delta.z()),
                speed,
                count
        );
        int countParticlesShown = 0;

        for (Player player : viewers) {
            if (!player.getWorld().equals(pos.getWorld())) continue;
            if (player.getLocation().distanceSquared(pos) > (force ? MAX_FORCE_DISTANCE : MAX_DISTANCE)) continue;

            playerManager.sendPacket(player, particlePacket);
            countParticlesShown++;
        }

        if (countParticlesShown == 0) throw Exceptions.ERROR_NO_PARTICLES_SHOWN;

        val particleName = particle.getType().getName().toString();

        val positionX = format(pos.x());
        val positionY = format(pos.y());
        val positionZ = format(pos.z());
        val position = positionX + " " + positionY + " " + positionZ;

        val deltaX = format(delta.x());
        val deltaY = format(delta.y());
        val deltaZ = format(delta.z());
        val deltaString = deltaX + " " + deltaY + " " + deltaZ;

        val forceString = (force) ? "Ja" : "Nein";

        EssentialsUtil.sendSuccess(source, Component.text("Der Partikel ", Colors.SUCCESS)
                .append(Component.text("[", Colors.GRAY))
                .append(Component.text(particleName, Colors.VARIABLE_VALUE)
                        .hoverEvent(HoverEvent.showText(Component.text("Partikel: ", Colors.VARIABLE_KEY)
                                .append(Component.text(particleName, Colors.VARIABLE_VALUE))
                                .append(Component.newline())
                                .append(Component.text("Position: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(position, Colors.VARIABLE_VALUE)))
                                .append(Component.newline())
                                .append(Component.text("Delta: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(deltaString, Colors.VARIABLE_VALUE)))
                                .append(Component.newline())
                                .append(Component.text("Anzahl: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(count, Colors.VARIABLE_VALUE)))
                                .append(Component.newline())
                                .append(Component.text("Geschwindigkeit: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(speed, Colors.VARIABLE_VALUE)))
                                .append(Component.newline())
                                .append(Component.text("Erzwungen: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(forceString, Colors.VARIABLE_VALUE)))
                                .append(Component.newline())
                                .append(Component.text("Betrachter: ", Colors.VARIABLE_KEY)
                                        .append(Component.text(viewers.size(), Colors.VARIABLE_VALUE))))))
                .append(Component.text("]", Colors.GRAY))
                .append(Component.text(" wird ", Colors.SUCCESS))
                .append(Component.text(countParticlesShown, Colors.VARIABLE_VALUE))
                .append(Component.text((countParticlesShown == 1) ? " Spieler" : " Spielern", Colors.SUCCESS))
                .append(Component.text(" gezeigt!", Colors.SUCCESS)));

        return countParticlesShown;
    }

    private String format(double input) {
        return String.valueOf(EssentialsUtil.makeDoubleReadable(input));
    }
}
