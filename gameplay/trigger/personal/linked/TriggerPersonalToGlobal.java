package com.tann.dice.gameplay.trigger.personal.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class TriggerPersonalToGlobal extends Personal {
   final Global global;
   final String imageName;
   private String overrideDesc;

   public TriggerPersonalToGlobal(Global global, String imageName) {
      this.global = global;
      this.imageName = imageName;
   }

   public TriggerPersonalToGlobal(Global global) {
      this(global, null);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return this.global.makePanelActor(big);
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.imageName != null;
   }

   @Override
   public String getImageName() {
      return this.imageName;
   }

   @Override
   public String describeForSelfBuff() {
      return this.overrideDesc != null ? this.overrideDesc : this.global.describeForSelfBuff();
   }

   public TriggerPersonalToGlobal overrideDesc(String desc) {
      this.overrideDesc = desc;
      return this;
   }

   @Override
   public Global getGlobalFromPersonalTrigger() {
      return this.global;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.global.getCollisionBits();
   }

   @Override
   public boolean isMultiplable() {
      return this.global.isMultiplable();
   }
}
