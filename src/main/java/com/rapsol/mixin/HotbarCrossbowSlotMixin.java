package com.rapsol.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(InGameHud.class)
public abstract class HotbarCrossbowSlotMixin {
    @Unique
    private static final Identifier redOverlay = Identifier.of("crossbowindicator", "textures/gui/red_slot.png");
    @Unique
    private static final Identifier greenOverlay = Identifier.of("crossbowindicator", "textures/gui/green_slot.png");

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    private void highlightHotbarCrossbowSlots(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int hotbarX = screenWidth / 2 - 91;
        int hotbarY = screenHeight - 22;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = client.player.getInventory().getStack(i);
            if (!stack.isOf(Items.CROSSBOW)) continue;

            AtomicReference<Identifier> overlay = new AtomicReference<>(null);

            if (stack.hasEnchantments()) {
                var enchantmentRegistry = client.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);

                enchantmentRegistry.getEntry(Enchantments.MULTISHOT.getValue()).ifPresent(multishotEntry -> {
                    if (EnchantmentHelper.getLevel(multishotEntry, stack) > 0) {
                        overlay.set(redOverlay);
                    }
                });

                enchantmentRegistry.getEntry(Enchantments.PIERCING.getValue()).ifPresent(piercingEntry -> {
                    if (EnchantmentHelper.getLevel(piercingEntry, stack) > 0) {
                        overlay.set(greenOverlay);
                    }
                });
            }

            if (overlay.get() != null) {
                int slotX = hotbarX + (i * 20);
                context.drawTexture(overlay.get(), slotX + 3, hotbarY + 3, 0, 0, 16, 16);
            }
        }
    }
}
