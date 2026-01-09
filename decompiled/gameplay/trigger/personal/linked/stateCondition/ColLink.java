package com.tann.dice.gameplay.trigger.personal.linked.stateCondition;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ColRestriction;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class ColLink extends PersonalConditionLink {
   final HeroCol col;

   public ColLink(HeroCol col, Personal linked) {
      super(new ColRestriction(col), linked);
      this.col = col;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return super.getCollisionBits(player) | this.col.getCollisionBit();
   }
}
