package com.tann.dice.gameplay.trigger.personal.linked.stateCondition;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ColRestriction;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.LinkedPersonal;
import com.tann.dice.util.Colours;
import java.util.Arrays;
import java.util.List;

public class PersonalConditionLink extends LinkedPersonal {
   final ConditionalRequirement req;
   final Personal linked;
   List<Personal> pList;

   public PersonalConditionLink(ConditionalRequirement req, Personal linked) {
      super(linked);
      this.req = req;
      this.linked = linked;
   }

   public PersonalConditionLink(StateConditionType type, Personal linked) {
      this(new GenericStateCondition(type), linked);
   }

   public PersonalConditionLink(GenericStateCondition gsc, Personal linked) {
      this(new GSCConditionalRequirement(gsc), linked);
   }

   @Override
   public String describeForSelfBuff() {
      String left = this.linked.describeForSelfBuff();
      String right = this.req.getBasicString();
      right = right.replaceAll("with", "I have");
      return "[notranslate]" + com.tann.dice.Main.t(left) + " " + com.tann.dice.Main.t("if " + right);
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, this.req.getRestrictionActor(), this.linked, this.getBorderCol(this.req));
   }

   private Color getBorderCol(ConditionalRequirement req) {
      return req instanceof ColRestriction ? ((ColRestriction)req).col.col : Colours.yellow;
   }

   @Override
   public List<Personal> getLinkedPersonals(Snapshot snapshot, EntState entState) {
      if (!this.req.isValid(snapshot, entState, entState, null)) {
         return null;
      } else {
         if (this.pList == null) {
            this.pList = Arrays.asList(this.linked);
         }

         return this.pList;
      }
   }

   @Override
   public float getPriority() {
      return -10.0F;
   }

   @Override
   public Personal splice(Personal p) {
      return new PersonalConditionLink(this.req, p);
   }
}
