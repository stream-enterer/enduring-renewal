package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class CopyBaseFromHeroAbove extends AffectSideEffect {
   final boolean above;

   public CopyBaseFromHeroAbove(boolean above) {
      this.above = above;
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String result = "Replace ";
      boolean plural = true;

      for (AffectSideCondition asc : conditions) {
         result = result + asc.describe() + " ";
         if (!asc.isPlural()) {
            plural = false;
         }
      }

      if (conditions.size() == 0) {
         result = result + "all sides ";
      }

      return result + "with the hero " + (this.above ? "above" : "below") + "'s base " + Words.plural("side", plural ? 2 : 1);
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw(this.above ? Images.ASEAbove : Images.ASEBelow, Colours.light);
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public String describe() {
      return "??err??";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int sideIndex = sideState.getIndex();
      if (sideIndex != -1) {
         EntState from = owner.getDeltaPosAllowDeath(this.above ? -1 : 1);
         ReplaceWith.replaceSide(sideState, from.getEnt().getSides()[sideIndex]);
      }
   }
}
