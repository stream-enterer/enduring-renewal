package com.tann.dice.gameplay.trigger.personal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class LostOnDeath extends Personal {
   final String itemName;

   public LostOnDeath(String itemName) {
      this.itemName = itemName;
   }

   @Override
   public String describeForSelfBuff() {
      return "Discarded upon death";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      return "permalose";
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new Pixl().image(Images.eq_skullWhite, Colours.grey).gap(2).image(Images.ui_cross, Colours.red).pix();
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      for (Item i : self.getEnt().getItems()) {
         if (i.getName(false).contains(this.itemName)) {
            self.discard(i, "[purple]Discarded " + i.getName(true), false);
         }
      }

      super.onDeath(self, snapshot);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
