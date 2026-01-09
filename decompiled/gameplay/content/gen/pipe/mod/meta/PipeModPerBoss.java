package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.perN.PerDefeatedBossGlobal;

public class PipeModPerBoss extends PipeModSimpleAbstractPer {
   public PipeModPerBoss() {
      super("pb");
   }

   @Override
   protected Modifier make(Modifier src) {
      float tier = src.getTier() * 2.2F;
      Global g = src.getSingleGlobalOrNull();
      return g != null && !g.allLevelsOnly() && g.isMultiplable() ? new Modifier(tier, this.nameFor(src), new PerDefeatedBossGlobal(g)) : null;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.make(ModifierLib.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
