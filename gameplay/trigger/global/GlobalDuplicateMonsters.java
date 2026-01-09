package com.tann.dice.gameplay.trigger.global;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class GlobalDuplicateMonsters extends Global {
   final int multiple;

   public GlobalDuplicateMonsters(int multiple) {
      this.multiple = multiple;
   }

   @Override
   public String describeForSelfBuff() {
      return Words.capitaliseFirst(Words.multiple(this.multiple) + " the monsters in each fight");
   }

   @Override
   public void affectStartMonsters(List<Monster> monsters) {
      for (int m = 1; m < this.multiple; m++) {
         for (int i = monsters.size() - 1; i >= 0; i--) {
            monsters.add(i, monsters.get(i).getEntType().makeEnt());
         }
      }
   }

   @Override
   public int affectReinforcements(int amt) {
      return amt * Math.max(1, this.multiple);
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public void onPick(DungeonContext context) {
      if (DungeonScreen.get() != null) {
         FightLog f = DungeonScreen.get().getFightLog();
         List<Monster> monsters = MonsterTypeLib.monsterList(context.getCurrentLevel().getMonsterList());
         List<Hero> heroes = context.getParty().getHeroes();
         f.resetDueToFiddling(heroes, monsters);
         super.onPick(context);
      }
   }

   @Override
   public float getPriority() {
      return 1000.0F;
   }
}
