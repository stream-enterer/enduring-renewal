package com.tann.dice.gameplay.trigger.personal.spell.learn;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.util.Colours;
import java.util.List;

public abstract class LearnAbility extends Personal {
   final Ability ability;

   public LearnAbility(Ability ability) {
      this.ability = ability;
      ability.setCol(Colours.grey);
   }

   public static Personal make(Ability ab) {
      if (ab instanceof Spell) {
         return new LearnSpell((Spell)ab);
      } else if (ab instanceof Tactic) {
         return new LearnTactic((Tactic)ab);
      } else {
         throw new RuntimeException("uhoh: " + ab);
      }
   }

   @Override
   public final Ability getAbility() {
      return this.ability;
   }

   @Override
   public final Actor makePanelActorI(boolean big) {
      return new Explanel(this.ability, false);
   }

   @Override
   public String describeForSelfBuff() {
      return "Learn the ability: [orange]" + this.ability.getTitle() + "[cu]";
   }

   @Override
   public final List<Keyword> getReferencedKeywords() {
      return this.ability.getBaseEffect().getReferencedKeywords();
   }

   @Override
   public abstract float affectStrengthCalc(float var1, float var2, EntType var3);

   @Override
   public final float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }

   @Override
   public final boolean showInDiePanel() {
      return false;
   }

   @Override
   public final boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public final Eff getSingleEffOrNull() {
      return this.ability.getBaseEffect();
   }
}
