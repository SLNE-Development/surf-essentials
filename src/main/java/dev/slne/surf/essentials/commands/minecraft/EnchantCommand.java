package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class EnchantCommand extends BrigadierCommand {

    @Override
    public String[] names() {
        return new String[]{"enchant"};
    }

    @Override
    public String usage() {
        return "/enchant <targets> <enchantment> [<level>]";
    }

    @Override
    public String description() {
        return "enchants the item in targets main hand";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.ENCHANT_PERMISSION));

        literal.then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("enchantment", ResourceLocationArgument.id())
                        .suggests(EnchantCommand::suggestEnchantments)
                        .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                ResourceLocationArgument.getId(context, "enchantment"), 1))

                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                        ResourceLocationArgument.getId(context, "enchantment"), IntegerArgumentType.getInteger(context, "level"))))));
    }

    private static int enchant(CommandSourceStack source, Collection<? extends net.minecraft.world.entity.Entity> targetsUnchecked, ResourceLocation enchantmentString, int level) throws CommandSyntaxException {
        var targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked).stream().map(net.minecraft.world.entity.Entity::getBukkitEntity).toList();
        var key = NamespacedKey.fromString(enchantmentString.toString());
        var enchantment = Enchantment.getByKey(key);
        if (enchantment == null)
            throw ResourceArgument.ERROR_UNKNOWN_RESOURCE.create(enchantmentString.toString(), "minecraft:enchantment");

        if (level > enchantment.getMaxLevel()) throw ERROR_LEVEL_TOO_HIGH.create(level, enchantment.getMaxLevel());

        int successfullEnchantment = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                var equipment = livingEntity.getEquipment();
                if (equipment == null) throw ERROR_NO_ITEM.create(EssentialsUtil.getMinecraftDisplayName(livingEntity));

                ItemStack itemStack = equipment.getItemInMainHand();

                if (itemStack.getType() == Material.AIR)
                    throw ERROR_NO_ITEM.create(EssentialsUtil.getMinecraftDisplayName(livingEntity));

                if (EssentialsUtil.isEnchantmentCompatible(enchantment, itemStack)) {
                    itemStack.addEnchantment(enchantment, level);
                    ++successfullEnchantment;

                } else if (targets.size() == 1)
                    throw ERROR_INCOMPATIBLE.create(PaperAdventure.asVanilla(itemStack.displayName()));

            } else throw ERROR_NOT_LIVING_ENTITY.create(EssentialsUtil.getMinecraftDisplayName(entity));

        }

        if (successfullEnchantment == 0) throw ERROR_NOTHING_HAPPENED.create();


        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                    .append(enchantment.displayName(level).colorIfAbsent(Colors.TERTIARY))
                    .append(Component.text(" wurde zu ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text("'s item hinzugefügt", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                    .append(enchantment.displayName(level).colorIfAbsent(Colors.TERTIARY))
                    .append(Component.text(" wurde zu ", Colors.SUCCESS))
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" entities hinzugefügt", Colors.SUCCESS)));
        }

        return successfullEnchantment;

    }

    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((entityName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.entity", entityName));

    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((entityName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.itemless", entityName));

    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((itemName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.incompatible", itemName));

    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((level, maxLevel) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.level", level, maxLevel));

    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed"));


    private static CompletableFuture<Suggestions> suggestEnchantments(@NotNull CommandContext<?> context, SuggestionsBuilder builder) {
        var args = context.getInput().split(" ");
        String currentArg;

        try {
            currentArg = args[2].toLowerCase(Locale.ENGLISH);
        } catch (IndexOutOfBoundsException e) {
            currentArg = "";
        }

        for (Enchantment enchantment : Enchantment.values()) {
            var key = enchantment.getKey();

            if (currentArg.isEmpty() || currentArg.isBlank() ||
                    key.toString().toLowerCase().startsWith(currentArg) || key.value().toLowerCase().startsWith(currentArg)) {

                builder.suggest(enchantment.getKey().asString(), PaperAdventure.asVanilla(enchantment.displayName(0)));
            }
        }
        return builder.buildFuture();
    }
}

