package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.util.Colours;
import java.util.List;

public class RemoveAllKeywords extends AffectSideEffect {
   @Override
   public String describe() {
      return "Remove all keywords";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      List<Keyword> keywordList = e.getKeywords();
      boolean removed = keywordList.size() > 0;

      for (int i = keywordList.size() - 1; i >= 0; i--) {
         Keyword k = keywordList.get(i);
         e.removeKeyword(k);
         e.getBonusKeywords().remove(k);
      }

      if (removed) {
         e.getBonusKeywords().add(Keyword.removed);
      }
   }

   @Override
   public String getToFrom() {
      return "from";
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            batch.setColor(Colours.z_white);
            batch.draw(Keyword.removed.getImage(), x + 1, y + 1);
         }
      };
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return player ? Collision.PLAYER_KEYWORD : Collision.MONSTER_KEYWORD;
   }
}
