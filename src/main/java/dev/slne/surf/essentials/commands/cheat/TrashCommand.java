package dev.slne.surf.essentials.commands.cheat;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Mask;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.pdc.UUIDDataType;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

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
        final var player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        final var edgeUUID = UUID.randomUUID();
        final var edge = createEdgeGuiItem(edgeUUID);
        final var confirm = createConfirmGuiItem(edgeUUID);
        final var outline = new OutlinePane(0, 0, 9, 6, Pane.Priority.LOWEST);
        final var confirmPane = new StaticPane(4, 5, 1, 1, Pane.Priority.LOW);
        final var input = new StaticPane(1, 1, 7, 4, Pane.Priority.NORMAL);
        final var chestGui = new ChestGui(6, ComponentHolder.of(Component.text("Trash", Colors.RED)
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
            final var contents = Arrays.stream(close.getInventory().getContents())
                    .parallel()
                    .filter(Objects::nonNull)
                    .filter(content -> !isEdgeItem(content, edgeUUID))
                    .toArray(ItemStack[]::new);

            final var closePlayer = close.getPlayer();
            final var leftovers = closePlayer.getInventory().addItem(contents);

            leftovers.forEach((integer, stack) -> closePlayer.getWorld().dropItem(closePlayer.getLocation(), stack, item -> item.setPickupDelay(-1)));
        });

        chestGui.show(player.getBukkitEntity());

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(player).append(Component.text(" wurde das Müll menü geöffnet", Colors.SUCCESS)));

        return 1;
    }

    @Contract("_ -> new")
    private @NotNull GuiItem createEdgeGuiItem(UUID edgeUUID) {
        final var edgeItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.RED_STAINED_GLASS_PANE),
                Component.empty()
        );

        return new GuiItem(addUUID(edgeItemStack, edgeUUID), click -> click.setCancelled(true));
    }

    @Contract("_ -> new")
    private @NotNull GuiItem createConfirmGuiItem(UUID edgeUUID) {
        var confirmItemStack = EssentialsUtil.changeName(
                new ItemStack(Material.LIME_STAINED_GLASS),
                Component.text("Bestätigen", Colors.GREEN)
        );

        return new GuiItem(addUUID(confirmItemStack, edgeUUID), click -> {
            final var inv = click.getClickedInventory();
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
        final var uuid = stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(SurfEssentials.getInstance(), "edgeUUID"), UUIDDataType.INSTANCE);
        return uuid != null && uuid.equals(edgeUUID);
    }
}
