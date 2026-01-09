package com.tann.dice.gameplay.modifier;

import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum ModifierPickContext {
   Cursed,
   Difficulty,
   Difficulty_But_Midgame,
   Trade,
   Dificulty_Allow_T1;

   final Map<String, List<Modifier>> cache = new HashMap<>();

   private List<Modifier> getStartCopy(Integer min, Integer max) {
      String key = min + ":" + max;
      if (this.cache.get(key) == null) {
         this.cache.put(key, this.makeStart(min, max));
      }

      return new ArrayList<>(this.cache.get(key));
   }

   private List<Modifier> makeStart(Integer min, Integer max) {
      List<Modifier> result = new ArrayList<>(ModifierLib.getAll(ModifierType.fromTier(min, max)));

      for (int i = result.size() - 1; i >= 0; i--) {
         Modifier m = result.get(i);
         if (UnUtil.isLocked(m) || m == PipeMod.getMissingno() || !ModifierLib.isWithin(m, min, max)) {
            result.remove(m);
         }
      }

      switch (this) {
         case Difficulty:
            for (int ixx = result.size() - 1; ixx >= 0; ixx--) {
               Modifier potential = result.get(ixx);
               if (potential.getTier() == -1 && Math.random() > 0.2) {
                  result.remove(potential);
               }
            }
            break;
         case Difficulty_But_Midgame:
         case Trade:
            for (int ix = result.size() - 1; ix >= 0; ix--) {
               Modifier potential = result.get(ix);
               if (potential.isOnPick()
                  || potential.allLevelsOnly()
                  || Collision.collides(potential.getCollisionBits(), Collision.SPECIFIC_LEVEL_WIDE | Collision.PHASE)) {
                  result.remove(ix);
               }
            }
      }

      return result;
   }

   public List<Modifier> getBase(List<Modifier> currentModifiers, Integer min, Integer max, Integer amt, long collisionBit) {
      List<Modifier> result = this.getStartCopy(min, max);
      Collections.shuffle(result);
      float rarityRoll = Tann.random();
      Set<String> currentModifierEssences = new HashSet<>();

      for (int i = 0; i < currentModifiers.size(); i++) {
         String ess = currentModifiers.get(i).getEssence();
         if (ess != null) {
            currentModifierEssences.add(ess);
         }
      }

      for (int ix = result.size() - 1; ix >= 0; ix--) {
         Modifier m = result.get(ix);
         if (m.chance() < rarityRoll
            || currentModifiers.contains(m)
            || ChoosableUtils.collides(m, collisionBit)
            || currentModifierEssences.contains(m.getEssence())) {
            result.remove(m);
         }
      }

      switch (this) {
         case Cursed:
            for (int ixxx = 0; ixxx < currentModifiers.size(); ixxx++) {
               Modifier existingModifier = currentModifiers.get(ixxx);
               if (max > 0 == existingModifier.getTier() > 0) {
                  Modifier upgradedModifier = ModifierUtils.someNextInChain(min, max, existingModifier);
                  if (upgradedModifier != null) {
                     result.add(upgradedModifier);
                  }
               }
            }
         default:
            Collections.shuffle(result);
            if (amt != null) {
               result = Tann.minList(result, amt);
            }

            boolean tweak = min != null && max != null && min == 0 && max == 0;
            boolean addGeneratedWild = !tweak;
            if (min != null && max != null && Math.abs(max - min) < 2 && Math.abs(max) > 7) {
               addGeneratedWild = false;
            }

            int actualAmt = result.size();
            if (addGeneratedWild) {
               Set<String> currentNames = new HashSet<>();

               for (int ixx = 0; ixx < currentModifiers.size(); ixx++) {
                  currentNames.add(currentModifiers.get(ixx).name);
               }

               for (boolean wild : Tann.BOTH) {
                  if (!wild || OptionLib.WILD_MODIFIERS.c()) {
                     int amtToGen;
                     if (wild) {
                        amtToGen = Tann.randomRound(actualAmt * OptionUtils.genChance());
                     } else {
                        amtToGen = Tann.randomRound(actualAmt * 0.21F);
                     }

                     List<Modifier> gennd = ModifierLib.getCache().get(amtToGen, wild, min, max, collisionBit);

                     for (int ixx = gennd.size() - 1; ixx >= 0; ixx--) {
                        if (currentNames.contains(gennd.get(ixx).getName())) {
                           gennd.remove(ixx);
                        }
                     }

                     result.addAll(gennd);
                  }
               }

               if (amt != null) {
                  Collections.reverse(result);
                  result = Tann.minList(result, amt);
               }
            }

            return result;
      }
   }
}
