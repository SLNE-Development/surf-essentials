package dev.slne.surf.essentials.commands.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class KillCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"kill"};
    }

    @Override
    public String usage() {
        return "/kill <targets>";
    }

    @Override
    public String description() {
        return "kill you or the targets";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.KILL_SELF_PERMISSION, Permissions.KILL_OTHER_PERMISSION));
        literal.executes(context -> kill(context, ImmutableList.of(context.getSource().getEntityOrException())));

        literal.then(Commands.argument("targets", EntityArgument.entities())
                .requires(EssentialsUtil.checkPermissions(Permissions.KILL_OTHER_PERMISSION))
                .executes(context -> kill(context, EntityArgument.getEntities(context, "targets"))));
    }

    private static int kill(CommandContext<CommandSourceStack> context, Collection<? extends Entity> targetsUnchecked) throws CommandSyntaxException {
        var targets = EssentialsUtil.checkEntitySuggestion(context.getSource(), targetsUnchecked);

        for (Entity entity : targets) {
            entity.kill();
        }

        if (context.getSource().isPlayer()){
            if (targets.size() == 1){
                EssentialsUtil.sendSuccess(context.getSource(), EssentialsUtil.getDisplayName(targets.iterator().next())
                        .append(Component.text(" wurde getötet!", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(context.getSource(), Component.text(targets.size(), Colors.TERTIARY)
                        .append(Component.text(" entities wurden getötet!", Colors.SUCCESS)));
            }
        }else {
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
