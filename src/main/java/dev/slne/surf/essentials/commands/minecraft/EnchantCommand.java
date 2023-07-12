package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.BrigadierMessage;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Arrays;
import java.util.Collection;

public class EnchantCommand extends EssentialsCommand {
    public EnchantCommand() {
        super("enchant", "enchant <targets> <enchantment> [<level>]", "enchants the item in targets main hand");

        withPermission(Permissions.ENCHANT_PERMISSION);

        then(entitiesArgument("targets")
                .then(keyArgument("enchantment")
                        .replaceSuggestions(SUGGEST_ENCHANTMENTS)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> enchant(
                                sender.getCallee(),
                                args.getUnchecked("targets"),
                                args.getUnchecked("enchantment"),
                                1
                        ))
                        .then(integerArgument("level", 1)
                                .executes((ResultingCommandExecutor) (sender, args) -> enchant(
                                        sender,
                                        args.getUnchecked("targets"),
                                        args.getUnchecked("enchantment"),
                                        args.getUnchecked("level")
                                ))
                        )
                )
        );
    }

    private static int enchant(CommandSender source, Collection<Entity> targetsUnchecked, NamespacedKey enchantmentKey, int level) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        val enchantment = Enchantment.getByKey(enchantmentKey);
        int successfullEnchantment = 0;

        if (enchantment == null) throw Exceptions.ERROR_UNKNOWN_ENCHANTMENT.create(enchantmentKey.asString());
        if (level > enchantment.getMaxLevel())
            throw Exceptions.ERROR_ENCHANTMENT_LEVEL_TOO_HIGH.create(level, enchantment.getMaxLevel());


        for (Entity entity : targets) {
            if (!(entity instanceof LivingEntity livingEntity))
                throw Exceptions.ERROR_NOT_VALID_ENTITY_FOR_COMMAND.create(entity);

            val equipment = livingEntity.getEquipment();
            if (equipment == null) throw Exceptions.ERROR_NOT_HOLDING_ITEM.create(livingEntity);

            val itemStack = equipment.getItemInMainHand();

            if (itemStack.getType() == Material.AIR) throw Exceptions.ERROR_NOT_HOLDING_ITEM.create(livingEntity);

            if (EssentialsUtil.isEnchantmentCompatible(enchantment, itemStack)) {
                itemStack.addEnchantment(enchantment, level);
                ++successfullEnchantment;

            } else if (targets.size() == 1)
                throw Exceptions.ERROR_ENCHANTMENT_INCOMPATIBLE.create(itemStack);
        }


        if (successfullEnchantment == 0) throw Exceptions.ERROR_ENCHANT_NOTHING_HAPPENED;

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(enchantment, level))
                    .append(Component.text(" wurde zu ", Colors.SUCCESS))
                    .append(EssentialsUtil.getDisplayName(targets.iterator().next()))
                    .append(Component.text("'s item hinzugefügt", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(source, Component.text("Die Verzauberung ", Colors.SUCCESS)
                    .append(EssentialsUtil.getDisplayName(enchantment, level))
                    .append(Component.text(" wurde zu ", Colors.SUCCESS))
                    .append(Component.text(targets.size(), Colors.TERTIARY))
                    .append(Component.text(" entities hinzugefügt", Colors.SUCCESS)));
        }

        return successfullEnchantment;
    }

    private static final ArgumentSuggestions<CommandSender> SUGGEST_ENCHANTMENTS = (info, builder) -> {
        val currentArg = info.currentArg().toLowerCase();

        Arrays.stream(Enchantment.values())
                .filter(enchantment -> currentArg.isEmpty() || currentArg.isBlank() ||
                        enchantment.getKey().asString().toLowerCase().startsWith(currentArg) || enchantment.getKey().value().startsWith(currentArg))
                .forEach(enchantment -> builder.suggest(enchantment.getKey().asString(), new BrigadierMessage(EssentialsUtil.getDisplayName(enchantment, 1))));

        return builder.buildFuture();
    };
}
