package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import java.util.List;

public class BecomeIdentical extends AffectSideEffect {
   @Override
   public String describe() {
      return "change but remain the same...";
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return "See the hidden reality behind some sides";
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw(Images.magnifyingGlass, Colours.blue);
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      if (e.getBonusKeywords().size() != 0) {
         int myHash = e.hashEff();
         List<EntSide> sides = EntSidesLib.getAllSidesWithValue();

         for (int i = 0; i < sides.size(); i++) {
            EntSide es = sides.get(i);
            es = es.withValue(e.getValue());
            Eff base = es.getBaseEffect();
            if (base.hashEff() == myHash) {
               ReplaceWith.replaceSide(sideState, es);
               return;
            }
         }
      }
   }
}
