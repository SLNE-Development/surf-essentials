package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class RepairCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"repair"};
    }

    @Override
    public String usage() {
        return "/repair [<player]";
    }

    @Override
    public String description() {
        return "Repairs the players main-hand-item";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.REPAIR_SELF_PERMISSION, Permissions.REPAIR_OTHER_PERMISSION));

        literal.executes(context -> repair(context.getSource(), context.getSource().getPlayerOrException()));
        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(EssentialsUtil.checkPermissions(Permissions.REPAIR_OTHER_PERMISSION))
                .executes(context -> repair(context.getSource(), EntityArgument.getPlayer(context, "players"))));
    }

    public int repair(CommandSourceStack source, ServerPlayer targetUnchecked) throws CommandSyntaxException{
        final var target = EssentialsUtil.checkPlayerSuggestion(source, targetUnchecked);
        final var item = target.getMainHandItem();

        if (!item.isDamageableItem()) throw ERROR_NOT_DAMAGEABLE.create(item);

        item.setDamageValue(0);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, PaperAdventure.asAdventure(item.getDisplayName())
                    .append(Component.text(" von ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(target))
                    .append(Component.text(" wurde repariert!", Colors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The item ")
                    .append(item.getDisplayName())
                    .append(net.minecraft.network.chat.Component.literal(" from "))
                    .append(target.getDisplayName())
                    .append(net.minecraft.network.chat.Component.literal(" was repaired")), false);
        }

        return 1;
    }

    private static final DynamicCommandExceptionType ERROR_NOT_DAMAGEABLE = new DynamicCommandExceptionType(item -> ((ItemStack) item).getDisplayName()
            .copy().append(net.minecraft.network.chat.Component.literal(" is not damageable!")
                    .withStyle(ChatFormatting.RED)));

}
