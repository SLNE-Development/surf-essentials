package dev.slne.surf.essentialsold.commands.minecraft;

/**
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AttributeCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"attribute"};
    }

    @Override
    public String usage() {
        return "/attribute <target> <attribute> <base | get | modifier> ";
    }

    @Override
    public String description() {
        return "Modifiziere die Attribute von Entities";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.ATTRIBUTE_PERMISSION));


        literal.then(Commands.argument("target", EntityArgument.entity())
                .then(Commands.argument("attribute", ResourceArgument.resource(this.commandBuildContext, Registries.ATTRIBUTE))

                        .then(Commands.literal("reset")
                                .executes(context -> resetAttributes(
                                        context.getSource(),
                                        EntityArgument.getTileState(context, "target"),
                                        ResourceArgument.getAttribute(context, "attribute")
                                )))

                        .then(Commands.literal("get")
                                .executes(context -> getAttributeValue(
                                        context.getSource(),
                                        EntityArgument.getTileState(context, "target"),
                                        ResourceArgument.getAttribute(context, "attribute"),
                                        1.0
                                ))
                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                        .executes(context -> getAttributeValue(
                                                context.getSource(),
                                                EntityArgument.getTileState(context, "target"),
                                                ResourceArgument.getAttribute(context, "attribute"),
                                                DoubleArgumentType.getDouble(context, "scale")
                                        ))))

                        .then(Commands.literal("base")
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes(context -> setAttributeBase(
                                                        context.getSource(),
                                                        EntityArgument.getTileState(context, "target"),
                                                        ResourceArgument.getAttribute(context, "attribute"),
                                                        DoubleArgumentType.getDouble(context, "value")
                                                ))))
                                .then(Commands.literal("get")
                                        .executes(context -> getAttributeBase(
                                                context.getSource(),
                                                EntityArgument.getTileState(context, "target"),
                                                ResourceArgument.getAttribute(context, "attribute"),
                                                1.0
                                        ))
                                        .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                .executes(context -> getAttributeBase(
                                                        context.getSource(),
                                                        EntityArgument.getTileState(context, "target"),
                                                        ResourceArgument.getAttribute(context, "attribute"),
                                                        DoubleArgumentType.getDouble(context, "scale")
                                                )))))

                        .then(Commands.literal("modifier")
                                .then(Commands.literal("add")
                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                .then(Commands.argument("name", StringArgumentType.string())
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .then(Commands.literal("add")
                                                                        .executes(context -> addModifier(
                                                                                context.getSource(),
                                                                                EntityArgument.getTileState(context, "target"),
                                                                                ResourceArgument.getAttribute(context, "attribute"),
                                                                                UuidArgument.getUuid(context, "uuid"),
                                                                                StringArgumentType.getString(context, "name"),
                                                                                DoubleArgumentType.getDouble(context, "value"),
                                                                                AttributeModifier.Operation.ADDITION
                                                                        )))
                                                                .then(Commands.literal("multiply")
                                                                        .executes(context -> addModifier(
                                                                                context.getSource(),
                                                                                EntityArgument.getTileState(context, "target"),
                                                                                ResourceArgument.getAttribute(context, "attribute"),
                                                                                UuidArgument.getUuid(context, "uuid"),
                                                                                StringArgumentType.getString(context, "name"),
                                                                                DoubleArgumentType.getDouble(context, "value"),
                                                                                AttributeModifier.Operation.MULTIPLY_TOTAL
                                                                        )))
                                                                .then(Commands.literal("multiply_base")
                                                                        .executes(context -> addModifier(
                                                                                context.getSource(),
                                                                                EntityArgument.getTileState(context, "target"),
                                                                                ResourceArgument.getAttribute(context, "attribute"),
                                                                                UuidArgument.getUuid(context, "uuid"),
                                                                                StringArgumentType.getString(context, "name"),
                                                                                DoubleArgumentType.getDouble(context, "value"),
                                                                                AttributeModifier.Operation.MULTIPLY_BASE
                                                                        )))))))
                                .then(Commands.literal("remove")
                                        .then(Commands.argument("uuid", UuidArgument.uuid())
                                                .executes(context -> removeModifier(
                                                        context.getSource(),
                                                        EntityArgument.getTileState(context, "target"),
                                                        ResourceArgument.getAttribute(context, "attribute"),
                                                        UuidArgument.getUuid(context, "uuid")
                                                ))))
                                .then(Commands.literal("value")
                                        .then(Commands.literal("get")
                                                .then(Commands.argument("uuid", UuidArgument.uuid())
                                                        .executes(context -> getAttributeModifier(
                                                                context.getSource(),
                                                                EntityArgument.getTileState(context, "target"),
                                                                ResourceArgument.getAttribute(context, "attribute"),
                                                                UuidArgument.getUuid(context, "uuid"),
                                                                1.0
                                                        ))
                                                        .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                                .executes(context -> getAttributeModifier(
                                                                        context.getSource(),
                                                                        EntityArgument.getTileState(context, "target"),
                                                                        ResourceArgument.getAttribute(context, "attribute"),
                                                                        UuidArgument.getUuid(context, "uuid"),
                                                                        DoubleArgumentType.getDouble(context, "scale")
                                                                )))))))));
    }



    private static int getAttributeValue(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double multiplier) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        final var livingEntity = getEntityWithAttribute(target, attribute);
        double attributeValue = livingEntity.getAttributeValue(attribute);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Wert von ", Colors.INFO)
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" ist bei ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" %s".formatted(attributeValue), Colors.VARIABLE_VALUE)));

        return (int)(attributeValue * multiplier);
    }

    private static int getAttributeBase(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double multiplier) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        final var livingEntity = getEntityWithAttribute(target, attribute);
        double baseValue = livingEntity.getAttributeBaseValue(attribute);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Grundwert von ", Colors.INFO)
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" ist bei ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" %s".formatted(baseValue), Colors.VARIABLE_VALUE)));

        return (int)(baseValue * multiplier);
    }

    private static int getAttributeModifier(CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid, double multiplier) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        final var livingEntity = getEntityWithAttribute(target, attribute);
        final var attributeMap = livingEntity.getAttributes();
        if (!attributeMap.hasModifier(attribute, uuid))
            throw ERROR_NO_SUCH_MODIFIER.create(EssentialsUtil.getDisplayName(target), getAttributeDescription(attribute), uuid);

        double modifierValue = attributeMap.getModifierValue(attribute, uuid);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Wert des Modifikators ", Colors.INFO)
                .append(Component.text(uuid.toString(), Colors.SECONDARY))
                .append(Component.text(" von ", Colors.INFO))
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" ist bei ", Colors.INFO))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" %s".formatted(modifierValue), Colors.VARIABLE_VALUE)));

        return (int) (modifierValue * multiplier);

    }

    private static int setAttributeBase(CommandSourceStack source, Entity target, Holder<Attribute> attribute, double value) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        getAttributeInstance(target, attribute).setBaseValue(value);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Grundwert von ", Colors.SUCCESS)
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" wurde bei ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" auf ", Colors.SUCCESS))
                .append(Component.text(value, Colors.VARIABLE_VALUE))
                .append(Component.text(" gesetzt", Colors.SUCCESS)));
        return 1;
    }

    private static int resetAttributes(CommandSourceStack source, Entity target, Holder<Attribute> attribute) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        getAttributeInstance(target, attribute).removeModifiers();

        EssentialsUtil.sendSourceSuccess(source, Component.text("Es wurden alle Attribute modifizierungen bei ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" zurückgesetzt", Colors.SUCCESS)));
        return 1;
    }

    private static int addModifier(CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid, String name, double value, AttributeModifier.Operation operation) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        final var attributeInstance = getAttributeInstance(target, attribute);
        final var attributeModifier = new AttributeModifier(uuid, name, value, operation);
        if (attributeInstance.hasModifier(attributeModifier))
            throw ERROR_MODIFIER_ALREADY_PRESENT.create(EssentialsUtil.getDisplayName(target), getAttributeDescription(attribute), uuid);

        attributeInstance.addPermanentModifier(attributeModifier);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Modifikator ", Colors.SUCCESS)
                .append(Component.text(uuid.toString(), Colors.SECONDARY))
                .append(Component.text(" wurde bei ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" in ", Colors.SUCCESS))
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" hinzugefügt", Colors.SUCCESS)));
        return 1;

    }

    private static int removeModifier(CommandSourceStack source, Entity target, Holder<Attribute> attribute, UUID uuid) throws CommandSyntaxException {
        EssentialsUtil.checkSingleEntitySuggestion(source, target);
        final var attributeInstance = getAttributeInstance(target, attribute);
        if (!attributeInstance.removePermanentModifier(uuid))
            throw ERROR_NO_SUCH_MODIFIER.create(EssentialsUtil.getDisplayName(target), getAttributeDescription(attribute), uuid);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Der Modifikator ", Colors.SUCCESS)
                .append(Component.text(uuid.toString(), Colors.SECONDARY))
                .append(Component.text(" wurde bei ", Colors.SUCCESS))
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" in ", Colors.SUCCESS))
                .append(getAttributeDescription(attribute).color(Colors.VARIABLE_KEY))
                .append(Component.text(" entfernt", Colors.SUCCESS)));
        return 1;
    }


    private static @NotNull AttributeInstance getAttributeInstance(Entity entity, Holder<Attribute> attribute) throws CommandSyntaxException {
        AttributeInstance attributeInstance = getLivingEntity(entity).getAttributes().getInstance(attribute);
        if (attributeInstance == null) throw ERROR_NO_SUCH_ATTRIBUTE.create(EssentialsUtil.getDisplayName(entity), getAttributeDescription(attribute));
        return attributeInstance;
    }

    @Contract("null -> fail")
    private static LivingEntity getLivingEntity(Entity entity) throws CommandSyntaxException {
        if (!(entity instanceof LivingEntity livingEntity)) throw ERROR_NOT_LIVING_ENTITY.create(EssentialsUtil.getDisplayName(entity));
        return livingEntity;
    }

    private static @NotNull LivingEntity getEntityWithAttribute(Entity entity, Holder<Attribute> attribute) throws CommandSyntaxException {
        LivingEntity livingEntity = getLivingEntity(entity);
        if (!livingEntity.getAttributes().hasAttribute(attribute)) throw ERROR_NO_SUCH_ATTRIBUTE.create(EssentialsUtil.getDisplayName(entity), getAttributeDescription(attribute));

        return livingEntity;
    }
    @Contract("_ -> new")
    private static @NotNull Component getAttributeDescription(Holder<Attribute> attribute) {
        return Component.translatable(attribute.value().getDescriptionId());
    }
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((name) ->
            PaperAdventure.asVanilla(Component.translatable("commands.attribute.failed.entity", (Component) name)));
    private static final Dynamic2CommandExceptionType ERROR_NO_SUCH_ATTRIBUTE = new Dynamic2CommandExceptionType((entityName, attributeName) ->
            PaperAdventure.asVanilla(Component.translatable("commands.attribute.failed.no_attribute", (Component) entityName, (Component) attributeName)));
    private static final Dynamic3CommandExceptionType ERROR_NO_SUCH_MODIFIER = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) ->
            PaperAdventure.asVanilla(Component.translatable("commands.attribute.failed.no_modifier", (Component) attributeName, (Component) entityName, Component.text(uuid.toString()))));
    private static final Dynamic3CommandExceptionType ERROR_MODIFIER_ALREADY_PRESENT = new Dynamic3CommandExceptionType((entityName, attributeName, uuid) ->
            PaperAdventure.asVanilla(Component.translatable("commands.attribute.failed.modifier_already_present", Component.text(uuid.toString()), (Component) attributeName, (Component) entityName)));
}
 */
