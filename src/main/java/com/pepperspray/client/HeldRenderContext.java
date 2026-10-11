package com.pepperspray.client;

import net.minecraft.item.ItemStack;

/** Set while the first-person arm is drawn, so the arm renderer knows to attach the spray can to the hand. */
public final class HeldRenderContext {
    public static ItemStack stack = ItemStack.EMPTY;
    private HeldRenderContext() {}
}
