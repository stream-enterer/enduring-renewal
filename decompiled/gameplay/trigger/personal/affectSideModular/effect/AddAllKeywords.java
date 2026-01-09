package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ui.TextWriter;
import java.util.List;

public class AddAllKeywords extends AffectSideEffect {
   final Color col;

   public AddAllKeywords(Color col) {
      this.col = col;
   }

   @Override
   public String describe() {
      return "Add all " + TextWriter.getTag(this.col) + TextWriter.getNameForColour(this.col) + "[cu] keywords";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      for (Keyword colourKeyword : KUtils.getColourKeywords(this.col)) {
         if (!colourKeyword.abilityOnly()) {
            sideState.getCalculatedEffect().addKeyword(colourKeyword);
         }
      }
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw(Images.plusBig, this.col);
   }
}
