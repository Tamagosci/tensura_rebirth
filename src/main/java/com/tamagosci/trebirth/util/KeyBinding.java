package com.tamagosci.trebirth.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBinding {
	public static final String KEY_CATEGORY_TREBIRTH = "key.category.trebirth";
	public static final String KEY_OPEN_REBIRTH_MENU = "key.trebirth.open_rebirth_menu";

	public static final KeyMapping REBIRTH_MENU_KEY = new KeyMapping(
		KEY_OPEN_REBIRTH_MENU,
		KeyConflictContext.IN_GAME,
		InputConstants.Type.KEYSYM,
		GLFW.GLFW_KEY_J,
		KEY_CATEGORY_TREBIRTH
	);
}