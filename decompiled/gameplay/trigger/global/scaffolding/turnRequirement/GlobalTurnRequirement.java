package com.tann.dice.gameplay.trigger.global.scaffolding.turnRequirement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.eff.GlobalEndTurnEff;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.gameplay.trigger.personal.linked.PersonalTurnRequirement;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassElement;
import com.tann.dice.util.Colours;

public class GlobalTurnRequirement extends GlobalLinked {
   public static final Color TURN_COL = Colours.grey;
   protected final TurnRequirement requirement;
   final Global linked;

   public GlobalTurnRequirement(int turn, Global linked) {
      this(new TurnRequirementN(turn), linked);
   }

   public GlobalTurnRequirement(TurnRequirement requirement, Global linked) {
      super(linked);
      this.requirement = requirement;
      this.linked = linked;
   }

   @Override
   public String describeForSelfBuff() {
      String s = PersonalTurnRequirement.describe(this.requirement, this.linked);
      if (this.linked.afterItems()) {
         s = s + ModifierUtils.afterItems();
      }

      return s;
   }

   @Override
   public Global getLinkedGlobal(DungeonContext context, int turn) {
      return this.requirement.isValid(turn) ? this.linked : super.getLinkedGlobal(context, turn);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return this.linked instanceof GlobalEndTurnEff
         ? DipPanel.makeSidePanelGroup(this.requirement.makePanelActor(), ((GlobalEndTurnEff)this.linked).makePanelActorGivenGTR(big), TURN_COL)
         : DipPanel.makeSidePanelGroup(big, this.requirement.makePanelActor(), this.linked, TURN_COL);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.linked.getCollisionBits() | Collision.SPECIFIC_TURN;
   }

   @Override
   public HourglassElement hourglassUtil() {
      return new HourglassElement(this.requirement, this.linked.describeForHourglass(), this.linked.getHourglassTime());
   }

   @Override
   public String hyphenTag() {
      return ModifierUtils.hyphenTag(this.linked.hyphenTag(), this.requirement.hyphenTag());
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new GlobalTurnRequirement(this.requirement, newCenter);
   }

   public Global debugLinked() {
      return this.linked;
   }

   public TurnRequirement getRequirement() {
      return this.requirement;
   }

   @Override
   protected boolean overrideAllTurnsOnly() {
      return !(this.getRequirement() instanceof TurnRequirementAll);
   }
}
