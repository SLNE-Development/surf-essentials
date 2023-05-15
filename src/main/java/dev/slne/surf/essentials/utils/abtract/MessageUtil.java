package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;

import static dev.slne.surf.essentials.utils.EssentialsUtil.getPrefix;

/**
 * A utility class for sending messages to players or command sources.
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
    public static boolean isDebugging(){
        return IS_DEBUGGING;
    }

    // error
    //---------------------------------------------------------------------------------

    /**
     * Sends an error message to the specified command source player with the specified error message.
     *
     * @param source the command source to send the message to
     * @param error the error message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendError(T source, String error) throws CommandSyntaxException {
        sendError(source, Component.text(error));
    }

    /**
     * Sends an error message to the specified command source player with the specified error message.
     *
     * @param source the command source to send the message to
     * @param error the error message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendError(T source, Component error) throws CommandSyntaxException {
        sendMessage(source, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified audience with the specified error message.
     *
     * @param audience the audience to send the message to
     * @param error the error message to send
     * @param <T> the type of the audience to send the message to
     */
    public static<T extends Audience> void sendError(T audience, Component error) {
        sendMessage(audience, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified audience with the specified error message.
     *
     * @param audience the audience to send the message to
     * @param error the error message to send
     * @param <T> the type of the audience to send the message to
     */
    public static<T extends Audience> void sendError(T audience, String error) {
        sendError(audience, Component.text(error));
    }

    /**
     * Sends an error message to the specified command source with the specified error message.
     *
     * @param commandSource the command source to send the message to
     * @param error the error message to send
     * @param <T> the type of the command source to send the message to
     */
    public static<T extends CommandSource> void sendError(T commandSource, Component error) {
        sendMessage(commandSource, error.colorIfAbsent(Colors.ERROR));
    }

    /**
     * Sends an error message to the specified command source with the specified error message.
     *
     * @param commandSource the command source to send the message to
     * @param error the error message to send
     * @param <T> the type of the command source to send the message to
     */
    public static<T extends CommandSource> void sendError(T commandSource, String error) {
        sendError(commandSource, Component.text(error));
    }



    // success
    //---------------------------------------------------------------------------------

    /**
     * Sends a success message to the specified command source player with the specified success message.
     *
     * @param source the command source to send the message to
     * @param success the success message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendSuccess(T source, Component success) throws CommandSyntaxException {
        sendMessage(source, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source player with the specified success message.
     *
     * @param source the command source to send the message to
     * @param success the success message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendSuccess(T source, String success) throws CommandSyntaxException {
        sendSuccess(source, Component.text(success));
    }

    /**
     * Sends a success message to the specified audience with the specified success message.
     *
     * @param audience the audience to send the message to
     * @param success the success message to send
     * @param <T> the type of audience to send the message to
     */
    public static<T extends Audience> void sendSuccess(T audience, String success) {
        sendSuccess(audience, Component.text(success));
    }

    /**
     * Sends a success message to the specified audience with the specified success message.
     *
     * @param audience the audience to send the message to
     * @param success the success message to send
     * @param <T> the type of audience to send the message to
     */
    public static<T extends Audience>  void sendSuccess(T audience, Component success) {
        sendMessage(audience, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source with the specified success message.
     *
     * @param commandSource the command source to send the message to
     * @param success the success message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSource> void sendSuccess(T commandSource, Component success){
        sendMessage(commandSource, success.colorIfAbsent(Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source with the specified success message.
     *
     * @param commandSource the command source to send the message to
     * @param success the success message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSource> void sendSuccess(T commandSource, String success){
        sendSuccess(commandSource, Component.text(success));
    }



    // info
    //---------------------------------------------------------------------------------

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param source the command source to send the message to
     * @param info the info message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendInfo(T source, Component info) throws CommandSyntaxException {
        sendMessage(source, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param source the command source to send the message to
     * @param info the info message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendInfo(T source, String info) throws CommandSyntaxException {
        sendInfo(source, Component.text(info));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param commandSource the command source to send the message to
     * @param info the info message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSource> void sendInfo(T commandSource, Component info){
        sendMessage(commandSource, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified command source with the specified info message.
     *
     * @param commandSource the command source to send the message to
     * @param info the info message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSource> void sendInfo(T commandSource, String info){
        sendInfo(commandSource, Component.text(info));
    }

    /**
     * Sends an info message to the specified audience with the specified info message.
     *
     * @param audience the audience to send the message to
     * @param info the info message to send
     * @param <T> the type of audience to send the message to
     */
    public static<T extends Audience> void sendInfo(T audience, Component info){
        sendMessage(audience, info.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an info message to the specified audience with the specified info message.
     *
     * @param audience the audience to send the message to
     * @param info the info message to send
     * @param <T> the type of audience to send the message to
     */
    public static<T extends Audience> void sendInfo(T audience, String info){
        sendInfo(audience, Component.text(info));
    }




    // debug
    //---------------------------------------------------------------------------------

    /**
     * Sends a debug message to the specified command source player with the specified debug message.
     *
     * @param sourceStack the command source to send the message to
     * @param debug the debug message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendDebug(T sourceStack, Component debug){
        if (!IS_DEBUGGING) return;
        sourceStack.getBukkitSender().sendMessage(debug.colorIfAbsent(Colors.DEBUG));
    }

    /**
     * Sends a debug message to the specified command source player with the specified debug message.
     *
     * @param sourceStack the command source to send the message to
     * @param debug the debug message to send
     */
    public static void sendDebug(CommandSourceStack sourceStack, String debug){
        sendDebug(sourceStack, Component.text(debug));
    }

    /**
     * Sends a debug message to the console.
     *
     * @param debug the debug message to send
     */
    public static void sendDebug(Component debug){
        if (!IS_DEBUGGING) return;
        COMPONENT_LOGGER.info(debug.colorIfAbsent(Colors.DEBUG));
    }

    /**
     * Sends a debug message to the console.
     *
     * @param debug the debug message to send
     */
    public static void sendDebug(String debug){
        sendDebug(Component.text(debug));
    }



    // normal message
    //---------------------------------------------------------------------------------

    /**
     * Sends a message to the specified command source player with the specified message.
     *
     * @param source the command source to send the message to
     * @param message the message to send
     * @param <T> the type of command source to send the message to
     * @throws CommandSyntaxException if the command source is not a player
     */
    public static<T extends CommandSourceStack> void sendMessage(T source, Component message) throws CommandSyntaxException {
        source.getPlayerOrException().getBukkitEntity().sendMessage(getPrefix()
                .append(message));
    }

    /**
     * Sends a message to the specified audience.
     *
     * @param audience the audience to send the message to
     * @param message the message to send
     * @param <T> the type of audience to send the message to
     */
    public static<T extends Audience> void sendMessage(T audience, Component message) {
        audience.sendMessage(getPrefix()
                .append(message));
    }

    /**
     * Sends a message to the specified command source.
     *
     * @param commandSource the command source to send the message to
     * @param message the message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSource> void sendMessage(T commandSource, Component message) {
        commandSource.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }



    // system message
    //---------------------------------------------------------------------------------

    public static<T extends CommandSourceStack> void sendSystemMessage(T source, Component message) {
        source.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }
    public static<T extends Audience> void sendSystemMessage(T audience, Component message) {
        audience.sendMessage(getPrefix()
                .append(message));
    }
    public static<T extends CommandSource> void sendSystemMessage(T commandSource, Component message) {
        commandSource.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }




    // command source stack message
    //---------------------------------------------------------------------------------

    /**
     * Sends a success message to the specified command source stack with the specified success message, optionally broadcasting to ops.
     *
     * @param source the command source to send the message to
     * @param message the success message to send
     * @param broadcastToOps whether to broadcast the message to ops
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, Component message, boolean broadcastToOps) {
        source.sendSuccess(PaperAdventure.asVanilla(getPrefix()
                .append(message.colorIfAbsent(Colors.SUCCESS))), broadcastToOps);
    }

    /**
     * Sends a success message to the specified command source stack with the specified success message, without broadcasting to ops.
     *
     * @param source the command source to send the message to
     * @param message the success message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, Component message){
        sendSourceSuccess(source, message, false);
    }

    /**
     * Sends a success message to the specified command source stack with the specified success message as a String, without broadcasting to ops.
     *
     * @param source the command source to send the message to
     * @param message the success message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, String message){
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS));
    }

    /**
     * Sends a success message to the specified command source player with the specified success message as a String, optionally broadcasting to ops.
     *
     * @param source the command source to send the message to
     * @param message the success message to send
     * @param broadcastToOps whether to broadcast the message to ops
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, String message, boolean broadcastToOps){
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS), broadcastToOps);
    }


    /**
     * Sends an error message to the specified command source stack with the specified error message.
     *
     * @param source the command source to send the message to
     * @param message the error message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceError(T source, Component message) {
        source.sendFailure(PaperAdventure.asVanilla(getPrefix()
                .append(message.colorIfAbsent(Colors.ERROR))));
    }

    /**
     * Sends an error message to the specified command source stack with the specified error message.
     *
     * @param source the command source to send the message to
     * @param message the error message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceError(T source, String message){
        sendSourceError(source, Component.text(message, Colors.ERROR));
    }

    /**
     * Sends an information message to the specified command source stack with the specified message.
     *
     * @param source the command source to send the message to
     * @param message the information message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceInfo(T source, Component message) {
        sendSourceMessage(source, message.colorIfAbsent(Colors.INFO));
    }

    /**
     * Sends an information message to the specified command source stack with the specified message.
     *
     * @param source the command source to send the message to
     * @param message the information message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceInfo(T source, String message){
        sendSourceInfo(source, Component.text(message, Colors.INFO));
    }

    /**
     * Sends a message to the specified command source stack with the specified message.
     *
     * @param source the command source to send the message to
     * @param message the message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceMessage(T source, Component message) {
        source.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }

    /**
     * Sends a message to the specified command source stack with the specified text message.
     *
     * @param source the command source to send the message to
     * @param message the text message to send
     * @param <T> the type of command source to send the message to
     */
    public static<T extends CommandSourceStack> void sendSourceMessage(T source, String message){
        sendSourceMessage(source, Component.text(message, Colors.GRAY));
    }




    static {
        IS_DEBUGGING = SurfEssentials.getInstance().getConfig().getBoolean("debug");
        COMPONENT_LOGGER = SurfEssentials.logger();
    }
}
