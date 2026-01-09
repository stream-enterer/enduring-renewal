package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class LinkedPhase extends Phase {
   final Phase a;
   final Phase b;

   public LinkedPhase(Phase a, Phase b) {
      this.a = a;
      this.b = b;
   }

   public LinkedPhase(String data) {
      String[] parts = data.split("@1", 2);
      if (parts.length == 2) {
         this.a = Phase.deserialise(parts[0]);
         this.b = Phase.deserialise(parts[1]);
      } else {
         this.a = new MessagePhase("err");
         this.b = new MessagePhase("err2");
      }
   }

   @Override
   public String serialise() {
      return "l" + this.a.serialise() + "@1" + this.b.serialise();
   }

   @Override
   public void activate() {
      PhaseManager pm = PhaseManager.get();
      pm.forceNext(this.roundTrip(this.a));
      pm.pushPhaseAfter(this.roundTrip(this.b), (Class<? extends Phase>)this.a.getClass());
      pm.popPhase(LinkedPhase.class);
   }

   private Phase roundTrip(Phase a) {
      return Phase.deserialise(a.serialise());
   }

   @Override
   public void deactivate() {
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.yellow;
   }

   @Override
   protected StandardButton getLevelEndButtonInternal() {
      return new StandardButton(TextWriter.getTag(this.getLevelEndColour()) + "chain", this.getLevelEndColour(), 53, 20);
   }
}
