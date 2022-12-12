package dev.slne.surf.essentials.main.commands.general;

import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.commands.EssentialsCommand;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BookCommand extends EssentialsCommand {
    public BookCommand(PluginCommand command) {
        super(command);
        command.setPermission("surf.essentials.commands.book");
        command.setUsage("/<command> [title|author <name>]");
        command.setDescription("Allows reopening and editing of sealed books.");
        command.permissionMessage(EssentialsUtil.NO_PERMISSION());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            @NotNull PlayerInventory inv = player.getInventory();
            @NotNull ItemStack mainHandItem = player.getActiveItem();

            if (inv.getItemInMainHand().getType() != Material.WRITTEN_BOOK) {
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                                .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                        .append(Component.text("/book [title|author <name>] + das Buch in der main-Hand halten", SurfColors.TERTIARY))));
                return true;
            }

            @NotNull ItemStack originalBook = inv.getItemInMainHand();
            BookMeta originalBookMeta = (BookMeta) originalBook.getItemMeta();
            ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
            BookMeta mbook = (BookMeta) book.getItemMeta();

            if (!(originalBookMeta.getAuthor().equals(player.getName())) && !player.hasPermission("surf.essentials.commands.book.bypass")){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Du hast keine Rechte Bücher von anderen Spielern zu bearbeiten!", SurfColors.ERROR))));
                return true;
            }

            if (args.length == 0){
                setBookMeta(originalBookMeta, mbook);
                book.setItemMeta(mbook);
                inv.setItem(inv.getItemInMainHand().getType().getEquipmentSlot(), book);
                return true;
            }

            if (!(args.length > 1)){
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Korrekte Benutzung: ", SurfColors.RED))
                        .append(Component.text("/book [title <name> | author <name>]", SurfColors.TERTIARY))));
                return true;
            }

            if (args[0].equalsIgnoreCase("title")){
                String title = args[1];
                originalBookMeta.setTitle(title);
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Der Title wurde zu ", SurfColors.SUCCESS))
                        .append(Component.text(title, SurfColors.TERTIARY))
                        .append(Component.text(" geändert!", SurfColors.SUCCESS))));
                return true;
            }
            if (args[0].equalsIgnoreCase("author")){
                String author = args[1];
                originalBookMeta.setAuthor(author);
                SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                        .append(Component.text("Der Autor wurde zu ", SurfColors.SUCCESS))
                        .append(Component.text(author, SurfColors.TERTIARY))
                        .append(Component.text(" geändert!", SurfColors.SUCCESS))));
                return true;
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }

    private void setBookMeta(@NotNull BookMeta originalBookMeta, @NotNull BookMeta mbook){
        if (originalBookMeta.hasAuthor()) mbook.setAuthor(originalBookMeta.getAuthor());
        if (originalBookMeta.hasGeneration()) mbook.setGeneration(originalBookMeta.getGeneration());
        if (originalBookMeta.hasTitle()) mbook.setTitle(originalBookMeta.getTitle());
        if (originalBookMeta.hasAttributeModifiers()) mbook.setAttributeModifiers(originalBookMeta.getAttributeModifiers());
        if (originalBookMeta.hasCustomModelData()) mbook.setCustomModelData(originalBookMeta.getCustomModelData());
        if (originalBookMeta.hasDestroyableKeys()) mbook.setDestroyableKeys(originalBookMeta.getDestroyableKeys());
        if (originalBookMeta.hasPlaceableKeys()) mbook.setPlaceableKeys(originalBookMeta.getPlaceableKeys());
        if (originalBookMeta.hasLore()) mbook.lore(originalBookMeta.lore());
        mbook.setUnbreakable(originalBookMeta.isUnbreakable());
        if (originalBookMeta.hasEnchants()) originalBookMeta.getEnchants().forEach((enchantment, integer) -> {
            mbook.addEnchant(enchantment, integer, true);
        });
        for (Component page : originalBookMeta.pages()) {
            mbook.addPages(page);
        }
    }
}
