package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;

public class TextureCondition extends AffectSideCondition {
   final EntSide es;
   final String desc;

   public TextureCondition(EntSide es, String desc) {
      this.es = es;
      this.desc = desc;
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      return sideState.getCalculatedTexture() == this.es.getTexture();
   }

   @Override
   public GenericView getActor() {
      return new RandomSidesView(1);
   }

   @Override
   public boolean needsArrow() {
      return true;
   }

   @Override
   public boolean hasSideImage() {
      return true;
   }

   @Override
   public Actor getPrecon() {
      return this.es.makeBasicSideActor(0, false, null);
   }

   @Override
   public boolean isPlural() {
      return true;
   }

   @Override
   public String describe() {
      return this.desc;
   }
}
