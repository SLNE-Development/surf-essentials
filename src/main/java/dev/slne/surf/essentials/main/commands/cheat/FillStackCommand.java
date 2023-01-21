package dev.slne.surf.essentials.main.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

@PermissionTag(name = Permissions.FILL_STACK_PERMISSION, desc = "This is the permission for the 'more' command")
public class FillStackCommand{

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("more", FillStackCommand::literal).setUsage("/more [<player>]")
                .setDescription("Fills the item stack in hand to maximum size.");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.FILL_STACK_PERMISSION));

        literal.executes(context -> more(context.getSource(), context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> more(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int more(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException{
        ItemStack item = target.getMainHandItem();

        if (item.isEmpty()){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY)
                        .append(Component.text(" hält nichts in der Haupthand!", SurfColors.ERROR)));
            }else {
                throw ERROR_HOLDS_NOTHING.create(target);
            }
            return 0;
        }
        if (item.getMaxStackSize() == 1){
            if (source.isPlayer()){
                EssentialsUtil.sendError(source, Component.text("Das Item ", SurfColors.ERROR)
                        .append(PaperAdventure.asAdventure(item.getDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" kann nicht gestackt werden!", SurfColors.ERROR)));
            }else {
                throw ERROR_CANNOT_STACK.create(item);
            }
            return 0;
        }

        item.setCount(item.getMaxStackSize());

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Das Item ", SurfColors.SUCCESS)
                    .append(PaperAdventure.asAdventure(item.getDisplayName()).colorIfAbsent(SurfColors.TERTIARY))
                    .append(Component.text(" wurde ", SurfColors.SUCCESS))
                    .append(Component.text("%dx".formatted(item.getMaxStackSize()), SurfColors.TERTIARY))
                    .append(Component.text(" für ", SurfColors.SUCCESS))
                    .append(target.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                    .append(Component.text(" gestackt!", SurfColors.SUCCESS)));
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("The item ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(item.getDisplayName())
                    .append(net.minecraft.network.chat.Component.literal("has been stacked "))
                    .withStyle(ChatFormatting.GREEN)
                    .append(net.minecraft.network.chat.Component.literal(item.getMaxStackSize() + "x"))
                    .withStyle(ChatFormatting.GOLD)
                    .append(net.minecraft.network.chat.Component.literal(" for "))
                    .withStyle(ChatFormatting.GREEN)
                    .append(target.getDisplayName()), false);
        }
        return item.getMaxStackSize();
    }

    private static final DynamicCommandExceptionType ERROR_HOLDS_NOTHING = new DynamicCommandExceptionType(target -> ((ServerPlayer) target).getDisplayName()
            .copy().append(net.minecraft.network.chat.Component.literal("´s main hand is empty!")));

    private static final DynamicCommandExceptionType ERROR_CANNOT_STACK = new DynamicCommandExceptionType(item -> net.minecraft.network.chat.Component.literal("The maximum stack size for ")
            .append(((net.minecraft.world.item.ItemStack) item).getDisplayName())
            .append(net.minecraft.network.chat.Component.literal(" is 1!")));
}
