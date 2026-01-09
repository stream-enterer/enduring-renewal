package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.MultiDifferentPersonal;
import com.tann.dice.gameplay.trigger.personal.util.PersonalGeneration;
import com.tann.dice.util.Tann;
import com.tann.dice.util.WhiskerRandom;
import java.util.Random;

public class PipeItemGenerated extends PipeRegexNamed<Item> {
   public static final PRNPart PREF = new PRNPref("ritemx");
   public static long ritemxSeed;

   public PipeItemGenerated() {
      super(PREF, UP_TO_FIFTEEN_HEX);
   }

   protected Item internalMake(String[] groups) {
      String seed = groups[0];
      long hx = GenUtils.hex(seed);
      if (hx < 0L) {
         return null;
      } else {
         WhiskerRandom r = new WhiskerRandom(hx);
         ritemxSeed = hx;
         return make(PREF + seed, r);
      }
   }

   public static String genItemPath(String name) {
      int genArtIndex = Math.abs(name.hashCode()) % 16;
      return "special/gen/" + genArtIndex;
   }

   private static Item make(String name, Random r) {
      ItBill ib = new ItBill(-69, name, genItemPath(name));
      int amt = (int)(r.nextFloat() * r.nextFloat() * 3.0F + 1.0F);

      for (int i = 0; i < amt; i++) {
         Personal p = PersonalGeneration.random(r);
         if (p instanceof MultiDifferentPersonal) {
            MultiDifferentPersonal mdp = (MultiDifferentPersonal)p;
            ib.prs(mdp.personals);
         } else {
            ib.prs(PersonalGeneration.random(r));
         }
      }

      return ib.bItem();
   }

   public Item example() {
      int seed = Tann.randomInt(100000);
      return make(PREF + GenUtils.hex(seed), new WhiskerRandom(seed));
   }
}
