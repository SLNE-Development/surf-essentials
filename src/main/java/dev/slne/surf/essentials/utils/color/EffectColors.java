package dev.slne.surf.essentials.utils.color;

import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;

public class EffectColors {
    private static final Map<MobEffect, TextColor> EFFECT_COLORS = new HashMap<>();
    static{
        EFFECT_COLORS.put(MobEffects.ABSORPTION, TextColor.fromHexString("#2552A5"));
        EFFECT_COLORS.put(MobEffects.BAD_OMEN, TextColor.fromHexString("#0B6138"));
        EFFECT_COLORS.put(MobEffects.BLINDNESS, TextColor.fromHexString("#1F1F23"));
        EFFECT_COLORS.put(MobEffects.CONFUSION, TextColor.fromHexString("#551D4A"));
        EFFECT_COLORS.put(MobEffects.CONDUIT_POWER, TextColor.fromHexString("#1DC2D1"));
        EFFECT_COLORS.put(MobEffects.DAMAGE_BOOST, TextColor.fromHexString("#962524"));
        EFFECT_COLORS.put(MobEffects.DAMAGE_RESISTANCE, TextColor.fromHexString("#99453A"));
        EFFECT_COLORS.put(MobEffects.DARKNESS, TextColor.fromHexString("#1E1E23"));
        EFFECT_COLORS.put(MobEffects.DIG_SLOWDOWN, TextColor.fromHexString("#4A4217"));
        EFFECT_COLORS.put(MobEffects.DIG_SPEED, TextColor.fromHexString("#D9C043"));
        EFFECT_COLORS.put(MobEffects.DOLPHINS_GRACE, TextColor.fromHexString("#C0A44D"));
        EFFECT_COLORS.put(MobEffects.FIRE_RESISTANCE, TextColor.fromHexString("#E89D3B"));
        EFFECT_COLORS.put(MobEffects.GLOWING, TextColor.fromHexString("#94A061"));
        EFFECT_COLORS.put(MobEffects.HARM, TextColor.fromHexString("#440A09"));
        EFFECT_COLORS.put(MobEffects.HEAL, TextColor.fromHexString("#FC2524"));
        EFFECT_COLORS.put(MobEffects.HEALTH_BOOST, TextColor.fromHexString("#F87D23"));
        EFFECT_COLORS.put(MobEffects.HERO_OF_THE_VILLAGE, TextColor.fromHexString("#44FF44"));
        EFFECT_COLORS.put(MobEffects.HUNGER, TextColor.fromHexString("#587653"));
        EFFECT_COLORS.put(MobEffects.INVISIBILITY, TextColor.fromHexString("#818595"));
        EFFECT_COLORS.put(MobEffects.JUMP, TextColor.fromHexString("#23FC4D"));
        EFFECT_COLORS.put(MobEffects.LEVITATION, TextColor.fromHexString("#33CCFF"));
        EFFECT_COLORS.put(MobEffects.LUCK, TextColor.fromHexString("#349C00"));
        EFFECT_COLORS.put(MobEffects.MOVEMENT_SLOWDOWN, TextColor.fromHexString("#5C6E83"));
        EFFECT_COLORS.put(MobEffects.MOVEMENT_SPEED, TextColor.fromHexString("#7EB2CA"));
        EFFECT_COLORS.put(MobEffects.NIGHT_VISION, TextColor.fromHexString("#2020A4"));
        EFFECT_COLORS.put(MobEffects.POISON, TextColor.fromHexString("#4F9632"));
        EFFECT_COLORS.put(MobEffects.REGENERATION, TextColor.fromHexString("#D15EAE"));
        EFFECT_COLORS.put(MobEffects.SATURATION, TextColor.fromHexString("#F82421"));
        EFFECT_COLORS.put(MobEffects.SLOW_FALLING, TextColor.fromHexString("#FCF4D5"));
        EFFECT_COLORS.put(MobEffects.UNLUCK, TextColor.fromHexString("#9D9D9D"));
        EFFECT_COLORS.put(MobEffects.WATER_BREATHING, TextColor.fromHexString("#4F9632"));
        EFFECT_COLORS.put(MobEffects.WEAKNESS, TextColor.fromHexString("#9D9D9D"));
        EFFECT_COLORS.put(MobEffects.WITHER, TextColor.fromHexString("#9D9D9D"));
    }

    public static TextColor getEffectColor(MobEffect effect){
        return EFFECT_COLORS.getOrDefault(effect, TextColor.fromHexString("#999999"));
    }
}
