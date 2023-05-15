package dev.slne.surf.essentials.listener.listeners;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.commands.general.other.help.HelpCommand;
import dev.slne.surf.essentials.commands.minecraft.ReloadCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link Listener} that registers and handles commands in the server.
 */
public class CommandRegisterListener implements Listener {

    /**
     * Overrides Bukkit's default commands when they are getting registered to the server
     * @param event the command registered event
     */
    @SuppressWarnings({"UnstableApiUsage"})
    @EventHandler
    public void onCommandRegistered(@NotNull CommandRegisteredEvent<CommandSourceStack> event) {
        final var label = event.getCommandLabel();

        switch (label.toLowerCase()) {
            case "help", "?" -> buildCommand(event, label, HelpCommand.getOrCreateCommand(HelpCommand.class));
            case "reload", "rl" -> buildCommand(event, label, ReloadCommand.getOrCreateCommand(ReloadCommand.class));
        }
    }

    /**
     * Handles unknown commands that are executed by players and sends a custom message
     * @param event the unknown command event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnknownCommand(@NotNull UnknownCommandEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender || event.getSender() instanceof RemoteConsoleCommandSender) return;
        if (event.message() == null || LegacyComponentSerializer.builder().build().serialize(event.message()).equalsIgnoreCase("Unknown command. Type \"/help\" for help.")){
            event.message(null);
            event.getSender().sendMessage(EssentialsUtil.getPrefix()
                    .append(PaperAdventure.asAdventure(Component.translatable("command.unknown.command")
                            .withStyle(ChatFormatting.RED)))
                    .appendNewline()
                    .append(EssentialsUtil.getPrefix())
                    .append(net.kyori.adventure.text.Component.text(event.getCommandLine(), Colors.RED, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.suggestCommand("/%s".formatted(event.getCommandLine()))))
                    .append(PaperAdventure.asAdventure(Component.translatable("command.context.here").withStyle(ChatFormatting.RED))));
            return;
        }

        String[] lines = LegacyComponentSerializer.legacySection().serialize(event.message()).split("\\n");
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();

        if (lines[0] == null) return;
        builder.append(EssentialsUtil.getPrefix().append(EssentialsUtil.deserialize(lines[0])
                .colorIfAbsent(Colors.GRAY)));

        for (int i = 1; i < lines.length; i++) {
            builder.append(net.kyori.adventure.text.Component.newline()
                    .append(EssentialsUtil.getPrefix().append(EssentialsUtil.deserialize(lines[i]))
                            .colorIfAbsent(Colors.GRAY)));
        }
        event.message(builder.build());
    }

    /**
     * Builds a command to be registered in the server based on the {@link CommandRegisteredEvent<CommandSourceStack>}
     * @param event the command registered event
     * @param name the name of the command
     * @param command the command to be built
     */
    @SuppressWarnings("UnstableApiUsage")
    private void buildCommand(final CommandRegisteredEvent<CommandSourceStack> event, final String name, final @Nullable BrigadierCommand command) {
        if (command == null) return;

        final var builder = LiteralArgumentBuilder.<CommandSourceStack>literal(name);
        command.literal(builder);

        event.setLiteral(builder.build());
    }
}
