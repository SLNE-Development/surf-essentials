package dev.slne.surf.essentialsold.utils.gui;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import dev.slne.surf.essentialsold.utils.color.Colors;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.UUID;

/**
 * Gui util class
 */
@UtilityClass
public class GuiUtils {

    /**
     * A boarder {@link GuiItem} using {@link Material#GRAY_STAINED_GLASS_PANE}
     *
     * @return the {@link GuiItem}
     */
    public @NotNull GuiItem boarder() {
        val guiItem = new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        GuiUtils.rename(guiItem, "");
        return guiItem;
    }

    /**
     * A page backward {@link GuiItem}
     */
    public @NotNull GuiItem BACKWARD_BUTTON() {
        val item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvNzc4ZWY4ZDEzYWU1M2FhNDMxNDNhMWZlNzU5YjVjNjIwNDEwNDZiMTc0NmI1MGZhNDUyZGYwZDUzNGM2YTNkIn19fQ==");

        val guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Zurück", Colors.INFO));

        return guiItem;
    }

    /**
     * A page backward {@link GuiItem} with function
     */
    public @NotNull GuiItem BACKWARD_BUTTON(ChestGui gui, PaginatedPane paginatedPane) {
        val backwardButton = BACKWARD_BUTTON();
        backwardButton.setAction(inventoryClickEvent -> {
            if (0 > paginatedPane.getPage() - 1) return;
            paginatedPane.setPage(paginatedPane.getPage() - 1);
            gui.update();
        });
        return backwardButton;
    }

    /**
     * A page forward {@link GuiItem}
     */
    public @NotNull GuiItem FORWARD_BUTTON() {
        val item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvMmI0ZTM0ZDA0ZDNhNTY1ZjYxNjY4YzcwOTMwN2MzNTE5YTRmNzA2YTY5ZjBkZTIwZDJmMDNiZGJjMTdlOTIwNSJ9fX0=");

        val guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Weiter", Colors.INFO));

        return guiItem;
    }

    /**
     * A page forward {@link GuiItem} with function
     */
    public @NotNull GuiItem FORWARD_BUTTON(ChestGui gui, PaginatedPane paginatedPane) {
        val forwardButton = FORWARD_BUTTON();
        forwardButton.setAction(inventoryClickEvent -> {
            if (paginatedPane.getPages() <= paginatedPane.getPage() + 1) return;
            paginatedPane.setPage(paginatedPane.getPage() + 1);
            gui.update();
        });
        return forwardButton;
    }

    /**
     * A close {@link GuiItem} using {@link Material#BARRIER}
     */
    public @NotNull GuiItem CLOSE_BUTTON() {
        val guiItem = new GuiItem(new ItemStack(Material.BARRIER, 1));

        rename(guiItem, Component.text("Schließen", Colors.RED));
        guiItem.setAction(inventoryClickEvent -> {
            Inventory inventory = inventoryClickEvent.getClickedInventory();
            if (inventory == null) return;
            inventory.close();
        });

        return guiItem;
    }

    /**
     * Renames a {@link GuiItem}
     *
     * @param guiItem     the {@link GuiItem} to be renamed
     * @param displayName the new display name {@link Component}
     */
    public void rename(@NotNull GuiItem guiItem, Component displayName) {
        guiItem.getItem().editMeta(itemMeta -> itemMeta.displayName(Component.empty().append(displayName.colorIfAbsent(Colors.TERTIARY)).decoration(TextDecoration.ITALIC, false)));
    }

    /**
     * Renames a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem} to be renamed
     * @param name    the new display name
     */
    public void rename(GuiItem guiItem, String name) {
        rename(guiItem, Component.text(name));
    }

    /**
     * Sets the lore of a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem}
     * @param lore    the new lore {@link Component}
     */
    public void lore(@NotNull GuiItem guiItem, Component lore) {
        guiItem.getItem().editMeta(itemMeta -> itemMeta.lore(Collections.singletonList(Component.empty().append(lore).colorIfAbsent(Colors.INFO))));
    }

