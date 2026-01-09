package com.tann.dice.gameplay.effect.eff.conditionalBonus;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntState;

public class InvertBonus extends ConditionalBonus {
   public InvertBonus(ConditionalBonusType type) {
      super(type);
   }

   @Override
   public int affectValue(Eff eff, EntState sourceState, EntState targetState, int value) {
      int sv = super.affectValue(eff, sourceState, targetState, value);
      return -sv;
   }
}
