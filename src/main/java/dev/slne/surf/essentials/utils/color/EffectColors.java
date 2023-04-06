package dev.slne.surf.essentials.utils.color;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.world.effect.MobEffects.*;

public class EffectColors {
    private static final Map<MobEffect, TextColor> EFFECT_COLORS = EssentialsUtil.make(new HashMap<>(), map -> {
        map.putIfAbsent(ABSORPTION, TextColor.fromHexString("#2552A5"));
        map.putIfAbsent(BAD_OMEN, TextColor.fromHexString("#0B6138"));
        map.putIfAbsent(BLINDNESS, TextColor.fromHexString("#1F1F23"));
        map.putIfAbsent(CONFUSION, TextColor.fromHexString("#551D4A"));
        map.putIfAbsent(CONDUIT_POWER, TextColor.fromHexString("#1DC2D1"));
        map.putIfAbsent(DAMAGE_BOOST, TextColor.fromHexString("#962524"));
        map.putIfAbsent(DAMAGE_RESISTANCE, TextColor.fromHexString("#99453A"));
        map.putIfAbsent(DARKNESS, TextColor.fromHexString("#1E1E23"));
        map.putIfAbsent(DIG_SLOWDOWN, TextColor.fromHexString("#4A4217"));
        map.putIfAbsent(DIG_SPEED, TextColor.fromHexString("#D9C043"));
        map.putIfAbsent(DOLPHINS_GRACE, TextColor.fromHexString("#C0A44D"));
        map.putIfAbsent(FIRE_RESISTANCE, TextColor.fromHexString("#E89D3B"));
        map.putIfAbsent(GLOWING, TextColor.fromHexString("#94A061"));
        map.putIfAbsent(HARM, TextColor.fromHexString("#440A09"));
        map.putIfAbsent(HEAL, TextColor.fromHexString("#FC2524"));
        map.putIfAbsent(HEALTH_BOOST, TextColor.fromHexString("#F87D23"));
        map.putIfAbsent(HERO_OF_THE_VILLAGE, TextColor.fromHexString("#44FF44"));
        map.putIfAbsent(HUNGER, TextColor.fromHexString("#587653"));
        map.putIfAbsent(INVISIBILITY, TextColor.fromHexString("#818595"));
        map.putIfAbsent(JUMP, TextColor.fromHexString("#23FC4D"));
        map.putIfAbsent(LEVITATION, TextColor.fromHexString("#33CCFF"));
        map.putIfAbsent(LUCK, TextColor.fromHexString("#349C00"));
        map.putIfAbsent(MOVEMENT_SLOWDOWN, TextColor.fromHexString("#5C6E83"));
        map.putIfAbsent(MOVEMENT_SPEED, TextColor.fromHexString("#7EB2CA"));
        map.putIfAbsent(NIGHT_VISION, TextColor.fromHexString("#2020A4"));
        map.putIfAbsent(POISON, TextColor.fromHexString("#4F9632"));
        map.putIfAbsent(REGENERATION, TextColor.fromHexString("#D15EAE"));
        map.putIfAbsent(SATURATION, TextColor.fromHexString("#F82421"));
        map.putIfAbsent(SLOW_FALLING, TextColor.fromHexString("#FCF4D5"));
        map.putIfAbsent(UNLUCK, TextColor.fromHexString("#9D9D9D"));
        map.putIfAbsent(WATER_BREATHING, TextColor.fromHexString("#4F9632"));
        map.putIfAbsent(WEAKNESS, TextColor.fromHexString("#9D9D9D"));
        map.putIfAbsent(WITHER, TextColor.fromHexString("#9D9D9D"));
    });

    public static TextColor getEffectColor(MobEffect effect){
        return EFFECT_COLORS.getOrDefault(effect, TextColor.fromHexString("#999999"));
    }
}
