package dev.slne.surf.essentials.main.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class LightningCommand {
    public static void register(){
        // This registers the "lighting" command with the Brigadier command framework, and associates it with the "literal" method
        SurfEssentials.registerPluginBrigadierCommand("lighting", LightningCommand::literal);
    }

    /**
     * This method defines the structure and behavior of the "lighting" command.
     *
     * @param literal a LiteralArgumentBuilder object that is used to define the structure of the command
     */
    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        // This adds a required argument to the command, which expects one or more players
        literal.then(Commands.argument("players", EntityArgument.players())
                // If the command is executed with just the required "players" argument
                .executes(context -> lightingCustom(context, new AtomicInteger(1)))
                // If the "lighting" command is executed with the required "players" argument and an additional "amount" argument
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> lightingCustom(context, new AtomicInteger(IntegerArgumentType.getInteger(context, "amount"))))));
    }

    /**
     * This method performs the action of the "lighting" command.
     *
     * @param context a CommandContext object that contains the arguments and context of the command
     * @param power an AtomicInteger that represents the number of times the lightning strike task should be run
     * @return 1 if the command succeeds, or 0 if it fails
     * @throws CommandSyntaxException if the command is used improperly
     */
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

        return 1;
    }
}
