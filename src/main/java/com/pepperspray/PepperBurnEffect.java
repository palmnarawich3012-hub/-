package com.pepperspray;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

/** Deals half a heart (1.0 HP) every second to ANY living entity, undead included. Can kill. */
public class PepperBurnEffect extends StatusEffect {
    public static final float DAMAGE = 1.0f;     // 1.0 = half a heart
    public static final int INTERVAL_TICKS = 20; // 20 ticks = 1 second

    public PepperBurnEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF8000);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % INTERVAL_TICKS == 0;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.damage(entity.getDamageSources().magic(), DAMAGE);
    }
}
