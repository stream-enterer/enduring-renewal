package com.tann.dice.gameplay.trigger.global.roll;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalNotMoreRolls extends Global {
   final int maxRoll;

   public GlobalNotMoreRolls(int maxRoll) {
      this.maxRoll = maxRoll;
   }

   @Override
   public String describeForSelfBuff() {
      return this.maxRoll == 1 ? "You can only roll 1 dice at a time" : "Cannot roll more than " + this.maxRoll + " dice at a time";
   }

   @Override
   public String getRollError(List<Ent> entitiesToRoll, int size) {
      return entitiesToRoll.size() > this.maxRoll ? this.describeForSelfBuff() : super.getRollError(entitiesToRoll, size);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().image(Images.reroll).row(2).text("[red]" + this.maxRoll + "/5").pix();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.REROLLS | Collision.NUM_HEROES | Collision.CURSED_MODE;
   }

   @Override
   public String hyphenTag() {
      return this.maxRoll + "";
   }
}
