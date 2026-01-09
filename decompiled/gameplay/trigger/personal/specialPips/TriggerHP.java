package com.tann.dice.gameplay.trigger.personal.specialPips;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.trigger.personal.Stunned;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.immunity.DamageImmunity;
import com.tann.dice.gameplay.trigger.personal.position.BackRow;
import com.tann.dice.gameplay.trigger.personal.specialPips.pipLoc.PipLoc;
import com.tann.dice.statics.Images;
import com.tann.dice.util.tp.TP;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TriggerHP extends SpecialHp {
   final Eff eff;
   final TextureRegion hpImage;
   final List<StateEvent> events;
   final SnapshotEvent snapshotEvent;
   final Color col;

   public TriggerHP(Eff eff, Color color, PipLoc loc) {
      this(eff, new ArrayList<>(), color, loc);
   }

   public TriggerHP(Eff eff, TextureRegion pipImage, Color color, PipLoc loc) {
      this(eff, new ArrayList<>(), null, pipImage, color, loc);
   }

   public TriggerHP(Eff eff, StateEvent event, Color col, PipLoc loc) {
      this(eff, Arrays.asList(event), col, loc);
   }

   public TriggerHP(Eff eff, List<StateEvent> events, Color color, PipLoc loc) {
      this(eff, events, null, color, loc);
   }

   public TriggerHP(Eff eff, List<StateEvent> events, SnapshotEvent snapshotEvent, Color color, PipLoc loc) {
      this(eff, events, snapshotEvent, Images.hp_hole, color, loc);
   }

   public TriggerHP(Eff eff, List<StateEvent> events, SnapshotEvent snapshotEvent, TextureRegion hpImage, Color color, PipLoc loc) {
      super(loc);
      this.eff = eff;
      this.events = events;
      this.snapshotEvent = snapshotEvent;
      this.col = color;
      this.hpImage = hpImage;
   }

   @Override
   protected String describe() {
      return this.eff.describe().toLowerCase().replaceAll("\\[n\\]", " ");
   }

   @Override
   public boolean showInEntPanelInternal() {
      return false;
   }

   @Override
   public boolean showInDiePanel() {
      return true;
   }

   protected int willTrigger(int oldMinHp, int newHp, int maxHp) {
      int result = 0;

      for (int threshold : this.getPips(maxHp)) {
         if (oldMinHp > threshold && newHp <= threshold) {
            result++;
         }
      }

      return result;
   }

   @Override
   public void damageTaken(
      EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable, int minTriggerPipHp
   ) {
      int oldHp = minTriggerPipHp;
      int newHp = self.getHp();
      boolean triggered = false;

      for (int i = 0; i < this.willTrigger(oldHp, newHp, self.getMaxHp()); i++) {
         triggered = true;
         if (this.eff.getTargetingType() == TargetingType.Self) {
            self.hit(this.eff, self.getEnt());
         } else {
            snapshot.target(null, new SimpleTargetable(self.getEnt(), this.eff), false);
         }
      }

      if (triggered) {
         for (StateEvent event : this.events) {
            if (event.chance()) {
               self.addEvent(event);
            }
         }

         if (this.snapshotEvent != null) {
            snapshot.addEvent(this.snapshotEvent);
         }
      }

      super.damageTaken(source, self, snapshot, damage, damageTakenThisTurn, sourceEff, targetable, minTriggerPipHp);
   }

   @Override
   public TP<TextureRegion, Color> getPipTannple(boolean big) {
      return new TP<>(big ? this.hpImage : Images.hp_small, this.col);
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      int[] pips = this.getPips(type.hp);
      int numPips = pips.length;
      float pipsPerHp = (float)type.hp / numPips;
      switch (this.eff.getType()) {
         case Mana:
            return total - 0.5F;
         case Kill:
            return total + 2 * pips.length;
         case Damage:
            switch (this.eff.getTargetingType()) {
               case Group:
                  return total + this.eff.getValue() * 1.0F * pips.length;
               case Top:
               case Bot:
               case Mid:
                  return total + this.eff.getValue() * 0.2F * pips.length;
               default:
                  throw new RuntimeException("invalid triggerpip damage");
            }
         case Shield:
         case Heal:
            return total;
         case Buff:
            if (this.eff.getBuff().personal instanceof AffectSides) {
               return total * ((avgRawValue + 0.25F * pips.length) / avgRawValue);
            } else if (this.eff.getBuff().personal instanceof BackRow) {
               return total;
            } else if (this.eff.getBuff().personal instanceof DamageImmunity) {
               return total;
            } else if (this.eff.getBuff().personal instanceof Stunned) {
               float lowerBound = 0.06F;
               float mult = (float)Math.pow(pipsPerHp, -0.49);
               float val = total * (lowerBound + (1.0F - lowerBound) * (1.0F - mult));
               if (type.calcBackRow(0)) {
                  val = Interpolation.linear.apply(val, total, 0.7F);
               }

               return val;
            }
         default:
            return this.eff.hasKeyword(Keyword.cleanse) ? total : super.affectStrengthCalc(total, avgRawValue, type);
      }
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      int[] pips = this.getPips(entType);
      switch (this.eff.getType()) {
         case Mana:
            return hp + -0.75F * pips.length;
         case Kill:
            return (float)(hp * Math.pow(1.15F, pips.length));
         case Damage:
            if (hp > 10.0F) {
               return hp + 2 * pips.length;
            }

            float extraMult = this.eff.getTargetingType() == TargetingType.Group ? 3.0F : 1.0F;
            return hp + this.eff.getValue() * 0.1F * pips.length * extraMult;
         case Shield:
            return hp + this.eff.getValue() * pips.length * 0.7F;
         case Heal:
            return hp + pips.length * Math.min(hp / 2.0F, (float)this.eff.getValue()) * 0.9F;
         case Buff:
            if (this.eff.getBuff().personal instanceof AffectSides) {
               return hp;
            } else if (this.eff.getBuff().personal instanceof BackRow) {
               return hp + 1.1F;
            } else if (this.eff.getBuff().personal instanceof DamageImmunity) {
               return hp + 1.2F * pips.length;
            } else if (this.eff.getBuff().personal instanceof Stunned) {
               return hp;
            }
         default:
            return this.eff.hasKeyword(Keyword.cleanse) ? hp * 1.05F : super.affectTotalHpCalc(hp, entType);
      }
   }

   public boolean lateTrigger() {
      return this.eff.getType() == EffType.Summon;
   }

   @Override
   public float getPriority() {
      return this.lateTrigger() ? -4.0F : -5.0F;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.eff != null ? this.eff.getCollisionBits(player) : 0L;
   }
}
