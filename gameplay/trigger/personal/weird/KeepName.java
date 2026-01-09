package com.tann.dice.gameplay.trigger.personal.weird;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.trigger.personal.Personal;

public class KeepName extends Personal {
   @Override
   public HeroType affectLevelup(HeroType from, HeroType to) {
      String prevName = from.getName(true, false);
      String newName = HeroTypeUtils.safeHeroName(prevName);
      String extra = ".n." + newName + (prevName.contains("name tag") ? "" : ".i.name tag");
      return HeroTypeLib.byName("(" + to.getName() + ")" + extra);
   }

   @Override
   public String describeForSelfBuff() {
      return "Keep old name when levelling up";
   }
}
