package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterGenerated;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.eff.GlobalSummonMonsterStartTurn;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import com.tann.dice.util.Tann;

public class PipeModAddMonsterMeta extends PipeRegexNamed<Modifier> {
   private static String[] PREFS = new String[]{"add", "summon", "3rd"};
   public static boolean ALLOW_PLURAL_NAME = false;

   public PipeModAddMonsterMeta() {
      super(prnSCapturedOneOf(PREFS), prnS("\\."), MONSTER);
   }

   protected Modifier internalMake(String[] groups) {
      String add = groups[0];
      String monster = groups[1];
      if (bad(add, monster)) {
         return null;
      } else {
         MonsterType mt = MonsterTypeLib.byName(monster);
         if (ALLOW_PLURAL_NAME && mt.isMissingno()) {
            mt = MonsterTypeLib.byPluralName(monster);
            if (mt == null) {
               return null;
            }
         }

         return mt.isMissingno() ? null : this.make(add, mt);
      }
   }

   private Modifier make(String add, MonsterType mon) {
      int pi = Tann.indexOfEq(PREFS, add.toLowerCase());
      return pi == -1 ? null : this.actuallyMake(pi, mon);
   }

   private Modifier actuallyMake(int pi, MonsterType mon) {
      if (mon.isMissingno()) {
         return null;
      } else {
         switch (pi) {
            case 0:
               return GlobalAddMonster.makeGenerated(mon);
            case 1:
               return GlobalSummonMonsterStartTurn.makeGenerated(mon, true);
            case 2:
               return GlobalSummonMonsterStartTurn.makeGenerated(mon, false);
            default:
               throw new RuntimeException("Unimp monstermeta");
         }
      }
   }

   public Modifier example() {
      return this.make(Tann.random(PREFS), MonsterTypeLib.randomWithRarity());
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   @Override
   public float getRarity(boolean wild) {
      return wild ? 1.0F : 2.0F;
   }

   protected Modifier generateInternal(boolean wild) {
      float c = (float)Math.random();
      String pref;
      if (c < 0.8F) {
         pref = PREFS[0];
      } else if (c < 0.9F) {
         pref = PREFS[1];
      } else {
         pref = PREFS[2];
      }

      MonsterType type = this.getTypeForGen(wild);
      return this.make(pref, type);
   }

   private MonsterType getTypeForGen(boolean wild) {
      int attempts = 200;

      for (int i = 0; i < 200; i++) {
         MonsterType mt;
         if (wild) {
            mt = PipeMonsterGenerated.makeMonstExt();
         } else {
            mt = MonsterTypeLib.randomWithRarity();
         }

         if (!mt.isUnique()) {
            return mt;
         }
      }

      return MonsterTypeLib.byName("missingno");
   }

   @Override
   public boolean showHigher() {
      return true;
   }
}
