package com.tamagosci.trebirth.ability.skill;

import com.github.manasmods.tensura.ability.skill.Skill;

public class FakeSkill extends Skill {
	public FakeSkill(SkillType type) {
		super(type);
	}

	public final Skill.SkillType getSkillType() {
		return super.getType();
	}
}