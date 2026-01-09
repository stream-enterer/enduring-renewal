package com.tann.dice.gameplay.trigger.personal.onHit;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.eff.keyword.KeywordAllowType;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.PanelHighlightEvent;
import java.util.List;

public class OnHitFromPipe extends OnHit {
   final Eff e;

   public static OnHitFromPipe make(HeroType ht) {
      Eff e = getLeftmostBlankDerived(ht);
      return !isValid(e) ? null : new OnHitFromPipe(e);
   }

   public static Eff getLeftmostBlankDerived(HeroType ht) {
      List<EntSideState> states = ht.makeEnt().getBlankState().getAllSideStates();
      return states.get(2).getCalculatedEffect();
   }

   private static boolean isValid(Keyword k) {
      if (k.getAllowType() == KeywordAllowType.SPELL) {
         return false;
      } else {
         switch (k) {
            case poison:
            case regen:
            case weaken:
            case boost:
            case smith:
            case serrated:
            case cleave:
            case cleanse:
            case vulnerable:
            case repel:
            case dispel:
            case vitality:
            case focus:
            case descend:
            case wither:
            case manaGain:
            case heal:
            case shield:
            case damage:
            case terminal:
               return true;
            case death:
            default:
               return false;
         }
      }
   }

   private static boolean isValid(Eff e) {
      for (ConditionalRequirement restriction : e.getRestrictions()) {
         if (restriction.toString().endsWith("Me")) {
            return false;
         }
      }

      EffType type = e.getType();
      if (type != EffType.RedirectIncoming && type != EffType.Blank) {
         for (Keyword w : e.getKeywords()) {
            if (!isValid(w)) {
               return false;
            }
         }

         switch (e.getTargetingType()) {
            case Single:
            case Self:
               return true;
            case Group:
            case ALL:
               return true;
            case Untargeted:
               return true;
            case Top:
            case Bot:
            case Mid:
            case TopAndBot:
               return false;
            case SpellSource:
               return false;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   private OnHitFromPipe(Eff e) {
      this.e = e;
   }

   @Override
   public String getImageName() {
      return "taunt";
   }

   @Override
   protected void onHit(EntState source, EntState self, Snapshot snapshot, int damage, int damageTakenThisTurn, Eff sourceEff, Targetable targetable) {
      if (damage > 0 && source.getEnt() != self.getEnt()) {
         source.addEvent(PanelHighlightEvent.painMirror);
         self.addEvent(PanelHighlightEvent.painMirror);
         if (this.e.needsTarget()) {
            source.hit(this.e, self.getEnt());
         } else {
            snapshot.untargetedUse(this.e, self.getEnt());
         }
      }
   }

   @Override
   protected String describeExtra() {
      String s;
      if (this.e.needsTarget()) {
         s = "the attacker: " + this.e.describe();
      } else {
         s = this.e.describe();
      }

      return s.replace("Target ally:[n]", "").replace("Another hero ", "");
   }
}
