package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.perN.PerFightGlobal;

public class PipeModPerLevel extends PipeModSimpleAbstractPer {
   public PipeModPerLevel() {
      super("pl");
   }

   @Override
   protected Modifier make(Modifier src) {
      float tier = src.getTier() * 10;
      Global g = src.getSingleGlobalOrNull();
      return g != null && !g.allLevelsOnly() && g.isMultiplable() ? new Modifier(tier, this.nameFor(src), new PerFightGlobal(g)) : null;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
