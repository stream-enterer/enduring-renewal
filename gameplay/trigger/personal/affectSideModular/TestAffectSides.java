package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;

public class TestAffectSides extends AffectSides {
   public TestAffectSides(AffectSideEffect... affectSides) {
      super(affectSides);
   }

   @Override
   public float getPriority() {
      return -999.0F;
   }
}
