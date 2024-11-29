package com.tamagosci.trebirth.event;

import com.tamagosci.trebirth.TRebirth;
import com.tamagosci.trebirth.util.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
	// This class registers keybindings in the controls menu
	@Mod.EventBusSubscriber(modid = TRebirth.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModBusEvents {
		@SubscribeEvent
		public static void onKeyRegister(RegisterKeyMappingsEvent event) { event.register(KeyBinding.REBIRTH_MENU_KEY); }
	}

	// This class handles key presses
	@Mod.EventBusSubscriber(modid = TRebirth.MOD_ID, value = Dist.CLIENT)
	public static class ClientForgeEvents {
		@SubscribeEvent
		public static void onKeyInput(InputEvent.Key event) {
			// This is client side
			if (KeyBinding.REBIRTH_MENU_KEY.consumeClick()) {
				// This is where the key is pressed
				//TODO: Open GUI
			}
		}
	}
}