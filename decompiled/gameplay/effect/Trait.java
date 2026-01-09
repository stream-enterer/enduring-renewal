package com.tann.dice.gameplay.effect;

import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.position.BackRow;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;

public class Trait {
   public final Personal personal;
   public final boolean visible;
   public final CalcStats calcStats;
   public static final Trait BACK_ROW = new Trait(new BackRow(true));

   public Trait(Personal personal, CalcStats calcStats, boolean visible) {
      this.personal = personal;
      this.calcStats = calcStats;
      personal.setCalcStats(calcStats);
      personal.setTrait(this);
      this.visible = visible;
   }

   public Trait(Personal personal, CalcStats calcStats) {
      this(personal, calcStats, true);
   }

   public Trait(Personal personal, boolean visible) {
      this(personal, null, visible);
   }

   public Trait(Personal personal) {
      this(personal, null, true);
   }

   public Trait copy() {
      return new Trait(this.personal, this.calcStats, this.visible);
   }
}
