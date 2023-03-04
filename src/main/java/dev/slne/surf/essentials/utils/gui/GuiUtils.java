package dev.slne.surf.essentials.utils.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import dev.slne.surf.api.utils.message.SurfColors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.UUID;

public class GuiUtils {

    /**
     * A boarder {@link GuiItem} using {@link Material#GRAY_STAINED_GLASS_PANE}
     * @return the {@link GuiItem}
     */
    public static final GuiItem boarder(){
        GuiItem guiItem = new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        GuiUtils.rename(guiItem, "");
        return guiItem;
    }

    /** A page backward {@link GuiItem} */
    public static final GuiItem BACKWARD_BUTTON() {
        ItemStack item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvNzc4ZWY4ZDEzYWU1M2FhNDMxNDNhMWZlNzU5YjVjNjIwNDEwNDZiMTc0NmI1MGZhNDUyZGYwZDUzNGM2YTNkIn19fQ==");

        GuiItem guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Zurück", SurfColors.INFO));

        return guiItem;
    }

    /** A page backward {@link GuiItem} with function */
    public static final GuiItem BACKWARD_BUTTON(ChestGui gui, PaginatedPane paginatedPane){
        GuiItem backwardButton = BACKWARD_BUTTON();
        backwardButton.setAction(inventoryClickEvent -> {
            if (0 > paginatedPane.getPage() - 1) return;
            paginatedPane.setPage(paginatedPane.getPage() - 1);
            gui.update();
        });
        return backwardButton;
    }

    /** A page forward {@link GuiItem} */
    public static final GuiItem FORWARD_BUTTON() {
        ItemStack item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvMmI0ZTM0ZDA0ZDNhNTY1ZjYxNjY4YzcwOTMwN2MzNTE5YTRmNzA2YTY5ZjBkZTIwZDJmMDNiZGJjMTdlOTIwNSJ9fX0=");

        GuiItem guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Weiter", SurfColors.INFO));

        return guiItem;
    }

    /** A page forward {@link GuiItem} with function*/
    public static final GuiItem FORWARD_BUTTON(ChestGui gui, PaginatedPane paginatedPane){
        GuiItem forwardButton = FORWARD_BUTTON();
        forwardButton.setAction(inventoryClickEvent -> {
            if (paginatedPane.getPages() <= paginatedPane.getPage() + 1) return;
            paginatedPane.setPage(paginatedPane.getPage() + 1);
            gui.update();
        });
        return forwardButton;
    }

    /** A close {@link GuiItem} using {@link Material#BARRIER} */
    public static final GuiItem CLOSE_BUTTON() {
        GuiItem guiItem = new GuiItem(new ItemStack(Material.BARRIER, 1));

        rename(guiItem, Component.text("Schließen", SurfColors.RED));
        guiItem.setAction(inventoryClickEvent -> inventoryClickEvent.getClickedInventory().close());

        return guiItem;
    }

    /**
     * Renames a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem} to be renamed
     * @param displayName the new display name {@link Component}
     */
    public static void rename(GuiItem guiItem, Component displayName) {
        ItemStack itemStack = guiItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        displayName = Component.empty().append(displayName.colorIfAbsent(SurfColors.TERTIARY)).decoration(TextDecoration.ITALIC, false);
        itemMeta.displayName(displayName);

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Renames a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem} to be renamed
     * @param name the new display name
     */
    public static void rename(GuiItem guiItem, String name){
        rename(guiItem, Component.text(name));
    }

    /**
     * Sets the lore of a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem}
     * @param lore the new lore {@link Component}
     */
    public static void lore(GuiItem guiItem, Component lore){
        ItemStack itemStack = guiItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        lore = Component.empty().append(lore).colorIfAbsent(SurfColors.INFO);
        itemMeta.lore(Collections.singletonList(lore));

        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Sets the lore of a {@link GuiItem}
     *
     * @param guiItem the {@link GuiItem}
     * @param lore the new lore
     */
    public static void lore(GuiItem guiItem, String lore){
        lore(guiItem, Component.text(lore));
    }

    /**
     * Gets a {@link Material#PLAYER_HEAD} based on the provided value
     *
     * @param value the value from which the head is fashioned
     * @return the head {@link ItemStack}
     */
    public static ItemStack getHeadFromValue(String value) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        UUID hashAsId = new UUID(value.hashCode(), value.hashCode());

        return Bukkit.getUnsafe().modifyItemStack(skull,
                "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + value + "\"}]}}}"
        );
    }

    /**
     * The left boarder {@link OutlinePane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public static final OutlinePane LEFT_BOARDER(){
        OutlinePane pane = new OutlinePane(0, 0, 1, 6);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The right boarder {@link OutlinePane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public static final OutlinePane RIGHT_BOARDER(){
        OutlinePane pane = new OutlinePane(8, 0, 1, 6);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * The bottom boarder {@link PatternPane} using {@link #boarder()}
     *
     * @return the boarder
     */
    public static final PatternPane BOTTOM_BOARDER(){
        Pattern pattern = new Pattern("0012300");
        PatternPane pane = new PatternPane(1, 5, 7, 1, pattern);

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
     * @param gui the main {@link ChestGui}
     *
     * @return the boarder
     */
    public static final PatternPane BOTTOM_BOARDER(PaginatedPane paginatedPane, ChestGui gui){
        Pattern pattern = new Pattern("0012300");
        PatternPane pane = new PatternPane(1, 5, 7, 1, pattern);

        pane.bindItem('0', boarder());
        pane.bindItem('2', CLOSE_BUTTON());

        GuiItem forwardButton = FORWARD_BUTTON();
        forwardButton.setAction(inventoryClickEvent -> {
            if (paginatedPane.getPages() <= paginatedPane.getPage() + 1) return;
            paginatedPane.setPage(paginatedPane.getPage() + 1);
            gui.update();
        });
        pane.bindItem('3', forwardButton);

        GuiItem backwardButton = BACKWARD_BUTTON();
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
    public static final OutlinePane UPPER_BOARDER(){
        OutlinePane pane = new OutlinePane(1, 0, 7, 1);
        pane.addItem(boarder());
        pane.setRepeat(true);
        return pane;
    }

    /**
     * set all boarders for a {@link ChestGui}
     *
     * @param gui the {@link ChestGui}
     */
    public static void setAllBoarders(ChestGui gui){
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER());
    }

    /**
     * set all boarders for a {@link ChestGui} with a {@link PaginatedPane} interface
     *
     * @param gui the {@link ChestGui}
     * @param paginatedPane the {@link PaginatedPane}
     */
    public static void setAllBoarders(ChestGui gui, PaginatedPane paginatedPane){
        gui.addPane(LEFT_BOARDER());
        gui.addPane(RIGHT_BOARDER());
        gui.addPane(UPPER_BOARDER());
        gui.addPane(BOTTOM_BOARDER(paginatedPane, gui));
    }
}
