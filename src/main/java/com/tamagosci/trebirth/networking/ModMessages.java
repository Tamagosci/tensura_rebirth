package com.tamagosci.trebirth.networking;

import com.tamagosci.trebirth.TRebirth;
import com.tamagosci.trebirth.networking.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
	private static SimpleChannel INSTANCE;

	private static int packetId = 0;
	private static int id() { return packetId++; }

	public static void register() {
		SimpleChannel net = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(TRebirth.MOD_ID, "messages"))
			.networkProtocolVersion(() -> "1.0")
			.clientAcceptedVersions(s -> true)
			.serverAcceptedVersions(s -> true)
			.simpleChannel();

		INSTANCE = net;

		// CLIENT TO SERVER

		// Trigger Rebirth
		net.messageBuilder(TriggerRebirthC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
			.decoder(TriggerRebirthC2SPacket::new)
			.encoder(TriggerRebirthC2SPacket::toBytes)
			.consumerMainThread(TriggerRebirthC2SPacket::handle)
			.add();
		// Reset Rebirth Count
		net.messageBuilder(ResetRebirthCountC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
			.decoder(ResetRebirthCountC2SPacket::new)
			.encoder(ResetRebirthCountC2SPacket::toBytes)
			.consumerMainThread(ResetRebirthCountC2SPacket::handle)
			.add();

		// SERVER TO CLIENT
	}

	public static <MSG> void sentToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}

	public static <MSG> void sentToPlayer(MSG message, ServerPlayer player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
	}
}