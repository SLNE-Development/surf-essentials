package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BookCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"book"};
    }

    @Override
    public String usage() {
        return "/book [<title | author>]";
    }

    @Override
    public String description() {
        return "Reopens the book or change the title / author";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.BOOK_PERMISSION));

        literal.executes(context -> reopenBook(context.getSource()));

        literal.then(Commands.literal("title")
                .then(Commands.argument("title", StringArgumentType.string())
                        .executes(context -> changeTitle(context.getSource(), StringArgumentType.getString(context, "title")))));

        literal.then(Commands.literal("author")
                .then(Commands.argument("author", StringArgumentType.string())
                        .executes(context -> changeAuthor(context.getSource(), StringArgumentType.getString(context, "author")))));
    }

    private static int reopenBook(CommandSourceStack source) throws CommandSyntaxException {
        Player player = source.getPlayerOrException().getBukkitEntity();
        PlayerInventory inv = player.getInventory();

        if (inv.getItemInMainHand().getType() != Material.WRITTEN_BOOK) {
            EssentialsUtil.sendError(source, "Du hältst kein Buch in der Hand!");
            return 0;
        }

        ItemStack originalBook = inv.getItemInMainHand();
        BookMeta originalBookMeta = (BookMeta) originalBook.getItemMeta();
        ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
        BookMeta mbook = (BookMeta) book.getItemMeta();

        checkPerm(source, net.minecraft.world.item.ItemStack.fromBukkitCopy(originalBook));

        setBookMeta(originalBookMeta, mbook);
        book.setItemMeta(mbook);
        inv.setItem(inv.getItemInMainHand().getType().getEquipmentSlot(), book);

        source.getPlayerOrException().openItemGui(net.minecraft.world.item.ItemStack.fromBukkitCopy(book), InteractionHand.MAIN_HAND);
        EssentialsUtil.sendSuccess(source, "Du kannst das Buch nun bearbeiten!");

        return 1;
    }

    private static int changeTitle(CommandSourceStack source, String title) throws CommandSyntaxException {
        final var mainHandItem = editMainHandItemCompound(source, compoundTag1 -> compoundTag1.putString("title", title));

        EssentialsUtil.sendSuccess(source, Component.text("Der Title vom Buch ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(mainHandItem.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));
        return 1;
    }

    private static int changeAuthor(CommandSourceStack source, String author) throws CommandSyntaxException {
        final var mainHandItem = editMainHandItemCompound(source, compoundTag1 -> compoundTag1.putString("author", author));

        EssentialsUtil.sendSuccess(source, Component.text("Der Autor vom Buch ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(mainHandItem.getDisplayName()).colorIfAbsent(Colors.TERTIARY))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));
        return 1;
    }

    private static net.minecraft.world.item.ItemStack editMainHandItemCompound(CommandSourceStack source, Consumer<CompoundTag> compoundTagConsumer) throws CommandSyntaxException {
        net.minecraft.world.item.ItemStack mainHandItem = source.getPlayerOrException().getMainHandItem();

        if (!mainHandItem.is(Items.WRITTEN_BOOK)){
            throw EssentialsUtil.createException(Component.text("Du musst ein Buch in deiner Hand halten!", Colors.ERROR));
        }

        checkPerm(source, mainHandItem);

        CompoundTag compoundTag = mainHandItem.getTag();
        if (compoundTag == null){
            compoundTag = new CompoundTag();
        }

        compoundTagConsumer.accept(compoundTag);
        mainHandItem.setTag(compoundTag);

        return mainHandItem;
    }

    private static void setBookMeta(@NotNull BookMeta originalBookMeta, @NotNull BookMeta mbook){
        if (originalBookMeta.hasAuthor()) mbook.setAuthor(originalBookMeta.getAuthor());
        if (originalBookMeta.hasGeneration()) mbook.setGeneration(originalBookMeta.getGeneration());
        if (originalBookMeta.hasTitle()) mbook.setTitle(originalBookMeta.getTitle());
        if (originalBookMeta.hasAttributeModifiers()) mbook.setAttributeModifiers(originalBookMeta.getAttributeModifiers());
        if (originalBookMeta.hasCustomModelData()) mbook.setCustomModelData(originalBookMeta.getCustomModelData());
        if (originalBookMeta.hasDestroyableKeys()) mbook.setDestroyableKeys(originalBookMeta.getDestroyableKeys());
        if (originalBookMeta.hasPlaceableKeys()) mbook.setPlaceableKeys(originalBookMeta.getPlaceableKeys());
        if (originalBookMeta.hasLore()) mbook.lore(originalBookMeta.lore());
        mbook.setUnbreakable(originalBookMeta.isUnbreakable());
        if (originalBookMeta.hasEnchants()) originalBookMeta.getEnchants().forEach((enchantment, integer) -> mbook.addEnchant(enchantment, integer, true));
        for (Component page : originalBookMeta.pages()) {
            mbook.addPages(page);
        }
    }

    private static void checkPerm(@NotNull CommandSourceStack source, net.minecraft.world.item.@NotNull ItemStack mainHandItem) throws CommandSyntaxException {
        if (mainHandItem.getTag() != null && !(mainHandItem.getTag().getString("author").equals(source.getPlayerOrException().getName().getString())) && !source.hasPermission(4, Permissions.BOOK_PERMISSION_BYPASS)) {
            throw EssentialsUtil.createException(Component.text("Du hast keine Berechtigung, Bücher von anderen Spielern zu bearbeiten!", Colors.ERROR));
        }
    }
}
