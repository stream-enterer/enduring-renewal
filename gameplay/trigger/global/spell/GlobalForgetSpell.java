package com.tann.dice.gameplay.trigger.global.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.ui.TextWriter;

public class GlobalForgetSpell extends Global {
   final String spellName;

   public GlobalForgetSpell(String spellName) {
      this.spellName = spellName;
   }

   public GlobalForgetSpell(Spell spell) {
      this(spell.getTitle());
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new TextWriter(this.describeForSelfBuff(), 15);
   }

   @Override
   public String describeForSelfBuff() {
      return "No [blue]" + this.spellName;
   }

   @Override
   public boolean canUseAbility(Ability ability, Snapshot snapshot) {
      return !this.spellName.equalsIgnoreCase(ability.getTitle());
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL;
   }
}
