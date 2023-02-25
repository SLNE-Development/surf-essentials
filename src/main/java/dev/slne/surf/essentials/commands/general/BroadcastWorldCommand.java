package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BroadcastWorldCommand {
    public static void register(){
        // Create a set of command aliases
        Set<String> aliases = new HashSet<>(Arrays.asList("worldbroadcast", "worldalert", "broadcastworld"));
        // Register the commands
        for (String command : aliases) {
            SurfEssentials.registerPluginBrigadierCommand(command, BroadcastWorldCommand::literal).setUsage("/broadcastworld <world> <broadcast message (supports color codes)>")
                    .setDescription("Broadcasts a message to all players in a certain world");
        }
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.BROADCAST_WORLD_PERMISSION));

        literal.then(Commands.argument("world", DimensionArgument.dimension())
                .then(Commands.argument("broadcast message", StringArgumentType.greedyString())
                        .executes(context -> broadcastWorld(context.getSource(), DimensionArgument.getDimension(context, "world"),
                                StringArgumentType.getString(context, "broadcast message")))
                        .suggests((context, builder) -> {
                            EssentialsUtil.suggestAllColorCodes(builder);
                            return builder.buildFuture();
                        })));
    }

    private static int broadcastWorld(CommandSourceStack source, ServerLevel level, String message) throws CommandSyntaxException {
        for (ServerPlayer player : level.players()) {
            SurfApi.getUser(player.getUUID()).thenAcceptAsync(user -> {
                user.sendMessage(SurfApi.getPrefix()
                        .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message).colorIfAbsent(SurfColors.TERTIARY)));
                user.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1);
            });
        }
        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Es wurde eine nachricht an alle Spieler in der Welt ", SurfColors.SUCCESS)
                    .append(Component.text(level.dimension().location().toString(), SurfColors.TERTIARY))
                    .append(Component.text(" geschickt!", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal(
                    "A message was send to all players in the world " + level.dimension().location()), false);
        }
        return 1;
    }
}
