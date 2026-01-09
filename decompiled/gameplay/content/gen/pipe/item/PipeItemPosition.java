package com.tann.dice.gameplay.content.gen.pipe.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.util.Tann;
import com.tann.dice.util.bsRandom.Checker;
import com.tann.dice.util.bsRandom.RandomCheck;
import com.tann.dice.util.bsRandom.Supplier;

public class PipeItemPosition extends PipeRegexNamed<Item> {
   public PipeItemPosition() {
      super(SIDE_POSITION, prnS("\\."), ITEM);
   }

   protected Item internalMake(String[] groups) {
      String posName = groups[0];
      String itemName = groups[1];
      SpecificSidesType type = SpecificSidesType.byName(posName);
      Item i = ItemLib.byName(itemName);
      return type != null && this.itemValid(i) ? make(type, i) : null;
   }

   private static Item make(SpecificSidesType sst, Item other) {
      TextureRegion tr = other.getImage();
      ItBill result = new ItBill(-69, sst.getShortName() + "." + other.getName(false), tr);

      for (Personal p : other.getPersonals()) {
         if (p instanceof AffectSides) {
            AffectSides as = (AffectSides)p;
            if (hasSSC(as)) {
               AffectSideEffect[] effs = as.getEffects().toArray(new AffectSideEffect[0]);
               if (effs.length != 1) {
                  return null;
               }

               AffectSideEffect ase = effs[0];
               if (ase.isIndexed()) {
                  Personal toAdd = transformIndexed(ase, as, sst);
                  if (toAdd == null) {
                     return null;
                  }

                  result.prs(toAdd);
               } else {
                  result.prs(new AffectSides(sst, effs));
               }
            }
         } else {
            result.prs(p);
         }
      }

      return result.bItem();
   }

   private static AffectSides transformIndexed(AffectSideEffect e, AffectSides as, SpecificSidesType sst) {
      if (!(e instanceof ReplaceWith)) {
         return null;
      } else {
         ReplaceWith rw = (ReplaceWith)e;
         EntSide[] rs = rw.getReplaceSides();
         SpecificSidesType prevSST = SpecificSidesType.All;

         for (AffectSideCondition condition : as.getConditions()) {
            if (condition instanceof SpecificSidesCondition) {
               prevSST = ((SpecificSidesCondition)condition).specificSidesType;
            }
         }

         int[] sideIndices = sst.sideIndices;
         EntSide[] reps = new EntSide[sideIndices.length];

         for (int i = 0; i < sideIndices.length; i++) {
            int sideIndex = sideIndices[i];
            int[] indices = prevSST.sideIndices;

            for (int j = 0; j < indices.length; j++) {
               int prevIndex = indices[j];
               if (sideIndex == prevIndex) {
                  reps[i] = rs[j];
               }
            }
         }

         for (EntSide rep : reps) {
            if (rep == null) {
               return null;
            }
         }

         return new AffectSides(sst, new ReplaceWith(reps));
      }
   }

   private boolean itemValid(Item item) {
      if (item.isMissingno()) {
         return false;
      } else {
         boolean has = false;

         for (int i = 0; i < item.getPersonals().size(); i++) {
            Personal p = item.getPersonals().get(i);
            if (p instanceof AffectSides) {
               AffectSides as = (AffectSides)p;
               if (hasSSC(as)) {
                  has = true;
               }
            }
         }

         return has;
      }
   }

   private static boolean hasSSC(AffectSides as) {
      if (as.getConditions().size() == 0) {
         return true;
      } else {
         for (AffectSideCondition asc : as.getConditions()) {
            if (asc instanceof SpecificSidesCondition) {
               return true;
            }
         }

         return false;
      }
   }

   public Item example() {
      return make(
         Tann.pick(
            SpecificSidesType.Top, SpecificSidesType.Bot, SpecificSidesType.Row, SpecificSidesType.Column, SpecificSidesType.Wings, SpecificSidesType.RightTwo
         ),
         RandomCheck.checkedRandom(new Supplier<Item>() {
            public Item supply() {
               return ItemLib.random();
            }
         }, new Checker<Item>() {
            public boolean check(Item item) {
               return PipeItemPosition.this.itemValid(item);
            }
         }, ItemLib.byName("short sword"))
      );
   }

   @Override
   public boolean isSlow() {
      return true;
   }
}
