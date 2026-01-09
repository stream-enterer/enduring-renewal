package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import java.util.List;

public class ChangeToAboveType extends CopyBaseFromHeroAbove {
   public ChangeToAboveType(boolean above) {
      super(above);
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw(this.above ? Images.ASEAbove : Images.ASEBelow, Colours.blue);
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return super.getOverrideDescription(conditions, effects) + ", retaining my side's pips and keywords";
   }

   @Override
   public String describe() {
      return "change to above, retain";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int sideIndex = sideState.getIndex();
      EntState aboveHero = owner.getDeltaPosAllowDeath(this.above ? -1 : 1);
      EntSide to = aboveHero.getEnt().getSides()[sideIndex];
      List<Keyword> keywords = sideState.getCalculatedEffect().getKeywords();
      int newVal = sideState.getCalculatedEffect().getValue();
      if (to.getBaseEffect().hasValue() && !sideState.getCalculatedEffect().hasValue()) {
         newVal = 0;
      }

      EntSide newSide = to.withValue(newVal);
      ReplaceWith.replaceSide(sideState, newSide);
      sideState.getCalculatedEffect().addKeywords(keywords);
   }
}
