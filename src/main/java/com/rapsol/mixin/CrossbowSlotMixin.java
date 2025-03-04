package com.rapsol.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(HandledScreen.class)
public abstract class CrossbowSlotMixin {
	@Unique
	private static final Identifier redOverlay = Identifier.of("crossbowindicator", "textures/gui/red_slot.png");
	@Unique
	private static final Identifier greenOverlay = Identifier.of("crossbowindicator", "textures/gui/green_slot.png");

	@Inject(method = "drawSlot", at = @At("HEAD"))
	private void changeSlotBackground(DrawContext context, Slot slot, CallbackInfo ci) {
		ItemStack stack = slot.getStack();

		if (stack.isOf(Items.CROSSBOW)) {
			AtomicReference<Identifier> overlay = new AtomicReference<>(null);

			if (stack.hasEnchantments()) {
				MinecraftClient client = MinecraftClient.getInstance();
				World world = client.world;

				if (world != null) {
					var enchantmentRegistry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);

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
			}

			if (overlay.get() != null) {
				context.drawTexture(overlay.get(), slot.x - 1, slot.y - 1, 0, 0, 18, 18);
			}
		}
	}
}
