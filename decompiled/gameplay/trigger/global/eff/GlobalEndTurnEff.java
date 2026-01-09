package com.tann.dice.gameplay.trigger.global.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;

public class GlobalEndTurnEff extends GlobalEffContainer {
   final Eff e;

   public GlobalEndTurnEff(EffBill e) {
      this(e.bEff());
   }

   public GlobalEndTurnEff(Eff e) {
      super(e);
      this.e = e;
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("At the end of each turn") + ", " + this.describeEff();
   }

   private String describeEff() {
      return this.e.describe().toLowerCase();
   }

   @Override
   public void endOfTurnGeneral(Snapshot snapshot, int turn) {
      snapshot.untargetedUse(this.e, null);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(new TurnRequirementAll().makePanelActor(), this.e.getBasicImage(), GlobalTurnRequirement.TURN_COL);
   }

   public Actor makePanelActorGivenGTR(boolean big) {
      return this.e.getBasicImage();
   }

   @Override
   public String describeForHourglass() {
      return this.describeEff();
   }

   @Override
   public String hyphenTag() {
      return this.e.getValue() + "";
   }

   @Override
   public Eff getSingleEffOrNull() {
      return this.e;
   }
}
