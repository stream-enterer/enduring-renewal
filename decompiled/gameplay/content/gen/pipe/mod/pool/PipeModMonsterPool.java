package com.tann.dice.gameplay.content.gen.pipe.mod.pool;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.pool.monster.GlobalClearPoolMonster;
import com.tann.dice.gameplay.trigger.global.pool.monster.GlobalExtraMonsterPool;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeModMonsterPool extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("monsterpool");

   public PipeModMonsterPool() {
      super(PREF, MONSTER_MULTI);
   }

   protected Modifier internalMake(String[] groups) {
      String monsterStrings = groups[0];
      if (bad(monsterStrings)) {
         return null;
      } else {
         String[] sep = monsterStrings.split("\\+", -1);
         if (bad(sep)) {
            return null;
         } else {
            List<MonsterType> types = new ArrayList<>();

            for (int i = 0; i < sep.length; i++) {
               MonsterType mt = MonsterTypeLib.byName(sep[i]);
               if (mt.isMissingno()) {
                  return null;
               }

               types.add(mt);
            }

            return this.create(types);
         }
      }
   }

   private Modifier create(List<MonsterType> types) {
      List<String> heroNames = new ArrayList<>();

      for (int i = 0; i < types.size(); i++) {
         MonsterType ht = types.get(i);
         if (ht.isMissingno()) {
            return null;
         }

         heroNames.add(ht.getName());
      }

      String name = PREF + Tann.commaList(heroNames, "+", "+");
      return new Modifier(name, new GlobalClearPoolMonster(), new GlobalExtraMonsterPool(types));
   }

   public Modifier example() {
      return this.create(Arrays.asList(MonsterTypeLib.byName("wolf.hue.50"), MonsterTypeLib.byName("imp.hue.50")));
   }
}
