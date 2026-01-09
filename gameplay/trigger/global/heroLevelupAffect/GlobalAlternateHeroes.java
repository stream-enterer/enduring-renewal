package com.tann.dice.gameplay.trigger.global.heroLevelupAffect;

import com.tann.dice.gameplay.trigger.global.Global;

public class GlobalAlternateHeroes extends Global {
   @Override
   public HeroGenType generateHeroes() {
      return HeroGenType.Alternate;
   }
}
