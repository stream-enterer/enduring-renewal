package com.tann.dice.gameplay.trigger.personal.replaceSides;

import com.tann.dice.gameplay.effect.eff.keyword.Keyword;

public class Decay extends BuffSideIndex {
   public Decay(int index, int delta) {
      super(index, delta);
   }

   @Override
   public Keyword getStronglyAssociatedKeyword() {
      return Keyword.decay;
   }
}
