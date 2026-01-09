package com.tann.dice.gameplay.trigger.global.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementAll;
import com.tann.dice.util.ImageActor;

public class GlobalEndTurnSpell extends GlobalEndTurnEff {
   final Ability sp;

   public GlobalEndTurnSpell(Ability sp) {
      super(sp.getBaseEffect());
      if (sp.getBaseEffect().needsTarget()) {
         throw new RuntimeException("invalid end of turn eff");
      } else {
         this.sp = sp;
      }
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("At the end of each turn") + ", " + this.descSpellEff();
   }

   private String descSpellEff() {
      return this.sp.getBaseEffect().describe(true);
   }

   @Override
   public void endOfTurnGeneral(Snapshot snapshot, int turn) {
      snapshot.target(null, this.sp, false);
   }

   @Override
   public Actor makePanelActorGivenGTR(boolean big) {
      return new ImageActor(this.sp.getImage());
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(new TurnRequirementAll().makePanelActor(), new ImageActor(this.sp.getImage()), GlobalTurnRequirement.TURN_COL);
   }

   @Override
   public String describeForHourglass() {
      return this.descSpellEff();
   }
}
