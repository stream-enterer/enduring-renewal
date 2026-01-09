package com.tann.dice.gameplay.trigger.personal.replaceSides;

import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.FlatBonus;

public class Groooooowth extends AffectSides {
   public Groooooowth() {
      super(new FlatBonus(1));
   }

   @Override
   public Keyword getStronglyAssociatedKeyword() {
      return Keyword.groooooowth;
   }
}
