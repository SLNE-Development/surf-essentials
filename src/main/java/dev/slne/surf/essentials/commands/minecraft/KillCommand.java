package dev.slne.surf.essentials.commands.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class KillCommand {
    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("kill", KillCommand::literal).setUsage("/kill [<targets>]")
                .setDescription("Kills the sender or the targets");
    }

    /**
     * Defines the "kill" command, which allows a player to kill one or more entities.
     *
     * @param literal the command builder
     */
    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        // Require the player to have the permission
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.KILL_SELF_PERMISSION));
        // Kills the player who issued the command
        literal.executes(context -> kill(context, ImmutableList.of(context.getSource().getEntityOrException())));
        // Kills multiple entities
        literal.then(Commands.argument("targets", EntityArgument.entities())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.KILL_OTHER_PERMISSION))
                .executes(context -> kill(context, EntityArgument.getEntities(context, "targets"))));
    }

    /**
     * Handles the logic for the "kill" command.
     *
     * @param context the command context
     * @param targetsUnchecked the target entities to kill
     * @return the result of the command
     * @throws CommandSyntaxException if the command was used incorrectly
     */
    private static int kill(CommandContext<CommandSourceStack> context, Collection<? extends Entity> targetsUnchecked) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EssentialsUtil.checkEntitySuggestion(context.getSource(), targetsUnchecked);
        // Kill each target entity
        for (Entity entity : targets) {
            entity.kill();
        }

        if (context.getSource().isPlayer()){
            // If the command was issued by a player, send a message to the player
            Player player = context.getSource().getPlayerOrException().getBukkitEntity();

            if (targets.size() == 1){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text(targets.iterator().next().getDisplayName().getString(), SurfColors.TERTIARY))
                        .append(Component.text(" wurde getötet!", SurfColors.SUCCESS))));
            }else {
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text(targets.size(), SurfColors.TERTIARY))
                        .append(Component.text(" entities wurden getötet!", SurfColors.SUCCESS))));
            }
        }else {
            // If the command was not issued by a player, send the success message to the command source
            if (targets.size() == 1) {
                context.getSource().sendSuccess(net.minecraft.network.chat.Component.translatable("commands.kill.success.single",
                        targets.iterator().next().getDisplayName()), true);
            } else {
                context.getSource().sendSuccess(net.minecraft.network.chat.Component.translatable("commands.kill.success.multiple", targets.size()), true);
            }
        }
        return 1;
    }
}
