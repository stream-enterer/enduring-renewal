package com.tann.dice.gameplay.trigger.personal.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.eff.GlobalStartTurnEff;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.GlobalTurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirement;
import com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement.TurnRequirementN;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassElement;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassTime;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class PersonalTurnRequirement extends LinkedPersonal {
   final TurnRequirement requirement;
   final Personal linked;
   static final String replaceTheTurn = "the turn";
   static final String replaceEachTurn = "each turn";
   boolean overrideShow = false;

   public PersonalTurnRequirement(int turn, Personal linked) {
      this(new TurnRequirementN(turn), linked);
   }

   public PersonalTurnRequirement(TurnRequirement requirement, Personal linked) {
      super(linked);
      this.requirement = requirement;
      this.linked = linked;
   }

   @Override
   public String describeForSelfBuff() {
      return describe(this.requirement, this.linked);
   }

   public static String describe(TurnRequirement requirement, Trigger linked) {
      String rq = requirement.describe();
      String lt = linked.describeForSelfBuff();
      if (com.tann.dice.Main.self().translator.shouldTranslate() && linked instanceof GlobalStartTurnEff) {
         return "[notranslate]" + com.tann.dice.Main.t("During " + rq.toLowerCase()) + ", " + ((GlobalStartTurnEff)linked).effDesc(false);
      } else if (lt.contains("the turn")) {
         return lt.replaceAll("the turn", rq.toLowerCase());
      } else if (lt.endsWith("each turn")) {
         return lt.replaceAll("each turn", "on " + rq.toLowerCase());
      } else {
         return lt.contains("each turn")
            ? lt.replaceAll("each turn", rq.toLowerCase())
            : "[notranslate]" + Words.capitaliseFirst(com.tann.dice.Main.t(rq)) + ": " + com.tann.dice.Main.t(lt);
      }
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      return this.requirement.isValid(snapshot.getTurn()) ? Arrays.asList(this.linked) : super.getLinkedPersonals(snapshot, entState);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, this.requirement.makePanelActor(), this.linked, GlobalTurnRequirement.TURN_COL);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.linked.getCollisionBits(player) | Collision.SPECIFIC_TURN;
   }

   @Override
   public Personal splice(Personal p) {
      return new PersonalTurnRequirement(this.requirement, p);
   }

   @Override
   public HourglassElement hourglassUtil() {
      return new HourglassElement(this.requirement, this.linked.describeForSelfBuff(), HourglassTime.DURING);
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.overrideShow;
   }

   public PersonalTurnRequirement show(boolean show) {
      this.overrideShow = show;
      return this;
   }

   public PersonalTurnRequirement show() {
      return this.show(true);
   }
}
