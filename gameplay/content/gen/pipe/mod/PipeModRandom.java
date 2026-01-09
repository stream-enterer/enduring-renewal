package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.generation.GenUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.gen.GlobalGeneration;
import com.tann.dice.util.WhiskerRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PipeModRandom extends PipeRegexNamed<Modifier> {
   public final PRNPart PREF;

   public PipeModRandom() {
      this(new PRNPref("rmod"));
   }

   public PipeModRandom(PRNPart a) {
      super(a, UP_TO_FIFTEEN_HEX);
      this.PREF = a;
   }

   protected Modifier internalMake(String[] groups) {
      long seed = GenUtils.hex(groups[0]);
      return this.make(seed);
   }

   private Modifier make(long seed) {
      Random r = new WhiskerRandom(seed);
      float roll = r.nextFloat();
      int amt;
      if (roll > 0.95F) {
         amt = 3;
      } else if (roll > 0.5F) {
         amt = 2;
      } else {
         amt = 1;
      }

      List<Global> list = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         list.add(GlobalGeneration.random(r));
      }

      return new Modifier(-0.069F, this.PREF + GenUtils.hex(seed), list);
   }

   public Modifier example() {
      return this.make((long)(Math.random() * 100000.0));
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
