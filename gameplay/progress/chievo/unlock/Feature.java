package com.tann.dice.gameplay.progress.chievo.unlock;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.util.Pixl;

public class Feature implements Unlockable {
   public static Feature EVENTS_SIMPLE = new Feature("simple events");
   public static Feature PARTY_LAYOUT_CHOICE = new Feature("party layout choice");
   public static Feature EVENTS_COMPLEX = new Feature("complex events");
   public static Feature EVENTS_WEIRD = new Feature("weird events");
   public static Feature NORMAL_TWEAKS = new Feature("tweak offer for normal");
   public static Feature ALTERNATE_RANDOM_ITEMS = new Feature("alternate random items");
   public static Feature WEIRD_RANDOM_ITEMS = new Feature("weird random items");
   public static Feature TACTICS = new Feature("tactics");
   final String name;

   public Feature(String name) {
      this.name = name;
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return new Pixl(0, 3).text(this.name).pix();
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return "[blue]F";
   }

   @Override
   public String toString() {
      return this.name;
   }
}
