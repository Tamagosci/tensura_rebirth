package com.tamagosci.trebirth.event;

import com.tamagosci.trebirth.TRebirth;
import com.tamagosci.trebirth.rebirth.PlayerRebirthCount;
import com.tamagosci.trebirth.rebirth.PlayerRebirthCountProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TRebirth.MOD_ID)
public class ModEvents {
	// Add stuff to player data
	@SubscribeEvent
	public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof Player) {
			// Add rebirthCount tag to players that don't have it
			if (!event.getObject().getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).isPresent()) {
				event.addCapability(
					new ResourceLocation(TRebirth.MOD_ID, "properties"),
					new PlayerRebirthCountProvider()
				);
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			event.getOriginal().getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).ifPresent(oldStore -> {
				event.getOriginal().getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).ifPresent(newStore -> {
					newStore.copyFrom(oldStore);
				});
			});
		}
	}

	@SubscribeEvent
	public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
		event.register(PlayerRebirthCount.class);
	}

//	@SubscribeEvent
//	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//		if (event.side == LogicalSide.SERVER) {
//			//
//		}
//	}
}