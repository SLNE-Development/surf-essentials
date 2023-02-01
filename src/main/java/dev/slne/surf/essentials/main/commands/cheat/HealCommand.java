package dev.slne.surf.essentials.main.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Collection;
import java.util.Collections;

@PermissionTag(name = Permissions.HEAL_SELF_PERMISSION, desc = "Allows you to heal yourself")
@PermissionTag(name = Permissions.HEAL_OTHER_PERMISSION, desc = "Allows you to heal others")
public class HealCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("heal", HealCommand::literal).setUsage("/heal [<players>]")
                .setDescription("heals the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HEAL_SELF_PERMISSION));

        literal.executes(context -> heal(context.getSource(), Collections.singleton(context.getSource().getPlayerOrException())));
        literal.then(Commands.argument("players", EntityArgument.players())
                        .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.HEAL_OTHER_PERMISSION))
                        .executes(context -> heal(context.getSource(), EntityArgument.getPlayers(context, "players"))));
    }

    private static int heal(CommandSourceStack source, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        int successfulChanges = 0;

        for (ServerPlayer target : targets) {
            target.heal(target.getMaxHealth(), EntityRegainHealthEvent.RegainReason.CUSTOM, true);
            successfulChanges ++;
            SurfApi.getUser(target.getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Du wurdest geheilt! ", SurfColors.GREEN))));
        }

        ServerPlayer target = targets.iterator().next();
        if (source.isPlayer()){
            if (successfulChanges == 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" wurde geheilt!", SurfColors.SUCCESS)));
            }else if (successfulChanges >= 1 && source.getPlayerOrException() != targets.iterator().next()){
                EssentialsUtil.sendSuccess(source, Component.text(successfulChanges, SurfColors.TERTIARY)
                        .append(Component.text(" Spieler wurden geheilt!", SurfColors.SUCCESS)));
            }
        }else {
            if (successfulChanges == 1){
                source.sendSuccess(target.getDisplayName()
                        .copy().append(net.minecraft.network.chat.Component.literal(" was healed")), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal(successfulChanges + " players were healed"), false);
            }
        }
        return successfulChanges;
    }
}
