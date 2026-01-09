package com.tann.dice.gameplay.content.gen.pipe.mod.level;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.util.Tann;

public class PipeModNthFightShifted extends PipeRegexNamed<Modifier> {
   public PipeModNthFightShifted() {
      super(prnS("e"), LEVEL, prnS("\\."), LEVEL, prnS("\\."), MODIFIER);
   }

   protected Modifier internalMake(String[] groups) {
      String n = groups[0];
      String o = groups[1];
      String m = groups[2];
      if (Tann.isInt(n) && Tann.isInt(o)) {
         int nInt = Integer.parseInt(n);
         int oInt = Integer.parseInt(o);
         return this.make(nInt, oInt, ModifierLib.byName(m));
      } else {
         return null;
      }
   }

   private Modifier make(int per, int offset, Modifier mod) {
      if (mod.isMissingno()) {
         return null;
      } else if (offset == 0) {
         return ModifierLib.byName("e" + per + "-" + mod.getName());
      } else if (per <= 0 || per >= 50 || offset < 0 || offset >= per) {
         return null;
      } else if (per == 1) {
         return mod;
      } else {
         Global g = mod.getSingleGlobalOrNull();
         if (g != null && !g.allLevelsOnly()) {
            float tier = mod.getTier() * CurseDistribution.getMultLevelRange(20 - 20 / per, 20);
            String realName = "e" + per + "." + offset + "." + mod.getName();
            return new Modifier(tier, realName, new GlobalLevelRequirement(new LevelRequirementMod(per, per - offset), g));
         } else {
            return null;
         }
      }
   }

   public Modifier example() {
      return this.make(Tann.randomInt(2, 8), 1, ModifierLib.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
