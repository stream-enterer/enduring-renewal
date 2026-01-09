package com.tann.dice.gameplay.context.config.cursed;

import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.scaffolding.CurseLevelModulus;
import com.tann.dice.gameplay.trigger.global.scaffolding.MaxLevel;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseCurseConfig extends ContextConfig {
   protected BaseCurseConfig(Mode mode) {
      super(mode);
   }

   @Override
   public final List<Global> getSpecificModeGlobals() {
      List<Global> result = new ArrayList<>();
      result.add(new CurseLevelModulus(20));
      result.add(new MaxLevel(999));
      return result;
   }

   @Override
   protected boolean offerChanceEvents() {
      return false;
   }
}
