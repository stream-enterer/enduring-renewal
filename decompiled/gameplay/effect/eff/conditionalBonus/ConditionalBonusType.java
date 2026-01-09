package com.tann.dice.gameplay.effect.eff.conditionalBonus;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import java.util.List;

public enum ConditionalBonusType {
   Add,
   Multiply,
   Divide,
   MyShields,
   FriendlyCrosses,
   DamagedEnemies,
   IncomingDamage,
   DeadAllies,
   NumKeywords,
   BagItems,
   CurrentMana,
   CurrentTurn,
   CurrentHP,
   TheirPoison,
   UnusedAllies,
   HealingReceived,
   MyTier,
   AbilitiesUsed,
   TotalPoison,
   MyPoison,
   ElapsedTurns,
   TimesUsedThisTurn,
   ItemsEquipped,
   TotalItemTier,
   Buffs,
   Triggers;

   public int getBonus(Snapshot s, EntState sourceState, EntState targetState, int bonusAmount, int value, Eff eff) {
      switch (this) {
         case MyShields:
            return sourceState.getShields() * bonusAmount;
         case FriendlyCrosses:
            throw new RuntimeException("friendly crosses");
         case Add:
            return bonusAmount;
         case Divide:
            return -value + (int)(value * (1.0F / bonusAmount));
         case Multiply:
            int ba = bonusAmount - 1;
            if (bonusAmount == 2 && eff.hasKeyword(Keyword.treble)) {
               ba++;
            }

            return value * ba;
         case IncomingDamage:
            return sourceState.getIncomingDamage();
         case DamagedEnemies:
            int damaged = 0;

            for (EntState esx : s.getStates(!sourceState.getEnt().isPlayer(), false)) {
               if (esx.isDamaged()) {
                  damaged++;
               }
            }

            return damaged;
         case DeadAllies:
            return s.getStates(sourceState.isPlayer(), true).size() * bonusAmount;
         case UnusedAllies:
            int total = 0;

            for (EntState es : s.getStates(sourceState.isPlayer(), false)) {
               if (!es.isUsed()) {
                  total++;
               }
            }

            return Math.max(0, total - 1);
         case MyPoison:
            return sourceState.getBasePoisonPerTurn();
         case TotalPoison:
            int total = 0;

            for (EntState es : s.getStates(null, false)) {
               total += es.getBasePoisonPerTurn();
            }

            return total;
         case HealingReceived:
            return sourceState.getHealingThisTurn();
         case NumKeywords:
            return eff.getKeywords().size();
         case BagItems:
            return s.getFightLog().getContext().getParty().getItems(false).size();
         case CurrentMana:
            return s.getTotalMana();
         case CurrentTurn:
            return s.getTurn();
         case ElapsedTurns:
            return sourceState.getTurnsElapsed();
         case CurrentHP:
            return sourceState.getHp();
         case TheirPoison:
            if (targetState == null) {
               return 0;
            }

            return targetState.getBasePoisonPerTurn();
         case AbilitiesUsed:
            return s.getTotalAbilitiesUsedThisTurn();
         case MyTier:
            Ent de = sourceState.getEnt();
            if (de instanceof Hero) {
               Hero h = (Hero)de;
               return h.getLevel();
            }

            return 0;
         case TimesUsedThisTurn:
            return s.getNumDiceUsedThisTurn(sourceState.getEnt());
         case ItemsEquipped:
            return sourceState.getEnt().getItems().size();
         case TotalItemTier:
            List<Item> its = sourceState.getEnt().getItems();
            int tot = 0;

            for (Item it : its) {
               tot += it.getTier();
            }

            return tot;
         case Buffs:
            return sourceState.numBuffs();
         case Triggers:
            return sourceState.getActivePersonals().size();
         default:
            throw new RuntimeException("unimplemented bonus type " + this);
      }
   }
}
