package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class AffectByIndex extends AffectSideEffect {
   final AffectSideEffect[] affectSideEffectList;

   public AffectByIndex(AffectSideEffect... affectSideEffectList) {
      this.affectSideEffectList = affectSideEffectList;
   }

   @Override
   public String describe() {
      return null;
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String end = " these sides";

      for (AffectSideCondition asc : conditions) {
         if (asc instanceof SpecificSidesCondition) {
            end = " " + asc.describe();
            break;
         }
      }

      String affect = this.affectSideEffectList[0].getGeneralDescription(this.affectSideEffectList);
      return affect + end;
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public EffectDraw getAddDraw(final boolean hasSideImage, final List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            AffectByIndex.this.affectSideEffectList[index].getAddDraw(hasSideImage, conditions).draw(batch, x, y, index);
         }
      };
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      this.affectSideEffectList[index].affect(sideState, owner, index, sourceTrigger, sourceIndex);
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      ArrayList<Keyword> list = new ArrayList<>();

      for (AffectSideEffect e : this.affectSideEffectList) {
         if (e.getReferencedKeywords() != null) {
            list.addAll(e.getReferencedKeywords());
         }
      }

      Tann.clearDupes(list);
      return list;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = 0L;

      for (AffectSideEffect affectSideEffect : this.affectSideEffectList) {
         result |= affectSideEffect.getCollisionBits(player);
      }

      return result;
   }

   @Override
   public boolean isIndexed() {
      return true;
   }
}
