package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class HurtCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("hurt", HurtCommand::literal).setUsage("/hurt <players> <damage>")
                .setDescription("Hurts the targets with the given amount of damage");
    }

    public static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HURT_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 250))
                        .executes(context -> executeHurt(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                IntegerArgumentType.getInteger(context, "amount")))));
    }

    private static int executeHurt(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, int amount) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
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
                EssentialsUtil.sendSuccess(source, (targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" wurde verletzt!", Colors.SUCCESS)));
            }else {
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" was hurt!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }else{
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, (Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" Spieler wurden verletzt!", Colors.SUCCESS)));
            }else {
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" players were hurted!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }
        return targets.size();
    }
}
