package com.tamagosci.trebirth.rebirth;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerRebirthCountProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
	public static Capability<PlayerRebirthCount> PLAYER_REBIRTH_COUNT = CapabilityManager.get(new CapabilityToken<PlayerRebirthCount>() {});

	private PlayerRebirthCount rebirthCount = null;
	private final LazyOptional<PlayerRebirthCount> optional = LazyOptional.of(this::getPlayerRebirthCount);

	private PlayerRebirthCount getPlayerRebirthCount() {
		if (rebirthCount == null) rebirthCount = new PlayerRebirthCount();
		return rebirthCount;
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
		if (capability == PLAYER_REBIRTH_COUNT) return optional.cast();
		else return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		getPlayerRebirthCount().saveNBTData(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		getPlayerRebirthCount().loadNBTData(nbt);
	}
}