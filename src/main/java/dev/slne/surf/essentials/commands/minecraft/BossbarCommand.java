package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

import static dev.slne.surf.essentials.utils.EssentialsUtil.sendError;

@DefaultQualifier(NotNull.class)
public class BossbarCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"bossbar"};
    }

    @Override
    public String usage() {
        return "/bossbar ";
    }

    @Override
    public String description() {
        return "Modify bossbar";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.BOSSBAR_PERMISSION));

        literal.then(Commands.literal("add")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .then(Commands.argument("name", ComponentArgument.textComponent())
                                .executes(context -> add(context.getSource(), ResourceLocationArgument.getId(context, "id"),
                                        ComponentArgument.getComponent(context, "name"))))));

        literal.then(Commands.literal("get")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests(SUGGEST_BOSS_BAR)
                        .then(Commands.literal("value")
                                .executes(context -> getValue(context.getSource(), getBossBar(context))))
                        .then(Commands.literal("max")
                                .executes(context -> getMax(context.getSource(), getBossBar(context))))
                        .then(Commands.literal("players")
                                .executes(context -> getPlayers(context.getSource(), getBossBar(context))))
                        .then(Commands.literal("visible")
                                .executes(context -> getVisible(context.getSource(), getBossBar(context))))));

        literal.then(Commands.literal("remove")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests(SUGGEST_BOSS_BAR)
                        .executes(context -> remove(context.getSource(), getBossBar(context)))));

        literal.then(Commands.literal("list")
                .executes(context -> list(context.getSource())));

        literal.then(Commands.literal("set")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests(SUGGEST_BOSS_BAR)
                        .then(Commands.literal("color")
                                .then(Commands.literal("blue")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.BLUE)))
                                .then(Commands.literal("green")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.GREEN)))
                                .then(Commands.literal("pink")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.PINK)))
                                .then(Commands.literal("purple")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.PURPLE)))
                                .then(Commands.literal("red")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.RED)))
                                .then(Commands.literal("white")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.WHITE)))
                                .then(Commands.literal("yellow")
                                        .executes(context -> setColor(context.getSource(), getBossBar(context), BossEvent.BossBarColor.YELLOW))))

                        .then(Commands.literal("max")
                                .then(Commands.argument("max", IntegerArgumentType.integer(1))
                                        .executes(context -> setMax(context.getSource(), getBossBar(context), IntegerArgumentType.getInteger(context, "max")))))

                        .then(Commands.literal("name")
                                .then(Commands.argument("name", ComponentArgument.textComponent())
                                        .executes(context -> setName(context.getSource(), getBossBar(context), ComponentArgument.getComponent(context, "name")))))

                        .then(Commands.literal("players")
                                .executes(context -> setPlayers(context.getSource(), getBossBar(context), Collections.emptyList()))
                                .then(Commands.argument("players", EntityArgument.players())
                                        .executes(context -> setPlayers(context.getSource(), getBossBar(context), EntityArgument.getPlayers(context, "players")))))

                        .then(Commands.literal("style")
                                .then(Commands.literal("notched_10")
                                        .executes(context -> setStyle(context.getSource(), getBossBar(context), BossEvent.BossBarOverlay.NOTCHED_10)))
                                .then(Commands.literal("notched_12")
                                        .executes(context -> setStyle(context.getSource(), getBossBar(context), BossEvent.BossBarOverlay.NOTCHED_12)))
                                .then(Commands.literal("notched_20")
                                        .executes(context -> setStyle(context.getSource(), getBossBar(context), BossEvent.BossBarOverlay.NOTCHED_20)))
                                .then(Commands.literal("notched_6")
                                        .executes(context -> setStyle(context.getSource(), getBossBar(context), BossEvent.BossBarOverlay.NOTCHED_6)))
                                .then(Commands.literal("progress")
                                        .executes(context -> setStyle(context.getSource(), getBossBar(context), BossEvent.BossBarOverlay.PROGRESS))))

                        .then(Commands.literal("value")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                        .executes(context -> setValue(context.getSource(), getBossBar(context), IntegerArgumentType.getInteger(context, "value")))))

                        .then(Commands.literal("visible")
                                .then(Commands.argument("visible", BoolArgumentType.bool())
                                        .executes(context -> setVisible(context.getSource(), getBossBar(context), BoolArgumentType.getBool(context, "visible")))))));
    }

    private static int add(CommandSourceStack source, ResourceLocation name, Component displayName) throws CommandSyntaxException {
        CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
        if (customBossEvents.get(name) != null) {
            throw ERROR_ALREADY_EXISTS.create(name.toString());
        } else {
            CustomBossEvent customBossEvent = customBossEvents.create(name, ComponentUtils.updateForEntity(source, displayName, null, 0));

            EssentialsUtil.sendSourceSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS)
                    .append(convertBossbar(customBossEvent))
                    .append(net.kyori.adventure.text.Component.text("wurde erfolgreich erstellt!", Colors.SUCCESS))));

            return customBossEvents.getEvents().size();
        }
    }

    private static int list(CommandSourceStack source) {
        Collection<CustomBossEvent> collection = source.getServer().getCustomBossEvents().getEvents();
        if (collection.isEmpty()) {
            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Es sind keine Bossbar´s aktiv!", Colors.INFO)));
        } else {

            ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();
            builder.append(net.kyori.adventure.text.Component.text("Es sind ", Colors.INFO))
                    .append(net.kyori.adventure.text.Component.text(collection.size(), Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" Bossbars aktiv:", Colors.INFO));

            for (CustomBossEvent bossBar : collection) {
                builder.append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text(",", Colors.INFO));
            }

            EssentialsUtil.sendSuccess(source, builder.build());
        }
        return collection.size();
    }

    private static int getValue(CommandSourceStack source, CustomBossEvent bossBar) {

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.INFO))
                .append(convertBossbar(bossBar))
                .append(net.kyori.adventure.text.Component.text("hat einen Wert von ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(bossBar.getValue(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("!", Colors.INFO)));

        return bossBar.getValue();
    }

    private static int getMax(CommandSourceStack source, CustomBossEvent bossBar) {

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.INFO))
                .append(convertBossbar(bossBar))
                .append(net.kyori.adventure.text.Component.text("kann maximal ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(bossBar.getMax(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" anzeigen!", Colors.INFO)));

        return bossBar.getMax();
    }

    private static int getPlayers(CommandSourceStack source, CustomBossEvent bossBar) {
        if (bossBar.getPlayers().isEmpty()) {

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Bei der Bossbar", Colors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("sind derzeit keine Spieler online!", Colors.INFO)));

        } else {

            ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();

            builder.append((net.kyori.adventure.text.Component.text("Bei der Bossbar", Colors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("sind derzeit ", Colors.INFO))
                    .append(net.kyori.adventure.text.Component.text(bossBar.getPlayers().size(), Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" Spieler online: ", Colors.INFO)));

            for (ServerPlayer bossBarPlayer : bossBar.getPlayers()) {
                builder.append(bossBarPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(", ", Colors.INFO));
            }

            EssentialsUtil.sendSuccess(source, builder.build());
        }
        return bossBar.getPlayers().size();
    }

    private static int getVisible(CommandSourceStack source, CustomBossEvent bossBar) {
        if (bossBar.isVisible()) {

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wird gerade gezeigt!", Colors.INFO)));

            return 1;

        } else {

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("ist gerade unsichtbar!", Colors.INFO)));
            return 0;
        }
    }

    private static int remove(CommandSourceStack source, CustomBossEvent bossBar) {
        CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
        bossBar.removeAllPlayers();
        customBossEvents.remove(bossBar);

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                .append(convertBossbar(bossBar))
                .append(net.kyori.adventure.text.Component.text("wurde gelöscht!", Colors.SUCCESS)));

        return 1;
    }

    private static int setColor(CommandSourceStack source, CustomBossEvent bossBar, BossEvent.BossBarColor color) throws CommandSyntaxException {
        if (bossBar.getColor().equals(color)) {
            if (source.isPlayer()) {
                sendError(source, "Die Bossbar hat bereits diese Farbe!");
            } else {
                throw ERROR_NO_COLOR_CHANGE.create();
            }

        } else {
            bossBar.setColor(color);

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("hat die Farbe geändert!", Colors.SUCCESS)));

        }
        return 1;
    }

    private static int setMax(CommandSourceStack source, CustomBossEvent bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getMax() == value) {
            if (source.isPlayer()) {
                sendError(source, "Die Bossbar hat bereits diesen Wert!");
            } else {
                throw ERROR_NO_MAX_CHANGE.create();
            }
        } else {
            bossBar.setMax(value);

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Wert für die Bossbar", Colors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wurde auf ", Colors.SUCCESS))
                    .append(net.kyori.adventure.text.Component.text(value, Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" geändert!", Colors.SUCCESS)));

        }
        return value;
    }


    private static int setName(CommandSourceStack source, CustomBossEvent bossBar, Component name) throws CommandSyntaxException {
        Component component = ComponentUtils.updateForEntity(source, name, null, 0);

        if (bossBar.getName().equals(component)) {
            sendError(source, "Die Bossbar hat bereits diesen Namen!");
        } else {
            bossBar.setName(component);

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wurde umbenannt!", Colors.SUCCESS)));

        }
        return 1;
    }

    private static int setPlayers(CommandSourceStack source, CustomBossEvent bossBar, Collection<ServerPlayer> playersUnchecked) throws CommandSyntaxException {
        Collection<ServerPlayer> players = EssentialsUtil.checkPlayerSuggestion(source, playersUnchecked);
        boolean notSamePlayers = bossBar.setPlayers(players);
        if (!notSamePlayers) {
            sendError(source, "An den Spielern hat sich nichts geändert!");
        } else {
            if (bossBar.getPlayers().isEmpty()) {
                EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("hat keine Spieler mehr!", Colors.SUCCESS)));


            } else {

                ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();
                builder.append(net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("hat jetzt ", Colors.SUCCESS))
                        .append(net.kyori.adventure.text.Component.text(bossBar.getPlayers().size(), Colors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" Spieler: ", Colors.SUCCESS));
                for (ServerPlayer bossBarPlayer : bossBar.getPlayers()) {
                    builder.append(bossBarPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(", ", Colors.INFO));
                }
                EssentialsUtil.sendSuccess(source, builder.build());
            }
        }
        return bossBar.getPlayers().size();
    }

    private static int setStyle(CommandSourceStack source, CustomBossEvent bossBar, BossEvent.BossBarOverlay style) {
        if (bossBar.getOverlay().equals(style)) {
            sendError(source, "Die Bossbar hat bereits diese Einteilung!");

        } else {
            bossBar.setOverlay(style);
            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Einteilung von der Bossbar", Colors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wurde geändert!", Colors.SUCCESS)));
            return 1;
        }
        return 0;
    }

    private static int setValue(CommandSourceStack source, CustomBossEvent bossBar, int value) {
        if (bossBar.getValue() == value) {
            sendError(source, "Die Bossbar hat bereits diesen Wert!");

        } else {
            bossBar.setValue(value);

            EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Der Wert von der Bossbar", Colors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wurde auf ", Colors.SUCCESS))
                    .append(net.kyori.adventure.text.Component.text(value, Colors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" geändert!", Colors.SUCCESS)));
            return value;
        }
        return 0;
    }

    private static int setVisible(CommandSourceStack source, CustomBossEvent bossBar, boolean visible) throws CommandSyntaxException {
        if (bossBar.isVisible() == visible) {
            if (visible) {
                if (source.isPlayer()) {
                    sendError(source, "Die Bossbar ist bereits sichtbar!");
                } else {
                    throw ERROR_ALREADY_VISIBLE.create();
                }
            } else {
                if (source.isPlayer()) {
                    sendError(source, "Die Bossbar ist bereits unsichtbar!");
                } else {
                    throw ERROR_ALREADY_HIDDEN.create();
                }
            }
            return 0;
        } else {
            bossBar.setVisible(visible);
            if (visible) {

                EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("ist nun sichtbar!", Colors.SUCCESS)));

            } else {

                EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("ist nun unsichtbar!", Colors.SUCCESS)));

            }
            return 1;
        }
    }


    public static CustomBossEvent getBossBar(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocationArgument.getId(context, "id");
        @Nullable CustomBossEvent customBossEvent = (context.getSource()).getServer().getCustomBossEvents().get(resourceLocation);
        if (customBossEvent == null) {
            throw ERROR_DOESNT_EXIST.create(resourceLocation.toString());
        } else {
            return customBossEvent;
        }
    }


    private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((name) ->
            Component.translatable("commands.bossbar.create.failed", name));

    private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((name) ->
            Component.translatable("commands.bossbar.unknown", name));

    private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.color.unchanged"));

    private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.max.unchanged"));


    private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.hidden"));

    private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.visible"));

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOSS_BAR = (context, builder) ->
            SharedSuggestionProvider.suggestResource(context.getSource().getServer().getCustomBossEvents().getIds(), builder);

    private static net.kyori.adventure.text.Component convertBossbar(CustomBossEvent bossBar) {
        ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();

        builder.append(net.kyori.adventure.text.Component.text(" [")
                        .append(net.kyori.adventure.text.Component.text(bossBar.getName().getString())
                                .append(net.kyori.adventure.text.Component.text("] "))))
                .hoverEvent(HoverEvent.showText(net.kyori.adventure.text.Component.text(bossBar.getTextId().toString(), NamedTextColor.WHITE)));

        switch (bossBar.getColor()) {
            case BLUE -> builder.color(NamedTextColor.BLUE);
            case GREEN -> builder.color(NamedTextColor.GREEN);
            case PINK -> builder.color(NamedTextColor.LIGHT_PURPLE);
            case RED -> builder.color(NamedTextColor.RED);
            case PURPLE -> builder.color(NamedTextColor.DARK_PURPLE);
            case WHITE -> builder.color(NamedTextColor.WHITE);
            case YELLOW -> builder.color(NamedTextColor.YELLOW);
            default -> builder.color(NamedTextColor.WHITE)
                    .append(net.kyori.adventure.text.Component.text("add new Color to code (line 456 in BossbarCommand.java) ", Colors.DARK_GRAY)
                            .decorate(TextDecoration.ITALIC));
        }
        return builder.build();
    }

}