    /**
     * Sets the lore of a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem}
     * @param lore    the new lore
     */
    public void lore(GuiItem guiItem, String lore) {
        lore(guiItem, Component.text(lore));
    }

    /**
     * Gets a {@link Material#PLAYER_HEAD} based on the provided value
     *
     * @param value the value from which the head is fashioned
     * @return the head {@link ItemStack}
     */
    @SuppressWarnings("DanglingJavadoc")
    public @NotNull ItemStack getHeadFromValue(@NotNull String value) {
        val skull = new ItemStack(Material.PLAYER_HEAD, 1);
        val hashAsId = new UUID(value.hashCode(), value.hashCode());

        skull.editMeta(SkullMeta.class, skullMeta -> {
            val profileExact = Bukkit.createProfile(hashAsId);
            profileExact.clearProperties();
            profileExact.setProperty(new ProfileProperty("textures", value));
            skullMeta.setPlayerProfile(profileExact);
        });

        return skull;

        /**
         return Bukkit.getUnsafe().modifyItemStack(skull,
         "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
         );
         */
        // TODO test code above
    }

    /**
     * The left boarder {@link OutlinePane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public @NotNull OutlinePane LEFT_BOARDER() {
        val pane = new OutlinePane(0, 0, 1, 6);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The right boarder {@link OutlinePane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public @NotNull OutlinePane RIGHT_BOARDER() {
        val pane = new OutlinePane(8, 0, 1, 6);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The bottom boarder {@link PatternPane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public @NotNull PatternPane BOTTOM_BOARDER() {
        val pattern = new Pattern("0012300");
        val pane = new PatternPane(1, 5, 7, 1, pattern);

        pane.bindItem('0', boarder());
        pane.bindItem('1', BACKWARD_BUTTON());
        pane.bindItem('2', CLOSE_BUTTON());
        pane.bindItem('3', FORWARD_BUTTON());

        return pane;
    }

    /**
     * The bottom boarder {@link PatternPane} that can change between {@link PaginatedPane}´s using {@link #boarder()}
     *
     * @param paginatedPane the {@link PaginatedPane} from witch the pages should be changed
     * @param gui           the main {@link ChestGui}
     * @return the boarder
     */
    public @NotNull PatternPane BOTTOM_BOARDER(PaginatedPane paginatedPane, ChestGui gui) {
        val pattern = new Pattern("0012300");
        val pane = new PatternPane(1, 5, 7, 1, pattern);

        pane.bindItem('0', boarder());
        pane.bindItem('2', CLOSE_BUTTON());

        val forwardButton = FORWARD_BUTTON();
        forwardButton.setAction(inventoryClickEvent -> {
            if (paginatedPane.getPages() <= paginatedPane.getPage() + 1) return;
            paginatedPane.setPage(paginatedPane.getPage() + 1);
            gui.update();
        });
        pane.bindItem('3', forwardButton);

        val backwardButton = BACKWARD_BUTTON();
        backwardButton.setAction(inventoryClickEvent -> {
            if (0 > paginatedPane.getPage() - 1) return;
            paginatedPane.setPage(paginatedPane.getPage() - 1);
            gui.update();
        });
        pane.bindItem('1', backwardButton);

        return pane;
    }

    /**
     * the upper boarder {@link OutlinePane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public @NotNull OutlinePane UPPER_BOARDER() {
        val pane = new OutlinePane(1, 0, 7, 1);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * set all boarders for a {@link ChestGui}
     *
     * @param gui the {@link ChestGui}
     */
    public void setAllBoarders(@NotNull ChestGui gui) {
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER());
    }

    /**
     * set all boarders for a {@link ChestGui} with a {@link PaginatedPane} interface
     *
     * @param gui           the {@link ChestGui}
     * @param paginatedPane the {@link PaginatedPane}
     */
    public void setAllBoarders(@NotNull ChestGui gui, PaginatedPane paginatedPane) {
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER(paginatedPane, gui));
    }
}
