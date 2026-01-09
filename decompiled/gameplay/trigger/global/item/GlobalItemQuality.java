package com.tann.dice.gameplay.trigger.global.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.BorderText;
import com.tann.dice.util.ui.TextWriter;

public class GlobalItemQuality extends Global {
   static final boolean MIN_ZERO = false;
   final int bonusQuality;

   public GlobalItemQuality(int bonus) {
      this.bonusQuality = bonus;
   }

   @Override
   public int affectGlobalLootQuality(int quality) {
      return quality + this.bonusQuality;
   }

   @Override
   public String describeForSelfBuff() {
      return Tann.delta(this.bonusQuality) + " item quality" + "";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Color c = this.bonusQuality > 0 ? Colours.green : Colours.red;
      String text = TextWriter.getTag(c) + Tann.delta(this.bonusQuality);
      return Tann.combineActors(new ImageActor(Images.phaseLootIcon), new BorderText(text, Colours.dark, 2));
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM_REWARD;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public String hyphenTag() {
      return Math.abs(this.bonusQuality) + "";
   }
}
