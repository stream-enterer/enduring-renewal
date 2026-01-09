package com.tann.dice.gameplay.trigger.global.pool.item;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import java.util.List;

public class GlobalExtraItemPool extends Global {
   private final List<Item> extras;

   public GlobalExtraItemPool(List<Item> extras) {
      this.extras = extras;
   }

   @Override
   public void affectItemOptions(List<Item> result, int itemTier) {
      for (Item option : this.extras) {
         if (option.getTier() == itemTier) {
            result.add(option);
         }
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(3, 3).border(Colours.yellow);
      p.text("[grey]Item Pool +").row();
      if (this.extras.size() > 60) {
         big = false;
      }

      if (big) {
         for (Item option : this.extras) {
            p.actor(option.makeUnlockActor(false), 100.0F);
         }
      } else {
         p.text(this.extras.size());
      }

      return p.pix();
   }

   @Override
   public String describeForSelfBuff() {
      return "Add " + this.extras.size() + " items to the pool";
   }
}
