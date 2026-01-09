package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.level.GlobalSetMonsters;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PipeModSetFight extends PipeRegexNamed<Modifier> {
   static final PRNPart PREF = new PRNPref("fight");

   public PipeModSetFight() {
      super(PREF, MONSTER_MULTI);
   }

   protected Modifier internalMake(String[] groups) {
      String monStrings = groups[0];
      if (bad(monStrings)) {
         return null;
      } else {
         String[] sep = monStrings.split("\\+", -1);
         if (bad(sep)) {
            return null;
         } else {
            List<MonsterType> types = new ArrayList<>();

            for (int i = 0; i < sep.length; i++) {
               types.add(MonsterTypeLib.byName(sep[i]));
            }

            return this.create(types);
         }
      }
   }

   private Modifier create(List<MonsterType> types) {
      float tier = 0.0F;
      List<String> monsterNames = new ArrayList<>();

      for (int i = 0; i < types.size(); i++) {
         MonsterType mt = types.get(i);
         if (mt.isMissingno()) {
            return null;
         }

         monsterNames.add(mt.getName());
      }

      String name = PREF + Tann.commaList(monsterNames, "+", "+");
      return new Modifier(tier, name, new GlobalSetMonsters(types.toArray(new MonsterType[0])));
   }

   public Modifier example() {
      return this.create(Arrays.asList(MonsterTypeLib.randomWithRarity(), MonsterTypeLib.randomWithRarity()));
   }

   @Override
   public boolean showHigher() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
