package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import static dev.slne.surf.essentials.utils.EssentialsUtil.getPrefix;

/**
 * A utility class for sending messages to players or command sources.
 *
 * @author twisti
 * @since 1.0.2
 */
@SuppressWarnings("unused")
public abstract class MessageUtil extends CraftUtil {
    private static final boolean IS_DEBUGGING;
    private static final ComponentLogger COMPONENT_LOGGER;

    /**
     * Returns whether the plugin is currently in debugging mode or not.
     *
     * @return true if the plugin is in debugging mode, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDebugging() {
        return IS_DEBUGGING;
    }

    // error
    //---------------------------------------------------------------------------------

    /**
     * Sends an error message to the specified command source player with the specified error message.
     *
     * @param source the command source to send the message to
     * @param error  the error message to send
     */
    public static void sendError(CommandSourceStack source, String error) {
        sendError(source, Component.text(error));
    }

    /**
     * Sends an error message to the specified command source player with the specified error message.
     *
     * @param source the command source to send the message to
     * @param error  the error message to send
     */
    public static void sendError(CommandSourceStack source, Component error) {
        sendSourceError(source, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified audience with the specified error message.
     *
     * @param audience   the audience to send the message to
     * @param error      the error message to send
     * @param <Audience> the type of the audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendError(Audience audience, Component error) {
        sendMessage(audience, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified audience with the specified error message.
     *
     * @param audience   the audience to send the message to
     * @param error      the error message to send
     * @param <Audience> the type of the audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendError(Audience audience, String error) {
        sendError(audience, Component.text(error));
    }

    /**
     * Sends an error message to the specified command source with the specified error message.
     *
     * @param commandSource the command source to send the message to
     * @param error         the error message to send
     * @param <Source>      the type of the command source to send the message to
     */
    public static <Source extends CommandSource> void sendError(Source commandSource, Component error) {
        sendMessage(commandSource, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified command source with the specified error message.
     *
     * @param commandSource the command source to send the message to
     * @param error         the error message to send
     * @param <Source>      the type of the command source to send the message to
     */
    public static <Source extends CommandSource> void sendError(Source commandSource, String error) {
        sendError(commandSource, Component.text(error));
    }


    // success
    //---------------------------------------------------------------------------------

    /**
     * Sends a success message to the specified command source player with the specified success message.
     *
     * @param source  the command source to send the message to
     * @param success the success message to send
     */
    public static void sendSuccess(CommandSourceStack source, Component success) {
        sendSourceSuccess(source, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source player with the specified success message.
     *
     * @param source  the command source to send the message to
     * @param success the success message to send
     */
    public static void sendSuccess(CommandSourceStack source, String success) {
        sendSuccess(source, Component.text(success));
    }

    /**
     * Sends a success message to the specified audience with the specified success message.
     *
     * @param audience   the audience to send the message to
     * @param success    the success message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendSuccess(Audience audience, String success) {
        sendSuccess(audience, Component.text(success));
    }

    /**
     * Sends a success message to the specified audience with the specified success message.
     *
     * @param audience   the audience to send the message to
     * @param success    the success message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendSuccess(Audience audience, Component success) {
        sendMessage(audience, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source with the specified success message.
     *
     * @param commandSource the command source to send the message to
     * @param success       the success message to send
     * @param <Source>      the type of command source to send the message to
     */
    public static <Source extends CommandSource> void sendSuccess(Source commandSource, Component success) {
        sendMessage(commandSource, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source with the specified success message.
     *
     * @param commandSource the command source to send the message to
     * @param success       the success message to send
     * @param <Source>      the type of command source to send the message to
     */
    public static <Source extends CommandSource> void sendSuccess(Source commandSource, String success) {
        sendSuccess(commandSource, Component.text(success));
    }


    // info
    //---------------------------------------------------------------------------------

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param source the command source to send the message to
     * @param info   the info message to send
     */
    public static void sendInfo(CommandSourceStack source, Component info) {
        sendMessage(source, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param source the command source to send the message to
     * @param info   the info message to send
     */
    public static void sendInfo(CommandSourceStack source, String info) {
        sendInfo(source, Component.text(info));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param commandSource the command source to send the message to
     * @param info          the info message to send
     * @param <Source>      the type of command source to send the message to
     */
    public static <Source extends CommandSource> void sendInfo(Source commandSource, Component info) {
        sendMessage(commandSource, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param commandSource the command source to send the message to
     * @param info          the info message to send
     * @param <Source>      the type of command source to send the message to
     */
    public static <Source extends CommandSource> void sendInfo(Source commandSource, String info) {
        sendInfo(commandSource, Component.text(info));
    }

    /**
     * Sends an info message to the specified audience with the specified info message.
     *
     * @param audience   the audience to send the message to
     * @param info       the info message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendInfo(Audience audience, Component info) {
        sendMessage(audience, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified audience with the specified info message.
     *
     * @param audience   the audience to send the message to
     * @param info       the info message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendInfo(Audience audience, String info) {
        sendInfo(audience, Component.text(info));
    }


    // debug
    //---------------------------------------------------------------------------------

    /**
     * Sends a debug message to the specified command source player with the specified debug message.
     *
     * @param sourceStack the command source to send the message to
     * @param debug       the debug message to send
     */
    public static void sendDebug(CommandSourceStack sourceStack, Component debug) {
        if (!IS_DEBUGGING) return;
        sourceStack.getBukkitSender().sendMessage(debug.colorIfAbsent(Colors.DEBUG));
    }

    /**
     * Sends a debug message to the specified command source player with the specified debug message.
     *
     * @param sourceStack the command source to send the message to
     * @param debug       the debug message to send
     */
    public static void sendDebug(CommandSourceStack sourceStack, String debug) {
        sendDebug(sourceStack, Component.text(debug));
    }

    /**
     * Sends a debug message to the console.
     *
     * @param debug the debug message to send
     */
    public static void sendDebug(Component debug) {
        if (!IS_DEBUGGING) return;
        COMPONENT_LOGGER.info(debug.colorIfAbsent(Colors.DEBUG));
    }

    /**
     * Sends a debug message to the console.
     *
     * @param debug the debug message to send
     */
    public static void sendDebug(String debug) {
        sendDebug(Component.text(debug));
    }


    // normal message
    //---------------------------------------------------------------------------------

    /**
     * Sends a message to the specified command source player with the specified message.
     *
     * @param source  the command source to send the message to
     * @param message the message to send
     */
    public static void sendMessage(CommandSourceStack source, Component message) {
        source.getBukkitSender().sendMessage(getPrefix()
                .append(message));
    }

    /**
     * Sends a message to the specified audience.
     *
     * @param audience   the audience to send the message to
     * @param message    the message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendMessage(@NotNull Audience audience, Component message) {
        audience.sendMessage(getPrefix()
                .append(message));
    }

    /**
     * Sends a message to the specified command source.
     *
     * @param commandSource the command source to send the message to
     * @param message       the message to send
     * @param <Source>      the type of command source to send the message to
     */
    public static <Source extends CommandSource> void sendMessage(@NotNull Source commandSource, Component message) {
        commandSource.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }


    // system message
    //---------------------------------------------------------------------------------

    public static void sendSystemMessage(@NotNull CommandSourceStack source, Component message) {
        source.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }

    public static <Audience extends net.kyori.adventure.audience.Audience> void sendSystemMessage(@NotNull Audience audience, Component message) {
        audience.sendMessage(getPrefix()
                .append(message));
    }

    public static <Source extends CommandSource> void sendSystemMessage(@NotNull Source commandSource, Component message) {
        commandSource.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }


    // command source stack message
    //---------------------------------------------------------------------------------

    /**
     * Sends a success message to the specified command source stack with the specified success message, optionally broadcasting to ops.
     *
     * @param source         the command source to send the message to
     * @param message        the success message to send
     * @param broadcastToOps whether to broadcast the message to ops
     */
    public static void sendSourceSuccess(@NotNull CommandSourceStack source, @NotNull Component message, boolean broadcastToOps) {
        source.sendSuccess(() -> PaperAdventure.asVanilla(getPrefix()
                .append(message.colorIfAbsent(Colors.SUCCESS))), broadcastToOps);
    }

    /**
     * Sends a success message to the specified command source stack with the specified success message, without broadcasting to ops.
     *
     * @param source  the command source to send the message to
     * @param message the success message to send
     */
    public static void sendSourceSuccess(CommandSourceStack source, Component message) {
        sendSourceSuccess(source, message, false);
    }

    /**
     * Sends a success message to the specified command source stack with the specified success message as a String, without broadcasting to ops.
     *
     * @param source  the command source to send the message to
     * @param message the success message to send
     */
    public static void sendSourceSuccess(CommandSourceStack source, String message) {
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source player with the specified success message as a String, optionally broadcasting to ops.
     *
     * @param source         the command source to send the message to
     * @param message        the success message to send
     * @param broadcastToOps whether to broadcast the message to ops
     */
    public static void sendSourceSuccess(CommandSourceStack source, String message, boolean broadcastToOps) {
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS), broadcastToOps);
    }


    /**
     * Sends an error message to the specified command source stack with the specified error message.
     *
     * @param source  the command source to send the message to
     * @param message the error message to send
     */
    public static void sendSourceError(@NotNull CommandSourceStack source, @NotNull Component message) {
        source.sendFailure(PaperAdventure.asVanilla(getPrefix()
                .append(message.colorIfAbsent(Colors.ERROR))));
    }

    /**
     * Sends an error message to the specified command source stack with the specified error message.
     *
     * @param source  the command source to send the message to
     * @param message the error message to send
     */
    public static void sendSourceError(CommandSourceStack source, String message) {
        sendSourceError(source, Component.text(message, Colors.ERROR));
    }

    /**
     * Sends an information message to the specified command source stack with the specified message.
     *
     * @param source  the command source to send the message to
     * @param message the information message to send
     */
    public static void sendSourceInfo(CommandSourceStack source, @NotNull Component message) {
        sendSourceMessage(source, message.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an information message to the specified command source stack with the specified message.
     *
     * @param source  the command source to send the message to
     * @param message the information message to send
     */
    public static void sendSourceInfo(CommandSourceStack source, String message) {
        sendSourceInfo(source, Component.text(message, Colors.INFO));
    }

    /**
     * Sends a message to the specified command source stack with the specified message.
     *
     * @param source  the command source to send the message to
     * @param message the message to send
     */
    public static void sendSourceMessage(@NotNull CommandSourceStack source, Component message) {
        source.getBukkitSender().sendMessage(getPrefix()
                .append(message));
    }

    /**
     * Sends a message to the specified command source stack with the specified text message.
     *
     * @param source  the command source to send the message to
     * @param message the text message to send
     */
    public static void sendSourceMessage(CommandSourceStack source, String message) {
        sendSourceMessage(source, Component.text(message, Colors.GRAY));
    }

    // exceptions
    // ---------------------------------------------------------------------------------

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param message the message to create the exception with
     * @return the created exception
     */
    public static CommandSyntaxException createException(Component message) {
        return new SimpleCommandExceptionType(PaperAdventure.asVanilla(message)).create();
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param t       the object to create the exception with
     * @param message the message to create the exception with
     * @param <T>     the type of the object to create the exception with
     * @return the created exception
     */
    public static <T> CommandSyntaxException createException(T t, ComponentConsumer<T> message) {
        return createException(message.apply(t));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @return the created exception
     */
    public static <A, B> CommandSyntaxException createException(A a, B b, BiComponentConsumer<A, B> message) {
        return createException(message.apply(a, b));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @return the created exception
     */
    public static <A, B, C> CommandSyntaxException createException(A a, B b, C c, TriComponentConsumer<A, B, C> message) {
        return createException(message.apply(a, b, c));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D> CommandSyntaxException createException(A a, B b, C c, D d, QuadComponentConsumer<A, B, C, D> message) {
        return createException(message.apply(a, b, c, d));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E> CommandSyntaxException createException(A a, B b, C c, D d, E e, PentaComponentConsumer<A, B, C, D, E> message) {
        return createException(message.apply(a, b, c, d, e));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param f       the sixth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @param <F>     the type of the sixth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E, F> CommandSyntaxException createException(A a, B b, C c, D d, E e, F f, HexaComponentConsumer<A, B, C, D, E, F> message) {
        return createException(message.apply(a, b, c, d, e, f));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param f       the sixth object to create the exception with
     * @param g       the seventh object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @param <F>     the type of the sixth object to create the exception with
     * @param <G>     the type of the seventh object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E, F, G> CommandSyntaxException createException(A a, B b, C c, D d, E e, F f, G g, HeptaComponentConsumer<A, B, C, D, E, F, G> message) {
        return createException(message.apply(a, b, c, d, e, f, g));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param f       the sixth object to create the exception with
     * @param g       the seventh object to create the exception with
     * @param h       the eighth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @param <F>     the type of the sixth object to create the exception with
     * @param <G>     the type of the seventh object to create the exception with
     * @param <H>     the type of the eighth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E, F, G, H> CommandSyntaxException createException(A a, B b, C c, D d, E e, F f, G g, H h, OctaComponentConsumer<A, B, C, D, E, F, G, H> message) {
        return createException(message.apply(a, b, c, d, e, f, g, h));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param f       the sixth object to create the exception with
     * @param g       the seventh object to create the exception with
     * @param h       the eighth object to create the exception with
     * @param i       the ninth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @param <F>     the type of the sixth object to create the exception with
     * @param <G>     the type of the seventh object to create the exception with
     * @param <H>     the type of the eighth object to create the exception with
     * @param <I>     the type of the ninth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E, F, G, H, I> CommandSyntaxException createException(A a, B b, C c, D d, E e, F f, G g, H h, I i, NonaComponentConsumer<A, B, C, D, E, F, G, H, I> message) {
        return createException(message.apply(a, b, c, d, e, f, g, h, i));
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     *
     * @param a       the first object to create the exception with
     * @param b       the second object to create the exception with
     * @param c       the third object to create the exception with
     * @param d       the fourth object to create the exception with
     * @param e       the fifth object to create the exception with
     * @param f       the sixth object to create the exception with
     * @param g       the seventh object to create the exception with
     * @param h       the eighth object to create the exception with
     * @param i       the ninth object to create the exception with
     * @param j       the tenth object to create the exception with
     * @param message the message to create the exception with
     * @param <A>     the type of the first object to create the exception with
     * @param <B>     the type of the second object to create the exception with
     * @param <C>     the type of the third object to create the exception with
     * @param <D>     the type of the fourth object to create the exception with
     * @param <E>     the type of the fifth object to create the exception with
     * @param <F>     the type of the sixth object to create the exception with
     * @param <G>     the type of the seventh object to create the exception with
     * @param <H>     the type of the eighth object to create the exception with
     * @param <I>     the type of the ninth object to create the exception with
     * @param <J>     the type of the tenth object to create the exception with
     * @return the created exception
     */
    public static <A, B, C, D, E, F, G, H, I, J> CommandSyntaxException createException(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, DecaComponentConsumer<A, B, C, D, E, F, G, H, I, J> message) {
        return createException(message.apply(a, b, c, d, e, f, g, h, i, j));
    }


    static {
        IS_DEBUGGING = SurfEssentials.getInstance().getConfig().getBoolean("debug");
        COMPONENT_LOGGER = SurfEssentials.logger();
    }

    /**
     * Creates a new {@link CommandSyntaxException} with the specified message.
     * @param <T> the type of the object to create the exception with
     */
    private interface ComponentConsumer<T> {
        Component apply(T t);
    }

    private interface BiComponentConsumer<A, B> {
        Component apply(A a, B b);
    }

    private interface TriComponentConsumer<A, B, C> {
        Component apply(A a, B b, C c);
    }

    private interface QuadComponentConsumer<A, B, C, D> {
        Component apply(A a, B b, C c, D d);
    }

    private interface PentaComponentConsumer<A, B, C, D, E> {
        Component apply(A a, B b, C c, D d, E e);
    }

    private interface HexaComponentConsumer<A, B, C, D, E, F> {
        Component apply(A a, B b, C c, D d, E e, F f);
    }

    private interface HeptaComponentConsumer<A, B, C, D, E, F, G> {
        Component apply(A a, B b, C c, D d, E e, F f, G g);
    }

    private interface OctaComponentConsumer<A, B, C, D, E, F, G, H> {
        Component apply(A a, B b, C c, D d, E e, F f, G g, H h);
    }

    private interface NonaComponentConsumer<A, B, C, D, E, F, G, H, I> {
        Component apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);
    }

    private interface DecaComponentConsumer<A, B, C, D, E, F, G, H, I, J> {
        Component apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);
    }
}
