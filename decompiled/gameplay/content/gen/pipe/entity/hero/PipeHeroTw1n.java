package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.personal.choosable.TwinPersonal;
import com.tann.dice.util.TannLog;

public class PipeHeroTw1n extends Pipe<HeroType> {
   protected HeroType make(String name) {
      HeroType clone = HeroTypeUtils.byName("Twin");

      for (Trait t : clone.traits) {
         if (t.personal instanceof TwinPersonal) {
            return t.personal.getExtraHero().getHeroType();
         }
      }

      TannLog.log("Error finding clone clone", TannLog.Severity.error);
      return null;
   }

   @Override
   protected boolean nameValid(String name) {
      return name.equalsIgnoreCase("Tw1n");
   }

   public HeroType example() {
      return null;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
