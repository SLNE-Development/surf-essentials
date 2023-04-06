package dev.slne.surf.essentials.listeners;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.commands.minecraft.HelpCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.jetbrains.annotations.NotNull;

public class CommandRegisterListener implements Listener {
    @SuppressWarnings({"rawTypes", "UnstableApiUsage"})
    @EventHandler
    public void onCommandRegistered(CommandRegisteredEvent<CommandSourceStack> event) {
        if (event.getCommandLabel().equalsIgnoreCase("help")){
            var builder = LiteralArgumentBuilder.<CommandSourceStack>literal("help");
            new HelpCommand().literal(builder);
            event.setLiteral(builder.build());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnknownCommand(@NotNull UnknownCommandEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender) return;
        if (event.message() == null || LegacyComponentSerializer.builder().build().serialize(event.message()).equalsIgnoreCase("Unknown command. Type \"/help\" for help.")){
            event.message(null);
            event.getSender().sendMessage(EssentialsUtil.getPrefix()
                    .append(PaperAdventure.asAdventure(Component.translatable("command.unknown.command")
                            .withStyle(ChatFormatting.RED)))
                    .appendNewline()
                    .append(EssentialsUtil.getPrefix())
                    .append(net.kyori.adventure.text.Component.text(event.getCommandLine(), Colors.GRAY)
                            .clickEvent(ClickEvent.suggestCommand("/%s".formatted(event.getCommandLine()))))
                    .append(PaperAdventure.asAdventure(Component.translatable("command.context.here").withStyle(ChatFormatting.RED))));
            return;
        }

        String[] lines = LegacyComponentSerializer.legacySection().serialize(event.message()).split("\\n");
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();

        if (lines[0] == null) return;
        builder.append(EssentialsUtil.getPrefix().append(LegacyComponentSerializer.legacySection().deserialize(lines[0])
                .colorIfAbsent(Colors.GRAY)));

        for (int i = 1; i < lines.length; i++) {
            builder.append(net.kyori.adventure.text.Component.newline()
                    .append(EssentialsUtil.getPrefix().append(LegacyComponentSerializer.legacySection().deserialize(lines[i]))
                            .colorIfAbsent(Colors.GRAY)));
        }
        event.message(builder.build());
    }
}
