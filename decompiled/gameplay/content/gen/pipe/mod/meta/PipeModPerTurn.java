package com.tann.dice.gameplay.content.gen.pipe.mod.meta;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.perN.PerTurnGlobal;

public class PipeModPerTurn extends PipeModSimpleAbstractPer {
   static final int attempts = 5;

   public PipeModPerTurn() {
      super("pt");
   }

   @Override
   protected Modifier make(Modifier src) {
      float tier = src.getTier() * 1.6F;
      Global g = src.getSingleGlobalOrNull();
      return g != null && !g.allTurnsOnly() && g.isMultiplable() ? new Modifier(tier, this.nameFor(src), new PerTurnGlobal(g)) : null;
   }

   @Override
   public Modifier example() {
      for (int i = 0; i < 5; i++) {
         Modifier m = this.make(ModifierLib.random());
         if (m != null) {
            return m;
         }
      }

      return null;
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return wild;
   }

   protected Modifier generateInternal(boolean wild) {
      return this.example();
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.5F;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
