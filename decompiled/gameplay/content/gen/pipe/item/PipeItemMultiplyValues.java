package com.tann.dice.gameplay.content.gen.pipe.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeItemMultiplyValues extends PipeRegexNamed<Item> {
   static final PRNPart SEP = new PRNMid("m");

   public PipeItemMultiplyValues() {
      super(ITEM, SEP, DIGIT);
   }

   protected Item internalMake(String[] groups) {
      String mulStr = groups[1];
      String itemStr = groups[0];
      return !bad(mulStr, itemStr) && Tann.isInt(mulStr) ? this.make(Integer.parseInt(mulStr), ItemLib.byName(itemStr)) : null;
   }

   private Item make(int mulStr, Item item) {
      if (item.isMissingno()) {
         return null;
      } else {
         return mulStr == 1 ? item : this.asLevel(item, mulStr, false);
      }
   }

   private Item asLevel(Item item, int mult, boolean requireAll) {
      if (item.getName(false).matches(".*\\.m\\.-?\\d.*")) {
         return null;
      } else {
         if (requireAll) {
            if (!this.canSimpleLevel(item)) {
               return null;
            }
         } else if (this.personals(item.getPersonals(), mult, false).isEmpty()) {
            return null;
         }

         List<Personal> personals = this.personals(item.getPersonals(), mult, true);
         if (personals.isEmpty()) {
            return null;
         } else {
            TextureRegion borderTex = item.getImage();
            ItBill ib = new ItBill(getNewTier(item.getTier(), mult), item.getName(false) + SEP + mult, borderTex);
            ib.prs(personals);
            return ib.bItem();
         }
      }
   }

   private static int getNewTier(int tier, int mult) {
      return tier * mult;
   }

   private boolean canSimpleLevel(Item item) {
      return this.personals(item.getPersonals(), 2, false).size() == item.getPersonals().size();
   }

   private List<Personal> personals(List<Personal> src, int mult, boolean allowPassthrough) {
      List<Personal> result = new ArrayList<>();

      for (Personal personalTrigger : src) {
         Personal potential = personalTrigger.genMult(mult);
         if (potential != null) {
            result.add(potential);
         } else if (allowPassthrough) {
            result.add(personalTrigger);
         }
      }

      return result;
   }

   private Item sExample(boolean mustBeGood) {
      int newTier = Tann.randomInt(3) + 2;
      if (Math.random() < 0.1) {
         newTier = 0;
      }

      for (int i = 0; i < 20; i++) {
         Item pt = this.asLevel(ItemLib.random(), newTier, mustBeGood);
         if (pt != null) {
            return pt;
         }
      }

      return this.asLevel(ItemLib.byName("Eye of Horus"), 2, false);
   }

   public Item example() {
      return this.sExample(false);
   }

   private static Color colFromMul(int mul) {
      switch (mul) {
         case 0:
            return Colours.grey;
         case 1:
         default:
            return Colours.pink;
         case 2:
            return Colours.yellow;
         case 3:
            return Colours.orange;
         case 4:
            return Colours.blue;
      }
   }

   @Override
   public boolean isSlow() {
      return true;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Item generateInternal(boolean wild) {
      return this.sExample(true);
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
