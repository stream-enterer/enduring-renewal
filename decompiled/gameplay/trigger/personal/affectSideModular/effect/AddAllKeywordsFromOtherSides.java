package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HasKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddAllKeywordsFromOtherSides extends AffectSideEffect {
   final SpecificSidesType sst;

   public AddAllKeywordsFromOtherSides() {
      this(SpecificSidesType.All);
   }

   public AddAllKeywordsFromOtherSides(SpecificSidesType sst) {
      this.sst = sst;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      switch (this.sst) {
         case All:
            List<Keyword> keywords = new ArrayList<>();

            for (AffectSideCondition condition : conditions) {
               if (condition instanceof HasKeyword) {
                  keywords.addAll(Arrays.asList(((HasKeyword)condition).keywords));
               }
            }

            return new EffectDraw() {
               @Override
               public void draw(Batch batch, int x, int y) {
                  for (int index = 0; index < 4; index++) {
                     Keyword keyword = Keyword.charged;
                     EntSide.drawBonusKeyword(batch, x, y, keyword, index);
                  }
               }
            };
         case Left:
            return new EffectDraw(Images.arrowLeft, Colours.orange);
         default:
            return null;
      }
   }

   @Override
   public String describe() {
      switch (this.sst) {
         case All:
            return "Add all keywords present on this dice";
         default:
            return "Add all keywords from " + this.sst.description;
      }
   }

   @Override
   protected String getGeneralDescription(AffectSideEffect[] affectSideEffectList) {
      return this.describe() + " to";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      List<Keyword> keywords = new ArrayList<>();
      int[] sideIndices = this.sst.sideIndices;
      int i = 0;

      for (int sideIndicesLength = sideIndices.length; i < sideIndicesLength; i++) {
         int sideIndex = sideIndices[i];
         keywords.addAll(new EntSideState(owner, owner.getEnt().getSides()[sideIndex], sourceIndex).getCalculatedEffect().getKeywords());
      }

      sideState.getCalculatedEffect().addKeywords(keywords);
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;
      return bit | (player ? Collision.PLAYER_KEYWORD : Collision.MONSTER_KEYWORD);
   }
}
