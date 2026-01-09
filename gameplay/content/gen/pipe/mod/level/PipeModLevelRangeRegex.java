package com.tann.dice.gameplay.content.gen.pipe.mod.level;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.generation.CurseDistribution;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.GlobalLevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementFirst;
import com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement.LevelRequirementRange;
import com.tann.dice.util.Tann;

public class PipeModLevelRangeRegex extends PipeRegexNamed<Modifier> {
   public PipeModLevelRangeRegex() {
      super(LEVEL, DASH, LEVEL, prnS("\\."), MODIFIER);
   }

   public Modifier example() {
      for (int i = 0; i < 50; i++) {
         Modifier src = ModifierLib.random();
         if (!Collision.collides(src.getCollisionBits(), Collision.SPECIFIC_LEVEL_WIDE)) {
            Modifier m = make(Tann.randomInt(9), Tann.randomInt(9) + 10, src);
            if (m != null && src.getTier() != m.getTier() && m.getTier() != 0) {
               return m;
            }
         }
      }

      return ModifierLib.getMissingno();
   }

   public static Modifier make(int start, int end, Modifier original) {
      if (!validateModifier(original)) {
         return null;
      } else {
         LevelRequirement lr = getFrom(start, end);
         if (lr == null) {
            return null;
         } else {
            float tier = 0.0F;
            if (start > 0 && start <= 20 && end > 0 && end <= 20) {
               tier = original.getFloatTier() * CurseDistribution.getMultLevelRange(start, end);
            }

            if (Collision.collides(original.getCollisionBits(), Collision.SPECIFIC_LEVEL_WIDE)) {
               tier = 0.0F;
            }

            Global g = original.getSingleGlobalOrNull();
            if (g == null) {
               return null;
            } else {
               String preTag = start + "-" + end;
               if (start == end) {
                  preTag = start + "";
               }

               String name = preTag + "." + original.getName();
               return new Modifier(tier, name, new GlobalLevelRequirement(lr, g));
            }
         }
      }
   }

   private static LevelRequirement getFrom(int start, int end) {
      if (start == 0 && end == 0) {
         return new LevelRequirementFirst();
      } else {
         return !validateLevel(start, end) ? null : new LevelRequirementRange(start, end);
      }
   }

   private static boolean validateModifier(Modifier original) {
      Global first = original.getGlobals().get(0);
      if (first.allLevelsOnly()) {
         return false;
      } else {
         return first instanceof GlobalLevelRequirement ? false : !original.isOnPick();
      }
   }

   private static boolean validateLevel(int start, int end) {
      return PipeModLevelRegex.validateLevel(start) && PipeModLevelRegex.validateLevel(end) && start <= end;
   }

   protected Modifier internalMake(String[] groups) {
      String start = groups[0];
      String end = groups[1];
      String mod = groups[2];
      Modifier original = ModifierLib.byName(mod);
      return Tann.isInt(start) && Tann.isInt(end) && !original.isMissingno() ? make(Integer.parseInt(start), Integer.parseInt(end), original) : null;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected Modifier generateInternal(boolean wild) {
      if (wild) {
         return this.example();
      } else {
         MonsterType mt = MonsterTypeLib.randomWithRarity();
         Modifier add = GlobalAddMonster.makeGenerated(mt);
         return add == null ? null : make(11, 20, add);
      }
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.3F;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
