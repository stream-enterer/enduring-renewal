package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.CopySide;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.screens.dungeon.panels.Explanel.affectSides.SwapSideView;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import java.util.List;

public class SwapSides extends AffectSideEffect {
   private static final int GAP = 4;
   final SpecificSidesType a;
   final SpecificSidesType b;

   public SwapSides(SpecificSidesType a, SpecificSidesType b) {
      this.a = a;
      this.b = b;
      if (a.sideIndices.length != b.sideIndices.length) {
         throw new RuntimeException("Invalid swap sides trigger: " + a + "/" + b);
      } else {
         for (int ai : a.sideIndices) {
            for (int bi : b.sideIndices) {
               if (ai == bi) {
                  throw new RuntimeException("Overlapping swap sides: " + a + "/" + b);
               }
            }
         }
      }
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      Color arrowInner = Colours.dark;
      Color arrowOuter = Colours.text;
      Actor special = CopySide.makeCombined(this.a, this.b, Images.arrowSwap, arrowOuter, arrowInner);
      return (Actor)(special != null
         ? special
         : new Pixl().actor(new SwapSideView(this.a, true)).gap(4).image(Images.arrowSwap, arrowOuter).gap(4).actor(new SwapSideView(this.b, true)).pix());
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return "Swap " + this.a.description + " with " + this.b.description;
   }

   @Override
   public String describe() {
      return null;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int aIndex = this.a.validIndex(sideState, owner);
      int bIndex = this.b.validIndex(sideState, owner);
      if (aIndex != -1 || bIndex != -1) {
         if (aIndex != -1 && bIndex != -1) {
            TannLog.log("Overlapping swap sides: " + this.a + "/" + this.b + ", skipping");
         } else {
            int indexToTake = aIndex == -1 ? bIndex : aIndex;
            SpecificSidesType takeFrom = aIndex == -1 ? this.a : this.b;
            int newIndex = takeFrom.sideIndices[indexToTake];
            ReplaceWith.replaceSide(sideState, new EntSideState(owner, owner.getEnt().getSides()[newIndex], sourceIndex));
         }
      }
   }

   @Override
   public boolean isIndexed() {
      return true;
   }
}
