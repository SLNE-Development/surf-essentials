package dev.slne.surf.essentials.main.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.network.chat.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class BroadcastWorldCommand {
    public static void register(){
        // Create a set of command aliases
        Set<String> aliases = new HashSet<>(Arrays.asList("worldbroadcast", "worldalert", "broadcastworld"));
        // Register the commands
        for (String command : aliases) {
            SurfEssentials.registerPluginBrigadierCommand(command, BroadcastWorldCommand::literal);
        }
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        // Require the sender to have the permission
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.broadcastworld"));
        // Specify the behavior of the command when it is executed without the required arguments
        literal.executes(context -> {
            // Check if the command was executed by a player
            if (context.getSource().isPlayer()){
                // If the command was executed by a player, send a usage message to the player
                SurfApi.getUser(Bukkit.getPlayer(context.getSource().getPlayer().getUUID())).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                        .append(Component.text("/broadcastworld <world> <broadcast message>", SurfColors.TERTIARY))));
                // Return 1 to indicate that the command was executed successfully
                return 1;
            }else {
                // If the command was executed by the console, send a usage message
                context.getSource().sendFailure(net.minecraft.network.chat.Component.literal("Correct usage: /broadcastworld <world> <broadcast message>")
                        .withStyle(style -> style.withColor(TextColor.parseColor("RED"))));
                return 1;
            }
        });
        // Add an argument for the world to which the broadcast message should be sent
        literal.then(Commands.argument("world", DimensionArgument.dimension())
                // Add an argument for the broadcast message
                .then(Commands.argument("broadcast message", StringArgumentType.greedyString())
                        // Specify the command execution logic
                        .executes(BroadcastWorldCommand::run)
                        .suggests((context, builder) -> {
                            EssentialsUtil.suggestAllColorCodes(builder);
                            return builder.buildFuture();
                        })));
    }

    private static int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // Get the specified world
        World world = context.getSource().withLevel(DimensionArgument.getDimension(context, "world")).getBukkitWorld();
        // Create the broadcast message
        Component message = SurfApi.getPrefix()
                .color(SurfColors.YELLOW)
                .append(Component.text(ChatColor.translateAlternateColorCodes('&',
                        context.getArgument("broadcast message", String.class))));

        // Send the broadcast message to all players in the specified world
        for (Player playerInWorld : Objects.requireNonNull(world).getPlayers()) {
            Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), bukkitTask ->
                    Bukkit.getPlayer(playerInWorld.getName()).sendMessage(message));
        }

        // Send a success message to the sender
        if (context.getSource().isPlayer()) {
            Player player = Bukkit.getPlayer(Objects.requireNonNull(context.getSource().getPlayer()).getUUID());
            Bukkit.getScheduler().runTaskAsynchronously(SurfEssentials.getInstance(), bukkitTask -> player.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Es wurde eine nachricht an alle Spieler in der Welt ", SurfColors.SUCCESS))
                    .append(Component.text(world.getName(), SurfColors.TERTIARY))
                    .append(Component.text(" geschickt!", SurfColors.SUCCESS))));

        } else {
            context.getSource().sendSuccess(net.minecraft.network.chat.Component.literal(
                    "A message was send to all players in the world " + world.getName()), false);
        }
        return 1;
    }
}
