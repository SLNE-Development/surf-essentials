package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import org.bukkit.Bukkit;

import java.util.List;

public class AlertCommand{
    private static final List<String> aliases = List.of("alert", "broadcast", "al");

    public static void register(){
        for (String alias : aliases) {
            SurfEssentials.registerPluginBrigadierCommand(alias, AlertCommand::literal);
        }
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.ALERT_PERMISSION));

        literal.then(Commands.argument("message", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    EssentialsUtil.suggestAllColorCodes(builder);
                    return builder.buildFuture();
                })
                .executes(context -> alert(context.getSource(), StringArgumentType.getString(context, "message"))));
    }

    private static int alert(CommandSourceStack source, String message){
        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message).colorIfAbsent(Colors.TERTIARY)));

        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            serverPlayer.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f);
        }
        return 1;
    }
}
