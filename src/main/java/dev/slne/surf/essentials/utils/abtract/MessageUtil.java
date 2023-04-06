package dev.slne.surf.essentials.utils.abtract;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.color.Colors;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.entity.Player;

import static dev.slne.surf.essentials.utils.EssentialsUtil.getPrefix;

@SuppressWarnings("unused")
public abstract class MessageUtil extends CraftUtil {

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isDebugging(){
        return SurfEssentials.getInstance().getConfig().getBoolean("debug");
    }

    public static<T extends CommandSourceStack> void sendError(T source, String error) throws CommandSyntaxException {
        sendError(source, Component.text(error));
    }

    public static<T extends CommandSourceStack> void sendError(T source, Component error) throws CommandSyntaxException {
        sendMessage(source, error.colorIfAbsent(Colors.ERROR));
    }

    public static<T extends Player> void sendError(T player, Component error) {
        sendMessage(player, error.colorIfAbsent(Colors.ERROR));
    }

    public static<T extends Player> void sendError(T player, String error) {
        sendError(player, Component.text(error));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendError(T player, Component error) {
        sendMessage(player, error.colorIfAbsent(Colors.ERROR));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendError(T player, String error) {
        sendError(player, Component.text(error));
    }


    public static<T extends CommandSourceStack> void sendSuccess(T source, Component success) throws CommandSyntaxException {
        sendMessage(source, success.colorIfAbsent(Colors.SUCCESS));
    }
    public static<T extends CommandSourceStack> void sendSuccess(T source, String success) throws CommandSyntaxException {
        sendSuccess(source, Component.text(success));
    }

    public static<T extends Player> void sendSuccess(T player, String success) {
        sendSuccess(player, Component.text(success));
    }
    public static<T extends Player>  void sendSuccess(T player, Component success) {
        sendMessage(player, success.colorIfAbsent(Colors.SUCCESS));
    }
    public static<T extends net.minecraft.world.entity.player.Player> void sendSuccess(T player, Component success){
        sendMessage(player, success.colorIfAbsent(Colors.SUCCESS));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendSuccess(T player, String success){
        sendSuccess(player, Component.text(success));
    }

    public static<T extends CommandSourceStack> void sendInfo(T source, Component info) throws CommandSyntaxException {
        sendMessage(source, info.colorIfAbsent(Colors.INFO));
    }

    public static<T extends CommandSourceStack> void sendInfo(T source, String info) throws CommandSyntaxException {
        sendInfo(source, Component.text(info));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendInfo(T player, Component info){
        sendMessage(player, info.colorIfAbsent(Colors.INFO));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendInfo(T player, String info){
        sendInfo(player, Component.text(info));
    }

    public static<T extends Player> void sendInfo(T player, Component info){
        sendMessage(player, info.colorIfAbsent(Colors.INFO));
    }

    public static<T extends Player> void sendInfo(T player, String info){
        sendInfo(player, Component.text(info));
    }

    public static<T extends CommandSourceStack> void sendDebug(T sourceStack, Component debug){
        if (!isDebugging()) return;
        sourceStack.getBukkitSender().sendMessage(debug.colorIfAbsent(Colors.DEBUG));
    }

    public static void sendDebug(CommandSourceStack sourceStack, String debug){
        sendDebug(sourceStack, Component.text(debug));
    }

    public static void sendDebug(Component debug){
        if (!isDebugging()) return;
        SurfEssentials.logger().info(debug.colorIfAbsent(Colors.DEBUG));
    }

    public static void sendDebug(String debug){
        sendDebug(Component.text(debug));
    }



    public static<T extends CommandSourceStack> void sendMessage(T source, Component message) throws CommandSyntaxException {
        source.getPlayerOrException().getBukkitEntity().sendMessage(getPrefix()
                .append(message));
    }

    public static<T extends Player> void sendMessage(T player, Component message) {
        player.sendMessage(getPrefix()
                .append(message));
    }

    public static<T extends net.minecraft.world.entity.player.Player> void sendMessage(T player, Component message) {
        player.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }

    public static<T extends CommandSourceStack> void sendSystemMessage(T source, Component message) {
        source.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }
    public static<T extends Player> void sendSystemMessage(T player, Component message) {
        player.sendMessage(getPrefix()
                .append(message));
    }
    public static<T extends net.minecraft.world.entity.player.Player> void sendSystemMessage(T player, Component message) {
        player.sendSystemMessage(PaperAdventure.asVanilla(getPrefix()
                .append(message)));
    }

    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, Component message, boolean broadcastToOps) {
        source.sendSuccess(PaperAdventure.asVanilla(getPrefix()
                .append(message)), broadcastToOps);
    }
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, Component message){
        sendSourceSuccess(source, message, false);
    }
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, String message){
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS));
    }
    public static<T extends CommandSourceStack> void sendSourceSuccess(T source, String message, boolean broadcastToOps){
        sendSourceSuccess(source, Component.text(message, Colors.SUCCESS), broadcastToOps);
    }

}
