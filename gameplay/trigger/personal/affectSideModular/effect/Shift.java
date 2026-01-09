package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.ImageActor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shift extends AffectSideEffect {
   final boolean horizontal;
   Map<Integer, Integer> replacements = new HashMap<>();

   public Shift() {
      this(true);
   }

   public Shift(boolean horizontal) {
      this.horizontal = horizontal;
      if (horizontal) {
         this.replacements.put(5, 3);
         this.replacements.put(2, 5);
         this.replacements.put(4, 2);
         this.replacements.put(3, 4);
      } else {
         this.replacements.put(0, 1);
         this.replacements.put(1, 4);
         this.replacements.put(4, 0);
      }
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      return new ImageActor(ImageUtils.loadExt("trigger/equip-stuff/itemDiagram/shift-" + this.horizontal));
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return this.horizontal ? "Shift my row across by 1" : "Shift my column down by 1";
   }

   @Override
   public String describe() {
      return null;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      int sideIndex = sideState.getIndex();
      Integer newIndex = this.replacements.get(sideIndex);
      if (newIndex != null) {
         ReplaceWith.replaceSide(sideState, new EntSideState(owner, owner.getEnt().getSides()[newIndex], sourceIndex));
      }
   }

   @Override
   public boolean isIndexed() {
      return true;
   }
}
