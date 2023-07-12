package dev.slne.surf.essentials.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BookCommand extends EssentialsCommand {
    public BookCommand() {
        super("book", "book [<title | author>]", "Reopens the book or change the title / author");

        withPermission(Permissions.BOOK_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> reopenBook(getPlayerOrException(sender)));

        then(literal("title")
                .then(greedyStringArgument("title")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeTitle(getPlayerOrException(sender), args.getUnchecked("title")))));

        then(literal("author")
                .then(greedyStringArgument("author")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> changeAuthor(getPlayerOrException(sender), args.getUnchecked("author")))));
    }

    private static int reopenBook(Player player) throws WrapperCommandSyntaxException {
        final PlayerInventory inv = player.getInventory();
        ItemStack originalBook = inv.getItemInMainHand();

        if (originalBook.getType() != Material.WRITTEN_BOOK) throw Exceptions.ERROR_NOT_HOLDING_WRITTEN_BOOK_IN_HAND;


        BookMeta originalBookMeta = (BookMeta) originalBook.getItemMeta();
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta mbook = (BookMeta) book.getItemMeta();

        checkPerm(player, originalBook);

        setBookMeta(originalBookMeta, mbook);
        book.setItemMeta(mbook);
        inv.setItem(player.getActiveItem().getType().getEquipmentSlot(), book);

        EssentialsUtil.sendSuccess(player, "Du kannst das Buch nun bearbeiten!");

        return 1;
    }

    private static int changeTitle(Player source, String title) throws WrapperCommandSyntaxException {
        final ItemStack mainHandItem = editMainHandItemCompound(source, bookMeta -> bookMeta.setTitle(title));

        EssentialsUtil.sendSuccess(source, Component.text("Der Title vom Buch ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(mainHandItem))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));
        return 1;
    }

    private static int changeAuthor(Player source, String author) throws WrapperCommandSyntaxException {
        final ItemStack mainHandItem = editMainHandItemCompound(source, bookMeta -> bookMeta.setAuthor(author));

        EssentialsUtil.sendSuccess(source, Component.text("Der Autor vom Buch ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(mainHandItem))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));
        return 1;
    }

    private static ItemStack editMainHandItemCompound(Player source, Consumer<BookMeta> bookMetaConsumer) throws WrapperCommandSyntaxException {
        ItemStack mainHandItem = source.getInventory().getItemInMainHand();

        if (mainHandItem.getType() != Material.WRITTEN_BOOK) {
            throw Exceptions.ERROR_NOT_HOLDING_WRITTEN_BOOK_IN_HAND;
        }

        checkPerm(source, mainHandItem);
        mainHandItem.editMeta(BookMeta.class, bookMetaConsumer);

        return mainHandItem;
    }

    private static void setBookMeta(@NotNull BookMeta originalBookMeta, @NotNull BookMeta mbook) {
        if (originalBookMeta.hasAuthor()) mbook.setAuthor(originalBookMeta.getAuthor());
        if (originalBookMeta.hasGeneration()) mbook.setGeneration(originalBookMeta.getGeneration());
        if (originalBookMeta.hasTitle()) mbook.setTitle(originalBookMeta.getTitle());
        if (originalBookMeta.hasAttributeModifiers())
            mbook.setAttributeModifiers(originalBookMeta.getAttributeModifiers());
        if (originalBookMeta.hasCustomModelData()) mbook.setCustomModelData(originalBookMeta.getCustomModelData());
        if (originalBookMeta.hasDestroyableKeys()) mbook.setDestroyableKeys(originalBookMeta.getDestroyableKeys());
        if (originalBookMeta.hasPlaceableKeys()) mbook.setPlaceableKeys(originalBookMeta.getPlaceableKeys());
        if (originalBookMeta.hasLore()) mbook.lore(originalBookMeta.lore());
        mbook.setUnbreakable(originalBookMeta.isUnbreakable());
        if (originalBookMeta.hasEnchants())
            originalBookMeta.getEnchants().forEach((enchantment, integer) -> mbook.addEnchant(enchantment, integer, true));
        for (Component page : originalBookMeta.pages()) {
            mbook.addPages(page);
        }
    }

    private static void checkPerm(@NotNull CommandSender source, @NotNull ItemStack mainHandItem) throws WrapperCommandSyntaxException {
        if (mainHandItem.getItemMeta() instanceof BookMeta bookMeta){
            String author = bookMeta.getAuthor();
            if (author != null && !author.equals(source.getName()) && !source.hasPermission(Permissions.BOOK_PERMISSION_BYPASS)) {
                throw Exceptions.NO_BOOK_BYPASS_PERMISSION;
            }
        }
    }
}
