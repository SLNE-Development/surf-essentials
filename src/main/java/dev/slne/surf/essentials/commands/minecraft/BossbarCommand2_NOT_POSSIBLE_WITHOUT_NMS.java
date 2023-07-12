package dev.slne.surf.essentials.commands.minecraft;

/**
import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.nms.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.boss.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.boss.CraftBossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static dev.slne.surf.essentials.utils.abtract.MessageUtil.sendError;

public class BossbarCommand2 extends EssentialsCommand {
    public BossbarCommand2() {
        super("bossbar", "bossbar", "Modify bossbars");

        withPermission(Permissions.BOSSBAR_PERMISSION);

    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private int add(@NotNull CommandSender source, NamespacedKey name, String title, Optional<BarColor> barColor, Optional<BarStyle> barStyle) throws WrapperCommandSyntaxException {
        final Server server = source.getServer();
        final KeyedBossBar existingBossBar = server.getBossBar(name);

        if (existingBossBar != null) throw Exceptions.ERROR_BOSS_BAR_ALREADY_EXISTS.create(existingBossBar);

        final KeyedBossBar bossBar = server.createBossBar(name, title, barColor.orElse(BarColor.WHITE), barStyle.orElse(BarStyle.SOLID));

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(bossBar))
                .append(net.kyori.adventure.text.Component.text(" wurde erfolgreich erstellt!", Colors.SUCCESS))));

        return ImmutableList.copyOf(server.getBossBars()).size();
    }

    private int list(@NotNull CommandSender source) throws WrapperCommandSyntaxException {
        final List<KeyedBossBar> bossBars = ImmutableList.copyOf(source.getServer().getBossBars());
        if (bossBars.isEmpty()) throw Exceptions.ERROR_NO_BOSS_BARS;

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Es sind ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(bossBars.size(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text(" Bossbars aktiv: ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.join(
                        JoinConfiguration.commas(true),
                        bossBars.stream()
                                .map(EssentialsUtil::getDisplayName)
                                .toList()
                )));

        return bossBars.size();
    }

    private int getValue(CommandSender source, BossBar bossBar) {

        EssentialsUtil.sendSuccess(source, (net.kyori.adventure.text.Component.text("Die Bossbar", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(bossBar))
                .append(net.kyori.adventure.text.Component.text("hat einen Wert von ", Colors.INFO))
                .append(net.kyori.adventure.text.Component.text(bossBar.getProgress(), Colors.TERTIARY))
                .append(net.kyori.adventure.text.Component.text("!", Colors.INFO)));

        net.kyori.adventure.bossbar.BossBar

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
 */
