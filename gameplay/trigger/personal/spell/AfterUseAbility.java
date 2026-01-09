package com.tann.dice.gameplay.trigger.personal.spell;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class AfterUseAbility extends Personal {
   final Integer nth;
   final Eff[] effs;

   public AfterUseAbility(Integer nth, Eff effs) {
      this(nth, effs);
   }

   public AfterUseAbility(Integer nth, Eff... effs) {
      this.nth = nth;
      this.effs = effs;
   }

   @Override
   public boolean showInEntPanelInternal() {
      return true;
   }

   @Override
   public String getImageName() {
      switch (this.effs[0].getType()) {
         case Heal:
            return "boltRed";
         case Damage:
            if (this.effs[0].getTargetingType() == TargetingType.Self) {
               return "boltPurple";
            }

            return "wail";
         case Mana:
            return "boltBlue";
         case Shield:
            return "boltGrey";
         default:
            return super.getImageName();
      }
   }

   @Override
   public String describeForSelfBuff() {
      String start;
      if (this.nth == null) {
         start = "After an ability is used";
      } else {
         start = "After the " + Words.ordinal(this.nth + 1) + " ability each turn is used";
      }

      return "[notranslate]" + com.tann.dice.Main.t(start) + ", " + this.effs[0].describe().toLowerCase();
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(3);
      p.actor(Images.eq_triggerSpell).text(":");
      Actor a = this.effs[0].getBasicImage();
      p.actor(a);
      return p.pix();
   }

   @Override
   public void afterUseAbility(Snapshot snapshot, Ability ability, EntState entState) {
      if (this.nth == null || this.nth == snapshot.getTotalAbilitiesUsedThisTurn()) {
         for (Eff e : this.effs) {
            snapshot.target(null, new SimpleTargetable(entState.getEnt(), e), false);
         }
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.SPELL | Collision.TACTIC;
   }
}
