package dev.slne.surf.essentials.main.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;

import java.util.Collection;

public class HurtCommand {
    public static String PERMISSION;
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("hurt", HurtCommand::literal).setUsage("/hurt <players> <damage>")
                .setDescription("Hurts the targets with the given amount of damage");
    }

    public static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 250))
                        .executes(context -> executeHurt(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                IntegerArgumentType.getInteger(context, "amount")))));
    }

    private static int executeHurt(CommandSourceStack source, Collection<? extends Player> targets, int amount) throws CommandSyntaxException {
        if (source.isPlayer()){
            for (Player target : targets) {
                target.hurt(DamageSource.playerAttack(source.getPlayerOrException()), amount);
            }
        }else {
            for (Player target : targets) {
                target.hurt(DamageSource.MAGIC, amount);
            }
        }

        if (targets.size() == 1){
            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Bukkit.getPlayer(targets.iterator().next().getUUID()).displayName().colorIfAbsent(SurfColors.YELLOW))
                        .append(Component.text(" wurde verletzt!", SurfColors.SUCCESS))));
            }else {
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" was hurt!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }else{
            if (source.isPlayer()){
                SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text(targets.size(), SurfColors.TERTIARY))
                        .append(Component.text(" Spieler wurden verletzt!", SurfColors.SUCCESS))));
            }else {
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" players were hurted!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }
        return targets.size();
    }
}
