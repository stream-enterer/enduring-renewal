package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.HpGrid;
import com.tann.dice.util.ui.TextWriter;

public enum TargetingRestriction implements ConditionalRequirement {
   LessOrEqualHpThanMe,
   MostHealth,
   TwoMostDamaged,
   AllMostDamaged,
   LeastHp,
   OrLessHp,
   NotMe,
   ExactlyValue,
   ExactlyValuePicky;

   @Override
   public String getBasicString() {
      switch (this) {
         case AllMostDamaged:
            return "most-damaged";
         case MostHealth:
            return "most-hp";
         case TwoMostDamaged:
            return "two most-damaged";
         case LeastHp:
            return "least-hp";
         default:
            return "unset basic string for" + this;
      }
   }

   @Override
   public String describe(Eff eff) {
      switch (this) {
         case AllMostDamaged:
            return "to the most-damaged " + Words.entName(eff.isFriendlyForce(), null);
         case MostHealth:
            return "with the most hp";
         case TwoMostDamaged:
            return "to the two most-damaged " + Words.entName(eff.isFriendlyForce(), null);
         case LeastHp:
            return "with the least HP";
         case LessOrEqualHpThanMe:
            return "with equal or less hp than me";
         case OrLessHp:
            return "with " + eff.getValue() + " or less hp";
         case ExactlyValuePicky:
            return "with exactly " + KUtils.getValue(eff) + " hp";
         case ExactlyValue:
            return "with exactly " + eff.getValue() + " hp";
         default:
            return "unknown targeting restriction " + this;
      }
   }

   @Override
   public String getInvalidString(Eff eff) {
      switch (this) {
         case AllMostDamaged:
         case TwoMostDamaged:
         case LeastHp:
         case ExactlyValuePicky:
         case ExactlyValue:
         default:
            return "Invalid due to targeting restriciton";
         case MostHealth:
            return "Target must have the most hp";
         case LessOrEqualHpThanMe:
            return "Target must not have more hp than me";
         case OrLessHp:
            return "Target must have " + eff.getValue() + " or less hp";
         case NotMe:
            return "Cannot target myself";
      }
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      switch (this) {
         case AllMostDamaged:
            return targetState.isDamaged() && targetState.getSnapshot().isMostDamaged(targetState);
         case MostHealth:
         case LeastHp:
            boolean most = this == MostHealth;
            int mostHp = most ? 0 : 5000;
            boolean ranged = eff == null || eff.hasKeyword(Keyword.ranged);

            for (EntState es : s.getAliveEntStates(targetState.getEnt().isPlayer())) {
               if (ranged || es.canBeTargetedAsForwards()) {
                  if (most) {
                     mostHp = Math.max(es.getHp(), mostHp);
                  } else {
                     mostHp = Math.min(es.getHp(), mostHp);
                  }
               }
            }

            return targetState.getHp() == mostHp;
         case TwoMostDamaged:
            boolean sourcePlayer = sourceState == null || sourceState.isPlayer();
            return s.getMostDamagedEnt(sourcePlayer, targetState.isPlayer()) == targetState
               || s.getMostDamagedEnt(sourcePlayer, targetState.isPlayer(), 1) == targetState;
         case LessOrEqualHpThanMe:
            return targetState.getHp() <= sourceState.getHp();
         case OrLessHp:
            int val = targetState.getValueIncludingConditionalBonuses(eff, sourceState);
            return targetState.getHp() <= val;
         case ExactlyValuePicky:
            return KUtils.getValue(eff) == targetState.getHp();
         case ExactlyValue:
            return eff.getValue() == targetState.getHp();
         case NotMe:
            return targetState != sourceState;
         default:
            throw new RuntimeException("Unimplemented restriction: " + this);
      }
   }

   @Override
   public boolean preCalculate() {
      return false;
   }

   @Override
   public Actor getRestrictionActor() {
      switch (this) {
         case AllMostDamaged:
            return new Pixl().text("[grey]max").row(1).actor(HpGrid.make(0, 0, 0, 3)).pix();
         case MostHealth:
            return new Pixl().text("[grey]max").row(1).actor(HpGrid.make(3, 0, 0, 3)).pix();
         case TwoMostDamaged:
         default:
            return new TextWriter("[text]" + this.getBasicString(), 80);
         case LeastHp:
            return new Pixl().text("[grey]min").row(1).actor(HpGrid.make(3, 0, 0, 4)).pix();
      }
   }

   @Override
   public boolean isPlural() {
      return true;
   }
}
