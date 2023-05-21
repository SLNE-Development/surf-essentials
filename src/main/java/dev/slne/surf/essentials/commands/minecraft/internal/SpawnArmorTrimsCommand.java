package dev.slne.surf.essentials.commands.minecraft.internal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.kyori.adventure.text.Component;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.armortrim.*;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.ToIntFunction;

public class SpawnArmorTrimsCommand extends BrigadierCommand {
    private static final List<ResourceKey<TrimPattern>> VANILLA_TRIM_PATTERNS;
    private static final List<ResourceKey<TrimMaterial>> VANILLA_TRIM_MATERIALS;
    private static final ToIntFunction<ResourceKey<TrimPattern>> TRIM_PATTERN_ORDER;
    private static final ToIntFunction<ResourceKey<TrimMaterial>> TRIM_MATERIAL_ORDER;
    private static final Map<Pair<ArmorMaterial, EquipmentSlot>, Item> MATERIAL_AND_SLOT_TO_ITEM;
    private static final Map<UUID, IntList> IDS;
    private static final SimpleCommandExceptionType ERROR_NO_ARMOR_TRIMS_SPAWNED;

    @Override
    public String[] names() {
        return new String[]{"spawn_armor_trims"};
    }

    @Override
    public String usage() {
        return "/spawn_armor_trims [<undo>]";
    }

    @Override
    public String description() {
        return "Spawn the new armor trims";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(stack -> EssentialsUtil.checkPermissions(Permissions.SPAWN_ARMOR_TRIMS_PERMISSION).test(stack) &&
                stack.getLevel().enabledFeatures().contains(FeatureFlags.UPDATE_1_20)
        );

        literal.executes(context -> spawnArmorTrims(context.getSource()));

        literal.then(Commands.literal("remove")
                .executes(context -> removeArmorTrims(context.getSource())));
    }

    private int spawnArmorTrims(CommandSourceStack source) throws CommandSyntaxException {

        final var player = source.getPlayerOrException();
        final var level = player.getLevel();
        final var armorTrims = new HashSet<ArmorTrim>();
        final var trimPattern = level.registryAccess().registryOrThrow(Registries.TRIM_PATTERN);
        final var trimMaterial = level.registryAccess().registryOrThrow(Registries.TRIM_MATERIAL);
        final var fromPos = player.blockPosition().relative(player.getDirection(), 5);
        final int armorMaterialsAmount = ArmorMaterials.values().length - 1;
        final double standDistance = 3.0;
        int patternIndex = 0;
        int materialIndex = 0;
        int currentDelay = 0;

        trimPattern.stream()
                .sorted(Comparator.comparing(pattern -> TRIM_PATTERN_ORDER.applyAsInt(trimPattern.getResourceKey(pattern).orElse(null))))
                .forEachOrdered(pattern -> trimMaterial.stream()
                        .sorted(Comparator.comparing(material -> TRIM_MATERIAL_ORDER.applyAsInt(trimMaterial.getResourceKey(material).orElse(null))))
                        .forEachOrdered(material -> armorTrims.add(new ArmorTrim(trimMaterial.wrapAsHolder(material), trimPattern.wrapAsHolder(pattern)))));

        for (ArmorTrim armorTrim : armorTrims) {
            final var armorMaterials = ArmorMaterials.values();
            patternIndex++;

            for (ArmorMaterials armorMaterial : armorMaterials) {
                final double x, y, z;
                x = (double) fromPos.getX() + 0.5 - (double) (patternIndex % trimMaterial.size()) * standDistance;
                y = (double) fromPos.getY() + 0.5 + (double) (materialIndex % armorMaterialsAmount) * 3.0;
                z = (double) fromPos.getZ() + 0.5 + (double) (patternIndex / trimMaterial.size() * 10);
                final var armorStand = new ArmorStand(level, x, y, z);
                final var equipmentSlots = EquipmentSlot.values();

                armorStand.setId(EssentialsUtil.getCustomEntityId());

                Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> {
                    IDS.computeIfAbsent(player.getUUID(), uuid -> new IntArrayList()).add(armorStand.getId());
                    EssentialsUtil.sendPackets(player, new ClientboundAddEntityPacket(armorStand));

                    for (EquipmentSlot equipmentSlot : equipmentSlots) {
                        final var item = MATERIAL_AND_SLOT_TO_ITEM.get(Pair.of(armorMaterial, equipmentSlot));
                        if (item == null) continue;
                        final var itemStack = new ItemStack(item);

                        ArmorTrim.setTrim(level.registryAccess(), itemStack, armorTrim);

                        EssentialsUtil.sendPackets(
                                player,
                                new ClientboundSetEquipmentPacket(armorStand.getId(), EssentialsUtil.make(new ArrayList<>(), pairs -> pairs.add(new Pair<>(equipmentSlot, itemStack)))),
                                new ClientboundSetEntityDataPacket(
                                        armorStand.getId(),
                                        EssentialsUtil.make(new ArrayList<>(), dataValues -> {
                                            if (item instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.TURTLE) {
                                                dataValues.add(new SynchedEntityData.DataValue<>(0, EntityDataSerializers.BYTE, (byte) 0x20));
                                            }
                                            dataValues.add(new SynchedEntityData.DataValue<>(3, EntityDataSerializers.BOOLEAN, true));
                                            dataValues.add(new SynchedEntityData.DataValue<>(2, EntityDataSerializers.OPTIONAL_COMPONENT, Optional.of(
                                                    armorTrim.pattern().value().copyWithStyle(armorTrim.material())
                                                            .copy().append(" ")
                                                            .append(armorTrim.material().value().description()))
                                            ));
                                        })
                                )

                        );
                    }
                }, currentDelay);

                materialIndex++;
                currentDelay++;
            }
        }

