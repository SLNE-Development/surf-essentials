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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FillStackCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"more"};
    }

    @Override
    public String usage() {
        return "/more [<player>]";
    }

    @Override
    public String description() {
        return "Fill the items tack in players main hand";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.FILL_STACK_PERMISSION));

        literal.executes(context -> more(context.getSource(), context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> more(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int more(CommandSourceStack source, ServerPlayer targetUnchecked) throws CommandSyntaxException {
        final ServerPlayer target = EssentialsUtil.checkPlayerSuggestion(source, targetUnchecked);
        final ItemStack item = target.getMainHandItem();

        if (item.isEmpty()) throw ERROR_HOLDS_NOTHING.create(target);
        if (item.getMaxStackSize() == 1) throw ERROR_CANNOT_STACK.create(item);

        item.setCount(item.getMaxStackSize());

        EssentialsUtil.sendSuccess(source, Component.text("Das Item ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(item.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append(Component.text("%dx".formatted(item.getMaxStackSize()), Colors.TERTIARY))
                .append(Component.text(" für ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" gestackt!", Colors.SUCCESS)));
        return item.getMaxStackSize();
    }

    private static final DynamicCommandExceptionType ERROR_HOLDS_NOTHING = new DynamicCommandExceptionType(target -> EssentialsUtil.getDisplayNameAsVanilla((ServerPlayer) target)
            .copy().append(net.minecraft.network.chat.Component.literal("´s hand is empty!")));

    private static final DynamicCommandExceptionType ERROR_CANNOT_STACK = new DynamicCommandExceptionType(item -> net.minecraft.network.chat.Component.literal("Cannot stack ")
            .append(((net.minecraft.world.item.ItemStack) item).getDisplayName()));
}
