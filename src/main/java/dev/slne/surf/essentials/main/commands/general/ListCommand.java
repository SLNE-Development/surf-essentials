package dev.slne.surf.essentials.main.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListCommand {
    public static void register(){
        // Register the  command
        SurfEssentials.registerPluginBrigadierCommand("list", ListCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        // Require the permission to use the command
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.list"));
        // If the user only types "list", run the listPlayerName method
        literal.executes(ListCommand::listPlayerName);
        // If the user types "list uuids", run the listPlayerUUID method
        literal.then(Commands.literal("uuids")
                .executes(ListCommand::listPlayerUUID));
    }

    private static int listPlayerName(CommandContext<CommandSourceStack> context){
        // Create two lists to store player names and components
        ArrayList<String> visibleOnlinePlayerNoComponent = new ArrayList<>();
        ArrayList<Component> visibleOnlinePlayer = new ArrayList<>();

        // Get a list of all online players and filter out vanished players
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (!EssentialsUtil.isVanished(player)) {
                visibleOnlinePlayer.add(player.teamDisplayName());
                visibleOnlinePlayerNoComponent.add(player.getName());
            }
        });

        if (context.getSource().isPlayer()) {
            // If the command was executed by a player, send the player a message with the list of online players
            Player player = Bukkit.getPlayer(context.getSource().getPlayer().getUUID());
            ComponentBuilder builder = Component.text();

            // Build the message using the ComponentBuilder
            builder.append(SurfApi.getPrefix()
                    .append(Component.text("Es sind gerade ", SurfColors.INFO)
                            .append(Component.text(visibleOnlinePlayer.size(), SurfColors.TERTIARY))
                            .append(Component.text(" von ", SurfColors.INFO))
                            .append(Component.text(Bukkit.getServer().getMaxPlayers(), SurfColors.TERTIARY))
                            .append(Component.text(" Spielern online: ", SurfColors.INFO))));
            for (Component playerName : visibleOnlinePlayer) {
                builder.append(playerName)
                        .append(Component.text(", ", SurfColors.INFO));
            }
            player.sendMessage(builder.build());

        }else{
            // If the command was executed by the server, send the server a message with the list of online players
            context.getSource().sendSuccess(net.minecraft.network.chat.Component.literal("There are " + visibleOnlinePlayer.size() + " of a max of "
                    + Bukkit.getServer().getMaxPlayers() + " players online: " + Arrays.toString(visibleOnlinePlayerNoComponent.toArray())), false);
        }
        return 1;
    }

    private static int listPlayerUUID(CommandContext<CommandSourceStack> context){
        // Call the format method with a function that creates a component for each player with their name and UUID
        return format(context.getSource(), (entityplayer) -> {
            return net.minecraft.network.chat.Component.translatable("commands.list.nameAndId", entityplayer.getName(), entityplayer.getGameProfile().getId());
        });
    }

    private static int format(CommandSourceStack source, Function<ServerPlayer, net.minecraft.network.chat.Component> nameProvider) {
        // Get the player list from the server
        PlayerList playerlist = source.getServer().getPlayerList();
        // Filter out vanished players
        for (ServerPlayer player : playerlist.getPlayers()) {
            if (EssentialsUtil.isVanished(player.getBukkitEntity())){
                playerlist.remove(player);
            }
        }
        // Get the filtered list of players
        List<ServerPlayer> list = playerlist.getPlayers();

        if (source.isPlayer()) {
            // If the command was executed by a player, send the player a message with the list of players and their UUIDs
            Player sender = (Player)source.getBukkitSender();
            // Filter the list of players to only include players that the sender can see
            list = list.stream().filter((ep) -> sender.canSee(ep.getBukkitEntity())).collect(Collectors.toList());

            // Build the message using the ComponentBuilder
            ComponentBuilder builder = Component.text();

            builder.append(SurfApi.getPrefix()
                    .append(Component.text("Es sind gerade ", SurfColors.INFO)
                            .append(Component.text(list.size(), SurfColors.TERTIARY))
                            .append(Component.text(" von ", SurfColors.INFO))
                            .append(Component.text(Bukkit.getServer().getMaxPlayers(), SurfColors.TERTIARY))
                            .append(Component.text(" Spielern online: ", SurfColors.INFO))));
            for (ServerPlayer serverPlayer : list) {
                builder.append(Component.text("(%s)".formatted(serverPlayer.getBukkitEntity().getName()), SurfColors.TERTIARY))
                        .append(Component.text(" %s, ".formatted(serverPlayer.getBukkitEntity().getUniqueId()), SurfColors.SECONDARY));
            }
            sender.sendMessage(builder.build());
            return 1;
        }
        // If the command was executed by the server, send the server a message with the list of players and their UUIDs
        net.minecraft.network.chat.Component ichatbasecomponent = ComponentUtils.formatList(list, nameProvider);
        // Use the translatable method to create a translated component with the list of players and their UUIDs
        source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.list.players", list.size(), playerlist.getMaxPlayers(), ichatbasecomponent), false);
        return list.size();
    }
}
