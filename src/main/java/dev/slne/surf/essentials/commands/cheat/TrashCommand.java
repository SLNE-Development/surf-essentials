package dev.slne.surf.essentials.commands.cheat;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TrashCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"trash", "disposal"};
    }

    @Override
    public String usage() {
        return "/trash [<player>]";
    }

    @Override
    public String description() {
        return "Opens a disposal menu";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.TRASH_PERMISSION_SELF, Permissions.TRASH_PERMISSION_OTHER));
        literal.executes(context -> trashMenu(context.getSource(), context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(EssentialsUtil.checkPermissions(Permissions.TRASH_PERMISSION_OTHER))
                .executes(context -> trashMenu(context.getSource(), context.getSource().getPlayerOrException())));
    }

    private int trashMenu(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        var player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);

        var chestGui = new ChestGui(
                6,
                ComponentHolder.of(Component.text("Trash", Colors.RED)
                        .append(Component.text("            Items hineinlegen", Colors.SECONDARY))),
                SurfEssentials.getInstance());

        var patternPane = new PatternPane(
                0,
                0,
                9,
                6,
                new Pattern(
                        "000000000",
                        "011111110",
                        "011111110",
                        "011111110",
                        "011111110",
                        "000020000"
                )
        );

        var edgeItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.RED_STAINED_GLASS_PANE),
                Component.empty()
        );
        var edge = new GuiItem(
                edgeItemStack,
                inventoryClickEvent -> inventoryClickEvent.setCancelled(true)
        );

        var confirmItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.LIME_STAINED_GLASS_PANE),
                Component.text("Bestätigen", Colors.GREEN)
        );
        var confirm = new GuiItem(
                confirmItemStack,
                inventoryClickEvent -> {
                    var inv = inventoryClickEvent.getClickedInventory();
                    if (inv == null) return;
                    inv.clear();
                    inv.close();

                }
        );

        patternPane.bindItem('0', edge);
        patternPane.bindItem('2', confirm);

        chestGui.setOnClose(inventoryCloseEvent -> {
            var contents = new ArrayList<ItemStack>();
            for (ItemStack content : inventoryCloseEvent.getInventory().getContents()) {
                if (content != null) {
                    if (content.equals(edgeItemStack) || content.equals(confirmItemStack)) return;
                    contents.add(content);
                }
            }

            inventoryCloseEvent.getPlayer().getInventory().addItem(contents.toArray(new ItemStack[0]));
        });

        chestGui.addPane(patternPane);
        chestGui.show(player.getBukkitEntity());

        if (source.isPlayer()) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                    .append(Component.text(" wurde das Müll menü geöffnet", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("The trash menu was opened for ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(player)));
        }

        return 1;
    }
}
