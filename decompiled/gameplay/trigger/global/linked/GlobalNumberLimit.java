package com.tann.dice.gameplay.trigger.global.linked;

import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.finalLayer.NumberLimit;

public class GlobalNumberLimit extends GlobalLinked {
   public static final int DEFAULT_MAX_VALUE = 999;
   final int limit;
   final NumberLimit personal;

   public GlobalNumberLimit() {
      this(999);
   }

   public GlobalNumberLimit(int limit) {
      this(new NumberLimit(limit));
   }

   public GlobalNumberLimit(NumberLimit contained) {
      super(contained);
      this.personal = contained;
      this.limit = contained.limit;
   }

   public static int box(int i) {
      return Math.max(-999, Math.min(999, i));
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      return this.personal;
   }

   @Override
   public int affectFinalMana(int mana) {
      return Math.min(mana, this.limit);
   }

   @Override
   public int affectFinalRerolls(int rerolls) {
      return Math.min(rerolls, this.limit);
   }
}
