package dev.slne.surf.essentials.commands.general;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@PermissionTag(name = Permissions.SET_ITEM_LORE_PERMISSION, desc = "Allows you to change the lore of the item you currently holding")
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
            loresComponents.add(EssentialsUtil.deserialize(lore).colorIfAbsent(SurfColors.INFO));
        }

        itemStack.lore(loresComponents);

        EssentialsUtil.sendSuccess(source, Component.text("Die Beschreibung von ", SurfColors.SUCCESS)
                .append(PaperAdventure.asAdventure(CraftItemStack.asNMSCopy(itemStack).getDisplayName()))
                .append(Component.text(" wurde geändert!", SurfColors.SUCCESS)));

        return 1;
    }
}
