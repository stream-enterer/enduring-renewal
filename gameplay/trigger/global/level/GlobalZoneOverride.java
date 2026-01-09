package com.tann.dice.gameplay.trigger.global.level;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.ImageActor;

public class GlobalZoneOverride extends Global {
   final Zone zone;

   public GlobalZoneOverride(Zone zone) {
      this.zone = zone;
   }

   @Override
   public Zone getOverrideZone() {
      return this.zone;
   }

   @Override
   public String describeForSelfBuff() {
      return "All fights take place in " + this.zone;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new ImageActor(this.zone.minimap);
   }

   @Override
   public boolean allLevelsOnly() {
      return true;
   }
}
