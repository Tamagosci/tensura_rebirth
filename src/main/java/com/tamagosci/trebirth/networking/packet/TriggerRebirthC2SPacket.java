package com.tamagosci.trebirth.networking.packet;

import com.github.manasmods.tensura.command.TensuraCommand;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.tamagosci.trebirth.rebirth.PlayerRebirthCountProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TriggerRebirthC2SPacket {
	public TriggerRebirthC2SPacket(){}
	public TriggerRebirthC2SPacket(FriendlyByteBuf buffer){}
	public void toBytes(FriendlyByteBuf buffer){}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// WE ARE ON THE SERVER HERE
			ServerPlayer player = context.getSender();
			MinecraftServer server = player.level.getServer();

			//TODO: Rebirth Logic here

			// Check if player can rebirth
			final Advancement higherFormAdvancement = server.getAdvancements().getAdvancement(TensuraAdvancementsHelper.Advancements.HIGHER_FORM);
			if (player.getAdvancements().getOrStartProgress(higherFormAdvancement).isDone()) {
				// Reset stuff based on config here
				TensuraCommand
				// Increment rebirth count
				player.getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).ifPresent(rebirthCount -> {
					rebirthCount.addRebirthCount(1);
				});
				// Remove advancement
				for (String criterion : player.getAdvancements().getOrStartProgress(higherFormAdvancement).getCompletedCriteria()) {
					player.getAdvancements().revoke(higherFormAdvancement, criterion);
				}
			}
		});
		return true;
	}
}