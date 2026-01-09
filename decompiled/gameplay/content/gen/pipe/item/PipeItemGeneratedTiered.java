package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.util.Tann;
import com.tann.dice.util.WhiskerRandom;

public class PipeItemGeneratedTiered extends PipeRegexNamed<Item> {
   public static final PRNPart PREF = new PRNPref("ritem");

   public PipeItemGeneratedTiered() {
      super(PREF, UP_TO_FIFTEEN_HEX);
   }

   protected Item internalMake(String[] groups) {
      String seed = groups[0];
      long hx = GenUtils.hex(seed);
      if (hx < 0L) {
         return null;
      } else {
         WhiskerRandom r = new WhiskerRandom(hx);
         return AutoItemGeneration.make(PREF + seed, r);
      }
   }

   public Item example() {
      long seed = Tann.randomInt(9999999);
      return AutoItemGeneration.make(PREF + GenUtils.hex(seed), new WhiskerRandom(seed));
   }

   public static boolean shouldGenerateRandom() {
      return OptionLib.GENERATED_ITEMS.c() && Tann.chance(OptionUtils.genChance());
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Item generateInternal(boolean wild) {
      return this.example();
   }
}
