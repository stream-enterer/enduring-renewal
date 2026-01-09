package com.tann.dice.gameplay.trigger.personal.finalLayer;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Pixl;

public class SidesMin extends Personal {
   final int limit;

   public SidesMin(int limit) {
      this.limit = limit;
   }

   @Override
   public String describeForSelfBuff() {
      return "Side pips minimum " + this.limit + ModifierUtils.afterItems();
   }

   @Override
   public void affectSideFinal(EntSideState entSideState, EntState entState) {
      Eff e = entSideState.getCalculatedEffect();
      if (e.hasValue()) {
         e.setValue(Math.max(e.getValue(), this.limit));
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().text("[blue]Min " + this.limit).pix();
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
   public String hyphenTag() {
      return "" + this.limit;
   }
}
