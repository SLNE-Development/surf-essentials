package dev.slne.surf.essentials.main.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.List;

public class AlertCommand{
    public static String PERMISSION;
    private static final List<String> aliases = List.of("alert", "broadcast", "al");

    public static void register(){
        for (String alias : aliases) {
            SurfEssentials.registerPluginBrigadierCommand(alias, AlertCommand::literal).setUsage("/alert <message>")
                    .setDescription("Sends a message to all online players");
        }
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        literal.then(Commands.argument("message", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    EssentialsUtil.suggestAllColorCodes(builder);
                    return builder.buildFuture();
                })
                .executes(context -> alert(context.getSource(), StringArgumentType.getString(context, "message"))));
    }

    private static int alert(CommandSourceStack source, String message){
        Bukkit.broadcast(SurfApi.getPrefix()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message).colorIfAbsent(SurfColors.TERTIARY)));

        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            SurfApi.getUser(serverPlayer.getUUID()).thenAcceptAsync(user -> user.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 1));
        }
        return 1;
    }
}
