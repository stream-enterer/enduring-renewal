package com.tann.dice.gameplay.trigger.personal.death;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.eff.PersonalEffContainer;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;

public class OnDeathEffect extends PersonalEffContainer {
   private static final int GAP = 3;
   Eff eff;
   SnapshotEvent snapshotEvent;
   final boolean show;

   public OnDeathEffect(EffBill eb) {
      this(eb.bEff());
   }

   public OnDeathEffect(Eff eff) {
      this(eff, null);
   }

   public OnDeathEffect(Eff eff, SnapshotEvent snapshotEvent) {
      this(eff, snapshotEvent, true);
   }

   public OnDeathEffect(Eff eff, SnapshotEvent snapshotEvent, boolean show) {
      super(eff);
      this.eff = eff;
      this.snapshotEvent = snapshotEvent;
      this.show = show;
   }

   @Override
   public void onDeath(EntState self, Snapshot snapshot) {
      if (this.snapshotEvent != null) {
         snapshot.addEvent(this.snapshotEvent);
      }

      snapshot.target(null, new SimpleTargetable(self.getEnt(), this.eff), false);
   }

   @Override
   public String getImageName() {
      switch (this.eff.getType()) {
         case Mana:
            return "skullBlue";
         default:
            return "death";
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      return this.show;
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("Upon death") + ": " + this.eff.toString().toLowerCase();
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      switch (this.eff.getType()) {
         case Mana:
            return total;
         default:
            return super.affectStrengthCalc(total, avgRawValue, type);
      }
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      switch (this.eff.getType()) {
         case Mana:
            return hp - 0.9F;
         default:
            return super.affectTotalHpCalc(hp, entType);
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(0);
      p.image(Images.eq_skullWhite, Colours.light).gap(3).text(":").gap(3);
      Actor a = this.eff.getBasicImage();
      p.actor(a);
      return p.pix();
   }

   @Override
   public float getPriority() {
      return this.eff.getType() == EffType.Summon ? 20.0F : super.getPriority();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long l = 0L;
      String st = this.eff.getSummonType();
      if ("Grave".equals(st) || "Bones".equals(st)) {
         l |= Collision.CURSED_MODE;
      }

      return l | Collision.death(player) | this.eff.getCollisionBits(player);
   }

   @Override
   public String hyphenTag() {
      return ModifierUtils.hyphenTag(this.eff);
   }
}
