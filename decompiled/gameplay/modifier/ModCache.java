package com.tann.dice.gameplay.modifier;

import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModCache {
   private static final int MAX_CACHE = 1000;
   private List<Modifier> genCache = new ArrayList<>();
   private List<Modifier> wildCache = new ArrayList<>();
   private static Set<String> nameCollCache = new HashSet<>();
   static final boolean print = false;

   public List<Modifier> get(int amt, boolean wild, Integer min, Integer max, long collisionBit) {
      if (amt == 0) {
         return new ArrayList<>();
      } else {
         List<Modifier> c = new ArrayList<>(wild ? this.wildCache : this.genCache);
         if (c.size() < 1000 && c.size() < amt * 5) {
            this.addMoreToCache(wild);
         }

         Collections.shuffle(c);
         List<Modifier> result = new ArrayList<>();
         nameCollCache.clear();

         for (int i = 0; i < c.size(); i++) {
            Modifier p = c.get(i);
            if (ModifierLib.isWithin(p, min, max) && !ChoosableUtils.collides(p, collisionBit) && !nameCollCache.contains(p.getName())) {
               nameCollCache.add(p.getName());
               result.add(p);
               if (result.size() == amt) {
                  break;
               }
            }
         }

         if (result.size() < amt) {
            if (Tann.chance(0.1F)) {
               this.addMoreToCache(wild);
            }

            return this.get(amt, wild, min, max, collisionBit);
         } else {
            return result;
         }
      }
   }

   private void decache(Modifier m) {
      this.genCache.remove(m);
      this.wildCache.remove(m);
   }

   public void decache(List<Modifier> result) {
      for (Modifier modifier : result) {
         this.decache(modifier);
      }
   }

   public void decacheChoosables(List<Choosable> result) {
      for (int i = 0; i < result.size(); i++) {
         if (result.get(i) instanceof Modifier) {
            this.decache((Modifier)result.get(i));
         }
      }
   }

   private void addMoreToCache(boolean wild) {
      long time = System.currentTimeMillis();
      List<Modifier> list = wild ? this.wildCache : this.genCache;
      if (list.size() > 1000) {
         if (!Tann.chance(0.02F)) {
            return;
         }

         list.clear();
      }

      int amtToAdd = list.isEmpty() ? 50 : 50;
      list.addAll(PipeMod.makeGenerated(amtToAdd, null, wild));
      long delta = System.currentTimeMillis() - time;
   }

   public void clear() {
      this.genCache.clear();
      this.wildCache.clear();
   }
}
