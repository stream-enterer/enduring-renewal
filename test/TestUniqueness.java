package com.tann.dice.test;

import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierType;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.gameplay.trigger.global.spell.GlobalLearnSpell;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.spell.learn.LearnSpell;
import com.tann.dice.test.util.Skip;
import com.tann.dice.test.util.Test;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TestUniqueness {
   @Test
   public static void modCounts() {
      Tann.assertEquals(
         "Should be same number",
         ModifierLib.getAll().size(),
         ModifierLib.getAll(ModifierType.Tweak).size() + ModifierLib.getAll(ModifierType.Blessing).size() + ModifierLib.getAll(ModifierType.Curse).size()
      );
   }

   @Test
   public static void testModifierUniqueness() {
      testUniqueness(ModifierLib.getAll((Boolean)null), new TestUniqueness.Stringifier<Modifier>() {
         public String getString(Modifier input) {
            return input.getFullDescription();
         }
      }, new TestUniqueness.Skipifier<Modifier>() {
         public boolean skip(Modifier input) {
            for (Global gt : input.getGlobals()) {
               if (gt instanceof GlobalStartWithItem || gt instanceof GlobalLearnSpell) {
                  return true;
               }
            }

            return false;
         }
      });
   }

   @Test
   @Skip
   public static void testItemUniqueness() {
      testUniqueness(ItemLib.getMasterCopy(), new TestUniqueness.Stringifier<Item>() {
         public String getString(Item input) {
            return input.getDescription();
         }
      }, new TestUniqueness.Skipifier<Item>() {
         public boolean skip(Item input) {
            for (Personal pt : input.getPersonals()) {
               if (pt instanceof LearnSpell) {
                  return true;
               }
            }

            String nf = input.getName(false);
            if (!nf.contains("of Spades") && !nf.contains("of Clubs")) {
               String desc = input.getDescription();
               return desc.startsWith("Change ") || desc.startsWith("[notranslate]Change ");
            } else {
               return true;
            }
         }
      });
   }

   private static <T> void testUniqueness(List<T> toCheck, TestUniqueness.Stringifier<T> stringifier) {
      testUniqueness(toCheck, stringifier, new TestUniqueness.Skipifier<T>() {
         @Override
         public boolean skip(T input) {
            return false;
         }
      });
   }

   private static <T> void testUniqueness(List<T> toCheck, TestUniqueness.Stringifier<T> stringifier, TestUniqueness.Skipifier<T> skipifier) {
      List<String> descriptions = new ArrayList<>();
      List<T> failed = new ArrayList<>();

      for (T t : toCheck) {
         if (!skipifier.skip(t)) {
            String desc = stringifier.getString(t);
            if (descriptions.contains(desc)) {
               for (int i = 0; i < toCheck.size(); i++) {
                  T t2 = toCheck.get(i);
                  if (Objects.equals(stringifier.getString(t2), desc) && t2 != t) {
                     failed.add(t2);
                  }
               }

               failed.add(t);
            } else {
               descriptions.add(desc);
            }
         }
      }

      Tann.assertTrue("should be no dupes " + failed, failed.size() == 0);
   }

   interface Skipifier<T> {
      boolean skip(T var1);
   }

   interface Stringifier<T> {
      String getString(T var1);
   }
}
