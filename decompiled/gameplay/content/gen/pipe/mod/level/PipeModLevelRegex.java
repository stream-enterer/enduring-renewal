package com.tann.dice.gameplay.content.gen.pipe.mod.level;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.util.Tann;

public class PipeModLevelRegex extends PipeRegexNamed<Modifier> {
   public PipeModLevelRegex() {
      super(LEVEL, prnS("\\."), MODIFIER);
   }

   public Modifier example() {
      for (int i = 0; i < 50; i++) {
         Modifier m = make(Tann.randomInt(20) + 1, ModifierLib.random());
         if (m != null) {
            return m;
         }
      }

      return ModifierLib.getMissingno();
   }

   public static Modifier make(int level, Modifier original) {
      return PipeModLevelRangeRegex.make(level, level, original);
   }

   public static boolean validateLevel(int level) {
      return level >= 1 && level <= 999;
   }

   protected Modifier internalMake(String[] groups) {
      String level = groups[0];
      String mod = groups[1];
      Modifier original = ModifierLib.byName(mod);
      return Tann.isInt(level) && !original.isMissingno() ? make(Integer.parseInt(level), original) : null;
   }
}
