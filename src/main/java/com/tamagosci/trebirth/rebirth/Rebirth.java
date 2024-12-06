package com.tamagosci.trebirth.rebirth;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.ManasSkillInstance;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.manascore.api.skills.capability.SkillStorage;
import com.github.manasmods.manascore.api.skills.event.RemoveSkillEvent;
import com.github.manasmods.tensura.ability.SkillUtils;
import com.github.manasmods.tensura.ability.TensuraSkillInstance;
import com.github.manasmods.tensura.ability.battlewill.Battewill;
import com.github.manasmods.tensura.ability.magic.Magic;
import com.github.manasmods.tensura.ability.skill.Skill;
import com.github.manasmods.tensura.ability.skill.resist.ResistSkill;
import com.github.manasmods.tensura.capability.effects.TensuraEffectsCapability;
import com.github.manasmods.tensura.capability.ep.TensuraEPCapability;
import com.github.manasmods.tensura.capability.race.ITensuraPlayerCapability;
import com.github.manasmods.tensura.capability.race.TensuraPlayerCapability;
import com.github.manasmods.tensura.capability.skill.TensuraSkillCapability;
import com.github.manasmods.tensura.capability.smithing.ISmithingCapability;
import com.github.manasmods.tensura.capability.smithing.SmithingCapability;
import com.github.manasmods.tensura.client.particle.TensuraParticleHelper;
import com.github.manasmods.tensura.menu.RaceSelectionMenu;
import com.github.manasmods.tensura.race.Race;
import com.github.manasmods.tensura.registry.dimensions.TensuraDimensions;
import com.github.manasmods.tensura.util.TensuraAdvancementsHelper;
import com.github.manasmods.tensura.world.TensuraGameRules;
import com.github.manasmods.tensura.world.savedata.UniqueSkillSaveData;
import com.tamagosci.trebirth.Config;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;

