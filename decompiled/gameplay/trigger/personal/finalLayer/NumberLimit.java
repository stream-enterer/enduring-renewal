package com.tann.dice.gameplay.trigger.personal.finalLayer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Pixl;

public class NumberLimit extends Personal {
   public final int limit;

   public NumberLimit(int limit) {
      this.limit = limit;
   }

   @Override
   public Integer limitHp(int maxHp) {
      return Math.min(this.limit, maxHp);
   }

   @Override
   public int affectFinalShields(int shields) {
      return Math.min(this.limit, shields);
   }

   @Override
   public String describeForSelfBuff() {
      return "Shields, health, and pips limited to " + this.limit + ModifierUtils.afterItems();
   }

   @Override
   public void affectSideFinal(EntSideState entSideState, EntState entState) {
      Eff e = entSideState.getCalculatedEffect();
      if (e.hasValue()) {
         e.setValue(Math.min(e.getValue(), this.limit));
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().text("[purple]Max " + this.limit).pix();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.MAX_VALUE;
   }

   @Override
   public float getPriority() {
      return 999999.0F;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public String hyphenTag() {
      return this.limit + "";
   }
}
