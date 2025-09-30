package dev.slne.surf.essentials.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand extends EssentialsCommand { // TODO
    public LightningCommand() {
        super("lighting", "lighting <players> [<amount>] [<realLighting>]", "Strike lightning at a player");

        withPermission(Permissions.LIGHTING_PERMISSION);

        then(playersArgument("players")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> lightingCustom(sender.getCallee(), args.getUnchecked("players"), 1, false))
                .then(integerArgument("amount", 1, 20)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> lightingCustom(sender.getCallee(), args.getUnchecked("players"), args.getUnchecked("amount"), false))
                        .then(booleanArgument("realLighting")
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> lightingCustom(sender.getCallee(), args.getUnchecked("players"), args.getUnchecked("amount"), args.getUnchecked("realLighting"))))));
    }

    private int lightingCustom(CommandSender sender, Collection<Player> targetsUnchecked, Integer power, Boolean realLighting) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(sender, targetsUnchecked);

        for (Player player : targets) {
            player.setPlayerWeather(WeatherType.DOWNFALL);

            AtomicInteger powerAtomic = new AtomicInteger(power);

            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (powerAtomic.get() < 1) bukkitTask.cancel();
                strikeLighting(player, realLighting);
                powerAtomic.getAndDecrement();
            }, 20, 5);


            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    player.resetPlayerWeather(), 20L * power + 40);
        }

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(sender, Component.text("Der Blitz hat ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text(" getroffen!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(sender, Component.text("Der Blitz hat ", Colors.SUCCESS)
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" Spieler getroffen.", Colors.SUCCESS)));
        }

        return 1;
    }

    private void strikeLighting(final Player player, boolean reaLighting) {
        val world = player.getWorld();
        val playerLocation = player.getLocation();

        if (reaLighting) world.strikeLightning(playerLocation);
        else world.strikeLightningEffect(playerLocation);
    }
}
