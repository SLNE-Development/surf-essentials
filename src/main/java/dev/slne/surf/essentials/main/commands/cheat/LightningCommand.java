package dev.slne.surf.essentials.main.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("lighting", LightningCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        // required permission
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.lighting"));
        // This adds a required argument to the command, which expects one or more players
        literal.then(Commands.argument("players", EntityArgument.players())
                // If the command is executed with just the required "players" argument
                .executes(context -> lightingCustom(context, new AtomicInteger(1)))
                // If the "lighting" command is executed with the required "players" argument and an additional "amount" argument
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, new AtomicInteger(IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int lightingCustom(CommandContext<CommandSourceStack> context, AtomicInteger power) throws CommandSyntaxException {
        // Get a list of players from the "players" argument
        EntityArgument.getPlayers(context, "players").forEach(serverPlayer -> {
            Player target = Bukkit.getPlayer(serverPlayer.getUUID());
            World world = target.getWorld();
            // Sets the player's weather to DOWNFALL (i.e. stormy weather)
            target.setPlayerWeather(WeatherType.DOWNFALL);

            // Each time the task runs, it strikes lightning at the player's location, and decrements the power value by 1
            Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
                if (power.get() < 1) bukkitTask.cancel();
                world.strikeLightning(target.getLocation());
                power.getAndDecrement();
            }, 20, 5);

            // The task will reset the player's weather to the default after "power" times the task has run
            Bukkit.getScheduler().runTaskLaterAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    target.resetPlayerWeather(), 20L *power.get() + 40);
        });

        if (context.getSource().isPlayer()){
            Player player = Objects.requireNonNull(context.getSource().getPlayer()).getBukkitEntity();

            if (EntityArgument.getPlayers(context, "players").size() == 1){
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    try {
                        user.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Der Blitz hat ", SurfColors.SUCCESS))
                                .append(EntityArgument.getPlayers(context, "players").stream().findFirst().get().getBukkitEntity().teamDisplayName())
                                .append(Component.text(" getroffen!", SurfColors.SUCCESS)));
                    } catch (CommandSyntaxException ignored) {}
                });
            }else {
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Der Blitz hat ", SurfColors.SUCCESS))
                        .append(Component.text())
                        .append(Component.text(" getroffen!", SurfColors.SUCCESS))));
            }

        }

        return 1;
    }
}
