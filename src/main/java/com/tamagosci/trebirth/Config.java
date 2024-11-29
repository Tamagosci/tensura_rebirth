package com.tamagosci.trebirth;

import com.github.manasmods.manascore.api.skills.ManasSkill;
import com.github.manasmods.manascore.api.skills.SkillAPI;
import com.github.manasmods.tensura.ability.TensuraSkill;
import com.github.manasmods.tensura.registry.TensuraRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = TRebirth.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final List<String> DEFAULT_ABILITY_LIST = List.of("tensura:absolute_severance", "tensura:berserk", "tensura:berserker", "tensura:bewilder", "tensura:chef", "tensura:chosen_one", "tensura:commander", "tensura:cook", "tensura:creator", "tensura:degenerate", "tensura:divine_berserker", "tensura:engorger", "tensura:envy", "tensura:falsifier", "tensura:fighter", "tensura:fusionist", "tensura:gourmand", "tensura:gourmet", "tensura:great_sage", "tensura:greed", "tensura:guardian", "tensura:healer", "tensura:infinity_prison", "tensura:lust", "tensura:martial_master", "tensura:mathematician", "tensura:merciless", "tensura:murderer", "tensura:musician", "tensura:observer", "tensura:oppressor", "tensura:predator", "tensura:pride", "tensura:reflector", "tensura:researcher", "tensura:royal_beast", "tensura:reaper", "tensura:reverser", "tensura:seer", "tensura:severer", "tensura:shadow_striker", "tensura:sloth", "tensura:sniper", "tensura:spearhead", "tensura:suppressor", "tensura:survivor", "tensura:traveler", "tensura:thrower", "tensura:tuner", "tensura:unyielding", "tensura:usurper", "tensura:villain", "tensura:wrath");

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // Skills
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> RANDOM_ABILITY_LIST = BUILDER
       .comment("The list from which random abilities will be selected")
       .defineListAllowEmpty(Collections.singletonList("randomAbilityList"), () -> DEFAULT_ABILITY_LIST, Config::validateAbilityName);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CHOICE_ABILITY_LIST = BUILDER
       .comment("The list from which you will be able to choose abilities")
       .defineListAllowEmpty(Collections.singletonList("choiceAbilityList"), () -> DEFAULT_ABILITY_LIST, Config::validateAbilityName);
    private static final ForgeConfigSpec.IntValue MAX_RANDOM_ABILITIES = BUILDER
       .comment("The maximum number of random abilities you can unlock by rebirthing multiple times")
       .defineInRange("maxRandomAbilities", 1, 0, 99);
    private static final ForgeConfigSpec.IntValue MAX_CHOICE_ABILITIES = BUILDER
       .comment("The maximum number of choice abilities you can unlock by rebirthing multiple times")
       .defineInRange("maxChoiceAbilities", 1, 0, 1);
    private static final ForgeConfigSpec.BooleanValue UNLOCK_CHOICE_BEFORE_RANDOM = BUILDER
       .comment("Whether skill choice will be unlocked before or after random skills")
       .define("unlockChoiceBeforeRandom", false);

    // Reset
    private static final ForgeConfigSpec.BooleanValue RESET_RACE = BUILDER
       .comment("Whether to reset race on rebirth")
       .define("resetRace", true);
    private static final ForgeConfigSpec.BooleanValue RESET_NON_UNIQUE_SKILLS = BUILDER
       .comment("Whether to reset non unique skills on rebirth")
       .define("resetNonUniqueSkills", true);
    private static final ForgeConfigSpec.BooleanValue RESET_SPELLS = BUILDER
       .comment("Whether to reset spells on rebirth")
       .define("resetSpells", true);
    private static final ForgeConfigSpec.BooleanValue RESET_BATTLEWILLS = BUILDER
       .comment("Whether to reset battlewills on rebirth")
       .define("resetBattlewills", true);
    private static final ForgeConfigSpec.BooleanValue RESET_SPIRITS = BUILDER
       .comment("Whether to reset spirits on rebirth")
       .define("resetSpirits", true);
    private static final ForgeConfigSpec.BooleanValue RESET_SCHEMATICS = BUILDER
       .comment("Whether to reset schematics on rebirth")
       .define("resetSchematics", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // Skills
    public static Set<ManasSkill> randomAbilityList;
    public static Set<ManasSkill> choiceAbilityList;
    public static int maxRandomAbilities;
    public static int maxChoiceAbilities;
    public static boolean unlockChoiceBeforeRandom;

    // Reset
    public static boolean resetRace;
    public static boolean resetNonUniqueSkills;
    public static boolean resetSpells;
    public static boolean resetBattlewills;
    public static boolean resetSpirits;
    public static boolean resetSchematics;

    private static boolean validateAbilityName(final Object obj) {
        return obj instanceof final String abilityName && SkillAPI.getSkillRegistry().containsKey(new ResourceLocation(abilityName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        final IForgeRegistry<ManasSkill> skillRegistry = SkillAPI.getSkillRegistry();
        // Skills
        randomAbilityList = RANDOM_ABILITY_LIST.get().stream()
                                               .map(abilityName -> skillRegistry.getValue(new ResourceLocation(abilityName)))
                                               .collect(Collectors.toSet());
        choiceAbilityList = CHOICE_ABILITY_LIST.get().stream()
                                               .map(abilityName -> skillRegistry.getValue(new ResourceLocation(abilityName)))
                                               .collect(Collectors.toSet());;
        maxRandomAbilities = MAX_RANDOM_ABILITIES.get();
        maxChoiceAbilities = MAX_CHOICE_ABILITIES.get();
        unlockChoiceBeforeRandom = UNLOCK_CHOICE_BEFORE_RANDOM.get();

        // Reset
        resetRace = RESET_RACE.get();
        resetNonUniqueSkills = RESET_NON_UNIQUE_SKILLS.get();
        resetSpells = RESET_SPELLS.get();
        resetBattlewills = RESET_BATTLEWILLS.get();
        resetSpirits = RESET_SPIRITS.get();
        resetSchematics = RESET_SCHEMATICS.get();
    }
}