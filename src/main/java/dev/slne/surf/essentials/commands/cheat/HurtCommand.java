package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.commands.minecraft.DamageCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class HurtCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"hurt"};
    }

    @Override
    public String usage() {
        return "/hurt <player>";
    }

    @Override
    public String description() {
        return "Hurt other players";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.HURT_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 255))
                        .executes(context -> executeHurt(context.getSource(), EntityArgument.getPlayers(context, "players"),
                                IntegerArgumentType.getInteger(context, "amount")))));
    }

    private int executeHurt(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, int amount) throws CommandSyntaxException {
        final var targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        final var sources = EssentialsUtil.getDamageSources();
        int successfulHurted = 0;
        if (source.isPlayer()){
            for (Player target : targets) {
                if (target.hurt(sources.playerAttack(source.getPlayerOrException()), amount)) successfulHurted++;
            }
        }else {
            for (Player target : targets) {
                if (target.hurt(sources.generic().critical(), amount)) successfulHurted++;
            }
        }

        if (successfulHurted == 0) throw DamageCommand.ERROR_INVULNERABLE.create();

        if (successfulHurted == 1){
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
                        .append(Component.text(" wurde verletzt!", Colors.SUCCESS)));
            }else {
                source.sendSuccess(EssentialsUtil.getDisplayNameAsVanilla(targets.iterator().next())
                        .copy().append(net.minecraft.network.chat.Component.literal(" was hurt!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }else{
            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, (Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" Spieler wurden verletzt!", Colors.SUCCESS)));
            }else {
                source.sendSuccess(targets.iterator().next().getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" players were hurt!")
                                .withStyle(ChatFormatting.GREEN)), false);
            }
        }
        return targets.size();
    }
}
