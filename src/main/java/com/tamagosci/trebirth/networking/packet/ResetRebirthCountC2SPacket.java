package com.tamagosci.trebirth.networking.packet;

import com.tamagosci.trebirth.rebirth.PlayerRebirthCountProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ResetRebirthCountC2SPacket {
	public ResetRebirthCountC2SPacket(){}
	public ResetRebirthCountC2SPacket(FriendlyByteBuf buffer){}
	public void toBytes(FriendlyByteBuf buffer){}

	public boolean handle(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// HERE WE ARE ON THE SERVER
			ServerPlayer player = context.getSender();

			if (player != null) {
				player.getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).ifPresent(rebirthCount -> {
					rebirthCount.setRebirthCount(0);
				});
			}
		});
		return true;
	}
}