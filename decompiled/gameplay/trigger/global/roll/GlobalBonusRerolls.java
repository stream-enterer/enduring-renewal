package com.tann.dice.gameplay.trigger.global.roll;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;

public class GlobalBonusRerolls extends Global {
   final int bonusRolls;

   public GlobalBonusRerolls(int bonusRolls) {
      this.bonusRolls = bonusRolls;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Color col = this.bonusRolls > 0 ? Colours.green : Colours.red;
      return Tann.combineActors(new ImageActor(Images.rerollBonus), TextWriter.withTannFontOverride(" " + TextWriter.getTag(col) + Tann.delta(this.bonusRolls)));
   }

   @Override
   public int affectMaxRerolls(int max, int turn) {
      return max + this.bonusRolls;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonusRolls) + " " + Words.plural("reroll", this.bonusRolls);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.REROLLS;
   }

   @Override
   public boolean isMultiplable() {
      return this.bonusRolls > 0;
   }
}
