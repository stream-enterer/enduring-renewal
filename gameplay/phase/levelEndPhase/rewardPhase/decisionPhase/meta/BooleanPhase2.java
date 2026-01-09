package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta;

import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MissingnoPhase;

public class BooleanPhase2 extends TransformPhase {
   final String key;
   final int threshold;
   final Phase a;
   final Phase b;
   static final String MINI_SEPS = "@6";
   static final String BOOL_PHASE_SEP = "@7";

   public BooleanPhase2(String key, int threshold, Phase a, Phase b) {
      this.key = key;
      this.threshold = threshold;
      this.a = a;
      this.b = b;
   }

   public BooleanPhase2(String data) {
      String[] parts = data.split("@6", 3);
      this.key = parts[0];
      this.threshold = Integer.parseInt(parts[1]);
      String[] phaseStrings = parts[2].split("@7", 2);
      this.a = Phase.deserialise(phaseStrings[0]);
      this.b = Phase.deserialise(phaseStrings[1]);
      if (this.a instanceof MissingnoPhase || this.b instanceof MissingnoPhase) {
         throw new RuntimeException("Invalid Boolean phase " + data);
      }
   }

   @Override
   public String serialise() {
      return "z" + this.key + "@6" + this.threshold + "@6" + this.a.serialise() + "@7" + this.b.serialise();
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   protected Phase makePhase(DungeonContext dc) {
      Integer v = dc.getValue(this.key);
      return v != null && v >= this.threshold ? this.a : this.b;
   }
}
