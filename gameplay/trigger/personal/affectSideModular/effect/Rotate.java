package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.ImageActor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rotate extends AffectSideEffect {
   Map<Integer, Integer> replacements = new HashMap<>();

   public Rotate() {
      this.replacements.put(0, 2);
      this.replacements.put(3, 0);
      this.replacements.put(1, 3);
      this.replacements.put(2, 1);
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      return new ImageActor(ImageUtils.loadExt("trigger/equip-stuff/itemDiagram/rotate"));
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return "Rotate sides clockwise around " + SpecificSidesType.Middle.description;
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
