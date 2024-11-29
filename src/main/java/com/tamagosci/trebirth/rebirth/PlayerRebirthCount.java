package com.tamagosci.trebirth.rebirth;

import net.minecraft.nbt.CompoundTag;

public class PlayerRebirthCount {
	private static final String REBIRTH_COUNT_NBT = "rebirth_count";
	private int rebirthCount;
	private static final int MIN_REBIRTH_COUNT = 0;

	public int getRebirthCount() { return rebirthCount; }
	public void addRebirthCount(int amount) {
		rebirthCount = Math.max(rebirthCount + amount, MIN_REBIRTH_COUNT);
	}
	public void setRebirthCount(int amount) {
		rebirthCount = Math.max(amount, MIN_REBIRTH_COUNT);
	}

	public void copyFrom(PlayerRebirthCount source) { this.rebirthCount = source.rebirthCount; }
	public void saveNBTData(CompoundTag nbt) { nbt.putInt(REBIRTH_COUNT_NBT, rebirthCount); }
	public void loadNBTData(CompoundTag nbt) { rebirthCount = nbt.getInt(REBIRTH_COUNT_NBT); }
}