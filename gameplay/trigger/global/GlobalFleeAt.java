package com.tann.dice.gameplay.trigger.global;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class GlobalFleeAt extends Global {
   private static final int DEFAULT_FLEE = 10;
   final int multRequired;

   public GlobalFleeAt() {
      this(10);
   }

   public GlobalFleeAt(int multRequired) {
      this.multRequired = multRequired;
   }

   @Override
   public String describeForSelfBuff() {
      return "Monsters flee if you have " + describeMult(this.multRequired) + " as much hp as them instead of " + describeMult(10);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(Images.eq_fleeFive);
   }

   private static String describeMult(int mult) {
      return mult + "x";
   }

   @Override
   public boolean flee(Snapshot snapshot) {
      float ratio = snapshot.getHeroHpDividedByMonster();
      return ratio >= this.multRequired;
   }
}
