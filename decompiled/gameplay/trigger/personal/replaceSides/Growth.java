package com.tann.dice.gameplay.trigger.personal.replaceSides;

import com.tann.dice.gameplay.effect.eff.keyword.Keyword;

public class Growth extends BuffSideIndex {
   public Growth(int index, int delta) {
      super(index, delta);
   }

   @Override
   public Keyword getStronglyAssociatedKeyword() {
      return Keyword.growth;
   }
}
