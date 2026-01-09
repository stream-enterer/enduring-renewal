package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorStandardLoot;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.List;

public class PhaseGeneratorTransformPhase extends TransformPhase {
   final PhaseGenerator phaseGenerator;
   private static final char firstLetter = 'g';
   private Phase cached;

   public PhaseGeneratorTransformPhase(PhaseGenerator phaseGenerator) {
      this.phaseGenerator = phaseGenerator;
   }

   public PhaseGeneratorTransformPhase(String data) {
      this(makePG(data));
   }

   private static PhaseGenerator makePG(String data) {
      if (data == null) {
         throw new RuntimeException("bad data");
      } else if (data.equals("h")) {
         return new PhaseGeneratorLevelup();
      } else if (data.equals("i")) {
         return new PhaseGeneratorStandardLoot();
      } else {
         throw new RuntimeException("bad data");
      }
   }

   @Override
   public String serialise() {
      if (this.phaseGenerator instanceof PhaseGeneratorLevelup) {
         return "gh";
      } else {
         return this.phaseGenerator instanceof PhaseGeneratorStandardLoot ? "gi" : super.serialise();
      }
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   protected Phase makePhase(DungeonContext dc) {
      if (this.cached != null) {
         Phase tmp = this.cached;
         this.cached = null;
         return tmp;
      } else {
         List<Phase> phaseList = this.phaseGenerator.get(dc);
         if (phaseList.size() > 1) {
            TannLog.error("making phase multi");
         }

         return phaseList.size() == 0 ? null : phaseList.get(0);
      }
   }

   @Override
   protected StandardButton getLevelEndButtonInternal() {
      try {
         return this.makePhaseCached(DungeonScreen.get().getDungeonContext()).getLevelEndButton();
      } catch (Exception var2) {
         TannLog.error(var2, "dact");
         return super.getLevelEndButtonInternal();
      }
   }

   private Phase makePhaseCached(DungeonContext dc) {
      return this.cached != null ? this.cached : (this.cached = this.makePhase(dc));
   }

   @Override
   public String toString() {
      return "pgtp";
   }

   @Override
   public boolean isPositive() {
      return true;
   }
}
