package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PipeModDelivery extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("delivery");

   public PipeModDelivery() {
      super(PREF, PipeRegexNamed.UP_TO_FIFTEEN_HEX);
   }

   protected Modifier internalMake(String[] groups) {
      String ss = groups[0];
      long seed = GenUtils.hex(ss);
      return this.makeMod(seed);
   }

   private Modifier makeMod(long seed) {
      Random r = Tann.makeStdRandom(seed);
      int amtItems = r.nextInt(4) + 2;
      int attempts = 100;
      List<Item> items = new ArrayList<>();

      for (int i = 0; i < 100; i++) {
         Item it = ItemLib.random(r);
         items.add(it);
         if (ItemLib.collides(items)) {
            items.remove(it);
         } else if (items.size() == amtItems) {
            float totalTier = 0.0F;

            for (Item item : items) {
               totalTier += item.getModTier();
            }

            if (totalTier > 0.0F) {
               totalTier *= 0.9F;
            }

            return new Modifier(totalTier, PREF + GenUtils.hex(seed), new GlobalStartWithItem(items.toArray(new Item[0])));
         }
      }

      return ModifierLib.byName("oiedjrfgoiejr");
   }

   public Modifier example() {
      return this.makeMod((long)(Math.random() * 50000.0));
   }

   @Override
   public float getRarity(boolean wild) {
      return 1.0F;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
