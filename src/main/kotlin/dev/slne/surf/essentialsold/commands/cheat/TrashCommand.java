package dev.slne.surf.essentialsold.commands.cheat;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.pdc.UUIDDataType;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class TrashCommand extends EssentialsCommand {
    public TrashCommand() {
        super("trash", "trash [<player>]", "Opens a disposal menu", "disposal");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.TRASH_PERMISSION_SELF, Permissions.TRASH_PERMISSION_OTHER));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> trashMenu(sender.getCallee(), getPlayerOrException(sender)));
        then(playerArgument("player")
                .withPermission(Permissions.TRASH_PERMISSION_OTHER)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> trashMenu(sender.getCallee(), args.getUnchecked("player"))));
    }

    private int trashMenu(CommandSender source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val edgeUUID = UUID.randomUUID();
        val edge = createEdgeGuiItem(edgeUUID);
        val confirm = createConfirmGuiItem(edgeUUID);
        val outline = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);
        val confirmPane = new StaticPane(4, 5, 1, 1, Pane.Priority.LOW);
        val input = new StaticPane(1, 1, 7, 4, Pane.Priority.NORMAL);
        val chestGui = new ChestGui(6, ComponentHolder.of(Component.text("Trash", Colors.RED)
                .append(Component.text("            Items hineinlegen", Colors.SECONDARY))), SurfEssentials.getInstance());

        confirmPane.addItem(confirm, 0, 0);

        outline.addItem(edge);
        outline.setRepeat(true);
        outline.applyMask(new Mask(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111101111"
        ));

        chestGui.addPane(outline);
        chestGui.addPane(confirmPane);
        chestGui.addPane(input);

        chestGui.setOnClose(close -> {
            val contents = Arrays.stream(close.getInventory().getContents())
                    .parallel()
                    .filter(Objects::nonNull)
                    .filter(content -> !isEdgeItem(content, edgeUUID))
                    .toArray(ItemStack[]::new);

            val closePlayer = close.getPlayer();
            val leftovers = closePlayer.getInventory().addItem(contents);

            leftovers.forEach((integer, stack) -> closePlayer.getWorld().dropItem(closePlayer.getLocation(), stack, item -> item.setPickupDelay(-1)));
        });

        chestGui.show(player);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player)
                .append(Component.text(" wurde das Müll menü geöffnet", Colors.SUCCESS)));

        return 1;
    }

    @Contract("_ -> new")
    private @NotNull GuiItem createEdgeGuiItem(UUID edgeUUID) {
        val edgeItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.RED_STAINED_GLASS_PANE),
                Component.empty()
        );

        return new GuiItem(addUUID(edgeItemStack, edgeUUID), click -> click.setCancelled(true));
    }

    @Contract("_ -> new")
    private @NotNull GuiItem createConfirmGuiItem(UUID edgeUUID) {
        val confirmItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.LIME_STAINED_GLASS),
                Component.text("Bestätigen", Colors.GREEN)
        );

        return new GuiItem(addUUID(confirmItemStack, edgeUUID), click -> {
            val inv = click.getClickedInventory();
            if (inv == null) return;
            inv.clear();
            inv.close();
        });
    }

    @Contract("_, _ -> param1")
    private @NotNull ItemStack addUUID(@NotNull ItemStack stack, UUID uuid) {
        stack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(
                        new NamespacedKey(SurfEssentials.getInstance(), "edgeUUID"),
                        UUIDDataType.INSTANCE,
                        uuid
                )
        );
        return stack;
    }

    private boolean isEdgeItem(@NotNull ItemStack stack, UUID edgeUUID) {
        val uuid = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SurfEssentials.getInstance(), "edgeUUID"), UUIDDataType.INSTANCE);
        return uuid != null && uuid.equals(edgeUUID);
    }
}
