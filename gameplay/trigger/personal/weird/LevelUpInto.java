package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class LevelUpInto extends Personal {
   final String t;

   public LevelUpInto(String t) {
      this.t = t;
   }

   @Override
   public HeroType affectLevelup(HeroType from, HeroType to) {
      return this.make();
   }

   @Override
   public String describeForSelfBuff() {
      return "Levels up into " + this.make().getName(true);
   }

   private HeroType make() {
      return HeroTypeLib.byName(this.t);
   }
}
