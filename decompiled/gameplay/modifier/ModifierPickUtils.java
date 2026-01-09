package com.tann.dice.gameplay.modifier;

import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierPickUtils {
   static final int safeguardMult = 7;

   public static List<Modifier> getModifiersAddingUpTo(int amt, int addUpTo, ModifierPickContext modifierPickContext, boolean wildcard, DungeonContext dc) {
      List<Modifier> avoid = new ArrayList<>(dc.getCurrentModifiers());
      avoid.addAll(dc.getAvoidModifiers());
      return getModifiersAddingUpTo(amt, addUpTo, modifierPickContext, wildcard, avoid, new ArrayList<>(), dc.getBannedCollisionBits());
   }

   private static List<Modifier> getModifiersAddingUpTo(
      int amt, int addUpTo, ModifierPickContext modifierPickContext, boolean wildcard, List<Modifier> avoid, List<Modifier> seen, long collisionBit
   ) {
      List<List<Modifier>> modLists = new ArrayList<>();
      List<Modifier> chosen = null;
      int min;
      int max;
      if (addUpTo < 0) {
         min = addUpTo;
         max = -1;
      } else if (addUpTo > 0) {
         min = 1;
         max = addUpTo;
      } else {
         min = -4;
         max = 4;
         if (addUpTo != 0) {
            TannLog.error("hmm minmax " + addUpTo);
         }
      }

      int amtWildcard = 0;
      if (wildcard) {
         amtWildcard = Tann.randomRound(amt / 8.0F);
      }

      int META_ATTEMPTS = 5;
      int INNER_ATTEMPTS = 100;
      int pn = 0;

      for (int metaAttempt = 0; metaAttempt < 5; metaAttempt++) {
         List<Modifier> totalBase = modifierPickContext.getBase(new ArrayList<>(), min, max, amt * 7, collisionBit);

         for (int attempt = 0; attempt < 100; attempt++) {
            List<Modifier> chooseFrom = new ArrayList<>(totalBase);
            Collections.shuffle(chooseFrom);
            int numNormal = amt - amtWildcard;
            List<Modifier> potentialList = Tann.getSelectiveRandom(chooseFrom, numNormal, ModifierLib.getMissingno(), avoid, seen);
            if (amtWildcard > 0) {
               long currentCollision = collisionBit;

               for (int i = 0; i < potentialList.size(); i++) {
                  Modifier m = potentialList.get(i);
                  currentCollision |= m.getCollisionBits();
               }

               potentialList.addAll(generateModifiers(-max, -min, amtWildcard, modifierPickContext, avoid, seen, currentCollision));
            }

            if (!anyMissingno(potentialList)) {
               PermStats perm = PermStats.make(addUpTo, potentialList);
               if (perm == null) {
                  pn++;
               } else {
                  if (perm.isFine()) {
                     chosen = potentialList;
                     break;
                  }

                  modLists.add(potentialList);
               }
            }
         }

         if (chosen != null || modLists.size() > 10) {
            break;
         }
      }

      final Map<List<Modifier>, PermStats> cachedPermStats = new HashMap<>();

      for (List<Modifier> modList : modLists) {
         cachedPermStats.put(modList, PermStats.make(addUpTo, modList));
      }

      if (chosen == null) {
         TannLog.error("Failed to find modifiers, resorting to fallback " + pn + ":" + modLists.size());
         if (modLists.isEmpty()) {
            return getFailsafeModifierOffer(amt, addUpTo, modifierPickContext, collisionBit);
         }

         Collections.sort(modLists, new Comparator<List<Modifier>>() {
            public int compare(List<Modifier> offer1, List<Modifier> offer2) {
               return PermStats.COMPARE(cachedPermStats.get(offer1), cachedPermStats.get(offer2));
            }
         });
         chosen = modLists.get(0);
         PermStats a = PermStats.make(addUpTo, chosen);
         TannLog.log("After sorting picked offer " + a);
      }

      Collections.sort(chosen, new Comparator<Modifier>() {
         public int compare(Modifier o1, Modifier o2) {
            int o1v = Math.abs(o1.getTier());
            int o2v = Math.abs(o2.getTier());
            return o1v != o2v ? o2v - o1v : o1.getName().compareTo(o2.getName());
         }
      });
      ModifierLib.getCache().decache(chosen);
      return chosen;
   }

   private static boolean anyMissingno(List<Modifier> lszt) {
      for (int i = 0; i < lszt.size(); i++) {
         if (lszt.get(i).isMissingno()) {
            return true;
         }
      }

      return false;
   }

   public static List<Modifier> generateModifiers(int tier, int amt, ModifierPickContext context, DungeonContext dc) {
      return generateModifiers(tier, tier, amt, context, dc);
   }

   public static List<Modifier> generateModifiers(Integer min, Integer max, int amt, ModifierPickContext context, DungeonContext dc) {
      List<Modifier> currentPlusAvoid = new ArrayList<>(dc.getCurrentModifiers());
      currentPlusAvoid.addAll(dc.getAvoidModifiers());
      return generateModifiers(min, max, amt, context, currentPlusAvoid, dc.makeSeenModifiers(), dc.getBannedCollisionBits());
   }

   private static List<Modifier> generateModifiers(
      Integer min, Integer max, int amt, ModifierPickContext context, List<Modifier> current, List<Modifier> seen, long collisionBit
   ) {
      if (min != null && max != null && min > max) {
         throw new RuntimeException("Invalid args: " + min + "/" + max);
      } else {
         List<Modifier> base = context.getBase(current, min, max, amt * 7, collisionBit);
         List<Modifier> result = Tann.getSelectiveRandom(base, amt, ModifierLib.getMissingno(), current, seen);
         replaceMissingnoWithGenerated(result, min, max, current);
         return result;
      }
   }

   private static void replaceMissingnoWithGenerated(List<Modifier> result, Integer min, Integer max, List<Modifier> current) {
      boolean anyMiss = false;

      for (int i = 0; i < result.size(); i++) {
         anyMiss |= result.get(i).isMissingno();
      }

      if (anyMiss) {
         List<String> cNames = new ArrayList<>();

         for (int i = 0; i < current.size(); i++) {
            cNames.add(current.get(i).getName());
         }

         for (int i = 0; i < result.size(); i++) {
            if (result.get(i).isMissingno()) {
               Modifier m = makeReplacement(result, cNames, min, max);
               if (m != null) {
                  result.set(i, m);
               }
            }
         }
      }
   }

   private static Modifier makeReplacement(List<Modifier> result, List<String> current, Integer min, Integer max) {
      int attempts = 1000;

      for (int i = 0; i < attempts; i++) {
         List<Modifier> lst = PipeMod.makeGenerated(1, min, max, Tann.chance(0.2F));
         if (!lst.isEmpty()) {
            Modifier m = lst.get(0);
            if (!result.contains(m) && !current.contains(m.getName())) {
               return m;
            }
         }
      }

      return null;
   }

   public static List<Modifier> getFailsafeModifierOffer(int amt, int addUpTo, ModifierPickContext modifierPickContext, long collisionBit) {
      float delta = Math.abs(addUpTo * 0.3F);
      int tier = addUpTo / 2;
      List<Modifier> result = new ArrayList<>(
         generateModifiers(
            Tann.randomRound(tier - delta), Tann.randomRound(tier + delta), amt, modifierPickContext, new ArrayList<>(), new ArrayList<>(), collisionBit
         )
      );

      for (int i = 0; i < result.size(); i++) {
         Modifier m = result.get(i);
         if (!m.isMissingno()) {
            String n = "(" + m.getName() + ").mn.Gen Fail Random";
            result.set(i, ModifierLib.byName(n));
         }
      }

      return result;
   }
}
