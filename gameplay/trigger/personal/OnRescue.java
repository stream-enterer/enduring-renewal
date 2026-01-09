package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.eff.PersonalEffContainer;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class OnRescue extends PersonalEffContainer {
   Eff eff;
   boolean visible = true;

   public OnRescue(Eff eff) {
      super(eff);
      this.eff = eff;
   }

   @Override
   public boolean onRescue(EntState self) {
      self.getSnapshot().target(null, new SimpleTargetable(self.getEnt(), this.eff), false);
      return true;
   }

   @Override
   public String describeForSelfBuff() {
      String result = this.eff.toString().toLowerCase();
      String bad = " to myself";
      if (result.endsWith(bad)) {
         result = result.substring(0, result.length() - bad.length());
      }

      return result + com.tann.dice.Main.t(" whenever I save a hero");
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().image(Images.eq_halo, Colours.light).gap(1).text(":").gap(1).actor(this.eff.getBasicImage()).pix();
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.visible;
   }

   public OnRescue hide() {
      this.visible = false;
      return this;
   }

   @Override
   public String getImageName() {
      return this.eff.getType() == EffType.Damage ? "onSaveRed" : "onSave";
   }
}
