package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import java.util.ArrayList;

public class NoKeywordsCondition extends AffectSideCondition {
   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedEffect().getKeywords().size() == 0;
   }

   @Override
   public boolean isAfterSides() {
      return true;
   }

   @Override
   public Actor getPrecon() {
      RandomSidesView result = new RandomSidesView(1);
      result.addDraw(new AddKeyword(Keyword.removed).getAddDraw(false, new ArrayList<>()));
      return result;
   }

   @Override
   public String describe() {
      return "with no keywords";
   }
}
