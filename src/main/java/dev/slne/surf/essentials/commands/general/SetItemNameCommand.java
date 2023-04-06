package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class SetItemNameCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"setname", "rename"};
    }

    @Override
    public String usage() {
        return "/setname <name>";
    }

    @Override
    public String description() {
        return "Sets the name of the item the sender is holding";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SET_ITEM_NAME_PERMISSION));

        literal.then(Commands.argument("name", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    EssentialsUtil.suggestAllColorCodes(builder);
                    return builder.buildFuture();
                })
                .executes(context -> setName(context.getSource(), StringArgumentType.getString(context, "name"))));
    }

    private int setName(CommandSourceStack source, String name) throws CommandSyntaxException {
        ItemStack itemStackInHand = source.getPlayerOrException().getItemInHand(InteractionHand.MAIN_HAND);
        Component displayName = EssentialsUtil.deserialize(name).colorIfAbsent(Colors.WHITE);

        itemStackInHand.setHoverName(PaperAdventure.asVanilla(displayName));

        EssentialsUtil.sendSuccess(source, Component.text("Das Item ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(itemStackInHand.getDisplayName()))
                .append(Component.text(" wurde umbenannt!", Colors.SUCCESS)));

        return 1;
    }

}
