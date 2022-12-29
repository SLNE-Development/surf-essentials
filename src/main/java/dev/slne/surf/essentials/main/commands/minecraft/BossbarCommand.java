package dev.slne.surf.essentials.main.commands.minecraft;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
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
import net.minecraft.world.entity.player.Player;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

import static dev.slne.surf.essentials.main.utils.EssentialsUtil.sendError;

@DefaultQualifier(NotNull.class)
public class BossbarCommand {
    /**
     * Registers the bossbar command.
     */
    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("bossbar", BossbarCommand::literal);
    }

    /**
     * Creates the bossbar command structure.
     *
     * @param literal the root command literal
     */
    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.bossbar"));

        //--------------------------------------------------------------------------------------------------------------
        //                                      add a bossbar
        //--------------------------------------------------------------------------------------------------------------
        literal.then(Commands.literal("add")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .then(Commands.argument("name", ComponentArgument.textComponent())
                                .executes(context -> add(context.getSource(), ResourceLocationArgument.getId(context, "id"),
                                        ComponentArgument.getComponent(context, "name"))))));

        //--------------------------------------------------------------------------------------------------------------
        //                                      get a bossbar
        //--------------------------------------------------------------------------------------------------------------
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

        //--------------------------------------------------------------------------------------------------------------
        //                                      remove a bossbar
        //--------------------------------------------------------------------------------------------------------------
        literal.then(Commands.literal("remove")
                .then(Commands.argument("id", ResourceLocationArgument.id())
                        .suggests(SUGGEST_BOSS_BAR)
                        .executes(context -> remove(context.getSource(), getBossBar(context)))));

        //--------------------------------------------------------------------------------------------------------------
        //                                      list all bossbars
        //--------------------------------------------------------------------------------------------------------------
        literal.then(Commands.literal("list")
                .executes(context -> list(context.getSource())));

        //--------------------------------------------------------------------------------------------------------------
        //                                      change values from a bossbar
        //--------------------------------------------------------------------------------------------------------------
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

    /**
     * Creates a new bossbar with the given ID and display name.
     *
     * @param source the command source
     * @param name the ID of the bossbar
     * @param displayName the display name of the bossbar
     * @return the number of bossbars in the server
     * @throws CommandSyntaxException if the bossbar already exists
     */
    private static int add(CommandSourceStack source, ResourceLocation name, Component displayName) throws CommandSyntaxException {
        CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
        if (customBossEvents.get(name) != null) {
            throw ERROR_ALREADY_EXISTS.create(name.toString());
        } else {
            CustomBossEvent customBossEvent = customBossEvents.create(name, ComponentUtils.updateForEntity(source, displayName, null, 0));
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS)
                                .append(convertBossbar(customBossEvent))
                                .append(net.kyori.adventure.text.Component.text("wurde erfolgreich erstellt!", SurfColors.SUCCESS)))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.create.success", customBossEvent.getDisplayName()), false);
            }
            return customBossEvents.getEvents().size();
        }
    }

    /**
     * Lists all the available bossbars.
     *
     * @param source the command source
     * @return the number of bossbars
     * @throws CommandSyntaxException if the command fails
     */
    private static int list(CommandSourceStack source) throws CommandSyntaxException {
        Collection<CustomBossEvent> collection = source.getServer().getCustomBossEvents().getEvents();
        if (collection.isEmpty()) {
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Es sind keine Bossbar´s aktiv!", SurfColors.INFO))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.list.bars.none"), false);
            }

        } else {
            if (source.isPlayer()) {
                ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();
                builder.append(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Es sind ", SurfColors.INFO))
                        .append(net.kyori.adventure.text.Component.text(collection.size(), SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" Bossbars aktiv:", SurfColors.INFO)));

                for (CustomBossEvent bossBar : collection) {
                    builder.append(convertBossbar(bossBar))
                            .append(net.kyori.adventure.text.Component.text(",", SurfColors.INFO));
                }

                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(builder.build()));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.list.bars.some", collection.size(),
                        ComponentUtils.formatList(collection, CustomBossEvent::getDisplayName)), false);
            }
        }
        return collection.size();
    }

    /**
     * Gets the value of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @return the value of the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int getValue(CommandSourceStack source, CustomBossEvent bossBar) throws CommandSyntaxException {
        if (source.isPlayer()) {
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("hat einen Wert von ", SurfColors.INFO))
                    .append(net.kyori.adventure.text.Component.text(bossBar.getValue(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text("!", SurfColors.INFO))));
        } else {
            source.sendSuccess(Component.translatable("commands.bossbar.get.value", bossBar.getDisplayName(), bossBar.getValue()), false);
        }
        return bossBar.getValue();
    }

    /**
     * Gets the maximum value of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @return the maximum value of the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int getMax(CommandSourceStack source, CustomBossEvent bossBar) throws CommandSyntaxException {
        if (source.isPlayer()) {
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.INFO))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("kann maximal ", SurfColors.INFO))
                    .append(net.kyori.adventure.text.Component.text(bossBar.getMax(), SurfColors.TERTIARY))
                    .append(net.kyori.adventure.text.Component.text(" anzeigen!", SurfColors.INFO))));
        } else {
            source.sendSuccess(Component.translatable("commands.bossbar.get.max", bossBar.getDisplayName(), bossBar.getMax()), false);
        }
        return bossBar.getMax();
    }

    /**
     * Gets the players that are currently viewing the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @return the number of players viewing the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int getPlayers(CommandSourceStack source, CustomBossEvent bossBar) throws CommandSyntaxException {
        if (bossBar.getPlayers().isEmpty()) {
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Bei der Bossbar", SurfColors.INFO))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("sind derzeit keine Spieler online!", SurfColors.INFO))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.get.players.none", bossBar.getDisplayName()), false);
            }
        } else {
            if (source.isPlayer()) {
                ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();

                builder.append(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Bei der Bossbar", SurfColors.INFO))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("sind derzeit ", SurfColors.INFO))
                        .append(net.kyori.adventure.text.Component.text(bossBar.getPlayers().size(), SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" Spieler online: ", SurfColors.INFO)));

                for (ServerPlayer bossBarPlayer : bossBar.getPlayers()) {
                    builder.append(bossBarPlayer.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(", ", SurfColors.INFO));
                }

                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(builder.build()));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.get.players.some", bossBar.getDisplayName(),
                        bossBar.getPlayers().size(), ComponentUtils.formatList(bossBar.getPlayers(), Player::getDisplayName)), false);
            }
        }
        return bossBar.getPlayers().size();
    }

    /**
     * Gets the visibility of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @return 1 if the bossbar is visible, 0 if it is hidden
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int getVisible(CommandSourceStack source, CustomBossEvent bossBar) throws CommandSyntaxException {
        if (bossBar.isVisible()) {
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.INFO))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("wird gerade gezeigt!", SurfColors.INFO))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.get.visible.visible", bossBar.getDisplayName()), false);
            }
            return 1;

        } else {
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.INFO))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("ist gerade unsichtbar!", SurfColors.INFO))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.get.visible.hidden", bossBar.getDisplayName()), false);
            }
            return 0;
        }
    }

    /**
     * Removes the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar to remove
     * @return the number of bossbars in the server
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int remove(CommandSourceStack source, CustomBossEvent bossBar) throws CommandSyntaxException {
        CustomBossEvents customBossEvents = source.getServer().getCustomBossEvents();
        bossBar.removeAllPlayers();
        customBossEvents.remove(bossBar);
        if (source.isPlayer()) {
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                    .append(convertBossbar(bossBar))
                    .append(net.kyori.adventure.text.Component.text("wurde gelöscht!", SurfColors.SUCCESS))));
        } else {
            source.sendSuccess(Component.translatable("commands.bossbar.remove.success", bossBar.getDisplayName()), false);
        }
        return 1;
    }

    /**
     * Sets the color of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param color the new color of the bossbar
     * @return 1 if the color of the bossbar was changed, 0 otherwise
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int setColor(CommandSourceStack source, CustomBossEvent bossBar, BossEvent.BossBarColor color) throws CommandSyntaxException {
        if (bossBar.getColor().equals(color)) {
            if (source.isPlayer()) {
                sendError(source, "Die Bossbar hat bereits diese Farbe!");
            } else {
                throw ERROR_NO_COLOR_CHANGE.create();
            }

        } else {
            bossBar.setColor(color);
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("hat die Farbe geändert!", SurfColors.SUCCESS))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.set.color.success", bossBar.getDisplayName()), false);
            }
        }
        return 1;
    }

    /**
     * Sets the maximum value of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param value the new maximum value
     * @return the new maximum value
     * @throws CommandSyntaxException if the bossbar does not exist or the value is not changed
     */
    private static int setMax(CommandSourceStack source, CustomBossEvent bossBar, int value) throws CommandSyntaxException {
        if (bossBar.getMax() == value) {
            if (source.isPlayer()) {
                sendError(source, "Die Bossbar hat bereits diesen Wert!");
            } else {
                throw ERROR_NO_MAX_CHANGE.create();
            }
        } else {
            bossBar.setMax(value);
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Der Wert für die Bossbar", SurfColors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("wurde auf ", SurfColors.SUCCESS))
                        .append(net.kyori.adventure.text.Component.text(value, SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" geändert!", SurfColors.SUCCESS))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.set.max.success", bossBar.getDisplayName(), value), false);
            }
        }
        return value;
    }

    /**
     * Sets the name of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param name the new name of the bossbar
     * @return 1 if the name was changed, 0 if the name was already set to the specified value
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int setName(CommandSourceStack source, CustomBossEvent bossBar, Component name) throws CommandSyntaxException {
        Component component = ComponentUtils.updateForEntity(source, name, null, 0);
        if (bossBar.getName().equals(component)) {
            if (source.isPlayer()) {
                sendError(source, "Die Bossbar hat bereits diesen Namen!");
            } else {
                throw ERROR_NO_NAME_CHANGE.create();
            }
        } else {
            bossBar.setName(component);
            if (source.isPlayer()) {
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("wurde umbenannt!", SurfColors.SUCCESS))));
            } else {
                source.sendSuccess(Component.translatable("commands.bossbar.set.name.success", bossBar.getDisplayName()), false);
            }
        }
        return 1;
    }

    /**
     * Sets the players of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param players the players to set
     * @return the number of players in the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int setPlayers(CommandSourceStack source, CustomBossEvent bossBar, Collection<ServerPlayer> players) throws CommandSyntaxException {
        boolean notSamePlayers = bossBar.setPlayers(players);
        if (!notSamePlayers) {
            if (source.isPlayer()) {
                sendError(source, "An den Spielern hat sich nichts geändert!");
            } else {
                throw ERROR_NO_PLAYER_CHANGE.create();
            }

        } else {
            if (bossBar.getPlayers().isEmpty()) {
                if (source.isPlayer()) {
                    SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                            .append(convertBossbar(bossBar))
                            .append(net.kyori.adventure.text.Component.text("hat keine Spieler mehr!", SurfColors.SUCCESS))));
                } else {
                    source.sendSuccess(Component.translatable("commands.bossbar.set.players.success.none", bossBar.getDisplayName()), false);
                }

            } else {
                if (source.isPlayer()) {
                    ComponentBuilder<TextComponent, TextComponent.Builder> builder = net.kyori.adventure.text.Component.text();
                    builder.append(SurfApi.getPrefix()
                            .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                            .append(convertBossbar(bossBar))
                            .append(net.kyori.adventure.text.Component.text("hat jetzt ", SurfColors.SUCCESS))
                            .append(net.kyori.adventure.text.Component.text(bossBar.getPlayers().size(), SurfColors.TERTIARY))
                            .append(net.kyori.adventure.text.Component.text(" Spieler: ", SurfColors.SUCCESS)));
                    for (ServerPlayer bossBarPlayer : bossBar.getPlayers()) {
                        builder.append(bossBarPlayer.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                                .append(net.kyori.adventure.text.Component.text(", ", SurfColors.INFO));
                    }
                    SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(builder.build()));
                } else {
                    source.sendSuccess(Component.translatable("commands.bossbar.set.players.success.some", bossBar.getDisplayName(),
                            players.size(), ComponentUtils.formatList(players, Player::getDisplayName)), false);
                }
            }
        }
        return bossBar.getPlayers().size();
    }

    /**
     * Sets the style of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param style the style to set
     * @return 1 if the style was successfully changed, 0 otherwise
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int setStyle(CommandSourceStack source, CustomBossEvent bossBar, BossEvent.BossBarOverlay style)throws CommandSyntaxException{
        if (bossBar.getOverlay().equals(style)) {
            if (source.isPlayer()){
                sendError(source, "Die Bossbar hat bereits diese Einteilung!");
            }else{
                throw ERROR_NO_STYLE_CHANGE.create();
            }
        } else {
            bossBar.setOverlay(style);
            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Die Einteilung von der Bossbar", SurfColors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("wurde geändert!", SurfColors.SUCCESS))));
            }else {
                source.sendSuccess(Component.translatable("commands.bossbar.set.style.success", bossBar.getDisplayName()), true);
            }
            return 1;
        }
        return 0;
    }

    /**
     * Sets the value of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param value the new value for the bossbar
     * @return the new value of the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist or if the value does not change
     */
    private static int setValue(CommandSourceStack source, CustomBossEvent bossBar, int value) throws CommandSyntaxException{
        if (bossBar.getValue() == value) {
            if (source.isPlayer()){
                sendError(source, "Die Bossbar hat bereits diesen Wert!");
            }else {
                throw ERROR_NO_VALUE_CHANGE.create();
            }
        } else {
            bossBar.setValue(value);
            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(net.kyori.adventure.text.Component.text("Der Wert von der Bossbar", SurfColors.SUCCESS))
                        .append(convertBossbar(bossBar))
                        .append(net.kyori.adventure.text.Component.text("wurde auf ", SurfColors.SUCCESS))
                        .append(net.kyori.adventure.text.Component.text(value, SurfColors.TERTIARY))
                        .append(net.kyori.adventure.text.Component.text(" geändert!", SurfColors.SUCCESS))));
            }else {
                source.sendSuccess(Component.translatable("commands.bossbar.set.value.success", bossBar.getDisplayName(), value), false);
            }
            return value;
        }
        return 0;
    }

    /**
     * Sets the visibility of the specified bossbar.
     *
     * @param source the command source
     * @param bossBar the bossbar
     * @param visible the visibility
     * @return 1 if the visibility was successfully set, 0 otherwise
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    private static int setVisible(CommandSourceStack source, CustomBossEvent bossBar, boolean visible) throws CommandSyntaxException{
        if (bossBar.isVisible() == visible) {
            if (visible) {
                if (source.isPlayer()){
                    sendError(source, "Die Bossbar ist bereits sichtbar!");
                }else {
                    throw ERROR_ALREADY_VISIBLE.create();
                }
            } else {
                if (source.isPlayer()){
                    sendError(source, "Die Bossbar ist bereits unsichtbar!");
                }else {
                    throw ERROR_ALREADY_HIDDEN.create();
                }
            }
            return 0;
        } else {
            bossBar.setVisible(visible);
            if (visible) {
                if (source.isPlayer()){
                    SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                            .append(convertBossbar(bossBar))
                            .append(net.kyori.adventure.text.Component.text("ist nun sichtbar!", SurfColors.SUCCESS))));
                }else {
                    source.sendSuccess(Component.translatable("commands.bossbar.set.visible.success.visible", bossBar.getDisplayName()), false);
                }

            } else {
                if (source.isPlayer()){
                    SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(net.kyori.adventure.text.Component.text("Die Bossbar", SurfColors.SUCCESS))
                            .append(convertBossbar(bossBar))
                            .append(net.kyori.adventure.text.Component.text("ist nun unsichtbar!", SurfColors.SUCCESS))));
                }else {
                    source.sendSuccess(Component.translatable("commands.bossbar.set.visible.success.hidden", bossBar.getDisplayName()), false);
                }

            }
            return 1;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    //                                           other Stuff
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Gets the bossbar with the specified ID.
     *
     * @param context the command context
     * @return the bossbar
     * @throws CommandSyntaxException if the bossbar does not exist
     */
    public static CustomBossEvent getBossBar(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation resourceLocation = ResourceLocationArgument.getId(context, "id");
        CustomBossEvent customBossEvent = (context.getSource()).getServer().getCustomBossEvents().get(resourceLocation);
        if (customBossEvent == null) {
            throw ERROR_DOESNT_EXIST.create(resourceLocation.toString());
        } else {
            return customBossEvent;
        }
    }

    /**
     * The command exception type for when a bossbar with the specified name already exists.
     */
    private static final DynamicCommandExceptionType ERROR_ALREADY_EXISTS = new DynamicCommandExceptionType((name) ->
            Component.translatable("commands.bossbar.create.failed", name));

    /**
     * The command exception type for when a bossbar with the specified name does not exist.
     */
    private static final DynamicCommandExceptionType ERROR_DOESNT_EXIST = new DynamicCommandExceptionType((name) ->
            Component.translatable("commands.bossbar.unknown", name));

    /**
     * The command exception type for when no players are changed in the bossbar.
     */
    private static final SimpleCommandExceptionType ERROR_NO_PLAYER_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.players.unchanged"));

    /**
     * The command exception type for when the bossbar name is not changed.
     */
    private static final SimpleCommandExceptionType ERROR_NO_NAME_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.name.unchanged"));

    /**
     * The command exception type for when the bossbar color is not changed.
     */
    private static final SimpleCommandExceptionType ERROR_NO_COLOR_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.color.unchanged"));

    /**
     * The command exception type for when the bossbar style is not changed.
     */
    private static final SimpleCommandExceptionType ERROR_NO_STYLE_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.style.unchanged"));

    /**
     * The command exception type for when the bossbar value is not changed.
     */
    private static final SimpleCommandExceptionType ERROR_NO_VALUE_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.value.unchanged"));

    /**
     * The command exception type for when the bossbar max value is not changed.
     */
    private static final SimpleCommandExceptionType ERROR_NO_MAX_CHANGE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.max.unchanged"));

    /**
     The command exception type for when the bossbar is already hidden.
     */
    private static final SimpleCommandExceptionType ERROR_ALREADY_HIDDEN = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.hidden"));

    /**
     The command exception type for when the bossbar is already visible.
     */
    private static final SimpleCommandExceptionType ERROR_ALREADY_VISIBLE = new SimpleCommandExceptionType(Component.translatable("commands.bossbar.set.visibility.unchanged.visible"));

    /**
     A suggestion provider for bossbar names.
     */
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_BOSS_BAR = (context, builder) ->
            SharedSuggestionProvider.suggestResource(context.getSource().getServer().getCustomBossEvents().getIds(), builder);

    private static net.kyori.adventure.text.Component convertBossbar(CustomBossEvent bossBar){
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
                        .append(net.kyori.adventure.text.Component.text("add new Color to code (line 681 in BossbarCommand.java) ", SurfColors.DARK_GRAY)
                                .decorate(TextDecoration.ITALIC));
        }
        return builder.build();
    }

}