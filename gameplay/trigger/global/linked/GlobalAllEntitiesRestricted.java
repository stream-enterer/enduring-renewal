package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ParamCondition;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalAllEntities;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.util.lang.Words;

public class GlobalAllEntitiesRestricted extends GlobalLinked {
   final boolean player;
   final ConditionalRequirement conReq;
   final Personal personal;
   long extraCollision;

   public GlobalAllEntitiesRestricted(boolean player, ConditionalRequirement req, Personal personal) {
      super(personal);
      this.player = player;
      this.conReq = req;
      this.personal = personal;
   }

   public GlobalAllEntitiesRestricted(boolean player, GenericStateCondition gsc, Personal personal) {
      this(player, new GSCConditionalRequirement(gsc), personal);
      this.extraCollision = gsc.getCollision();
   }

   @Override
   public String describeForSelfBuff() {
      return this.describeRestriction() + ":[n]" + this.personal.describeForSelfBuff();
   }

   private String describeRestriction() {
      if (this.conReq instanceof GSCConditionalRequirement) {
         return this.conReq.getBasicString() + " " + (this.player ? "heroes" : "monsters");
      } else {
         boolean plural = this.conReq.isPlural();
         boolean xWith = this.conReq instanceof GSCConditionalRequirement || this.conReq instanceof ParamCondition;
         String start = plural ? "All " : "The ";
         String cbs = this.conReq.getBasicString().toLowerCase();
         if (xWith) {
            if (this.player) {
               start = start + "heroes ";
            } else {
               start = start + "monsters ";
            }

            return start + cbs;
         } else {
            return start + cbs + " " + Words.entName(null, this.player, plural ? true : null);
         }
      }
   }

   @Override
   public Personal getLinkedPersonal(EntState entState) {
      if (entState.getEnt().isPlayer() == this.player && entState.getSnapshot() != null) {
         boolean valid = this.conReq.isValid(entState.getSnapshot(), entState, entState, null);
         return valid ? this.personal : super.getLinkedPersonal(entState);
      } else {
         return null;
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, this.conReq.getRestrictionActor(), this.personal, GlobalAllEntities.colForPlayer(this.player));
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.extraCollision | this.personal.getCollisionBits(player);
   }

   @Override
   public boolean isMultiplable() {
      return false;
   }

   @Override
   public String hyphenTag() {
      return ModifierUtils.hyphenTag(this.personal.hyphenTag(), makeTag(this.conReq));
   }

   private static String makeTag(ConditionalRequirement conReq) {
      return conReq instanceof ParamCondition ? ((ParamCondition)conReq).hyphenTag() : null;
   }

   @Override
   public GlobalLinked splice(Personal newCenter) {
      return new GlobalAllEntitiesRestricted(this.player, this.conReq, newCenter);
   }
}
