package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.EntDieUtils;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Tann;
import java.util.List;

public class ReplaceWithOtherHeroSideInOrder extends AffectSideEffect {
   final int sideIndex;

   public ReplaceWithOtherHeroSideInOrder(int sideIndex) {
      this.sideIndex = sideIndex;
   }

   @Override
   public String describe() {
      return "unimp " + this.getClass().getSimpleName();
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      return "Replace my sides with the "
         + this.descSideIndex()
         + " base sides of other heroes[n][grey](in petrify order from top to bottom) [col222](before items)";
   }

   private String descSideIndex() {
      return EntDieUtils.fromIndex(this.sideIndex).name().toLowerCase();
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      if (owner.getSnapshot() != null) {
         int ownerLvel = -5;
         HeroCol ownerCol = null;
         if (owner.getEnt() instanceof Hero) {
            Hero ownero = (Hero)owner.getEnt();
            ownerLvel = ownero.getLevel();
            ownerCol = ownero.getHeroCol();
         }

         List<Ent> ents;
         if (owner.hasOverrideGlobals()) {
            PhaseManager pm = PhaseManager.get();
            if (pm == null) {
               return;
            }

            Phase p = pm.getPhase();
            int foundIndex = -1;
            if (p instanceof ChoicePhase) {
               ChoicePhase cp = (ChoicePhase)p;
               List<Choosable> options = cp.getOptions();

               for (int i = 0; i < options.size(); i++) {
                  Choosable option = options.get(i);
                  if (option instanceof LevelupHeroChoosable) {
                     LevelupHeroChoosable lu = (LevelupHeroChoosable)option;
                     if (lu.getHeroType() == owner.getEnt().getEntType()) {
                        foundIndex = i;
                     }
                  }
               }
            }

            if (foundIndex == -1) {
               return;
            }

            ents = owner.getSnapshot().getEntities(true, null);
            if (foundIndex >= ents.size()) {
               return;
            }

            for (Ent ent : ents) {
               if (ent.getName(false).contains("ousecat")) {
                  return;
               }
            }

            for (int ix = 0; ix < ents.size(); ix++) {
               Ent entx = ents.get(ix);
               if (entx instanceof Hero) {
                  Hero h = (Hero)entx;
                  if (h.getLevel() == ownerLvel - 1 && h.getHeroCol() == ownerCol) {
                     if (foundIndex == 0) {
                        ents.remove(ix);
                     }

                     foundIndex--;
                  }
               }
            }

            if (foundIndex != -1) {
               for (int ixx = 0; ixx < ents.size(); ixx++) {
                  Ent entx = ents.get(ixx);
                  if (entx instanceof Hero) {
                     Hero h = (Hero)entx;
                     if (h.getLevel() == ownerLvel - 1 && h.getHeroCol() == ownerCol) {
                        ents.remove(ixx);
                        break;
                     }
                  }
               }
            }
         } else {
            ents = owner.getSnapshot().getEntities(true, null);
         }

         int actualHeroIndex = Tann.indexOf(SpecificSidesType.PetrifyOrder.sideIndices, sideState.getIndex());
         if (actualHeroIndex != -1) {
            Ent copyFrom = this.getIndexedExcept(ents, actualHeroIndex, owner.getEnt());
            if (copyFrom == null) {
               ReplaceWith.replaceSide(sideState, owner.getEnt().getSize().getBlank());
            } else {
               EntSide rep = copyFrom.getSides()[this.sideIndex];
               ReplaceWith.replaceSide(sideState, rep.copy());
            }
         }
      }
   }

   private Ent getIndexedExcept(List<Ent> entities, int requestedIndex, Ent ent) {
      if (requestedIndex > entities.size()) {
         return null;
      } else {
         int counter = 0;

         for (Ent entity : entities) {
            if (entity != ent) {
               if (counter == requestedIndex) {
                  return entity;
               }

               counter++;
            }
         }

         return null;
      }
   }

   @Override
   public String getImageName() {
      return "mimic";
   }
}
