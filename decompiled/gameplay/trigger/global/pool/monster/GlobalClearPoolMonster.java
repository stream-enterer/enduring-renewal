package com.tann.dice.gameplay.trigger.global.pool.monster;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.List;

public class GlobalClearPoolMonster extends Global {
   @Override
   public String describeForSelfBuff() {
      return "clear pool of monster";
   }

   @Override
   public void affectMonsterPool(List<MonsterType> results) {
      results.clear();
   }
}
