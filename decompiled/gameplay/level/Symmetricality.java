package com.tann.dice.gameplay.level;

import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Symmetricality {
   public static void sort(final List<MonsterType> originalList) {
      Set<MonsterType> numTypes = new HashSet<>(originalList);
      if (numTypes.size() != originalList.size()) {
         List<MonsterType> sortedTypes = new ArrayList<>(new HashSet<>(originalList));
         Collections.sort(sortedTypes, new Comparator<MonsterType>() {
            public int compare(MonsterType o1, MonsterType o2) {
               boolean even1 = Tann.countInList(o1, originalList) % 2 == 0;
               boolean even2 = Tann.countInList(o2, originalList) % 2 == 0;
               if (even1 != even2) {
                  return even1 ? 1 : -1;
               } else {
                  int diff = Tann.countInList(o1, originalList) - Tann.countInList(o2, originalList);
                  return diff != 0 ? diff : o2.size.getReinforceSize() - o1.size.getReinforceSize();
               }
            }
         });
         List<MonsterType> finalList = new ArrayList<>();

         for (MonsterType mt : sortedTypes) {
            int totalInList = Tann.countInList(mt, originalList);
            int leftHalf = (totalInList + (int)(Math.random() * 2.0)) / 2;
            int rightHalf = totalInList - leftHalf;
            Tann.addMultiple(finalList, mt, leftHalf, false);
            Tann.addMultiple(finalList, mt, rightHalf, true);
         }

         if (finalList.size() != originalList.size()) {
            throw new RuntimeException("Failed to sort list: " + originalList);
         } else {
            for (int i = 0; i < finalList.size(); i++) {
               originalList.set(i, finalList.get(i));
            }
         }
      }
   }
}
