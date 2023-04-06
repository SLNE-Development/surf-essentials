package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SetItemLoreCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"setlore", "lore"};
    }

    @Override
    public String usage() {
        return "/setlore <lore>";
    }

    @Override
    public String description() {
        return "Change the lore of the item you currently holding";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.SET_ITEM_LORE_PERMISSION));

        literal.then(Commands.argument("lore", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    builder.suggest("\n", net.minecraft.network.chat.Component.literal("Create a new line"));
                    EssentialsUtil.suggestAllColorCodes(builder);
                    return builder.buildFuture();
                })
                .executes(context -> setLore(context.getSource(), StringArgumentType.getString(context, "lore"))));
    }

    private int setLore(CommandSourceStack source, String loreString) throws CommandSyntaxException {
        ItemStack itemStack = source.getPlayerOrException().getItemInHand(InteractionHand.MAIN_HAND).getBukkitStack();

        String[] lores = loreString.translateEscapes().split("\n");
        List<Component> loresComponents = new ArrayList<>();

        for (String lore : lores) {
            loresComponents.add(EssentialsUtil.deserialize(lore).colorIfAbsent(Colors.INFO));
        }

        itemStack.lore(loresComponents);

        EssentialsUtil.sendSuccess(source, Component.text("Die Beschreibung von ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(net.minecraft.world.item.ItemStack.fromBukkitCopy(itemStack).getDisplayName()))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));

        return 1;
    }
}
