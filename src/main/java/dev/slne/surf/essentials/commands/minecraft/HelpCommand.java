package dev.slne.surf.essentials.commands.minecraft;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PermissionTag(name = Permissions.HELP_PERMISSION, desc = "Allows to get help with commands")
public class HelpCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"help"};
    }

    @Override
    public String usage() {
        return "/help <Command | Plugin> [<page]";
    }

    @Override
    public String description() {
        return "Shows you the usage of commands";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HELP_PERMISSION));

        for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            literal.then(Commands.literal(plugin.getName())
                    .executes(context -> showPluginHelp(context.getSource(), plugin, 1))
                    .then(Commands.argument("page", IntegerArgumentType.integer(1, Arrays.stream(getAllCommandsFromPlugin(null, plugin)).toList().size()))
                            .executes(context -> showPluginHelp(context.getSource(), plugin, IntegerArgumentType.getInteger(context, "page")))));
        }

        Bukkit.getServer().getCommandMap().getKnownCommands().forEach((s, command) -> {
            literal.then(Commands.literal(s)
                    .executes(context -> showCommandHelp(context.getSource(), command)));
        });

    }

    private int showCommandHelp(CommandSourceStack source, Command command) throws CommandSyntaxException {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        builder.append(header()
                .append(newLine())
                .append(newLine()));

        if (!command.getDescription().equals("")) {
            builder.append(description(command.getDescription()))
                    .append(newLine())
                    .append(newLine());
        }
        builder.append(correctUsage(command.getUsage()))
                .append(newLine())
                .append(newLine());

        if (command.getAliases().size() != 0) {
            builder.append(aliases(command.getAliases()))
                    .append(newLine())
                    .append(newLine());
        }

        if (command.getPermission() != null) {
            builder.append(permission(command.getPermission())
                    .append(newLine()));
        }

        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAccept(user -> user.sendMessage(builder.build()));
        }else {
            source.sendSuccess(PaperAdventure.asVanilla(builder.build()), false);
        }

        return 1;
    }

    private int showPluginHelp(CommandSourceStack source, Plugin plugin, int page) throws CommandSyntaxException {
        String[] allCommands = getAllCommandsFromPlugin(source, plugin);
        String[] currentPageCommands = allCommands[page - 1].translateEscapes().split("\n");
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

        builder.append(header()
                .append(newLine()));

        for (String command : currentPageCommands) {
            builder.append(EssentialsUtil.deserialize(command).colorIfAbsent(SurfColors.TERTIARY))
                    .append(newLine());
        }

        builder.append(newLine());

        if (page != 1){
            builder.append(Component.text("⬅ Zurück", SurfColors.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("Gehe eine Seite zurück", SurfColors.INFO)))
                    .clickEvent(ClickEvent.runCommand("/help %s %d".formatted(plugin.getName(), page - 1)))
                    .append(Component.text("──", SurfColors.GRAY)));
        }else {
            builder.append(Component.text("────────", SurfColors.GRAY));
        }

        builder.append(Component.text("───────", SurfColors.GRAY));

        if (allCommands.length >= page + 1){
            builder.append(Component.text("➡ Weiter", SurfColors.GREEN)
                    .hoverEvent(HoverEvent.showText(Component.text("Gehe eine Seite weiter", SurfColors.INFO)))
                    .clickEvent(ClickEvent.runCommand("/help %s %d".formatted(plugin.getName(), page + 1))));
        }else {
            builder.append(Component.text("────────", SurfColors.GRAY));
        }


        if (source.isPlayer()) {
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(builder.build()));
        }else {
            source.sendSuccess(PaperAdventure.asVanilla(builder.build()), false);
        }


        return 1;
    }


    private Component prefix(){
        return Component.text(">> ", SurfColors.DARK_GRAY)
                .append(Component.text("Help", SurfColors.GREEN))
                .append(Component.text(" | ", SurfColors.DARK_GRAY));
    }

    private Component header(){
        return Component.text("╾────────── ", SurfColors.GRAY)
                .append(Component.text("Help", SurfColors.GREEN))
                .append(Component.text(" ──────────╼", SurfColors.GRAY));
    }

    private Component correctUsage(String usage){
        return Component.text("Korrekte Benutzung: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(usage, SurfColors.TERTIARY)).colorIfAbsent(SurfColors.TERTIARY);
    }

    private Component description(String description){
        return Component.text("Beschreibung: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(description, SurfColors.TERTIARY));
    }

    private Component aliases(List<String> aliases){
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
        builder.append(Component.text("Aliases: ", TextColor.fromHexString("#e67e22")));

        for (String alias : aliases) {
            builder.append(Component.text(alias, SurfColors.TERTIARY)
                    .append(Component.text(", ", SurfColors.INFO)));
        }

        return builder.build();
    }

    private Component permission(String permission){
        return Component.text("Permission: ", TextColor.fromHexString("#e67e22"))
                .append(Component.text(permission, SurfColors.TERTIARY));
    }

    private Component newLine(){
        return Component.newline().append(prefix());
    }

    private String[] getAllCommandsFromPlugin(@Nullable CommandSourceStack source, @NotNull Plugin plugin){
        source = (source == null) ? MinecraftServer.getServer().createCommandSourceStack() : source;

        HelpMap helpMap = Bukkit.getServer().getHelpMap();
        HelpTopic topic = helpMap.getHelpTopic(plugin.getName());

        if (topic == null){
            return new String[]{"§cNo help for " + plugin.getName()};
        }

        String fullText = topic.getFullText(source.getBukkitSender()).translateEscapes();
        String[] splitText = fullText.split("\n");

        List<String> stringArrayList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < splitText.length; i++) {
            stringBuilder.append(splitText[i]).append("\n");
            if ((i + 1) % 7 == 0) {
                stringArrayList.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }
        if (stringBuilder.length() > 0) {
            stringArrayList.add(stringBuilder.toString());
        }

        return stringArrayList.toArray(new String[0]);
    }
}
