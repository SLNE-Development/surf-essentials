package dev.slne.surf.essentialsold.commands.general.other.troll.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.gui.GuiUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

@UtilityClass
public class TrollGuiItems {
    /**
     * A boarder {@link GuiItem} using {@link Material#GRAY_STAINED_GLASS_PANE}
     *
     * @return the {@link GuiItem}
     */
    public GuiItem boarder() {
        val guiItem = new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1));
        rename(guiItem, "");
        return guiItem;
    }

    /**
     * Creates a new anvil troll {@link GuiItem} using {@link Material#ANVIL}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem anvilTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.ANVIL, 1));

        rename(guiItem, "Anvil Troll");
        lore(guiItem, "Lasse Ambosse auf den Spieler regnen");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"anvil", ""}));

        return guiItem;
    }

    /**
     * Creates a new bell troll {@link GuiItem} using {@link Material#BELL}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem bellTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.BELL, 1));

        rename(guiItem, "Bell Troll");
        lore(guiItem, "Störe den Spieler mit Glocken-geräuschen");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"bell", ""}));

        return guiItem;
    }

    /**
     * Creates a new boom troll {@link GuiItem} using {@link Material#TNT}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem boomTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.TNT, 1));

        rename(guiItem, "Boom Troll");
        lore(guiItem, "Sprenge einen Spieler in die Luft");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"boom", ""}));

        return guiItem;
    }

    /**
     * Creates a new cage troll {@link GuiItem} using {@link Material#IRON_BARS}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem cageTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.IRON_BARS, 1));

        rename(guiItem, "Cage Troll");
        lore(guiItem, "Sperre einen Spieler in einem Glaskäfig ein");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"cage", ""}));

        return guiItem;
    }

    /**
     * Creates a new demo troll {@link GuiItem} using {@link Material#LIGHT_GRAY_WOOL}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem demoTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.LIGHT_GRAY_WOOL, 1));

        rename(guiItem, "Demo Troll");
        lore(guiItem, "Zeige einem Spieler die fake demo Nachricht");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"demo", ""}));

        return guiItem;
    }

    /**
     * Creates a new herobrine troll {@link GuiItem} using {@link Material#PLAYER_HEAD}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem herobrineTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.PLAYER_HEAD, 1));

        rename(guiItem, "Herobrine Troll");
        lore(guiItem, "Lasse Herobrine für einen Spieler erscheinen");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"herobrine", ""}));

        return guiItem;
    }

    /**
     * Creates a new illusioner troll {@link GuiItem} using {@link Material#ALLAY_SPAWN_EGG}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem illusionerTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.ALLAY_SPAWN_EGG, 1));

        rename(guiItem, "Illusioner Troll");
        lore(guiItem, "Lasse den Spieler gegen Illusioner kämpfen, während sie den darkness Effekt haben");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"illusioner", ""}));

        return guiItem;
    }

    /**
     * Creates a new mlg troll {@link GuiItem} using {@link Material#WATER_BUCKET}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem mlgTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.WATER_BUCKET, 1));

        rename(guiItem, "MLG Troll");
        lore(guiItem, "Lasse den Spieler einen MLG versuchen");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"mlg", "water"}));

        return guiItem;
    }

    /**
     * Creates a new villager-annoy troll {@link GuiItem} using {@link Material#VILLAGER_SPAWN_EGG}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem villagerAnnoyTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.VILLAGER_SPAWN_EGG, 1));

        rename(guiItem, "Villager annoy Troll");
        lore(guiItem, "Spielt nervige villager geräusche für den Spieler ab");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"villager", ""}));

        return guiItem;
    }

    /**
     * Creates a new water troll {@link GuiItem} using {@link Material#SPLASH_POTION}
     *
     * @return the troll {@link GuiItem}
     */
    public GuiItem waterTroll() {
        val guiItem = new GuiItem(new ItemStack(Material.SPLASH_POTION, 1));

        rename(guiItem, "Water Troll");
        lore(guiItem, "Der Spieler bekommt plötzlich eine Wasserphobie");
        guiItem.setAction(clickAndShowOnlinePlayers(new String[]{"water", ""}));

        return guiItem;
    }

    public GuiItem crashTroll() {
        val item = new GuiItem(new ItemStack(Material.TNT_MINECART, 1));

        rename(item, "Crash Troll");
        lore(item, "Lässt das Spiel von einem Spieler zum absturz bringen");

        return item;
    }

    /**
     * A page forward {@link GuiItem}
     */
    public GuiItem FORWARD_BUTTON() {
        val item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvMmI0ZTM0ZDA0ZDNhNTY1ZjYxNjY4YzcwOTMwN2MzNTE5YTRmNzA2YTY5ZjBkZTIwZDJmMDNiZGJjMTdlOTIwNSJ9fX0=");

        val guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Weiter", Colors.INFO));

        return guiItem;
    }

    /**
     * A page backward {@link GuiItem}
     */
    public GuiItem BACKWARD_BUTTON() {
        val item = getHeadFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3Rl" +
                "eHR1cmUvNzc4ZWY4ZDEzYWU1M2FhNDMxNDNhMWZlNzU5YjVjNjIwNDEwNDZiMTc0NmI1MGZhNDUyZGYwZDUzNGM2YTNkIn19fQ==");

        val guiItem = new GuiItem(item);
        rename(guiItem, Component.text("Zurück", Colors.INFO));

        return guiItem;
    }

    /**
     * A close {@link GuiItem} using {@link Material#BARRIER}
     */
    public GuiItem CLOSE_BUTTON() {
        val guiItem = new GuiItem(new ItemStack(Material.BARRIER, 1));

        rename(guiItem, Component.text("Schließen", Colors.RED));
        guiItem.setAction(inventoryClickEvent -> Objects.requireNonNull(inventoryClickEvent.getClickedInventory(), "Inventory may not be null").close());

        return guiItem;
    }

    /**
     * Shows the player selecting {@link ChestGui} and execute the troll on click
     *
     * @param trollName the name of the troll for the command that will be executed
     */
    public Consumer<InventoryClickEvent> clickAndShowOnlinePlayers(String[] trollName) {
        return clickEvent -> {
            val player = clickEvent.getWhoClicked();
            val gui = new ChestGui(6, ComponentHolder.of(Component.text("Spieler Auswählen", Colors.SECONDARY)));
            gui.setOnGlobalClick(event -> event.setCancelled(true));


            int x = 0;
            int y = 0;
            int length = 7;
            int height = 3;
            val playerPage1 = new StaticPane(x, y, length, height);
            val playerPage2 = new StaticPane(x, y, length, height);
            val playerPage3 = new StaticPane(x, y, length, height);
            val playerPage4 = new StaticPane(x, y, length, height);
            val playerPage5 = new StaticPane(x, y, length, height);
            val playerPage6 = new StaticPane(x, y, length, height);
            val playerPage7 = new StaticPane(x, y, length, height);
            val playerPage8 = new StaticPane(x, y, length, height);
            val playerPage9 = new StaticPane(x, y, length, height);
            val playerPage10 = new StaticPane(x, y, length, height);


            val pages = new ArrayList<StaticPane>();
            pages.add(playerPage1);
            pages.add(playerPage2);
            pages.add(playerPage3);
            pages.add(playerPage4);
            pages.add(playerPage5);
            pages.add(playerPage6);
            pages.add(playerPage7);
            pages.add(playerPage8);
            pages.add(playerPage9);
            pages.add(playerPage10);

            int currentPage = 0;
            int currentSlot = 0;

            for (Player onlinePlayer : EssentialsUtil.checkPlayerSuggestionWithoutException(player, new ArrayList<>(Bukkit.getOnlinePlayers()))) {
                val playerHead = new GuiItem(getHead(onlinePlayer), inventoryClickEvent -> {
                    Objects.requireNonNull(inventoryClickEvent.getClickedInventory()).close();
                    if (inventoryClickEvent.getWhoClicked() instanceof Player player1) {
                        player1.performCommand("troll " + trollName[0] + " " + onlinePlayer.getName() + " " + String.join(" ", trollName));
                    }
                });

                pages.get(currentPage).addItem(playerHead, Slot.fromIndex(currentSlot));
                currentSlot++;

                if (currentSlot >= 21) {
                    currentPage++;
                    currentSlot = 0;
                }
            }

            val paginatedPane = new PaginatedPane(1, 1, 7, 4);

            paginatedPane.addPane(0, playerPage1);
            paginatedPane.addPane(1, playerPage2);
            paginatedPane.addPane(2, playerPage3);
            paginatedPane.addPane(3, playerPage4);
            paginatedPane.addPane(4, playerPage5);
            paginatedPane.addPane(5, playerPage6);
            paginatedPane.addPane(6, playerPage7);
            paginatedPane.addPane(7, playerPage8);
            paginatedPane.addPane(8, playerPage9);
            paginatedPane.addPane(9, playerPage10);


            gui.addPane(paginatedPane);
            Boarders.setAllBoarders(gui, paginatedPane);

            gui.show(player);
        };
    }


    /**
     * Renames a {@link GuiItem}
     *
     * @param guiItem     the {@link GuiItem} to be renamed
     * @param displayName the new display name {@link Component}
     */
    public void rename(GuiItem guiItem, Component displayName) {
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
    public void lore(GuiItem guiItem, Component lore) {
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
    private ItemStack getHeadFromValue(String value) {
        return GuiUtils.getHeadFromValue(value);
    }

    /**
     * Gets the head from a {@link Player}
     *
     * @param player the {@link Player} to get the head from
     * @return the head {@link ItemStack}
     */
    private ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        item.damage(3, player);
        item.editMeta(SkullMeta.class, skullMeta -> {
            skullMeta.displayName(player.name());
            skullMeta.setOwningPlayer(player);
        });
        return item;
    }
}
