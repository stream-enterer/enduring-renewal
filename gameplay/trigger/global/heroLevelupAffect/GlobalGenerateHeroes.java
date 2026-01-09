package com.tann.dice.gameplay.trigger.global.heroLevelupAffect;

import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalGenerateHeroes extends Global {
   @Override
   public HeroGenType generateHeroes() {
      return HeroGenType.Generate;
   }
}