public class Rebirth {
	public static boolean rebirthEntity(@NotNull LivingEntity entity, boolean incrementRebirthCounter) {
		// Check if player can rebirth
		if (!canRebirthEntity(entity)) return false;
		// Make important variables accessible
		ServerPlayer player = (ServerPlayer) entity;
		MinecraftServer server = player.getServer();
		Level level = player.getLevel();
		// Reset stuff based on config here
		resetPlayerHonorConfig(player);
		// Disable flight
		if (Config.resetRace) {
			if (!player.isCreative() && !player.isSpectator() && (player.getAbilities().flying || player.getAbilities().mayfly)) {
				player.getAbilities().flying = false;
				player.getAbilities().mayfly = false;
				player.onUpdateAbilities();
			}
		}
		// Increment rebirth count
		if (incrementRebirthCounter) {
			player.getCapability(PlayerRebirthCountProvider.PLAYER_REBIRTH_COUNT).ifPresent(rebirthCount -> {
				rebirthCount.addRebirthCount(1);
			});
		}
		// Play sound
		entity.getLevel().playSound((Player)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
		// Shinies
		TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, (double)1.0F);
		TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, (double)2.0F);
		TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, (double)1.0F);
	}

	public static boolean canRebirthEntity(@NotNull LivingEntity entity) {
		if (!(entity instanceof ServerPlayer)) { return false; }
		ServerPlayer player = (ServerPlayer) entity;
		Level level = player.getLevel();
		return player.getAbilities().instabuild &&
				 // Check for dangerous dimensions
				 !(level.dimension().equals(TensuraDimensions.LABYRINTH) || level.dimension().equals(TensuraDimensions.HELL)) &&
				 // Check if player has the Higher Form advancement
				 player.getAdvancements().getOrStartProgress(
				 	player.getServer().getAdvancements().getAdvancement(TensuraAdvancementsHelper.Advancements.HIGHER_FORM)
				 ).isDone();
	}

	public static void resetPlayerHonorConfig(Player player) {
		SkillStorage storage = SkillAPI.getSkillsFrom(player);
		Iterator<ManasSkillInstance> iterator = storage.getLearnedSkills().iterator();
		MinecraftServer server = player.level.getServer();

		while(iterator.hasNext()) {
			Object learnedSkill = iterator.next();
			if (learnedSkill instanceof TensuraSkillInstance skillInstance) {
				ManasSkill manasSkill = skillInstance.getSkill();
				if (shouldResetSkill(player, skillInstance)) {
					if (!MinecraftForge.EVENT_BUS.post(new RemoveSkillEvent(skillInstance, player))) {
						iterator.remove();
						// This is for true unique gamerule
						if (server != null) {
							UniqueSkillSaveData saveData = UniqueSkillSaveData.get(server.overworld());
							if (manasSkill.getRegistryName() != null && saveData.hasSkill(manasSkill.getRegistryName())) {
								saveData.removeSkill(manasSkill.getRegistryName());
							}
						}
					}
				}
			}
		}

		storage.syncAll();
		TensuraPlayerCapability.resetEverything(player);
		TensuraEPCapability.resetEverything(player);
		TensuraSkillCapability.resetEverything(player, Config.resetSpells, Config.resetSpirits);
		TensuraEffectsCapability.resetEverything(player, true, true);

		// TODO: Find a way to copy the dirty stats reset

		// Schematic Reset
		if (Config.resetSchematics) {
			SmithingCapability.getFrom(player).ifPresent(ISmithingCapability::clearSchematics);
			SmithingCapability.sync(player);
		}

		if (player instanceof ServerPlayer serverPlayer) {
			// Race Reset
			if (Config.resetRace) {
				TensuraPlayerCapability.getFrom(player).ifPresent(ITensuraPlayerCapability::clearIntrinsicSkills);

				if (player.getLevel().getGameRules().getBoolean(TensuraGameRules.RIMURU_MODE)) {
					RaceSelectionMenu.reincarnateAsRimuru(player);
				} else {
					serverPlayer.setRespawnPosition(Level.OVERWORLD, (BlockPos) null, 0.0F, false, false);
					player.setInvulnerable(true);
					List<ResourceLocation> races = TensuraPlayerCapability.loadRaces();
					NetworkHooks.openScreen(
						serverPlayer,
						new SimpleMenuProvider(RaceSelectionMenu::new, Component.translatable("tensura.race.selection")),
						(buf) -> {
							buf.writeBoolean(true);
							buf.writeCollection(races, FriendlyByteBuf::writeResourceLocation);
						}
					);
					if (player.getLevel().getGameRules().getBoolean(TensuraGameRules.SKILL_BEFORE_RACE)) {
						RaceSelectionMenu.grantUniqueSkill(player);
					}
				}
			}
			// Advancements reset
			TensuraAdvancementsHelper.revokeAllTensuraAdvancements(serverPlayer);
		}

		RaceSelectionMenu.grantLearningResistance(player);
		TensuraSkillCapability.sync(player);
	}

	public static boolean shouldResetSkill(Player player, ManasSkillInstance skillInstance) {
		ManasSkill manasSkill = skillInstance.getSkill();
		return
				// Spell
				(Config.resetSpells && manasSkill instanceof Magic) ||
				// Battlewill
				(Config.resetBattlewills && manasSkill instanceof Battewill) ||
				// Skill
				(manasSkill instanceof Skill tensuraSkill && (
					// Not unique
					(Config.resetNonUniqueSkills && !isUniqueSkill(tensuraSkill)) ||
					// Unique
					(Config.resetUniqueSkills && isUniqueSkill(tensuraSkill)) ||
					// Intrinsic
					(Config.resetRace && isIntrinsicSkill(player, skillInstance))));
	}

	public static boolean isIntrinsicSkill(Player player, ManasSkillInstance skill) {
		Race race = TensuraPlayerCapability.getRace(player);
		if (skill.isTemporarySkill()) {
			return true;
		} else if (race == null) {
			return false;
		} else if (race.isIntrinsicSkill(skill.getSkill())) {
			return true;
		} else {
			return TensuraPlayerCapability.getIntrinsicList(player).contains(SkillUtils.getSkillId(skill));
		}
	}

	public static boolean isUniqueSkill(Skill skill) {
		return skill.getType() == Skill.SkillType.UNIQUE || skill.getType() == Skill.SkillType.ULTIMATE;
	}
}