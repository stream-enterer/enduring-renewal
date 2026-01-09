package com.tann.dice.gameplay.trigger.global.eff;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.EffUtils;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassTime;
import com.tann.dice.util.Pixl;

public class GlobalStartTurnEff extends GlobalEffContainer {
   final Eff[] effs;
   final SnapshotEvent event;

   public GlobalStartTurnEff(Eff... effs) {
      this(null, effs);
   }

   public GlobalStartTurnEff(SnapshotEvent event, Eff... effs) {
      super(effs);
      this.effs = effs;
      this.event = event;
   }

   @Override
   public String describeForSelfBuff() {
      String end = this.effDesc(true).replace(" this turn", "");
      String start;
      if (this.lastsOneTurn(this.effs)) {
         start = com.tann.dice.Main.t("During each turn");
      } else {
         start = com.tann.dice.Main.t("At the start of each turn");
      }

      return "[notranslate]" + start + ", " + end;
   }

   public String effDesc(boolean withThisTurn) {
      return EffUtils.describe(this.effs, withThisTurn).toLowerCase();
   }

   private boolean lastsOneTurn(Eff[] effs) {
      Eff e = effs[0];
      Buff b = e.getBuff();
      return b != null && b.turns == 1;
   }

   @Override
   public void startOfTurnGeneral(Snapshot snapshot, int turn) {
      for (Eff eff : this.effs) {
         snapshot.target(null, new SimpleTargetable(null, eff), false);
      }

      if (this.event != null) {
         snapshot.addEvent(this.event);
      }
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      Pixl p = new Pixl(0);

      for (Eff e : this.effs) {
         int v = e.getValue();
         String s = "";
         if (v != -999) {
            s = v + "";
         }

         if (e.getRestrictions().size() > 0) {
            s = s + "!";
         }

         Actor a = e.getBasicImage(s);
         if (a != null) {
            p.actor(a);
         }
      }

      return p.pix();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;

      for (Eff e : this.effs) {
         bit |= e.getCollisionBits(player);
      }

      return bit;
   }

   @Override
   public String describeForHourglass() {
      return this.effDesc(true);
   }

   @Override
   public HourglassTime getHourglassTime() {
      return this.effs[0].getType() == EffType.Summon ? HourglassTime.START : super.getHourglassTime();
   }

   @Override
   public String hyphenTag() {
      return ModifierUtils.hyphenTag(this.effs[0]);
   }

   @Override
   public Eff getSingleEffOrNull() {
      return this.effs.length == 1 ? this.effs[0] : super.getSingleEffOrNull();
   }
}
