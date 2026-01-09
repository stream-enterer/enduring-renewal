package com.tann.dice.gameplay.trigger.personal.death;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.eff.PersonalEffContainer;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;

public class OtherDeathEffect extends PersonalEffContainer {
   final String name;
   final Boolean friendly;
   final Eff eff;

   public OtherDeathEffect(String name, Boolean friendly, EffBill eff) {
      this(name, friendly, eff.bEff());
   }

   public OtherDeathEffect(String name, Boolean friendly, Eff eff) {
      super(eff);
      this.name = name;
      this.friendly = friendly;
      this.eff = eff;
   }

   @Override
   public void onOtherDeath(Snapshot snapshot, EntState dead, EntState self) {
      if (this.friendly == null || this.friendly == (dead.isPlayer() == self.isPlayer())) {
         if (!dead.isFled()) {
            if (this.eff.needsTarget()) {
               self.hit(this.eff, null);
            } else if (this.eff.getTargetingType() == TargetingType.Group) {
               snapshot.target(null, new SimpleTargetable(self.getEnt(), this.eff), false);
            } else {
               snapshot.untargetedUse(this.eff, self.getEnt());
            }
         }
      }
   }

   @Override
   public String getImageName() {
      return "necromancy";
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.name != null;
   }

   @Override
   public String describeForSelfBuff() {
      String s = "When ";
      if (this.friendly == null) {
         s = s + "anything ";
      } else {
         s = s + Words.entName(false, this.friendly, false) + " dies, ";
      }

      return this.eff.getType() == EffType.Buff
         ? "[notranslate]" + com.tann.dice.Main.t(s) + com.tann.dice.Main.t(this.eff.getBuff().toNiceString(this.eff))
         : "[notranslate]" + com.tann.dice.Main.t(s) + com.tann.dice.Main.t(this.eff.describe().toLowerCase());
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(1);
      int GAP = 3;
      Actor trigger = new Pixl(0, 3)
         .border(this.friendly == null ? Colours.dark : (this.friendly ? Colours.green : Colours.red))
         .image(Images.eq_skullWhite, Colours.light)
         .pix();
      p.actor(trigger).gap(3).text(":").gap(3);
      Actor a = this.eff.getBasicImage();
      p.actor(a);
      return p.pix();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return Collision.death(player);
   }
}
