package com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.command.AbilityCommand;
import com.tann.dice.gameplay.fightLog.command.DieCommand;
import com.tann.dice.gameplay.fightLog.command.TargetableCommand;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum EnumConditionalRequirement implements ConditionalRequirement {
   Always(true),
   Bigger(false),
   TargetTargetingMe(false),
   SelfTarget(false),
   PreviousDiceIdentical(true),
   PreviousAbilitySharesKeyword(true),
   PreviousAbilitySame(true),
   PreviousTwoAbilitiesSame(true),
   PreviousFourAbilitiesSame(true),
   PreviousSixAbilitiesSame(true),
   PreviousAbilityHigher(true),
   PreviousAbilitySameTarget(false),
   SixthDiceUsed(true),
   RunOfTwo(true),
   RunOfThree(true),
   RunOfFive(true),
   NoOtherDice(true),
   JustCastASpell(true),
   MorePipsThanTarget,
   FewerPipsThanTarget,
   MoreBlanksThanTarget,
   MoreHpThanTarget,
   SameHpAsTarget,
   CoprimeWithTarget,
   LessHpThanTarget,
   UnusedLastTurn(true),
   Top;

   public final boolean preCalculate;

   private EnumConditionalRequirement() {
      this(false);
   }

   private EnumConditionalRequirement(boolean preCalculate) {
      this.preCalculate = preCalculate;
   }

   public static EnumConditionalRequirement previousNSame(int n) {
      switch (n) {
         case 1:
            return PreviousAbilitySame;
         case 2:
            return PreviousTwoAbilitiesSame;
         case 3:
         case 5:
         default:
            throw new RuntimeException("Invalid N same:" + n);
         case 4:
            return PreviousFourAbilitiesSame;
         case 6:
            return PreviousSixAbilitiesSame;
      }
   }

   @Override
   public boolean isValid(Snapshot s, EntState sourceState, EntState targetState, Eff eff) {
      switch (this) {
         case TargetTargetingMe:
            if (targetState == null) {
               return false;
            }

            List<Ent> targeters = s.getAllTargeters(sourceState.getEnt(), true);
            return targeters.contains(targetState.getEnt());
         case Top:
            return s.getStates(targetState.isPlayer(), false).indexOf(targetState) == 0;
         case Bigger:
            return targetState.getEnt().getSize().getPixels() > sourceState.getEnt().getSize().getPixels();
         case MoreHpThanTarget:
            return sourceState.getHp() > targetState.getHp();
         case SameHpAsTarget:
            return sourceState.getHp() == targetState.getHp();
         case CoprimeWithTarget:
            return Tann.gcd(sourceState.getHp(), targetState.getHp()) == 1;
         case LessHpThanTarget:
            return sourceState.getHp() < targetState.getHp();
         case Always:
            return true;
         case SelfTarget:
            return sourceState == targetState;
         case PreviousDiceIdentical:
         case NoOtherDice:
         case PreviousAbilitySharesKeyword:
         case PreviousAbilityHigher:
         case PreviousAbilitySameTarget:
            FightLog f = s.getFightLog();
            DieCommand lastDieCommand = f.getDieCommandFromAtOrBeforeSnapshot(s);
            if (lastDieCommand == null) {
               return this == NoOtherDice;
            } else {
               Eff contempEffect = f.getContemporaneousEffect(lastDieCommand);
               if (contempEffect == null) {
                  return false;
               } else if (this == PreviousAbilityHigher) {
                  return contempEffect.getValue() > eff.getValue();
               } else if (this == PreviousAbilitySharesKeyword) {
                  return Tann.anySharedItems(contempEffect.getKeywords(), eff.getKeywords());
               } else {
                  if (this != PreviousAbilitySameTarget) {
                     if (this == PreviousDiceIdentical) {
                        return contempEffect.sameAs(eff);
                     }

                     return false;
                  }

                  return targetState != null
                     && (
                        targetState.getEnt() == lastDieCommand.target
                           || contempEffect.getTargetingType() == TargetingType.Self && targetState.getEnt() == lastDieCommand.getSource()
                     );
               }
            }
         case PreviousAbilitySame:
            return lastNDiceValue(eff.getValue(), 1, s, sourceState.getEnt());
         case PreviousTwoAbilitiesSame:
            return lastNDiceValue(eff.getValue(), 2, s, sourceState.getEnt());
         case PreviousFourAbilitiesSame:
            return lastNDiceValue(eff.getValue(), 4, s, sourceState.getEnt());
         case PreviousSixAbilitiesSame:
            return lastNDiceValue(eff.getValue(), 6, s, sourceState.getEnt());
         case RunOfTwo:
            return this.lastNDiceStraight(eff.getValue(), 2, s);
         case RunOfThree:
            return this.lastNDiceStraight(eff.getValue(), 3, s);
         case RunOfFive:
            return this.lastNDiceStraight(eff.getValue(), 5, s);
         case SixthDiceUsed:
            return getNumDiceUsed(s) == 5;
         case JustCastASpell:
            TargetableCommand lastTargetableCommand = s.getPreviousCommandOfType(TargetableCommand.class);
            return lastTargetableCommand instanceof AbilityCommand;
         case UnusedLastTurn:
            return sourceState.getSnapshot().getTurn() > 1 && sourceState.getSnapshot().getSideIndicesFromTurnsAgoAndEnt(1, sourceState.getEnt()).isEmpty();
         case MorePipsThanTarget:
         case FewerPipsThanTarget:
            if (sourceState != null && targetState != null) {
               EntSide mySide = sourceState.getEnt().getDie().getCurrentSide();
               EntSide theirSide = targetState.getEnt().getDie().getCurrentSide();
               if (mySide != null && theirSide != null) {
                  boolean more = this == MorePipsThanTarget;
                  int myVal = sourceState.getSideState(mySide).getCalculatedEffect().getValue();
                  int theirVal = targetState.getSideState(theirSide).getCalculatedEffect().getValue();
                  if (more) {
                     return myVal > theirVal;
                  }

                  return theirVal > myVal;
               }

               return false;
            }

            return false;
         case MoreBlanksThanTarget:
            if (sourceState != null && targetState != null) {
               int blanksDelta = 0;

               for (EntSideState ss : sourceState.getAllSideStates()) {
                  if (ss.getCalculatedEffect().getType() == EffType.Blank) {
                     blanksDelta++;
                  }
               }

               for (EntSideState ssx : targetState.getAllSideStates()) {
                  if (ssx.getCalculatedEffect().getType() == EffType.Blank) {
                     blanksDelta--;
                  }
               }

               return blanksDelta > 1;
            }

            return false;
         default:
            throw new RuntimeException(this + "(reqs)");
      }
   }

   private static int getNumDiceUsed(Snapshot s) {
      return s.getNumDiceUsedThisTurn();
   }

   private static boolean lastNDiceValue(int value, int N, Snapshot s, Ent source) {
      for (int i = 0; i < N; i++) {
         DieCommand lastButOne = s.getPreviousCommandOfType(i, DieCommand.class);
         if (lastButOne == null) {
            return false;
         }

         Eff contempEffect = s.getFightLog().getContemporaneousEffect(lastButOne);
         if (contempEffect == null) {
            return false;
         }

         if (contempEffect.getValue() != value) {
            return false;
         }
      }

      return true;
   }

   private boolean lastNDiceStraight(int value, int ln, Snapshot s) {
      List<Integer> vals = new ArrayList<>();

      for (int i = 0; i < ln - 1; i++) {
         DieCommand lastButOne = s.getPreviousCommandOfType(i, DieCommand.class);
         if (lastButOne == null) {
            return false;
         }

         Eff contempEffect = s.getFightLog().getContemporaneousEffect(lastButOne);
         if (contempEffect == null) {
            return false;
         }

         vals.add(contempEffect.getValue());
      }

      if (vals.size() != ln - 1) {
         return false;
      } else {
         Collections.reverse(vals);
         vals.add(value);
         return Tann.formsStraight(vals);
      }
   }

   @Override
   public boolean preCalculate() {
      return this.preCalculate;
   }

   @Override
   public String getInvalidString(Eff eff) {
      return "invalid target?";
   }

   @Override
   public String describe(Eff eff) {
      return null;
   }

   @Override
   public String getBasicString() {
      switch (this) {
         case Top:
            return "top";
         default:
            return "?!" + this.name();
      }
   }

   @Override
   public Actor getRestrictionActor() {
      return new Pixl().text(this.getBasicString()).pix();
   }

   @Override
   public boolean isPlural() {
      return false;
   }
}
