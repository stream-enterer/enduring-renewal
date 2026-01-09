package com.tann.dice.gameplay.trigger.global.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.BorderText;
import com.tann.dice.util.ui.TextWriter;

public class GlobalLevelupQuantity extends Global {
   final int bonusQuantity;

   public GlobalLevelupQuantity(int bonus) {
      this.bonusQuantity = bonus;
   }

   @Override
   public int affectLevelupChoices(int amt) {
      return amt + this.bonusQuantity;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonusQuantity) + " levelup " + Words.plural("choice", this.bonusQuantity);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Color c = this.bonusQuantity > 0 ? Colours.green : Colours.red;
      String text = TextWriter.getTag(c) + "#" + Tann.delta(this.bonusQuantity);
      return Tann.combineActors(new ImageActor(Images.phaseLevelupIcon), new BorderText(text, Colours.dark, 2));
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.LEVELUP_REWARD;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isMultiplable() {
      return this.bonusQuantity > 0;
   }

   @Override
   public String hyphenTag() {
      return Math.abs(this.bonusQuantity) + "";
   }
}
