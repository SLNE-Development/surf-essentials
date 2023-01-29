package dev.slne.surf.essentials.main.commands.general.other.world.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.GuiUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class WorldItems {
    private static final List<Material> worldMaterials = new ArrayList<>(List.of(Material.BLACK_CONCRETE, Material.BLUE_CONCRETE, Material.GREEN_CONCRETE,
            Material.BROWN_CONCRETE, Material.CYAN_CONCRETE, Material.LIME_CONCRETE, Material.ORANGE_CONCRETE, Material.RED_CONCRETE, Material.MAGENTA_CONCRETE,
            Material.PINK_CONCRETE, Material.PURPLE_CONCRETE, Material.WHITE_CONCRETE, Material.YELLOW_CONCRETE));

    public static final GuiItem nothing(){
        GuiItem item = new GuiItem(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1));
        GuiUtils.rename(item, "");
        return item;
    }

    public static final GuiItem WORLD_JOIN(){
        GuiItem guiItem = new GuiItem(new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA, 1));
        GuiUtils.rename(guiItem, Component.text("Join", SurfColors.GREEN));
        guiItem.setAction(joinWorldClick());

        return guiItem;
    }

    public static final GuiItem WORLD_LOAD(){
        GuiItem guiItem = new GuiItem(new ItemStack(Material.LIME_TERRACOTTA, 1));
        GuiUtils.rename(guiItem, Component.text("Load", SurfColors.GREEN));
        guiItem.setAction(loadWorldClick());

        return guiItem;
    }

    public static final GuiItem WORLD_UNLOAD(){
        GuiItem guiItem = new GuiItem(new ItemStack(Material.CYAN_TERRACOTTA, 1));
        GuiUtils.rename(guiItem, Component.text("unload", SurfColors.INFO));
        guiItem.setAction(unloadWorldClick());

        return guiItem;
    }

    public static final GuiItem WORLD_REMOVE(){
        GuiItem guiItem = new GuiItem(new ItemStack(Material.RED_TERRACOTTA, 1));
        GuiUtils.rename(guiItem, Component.text("remove", SurfColors.RED));
        guiItem.setAction(removeWorldClick());

        return guiItem;
    }

    private static final Consumer<InventoryClickEvent> unloadWorldClick() {
        return createGui(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()), (player, world) -> {
            if (Bukkit.getWorld(world).getPlayerCount() != 0){
                ChestGui askForConfirmationGui = new ChestGui(6, ComponentHolder.of(Component.text("Confirmation", SurfColors.SECONDARY)));
                askForConfirmationGui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));

                GuiUtils.setAllBoarders(askForConfirmationGui);

                StaticPane staticPane = new StaticPane(0,0,7,4);

                GuiItem info = new GuiItem(new ItemStack(Material.PAPER, 1));
                GuiUtils.rename(info, "In der Welt befinden sich noch Spieler");
                GuiUtils.lore(info, "Sollen die Spieler in die overworld Teleportiert werden?");

                GuiItem yes = new GuiItem(new ItemStack(Material.LIME_WOOL));
                GuiUtils.rename(yes, Component.text("Ja", SurfColors.GREEN));
                yes.setAction(inventoryClickEvent1 -> {
                    player.performCommand("world allPlayers " + world);
                    Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), bukkitTask -> {
                        player.performCommand("world unload " + world);
                    }, 20);
                });

                GuiItem no = new GuiItem(new ItemStack(Material.RED_WOOL));
                GuiUtils.rename(no, Component.text("Nein", SurfColors.RED));
                no.setAction(inventoryClickEvent1 -> inventoryClickEvent1.getClickedInventory().close());

                staticPane.addItem(info, 4,2);
                staticPane.addItem(yes, 3,3);
                staticPane.addItem(no, 5,3);

                askForConfirmationGui.addPane(staticPane);
                askForConfirmationGui.show(player);
            }else {
                player.performCommand("world unload " + world);
            }
        });
    }

    private static final Consumer<InventoryClickEvent> joinWorldClick(){
        List<World> worlds = Bukkit.getWorlds();
        return createGui(worlds.stream().map(World::getName).collect(Collectors.toList()), (player, world) -> player.performCommand("world join " + world));
    }

    private static final Consumer<InventoryClickEvent> loadWorldClick(){
        return createGui(offlineWorlds(), (player, world) -> player.performCommand("world load " + world));
    }

    private static final Consumer<InventoryClickEvent> removeWorldClick(){
        return createGui(offlineWorlds(), (player, world) -> player.performCommand("world remove " + world + " --confirm"));
    }


    private static final Consumer<InventoryClickEvent> createGui(List<String> worlds, BiConsumer<Player, String> command){
        return clickEvent -> {
            Player player = (Player) clickEvent.getWhoClicked();
            List<StaticPane> pages = new ArrayList<>();
            PaginatedPane paginatedPane = new PaginatedPane(1, 1, 7, 4);
            int x = 0, y = 0, length = 7, height = 3;

            ChestGui gui = new ChestGui(6, ComponentHolder.of(Component.text("Welt auswählen", SurfColors.SECONDARY)));
            gui.setOnGlobalClick(event -> event.setCancelled(true));

            int currentPage = 0;
            int currentSlot = 0;
            for (String world : worlds) {
                GuiItem guiItem = new GuiItem(new ItemStack(getNextMaterial(), 1), inventoryClickEvent -> {
                    Player whoClicked = (Player) inventoryClickEvent.getWhoClicked();
                    inventoryClickEvent.getClickedInventory().close();
                    resetMaterialCounter();
                    command.accept(whoClicked, world);
                });
                GuiUtils.rename(guiItem, world);

                if (currentPage >= pages.size()){
                    StaticPane newPane = new StaticPane(x, y, length, height);
                    pages.add(newPane);
                    paginatedPane.addPane(currentPage, newPane);
                }
                pages.get(currentPage).addItem(guiItem, Slot.fromIndex(currentSlot));
                currentSlot++;

                if (currentSlot >= 21) {
                    currentPage++;
                    currentSlot = 0;
                }
            }

            gui.addPane(paginatedPane);
            GuiUtils.setAllBoarders(gui, paginatedPane);

            gui.show(player);
        };
    }


    private static final List<String> offlineWorlds(){
        List<String> offlineWorlds = new ArrayList<>();
        for (File file : Objects.requireNonNull(SurfEssentials.getInstance().getServer().getWorldContainer().listFiles())) {
            if (!file.isDirectory()) continue;
            if (!Arrays.asList(Objects.requireNonNull(file.list())).contains("level.dat") || !Arrays.asList(Objects.requireNonNull(file.list())).contains("uid.dat")) continue;
            if (SurfEssentials.getInstance().getServer().getWorld(file.getName()) != null) continue;
            offlineWorlds.add(file.getName());
        }
        return offlineWorlds;
    }


    private static int counter = 0;
    private static Material getNextMaterial() {
        Material nextMaterial = worldMaterials.get(counter);
        counter++;
        if (counter >= worldMaterials.size()) {
            counter = 0;
        }
        return nextMaterial;
    }
    private static void resetMaterialCounter() {
        counter = 0;
    }
}
