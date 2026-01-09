package com.tann.dice.gameplay.content.gen.pipe.mod.level;

import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementMod;
import com.tann.dice.util.Tann;

public class PipeModNthFight extends PipeRegexNamed<Modifier> {
   public PipeModNthFight() {
      super(prnS("e"), UP_TO_THREE_DIGITS, prnS("\\."), MODIFIER);
   }

   protected Modifier internalMake(String[] groups) {
      String n = groups[0];
      String m = groups[1];
      if (bad(n, m)) {
         return null;
      } else if (!Tann.isInt(n)) {
         return null;
      } else {
         int nInt = Integer.parseInt(n);
         return this.make(nInt, ModifierLib.byName(m));
      }
   }

   private Modifier make(int n, Modifier mod) {
      if (mod.isMissingno()) {
         return null;
      } else if (n <= 0 || n >= 999) {
         return null;
      } else if (n == 1) {
         return mod;
      } else {
         Global g = mod.getSingleGlobalOrNull();
         if (g != null && !g.allLevelsOnly()) {
            float tier = mod.getTier() * CurseDistribution.getMultLevelRange(20 - 20 / n, 20);
            return new Modifier(tier, "e" + n + "." + mod.getName(), new GlobalLevelRequirement(new LevelRequirementMod(n, 0), g));
         } else {
            return null;
         }
      }
   }

   public Modifier example() {
      Modifier m = ModifierLib.random();
      return Collision.collides(m.getCollisionBits(), Collision.ITEM_REWARD | Collision.LEVELUP_REWARD)
         ? null
         : this.make(Tann.randomInt(2, 8), ModifierLib.random());
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
