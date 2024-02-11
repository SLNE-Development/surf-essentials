package dev.slne.surf.essentials.utils.abtract;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.brigadier.PaperBrigadier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

/**
 * A utility class for sending messages to players or command sources.
 *
 * @author twisti
 * @since 1.0.2
 */

public abstract class MessageUtil extends PacketUtil {
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


    // success
    //---------------------------------------------------------------------------------

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


    // info
    //---------------------------------------------------------------------------------

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
     * Sends a message to the specified audience.
     *
     * @param audience   the audience to send the message to
     * @param message    the message to send
     * @param <Audience> the type of audience to send the message to
     */
    public static <Audience extends net.kyori.adventure.audience.Audience> void sendMessage(@NotNull Audience audience, Component message) {
        audience.sendMessage(EssentialsUtil.getPrefix()
                .append(message));
    }


    // system message
    //---------------------------------------------------------------------------------

    public static <Audience extends net.kyori.adventure.audience.Audience> void sendSystemMessage(@NotNull Audience audience, Component message) {
        audience.sendMessage(EssentialsUtil.getPrefix()
                .append(message));
    }

    // exceptions
    // ---------------------------------------------------------------------------------

    /**
     * Creates a new {@link WrapperCommandSyntaxException} with the specified message.
     *
     * @param message the message to create the exception with
     * @return the created exception
     */
    public static WrapperCommandSyntaxException createException(Component message) {
//        return CommandAPIBukkit.failWithAdventureComponent(message);
        return CommandAPI.failWithMessage(PaperBrigadier.message(message));
    }


    static {
        IS_DEBUGGING = SurfEssentials.getInstance().getConfig().getBoolean("debug");
        COMPONENT_LOGGER = SurfEssentials.logger();
    }
}
