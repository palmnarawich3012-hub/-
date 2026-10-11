package com.pepperspray;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

/**
 * Hold right-click to spray (one-handed, normal item pose). Unlimited uses.
 * Every living entity hit takes half a heart per second for 5 seconds (+ slowness + blindness).
 */
public class PepperSprayItem extends Item {
    private static final int EFFECT_TICKS = 100;      // 5 seconds
    private static final double RANGE = 5.0;
    private static final double CONE = 0.85;          // dot product threshold
    private static final DustParticleEffect ORANGE =
            new DustParticleEffect(new Vector3f(1.0f, 0.5f, 0.05f), 1.6f);

    public PepperSprayItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    /** NONE = ordinary one-handed item pose (no bow/crossbow two-hand pose). */
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(world instanceof ServerWorld sw)) return;

        Vec3d eye = user.getEyePos();
        Vec3d dir = user.getRotationVec(1.0f);

        // Mist leaves the nozzle: a bit forward, down and toward the hand that holds the can
        boolean rightSide = (user.getActiveHand() == Hand.MAIN_HAND) == (user.getMainArm() == Arm.RIGHT);
        Vec3d right = dir.crossProduct(new Vec3d(0, 1, 0)).normalize();
        Vec3d nozzle = eye.add(dir.multiply(0.5)).add(right.multiply(rightSide ? 0.3 : -0.3)).add(0, -0.3, 0);

        for (int i = 0; i <= 10; i++) {
            Vec3d p = nozzle.add(dir.multiply(i * 0.5));
            double spread = 0.04 + i * 0.03;
            sw.spawnParticles(ORANGE, p.x, p.y, p.z, 4, spread, spread, spread, 0.01);
        }

        int used = getMaxUseTime(stack) - remainingUseTicks;
        if (used % 4 == 0) {
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS, 0.5f, 2.0f);
        }

        // Apply effects every 5 ticks
        if (used % 5 == 0) {
            Box box = new Box(eye, eye.add(dir.multiply(RANGE))).expand(1.0);
            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box,
                    x -> x != user && x.isAlive())) {
                Vec3d to = e.getEyePos().subtract(eye);
                if (to.length() > RANGE + 0.5) continue;
                if (to.normalize().dotProduct(dir) > CONE) {
                    // Only (re)start the burn when it is gone or nearly over, so the
                    // half-heart ticks stay at exactly one per second while spraying.
                    StatusEffectInstance burn = e.getStatusEffect(PepperSprayMod.PEPPER_BURN);
                    if (burn == null || burn.getDuration() <= 5) {
                        e.addStatusEffect(new StatusEffectInstance(PepperSprayMod.PEPPER_BURN, EFFECT_TICKS, 0), user);
                    }
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_TICKS, 1), user);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_TICKS, 0), user);
                }
            }
        }
    }
}
