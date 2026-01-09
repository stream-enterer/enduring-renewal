package com.tann.dice.gameplay.trigger.personal.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassElement;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassTime;
import com.tann.dice.statics.Images;
import com.tann.dice.util.ImageActor;

public class EndOfTurnEff extends PersonalEffContainer {
   public Eff eff;

   public EndOfTurnEff(Eff eff) {
      super(eff);
      this.eff = eff;
   }

   public EndOfTurnEff(EffBill e) {
      this(e.bEff());
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public void endOfTurn(EntState entState) {
      entState.getSnapshot().untargetedUse(this.eff, entState.getEnt());
   }

   @Override
   public String describeForSelfBuff() {
      return "At the end of the turn, " + this.eff.toString().toLowerCase();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.eff.getCollisionBits(player);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(new ImageActor(Images.turnIcon), this.eff.getBasicImage(), GlobalTurnRequirement.TURN_COL);
   }

   @Override
   public HourglassElement hourglassUtil() {
      return new HourglassElement(TurnRequirementAll.get(), this.eff.toString().toLowerCase(), HourglassTime.END);
   }

   @Override
   public Personal genMult(int mult) {
      Eff cpy = gme(this.eff, mult);
      return cpy == null ? null : new EndOfTurnEff(cpy);
   }

   public static Eff gme(Eff eff, int mult) {
      if (!eff.hasValue()) {
         return null;
      } else {
         Eff cpy = eff.copy();
         cpy.setValue(GlobalNumberLimit.box(eff.getValue() * mult));
         return cpy;
      }
   }
}
