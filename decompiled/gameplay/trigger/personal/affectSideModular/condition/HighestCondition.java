package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.util.TannFont;

public class HighestCondition extends AffectSideCondition {
   private final boolean highest;

   public HighestCondition() {
      this(true);
   }

   public HighestCondition(boolean highest) {
      this.highest = highest;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int origin) {
      Eff mEff = sideState.getCalculatedEffect();
      if (mEff.getType() == EffType.Blank) {
         return false;
      } else if (!mEff.hasValue()) {
         return false;
      } else {
         int mine = mEff.getValue();
         if (mine == -999) {
            mine = 0;
         }

         for (int i = 0; i < 6; i++) {
            Eff teff = new EntSideState(owner, owner.getEnt().getSides()[i], origin).getCalculatedEffect();
            if (teff.hasValue() && teff.getType() != EffType.Blank) {
               int theirs = teff.getValue();
               if (theirs != -999 && mine != theirs && mine < theirs == this.highest) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   @Override
   public EffectDraw getAddDraw() {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            int mid = 8;
            TannFont.font.drawString(batch, HighestCondition.this.highest ? ">" : "<", x + 8 - 1, y + 8 + 1, 1);
         }
      };
   }

   @Override
   public boolean hasSideImage() {
      return true;
   }

   @Override
   public String describe() {
      return this.highest ? "highest-pip" : "lowest-pip";
   }
}
