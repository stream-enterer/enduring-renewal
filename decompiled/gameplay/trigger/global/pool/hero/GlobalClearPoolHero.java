package com.tann.dice.gameplay.trigger.global.pool.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.List;

public class GlobalClearPoolHero extends Global {
   @Override
   public String describeForSelfBuff() {
      return "clear pool of heroes for levelup";
   }

   @Override
   public void affectLevelupOptions(List<HeroType> results) {
      results.clear();
   }
}
