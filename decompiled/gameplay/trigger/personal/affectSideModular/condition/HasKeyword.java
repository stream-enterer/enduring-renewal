package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class HasKeyword extends AffectSideCondition {
   public final Keyword[] keywords;

   public HasKeyword(Keyword... keywords) {
      this.keywords = keywords;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      for (Keyword k : this.keywords) {
         if (sideState.getCalculatedEffect().hasKeyword(k)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String describe() {
      List<String> list = new ArrayList<>();

      for (Keyword k : this.keywords) {
         list.add(k.getColourTaggedString());
      }

      return Tann.commaList(list);
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public Actor getPrecon() {
      RandomSidesView result = new RandomSidesView(1);
      result.addDraw(new AddKeyword(this.keywords).getAddDraw(false, new ArrayList<>()));
      return result;
   }

   @Override
   public boolean needsArrow() {
      return true;
   }
}
