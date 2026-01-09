package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase;

public class MissingnoPhase extends MessagePhase {
   final String pm;

   public MissingnoPhase(String msg) {
      super("[red]Error[cu][n]" + msg);
      this.pm = msg;
   }

   @Override
   public String serialise() {
      return "x" + this.pm;
   }
}
