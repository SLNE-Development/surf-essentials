package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Collection;

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
                .then(Commands.argument("enchantment", ResourceArgument.resource(EssentialsUtil.buildContext(), Registries.ENCHANTMENT))
                        .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                ResourceArgument.getEnchantment(context, "enchantment"), 1))

                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                        ResourceArgument.getEnchantment(context, "enchantment") , IntegerArgumentType.getInteger(context, "level"))))));
    }

    private static int enchant(CommandSourceStack source, Collection<? extends Entity> targetsUnchecked, Holder<Enchantment> enchantmentHolder, int level) throws CommandSyntaxException {
        var targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        Enchantment enchantment = enchantmentHolder.value();

        if (level > enchantment.getMaxLevel()) throw ERROR_LEVEL_TOO_HIGH.create(level, enchantment.getMaxLevel());

        int successfullEnchantment = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                ItemStack itemStack = livingEntity.getMainHandItem();
                if (itemStack.isEmpty()) throw ERROR_NO_ITEM.create(livingEntity.getName().getString());

                if (enchantment.canEnchant(itemStack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemStack).keySet(), enchantment)) {
                    itemStack.enchant(enchantment, level);
                    ++successfullEnchantment;

                } else if (targets.size() == 1) throw ERROR_INCOMPATIBLE.create(itemStack.getItem().getName(itemStack).getString());

            } else throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());

        }

        if (successfullEnchantment == 0) throw ERROR_NOTHING_HAPPENED.create();

        if (source.isPlayer()) {
            if (targets.size() == 1) {
                EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                        .append(Component.text(enchantment.getFullname(level).getString(), Colors.TERTIARY))
                        .append(Component.text(" wurde zu ", Colors.SUCCESS))
                        .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                        .append(Component.text("'s item hinzugefügt", Colors.SUCCESS)));
            } else {
                EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                        .append(Component.text(enchantment.getFullname(level).getString(), Colors.TERTIARY))
                        .append(Component.text(" wurde zu ", Colors.SUCCESS))
                        .append(Component.text(targets.size(), Colors.TERTIARY))
                        .append(Component.text(" entities hinzugefügt", Colors.SUCCESS)));
            }
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.enchant.success.single", enchantment.getFullname(level), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.enchant.success.multiple", enchantment.getFullname(level), targets.size()), true);
            }
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
}

