package com.tann.dice.gameplay.trigger.global.item;

import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class GlobalStartWithRandomItem extends Global {
   final int tier;
   final int amt;

   public GlobalStartWithRandomItem(int amt, int tier) {
      this.amt = amt;
      this.tier = tier;
   }

   @Override
   public String describeForSelfBuff() {
      return "Gain " + (this.amt == 1 ? "a" : this.amt + "x") + " random tier " + this.tier + " " + Words.plural("item", this.amt);
   }

   @Override
   public void onPick(DungeonContext context) {
      List<Item> toGain = this.getStartingItems(context);
      this.addItems(context, toGain);
      GlobalStartWithItem.onPickItem();
      PhaseManager.get().forceNext(new RandomRevealPhase(toGain));
   }

   private void addItems(DungeonContext context, List<Item> toGain) {
      if (!context.allowInventory()) {
         Sounds.playSound(Sounds.error);
      } else {
         Party p = context.getParty();
         p.addItems(toGain);
      }
   }

   @Override
   public List<Item> getStartingItems(DungeonContext dc) {
      return ItemLib.randomWithExactQuality(this.amt, this.tier, dc);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long result = GlobalStartWithItem.SWI_COLL;
      if (this.amt > 3) {
         result |= Collision.ITEM;
      }

      return result;
   }

   @Override
   public boolean isOnPick() {
      return true;
   }
}
