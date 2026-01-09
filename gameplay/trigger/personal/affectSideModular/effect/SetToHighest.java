package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannFont;
import java.util.List;

public class SetToHighest extends AffectSideEffect {
   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String sideDescription = "";
      if (conditions.isEmpty()) {
         sideDescription = "all sides";
      }

      for (AffectSideCondition asc : conditions) {
         sideDescription = sideDescription + asc.describe();
      }

      return "Set the pips of " + sideDescription + " to the maximum of my other side's pips";
   }

   @Override
   public String describe() {
      return null;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            batch.setColor(Colours.light);
            TannFont.font.drawString(batch, "max", x + 8, y + 8, 1);
         }
      };
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int max = 0;

      for (int i = 0; i < 6; i++) {
         EntSideState ess = new EntSideState(owner, owner.getEnt().getSides()[i], sourceIndex);
         max = Math.max(ess.getCalculatedEffect().getValue(), max);
      }

      sideState.getCalculatedEffect().setValue(max);
   }
}
