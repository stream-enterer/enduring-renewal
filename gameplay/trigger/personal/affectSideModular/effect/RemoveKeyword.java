package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.util.Tann;
import java.util.Arrays;
import java.util.List;

public class RemoveKeyword extends AffectSideEffect {
   final List<Keyword> keywordsToRemove;

   public RemoveKeyword(Keyword... keywords) {
      this.keywordsToRemove = Arrays.asList(keywords);
   }

   @Override
   public String getToFrom() {
      return "from";
   }

   @Override
   public String describe() {
      return "Remove " + Tann.commaList(this.keywordsToRemove);
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();

      for (Keyword keyword : this.keywordsToRemove) {
         if (e.hasKeyword(keyword)) {
            e.removeKeyword(keyword);
            if (!e.getBonusKeywords().contains(keyword)) {
               if (!e.getBonusKeywords().contains(Keyword.removed)) {
                  e.getBonusKeywords().add(Keyword.removed);
               }
            } else {
               e.getBonusKeywords().remove(keyword);
            }
         }
      }
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }
}
