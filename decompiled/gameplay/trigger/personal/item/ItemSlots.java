package com.tann.dice.gameplay.trigger.personal.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.BorderText;
import com.tann.dice.util.ui.TextWriter;

public class ItemSlots extends Personal {
   public static final int MAX_SLOTS = 4;
   final int delta;

   public ItemSlots(int delta) {
      this.delta = delta;
   }

   @Override
   public String describeForSelfBuff() {
      String result = Tann.delta(this.delta) + " item " + Words.plural("slot", this.delta);
      if (this.delta > 0) {
         result = result + " (max 4)";
      }

      return result;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      ImageActor ia = new ImageActor(Images.equipSlot);
      Group parent = Tann.makeGroup(ia);
      Color col = this.delta > 0 ? Colours.green : Colours.red;
      col = Colours.dark;
      String colTag = TextWriter.getTag(col);
      Actor bonus = new BorderText(colTag + Tann.delta(this.delta), Colours.text, 1);
      parent.addActor(bonus);
      Tann.center(bonus);
      return parent;
   }

   @Override
   public int affectItemSlots(int amt) {
      return Math.min(4, amt + this.delta);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.ITEM;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean allLevelsOnly() {
      return true;
   }
}
