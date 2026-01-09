package com.tann.dice.gameplay.content.gen.pipe.item.dataFromHero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.personal.onHit.OnHitFromPipe;
import com.tann.dice.gameplay.trigger.personal.specialPips.TriggerHP;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLocType;

public class PipeItemTriggerHP extends PipeRegexNamed<Item> {
   public static final PRNPart PREF = new PRNPref("triggerhpdata");

   public PipeItemTriggerHP() {
      super(PREF, TRIGGERHP);
   }

   protected Item internalMake(String[] groups) {
      String heroName = groups[0];
      if (heroName == null) {
         return null;
      } else {
         HeroType ht = HeroTypeLib.byName(heroName);
         return ht.isMissingno() ? null : this.makeInternal(ht);
      }
   }

   private Item makeInternal(HeroType ht) {
      Eff e = OnHitFromPipe.getLeftmostBlankDerived(ht);
      if (e.needsTarget()) {
         return null;
      } else {
         String name = PREF + ht.getName();
         return new ItBill(name).prs(new TriggerHP(e, ht.heroCol.col, this.pipLocFromIndex(ht.hp))).bItem();
      }
   }

   private PipLoc pipLocFromIndex(int i) {
      switch (i) {
         case 0:
         case 1:
            return PipLoc.all();
         case 2:
            return PipLoc.offsetEvery(2, 1);
         case 3:
            return PipLoc.offsetEvery(3, 2);
         case 4:
            return PipLoc.offsetEvery(4, 3);
         case 5:
            return PipLoc.offsetEvery(5, 4);
         case 6:
            return PipLoc.offsetEvery(10, 9);
         case 7:
            return PipLoc.offsetEvery(10, 4);
         case 8:
            return PipLoc.offsetEvery(2, 0);
         case 9:
            return PipLoc.offsetEvery(3, 0);
         case 10:
            return new PipLoc(PipLocType.LeftmostN, 1);
         case 11:
            return new PipLoc(PipLocType.LeftmostN, 2);
         case 12:
            return new PipLoc(PipLocType.LeftmostN, 3);
         case 13:
            return new PipLoc(PipLocType.LeftmostN, 5);
         case 14:
            return new PipLoc(PipLocType.RightmostN, 1);
         case 15:
            return new PipLoc(PipLocType.RightmostN, 2);
         case 16:
            return new PipLoc(PipLocType.RightmostN, 3);
         case 17:
            return new PipLoc(PipLocType.RightmostN, 5);
         case 18:
            return new PipLoc(PipLocType.EveryFraction, 2);
         case 19:
            return new PipLoc(PipLocType.EveryFraction, 3);
         case 20:
            return new PipLoc(PipLocType.EveryFraction, 4);
         case 21:
            return new PipLoc(PipLocType.EveryFraction, 5);
         default:
            if (i < 0) {
               return PipLoc.all();
            } else {
               int iv = i - 21;
               return new PipLoc(PipLocType.Specific, iv);
            }
      }
   }

   public Item example() {
      return this.makeInternal(HeroTypeUtils.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
