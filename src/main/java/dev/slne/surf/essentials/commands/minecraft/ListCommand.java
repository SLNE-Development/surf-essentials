package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListCommand {
    public static void register(){
        // Register the  command
        SurfEssentials.registerPluginBrigadierCommand("list", ListCommand::literal).setUsage("/list [<uuids>]")
                .setDescription("Lists all visible online players and if specified with their uuid");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        // Require the permission to use the command
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.LIST_PERMISSION));
        // If the user only types "list", run the listPlayerName method
        literal.executes(ListCommand::listPlayerName);
        // If the user types "list uuids", run the listPlayerUUID method
        literal.then(Commands.literal("uuids")
                .executes(ListCommand::listPlayerUUID));
    }

    private static int listPlayerName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

            // Build the message using the ComponentBuilder
            builder.append(Component.text("Es sind gerade ", Colors.INFO)
                            .append(Component.text(visibleOnlinePlayer.size(), Colors.TERTIARY))
                            .append(Component.text(" von ", Colors.INFO))
                            .append(Component.text(Bukkit.getServer().getMaxPlayers(), Colors.TERTIARY))
                            .append(Component.text(" Spielern online: ", Colors.INFO)));
            for (Component playerName : visibleOnlinePlayer) {
                builder.append(playerName)
                        .append(Component.text(", ", Colors.INFO));
            }
            EssentialsUtil.sendSuccess(context.getSource(), builder.build());

        }else{
            // If the command was executed by the server, send the server a message with the list of online players
            context.getSource().sendSuccess(net.minecraft.network.chat.Component.literal("There are " + visibleOnlinePlayer.size() + " of a max of "
                    + context.getSource().getServer().getPlayerList().getMaxPlayers() + " players online: " + Arrays.toString(visibleOnlinePlayerNoComponent.toArray())), false);
        }
        return 1;
    }

    private static int listPlayerUUID(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // Call the format method with a function that creates a component for each player with their name and UUID
        return format(context.getSource(), (entityPlayer) ->
                net.minecraft.network.chat.Component.translatable("commands.list.nameAndId", entityPlayer.getName(), entityPlayer.getGameProfile().getId()));
    }

    private static int format(CommandSourceStack source, Function<ServerPlayer, net.minecraft.network.chat.Component> nameProvider) throws CommandSyntaxException {
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
            list = list.stream().filter((ep) -> EssentialsUtil.canPlayerSeePlayer(Objects.requireNonNull(source.getPlayer()), ep)).collect(Collectors.toList());

            // Build the message using the ComponentBuilder
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

            builder.append(Component.text("Es sind gerade ", Colors.INFO)
                            .append(Component.text(list.size(), Colors.TERTIARY))
                            .append(Component.text(" von ", Colors.INFO))
                            .append(Component.text(Bukkit.getServer().getMaxPlayers(), Colors.TERTIARY))
                            .append(Component.text(" Spielern online: ", Colors.INFO)));
            for (ServerPlayer serverPlayer : list) {
                builder.append(Component.text("(%s)".formatted(serverPlayer.getBukkitEntity().getName()), Colors.TERTIARY))
                        .append(Component.text(" %s, ".formatted(serverPlayer.getBukkitEntity().getUniqueId()), Colors.SECONDARY));
            }
            EssentialsUtil.sendSuccess(source, builder.build());
            return 1;
        }
        // If the command was executed by the server, send the server a message with the list of players and their UUIDs
        net.minecraft.network.chat.Component ichatbasecomponent = ComponentUtils.formatList(list, nameProvider);
        // Use the translatable method to create a translated component with the list of players and their UUIDs
        source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.list.players", list.size(), playerlist.getMaxPlayers(), ichatbasecomponent), false);
        return list.size();
    }
}