        EssentialsUtil.sendSourceSuccess(source, "Rüstungsständer mit Armor-trims werden um dich herum gespawnt.");

        return 1;
    }

    private int removeArmorTrims(CommandSourceStack source) throws CommandSyntaxException {
        final var player = source.getPlayerOrException();
        final var armorIds = IDS.get(player.getUUID());

        if (armorIds == null) throw ERROR_NO_ARMOR_TRIMS_SPAWNED.create();

        EssentialsUtil.sendPackets(player, new ClientboundRemoveEntitiesPacket(armorIds));

        IDS.remove(player.getUUID());

        EssentialsUtil.sendSuccess(player, "Es wurden alle Rüstungsständer mit Armor-trims entfernt.");
        return 1;
    }




    static {
        VANILLA_TRIM_PATTERNS = List.of(TrimPatterns.SENTRY, TrimPatterns.DUNE, TrimPatterns.COAST, TrimPatterns.WILD, TrimPatterns.WARD, TrimPatterns.EYE, TrimPatterns.VEX, TrimPatterns.TIDE, TrimPatterns.SNOUT, TrimPatterns.RIB, TrimPatterns.SPIRE);
        VANILLA_TRIM_MATERIALS = List.of(TrimMaterials.QUARTZ, TrimMaterials.IRON, TrimMaterials.NETHERITE, TrimMaterials.REDSTONE, TrimMaterials.COPPER, TrimMaterials.GOLD, TrimMaterials.EMERALD, TrimMaterials.DIAMOND, TrimMaterials.LAPIS, TrimMaterials.AMETHYST);
        TRIM_PATTERN_ORDER = Util.createIndexLookup(VANILLA_TRIM_PATTERNS);
        TRIM_MATERIAL_ORDER = Util.createIndexLookup(VANILLA_TRIM_MATERIALS);
        IDS = new HashMap<>();

        ERROR_NO_ARMOR_TRIMS_SPAWNED = new SimpleCommandExceptionType(PaperAdventure.asVanilla(Component.text("Du hast noch keine Armor-trims gespawnt")));

        MATERIAL_AND_SLOT_TO_ITEM = EssentialsUtil.make(new HashMap<>(), map -> {
            map.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.HEAD), Items.CHAINMAIL_HELMET);
            map.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.CHEST), Items.CHAINMAIL_CHESTPLATE);
            map.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.LEGS), Items.CHAINMAIL_LEGGINGS);
            map.put(Pair.of(ArmorMaterials.CHAIN, EquipmentSlot.FEET), Items.CHAINMAIL_BOOTS);
            map.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.HEAD), Items.IRON_HELMET);
            map.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.CHEST), Items.IRON_CHESTPLATE);
            map.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.LEGS), Items.IRON_LEGGINGS);
            map.put(Pair.of(ArmorMaterials.IRON, EquipmentSlot.FEET), Items.IRON_BOOTS);
            map.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.HEAD), Items.GOLDEN_HELMET);
            map.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.CHEST), Items.GOLDEN_CHESTPLATE);
            map.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.LEGS), Items.GOLDEN_LEGGINGS);
            map.put(Pair.of(ArmorMaterials.GOLD, EquipmentSlot.FEET), Items.GOLDEN_BOOTS);
            map.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD), Items.NETHERITE_HELMET);
            map.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST), Items.NETHERITE_CHESTPLATE);
            map.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS), Items.NETHERITE_LEGGINGS);
            map.put(Pair.of(ArmorMaterials.NETHERITE, EquipmentSlot.FEET), Items.NETHERITE_BOOTS);
            map.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD), Items.DIAMOND_HELMET);
            map.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST), Items.DIAMOND_CHESTPLATE);
            map.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS), Items.DIAMOND_LEGGINGS);
            map.put(Pair.of(ArmorMaterials.DIAMOND, EquipmentSlot.FEET), Items.DIAMOND_BOOTS);
            map.put(Pair.of(ArmorMaterials.TURTLE, EquipmentSlot.HEAD), Items.TURTLE_HELMET);
        });
    }
}
