package com.tann.dice.screens.dungeon.panels.book.page.stuffPage;

import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.test.util.TestUtils;
import com.tann.dice.util.saves.Prefs;

public abstract class Incantations {
   public static void input(String text) {
      if (validForTest(text)) {
         TestRunner.runTests(TestRunner.TestType.Quick);
      } else {
         if (validForCrash(text)) {
            throw new RuntimeException("intentional crash");
         }

         if (validForResetSave3(text)) {
            Sounds.playSound(Sounds.fireBreath);
            Prefs.RESETSAVEDATA();
         } else if (validForAllTests(text)) {
            TestRunner.runTests(TestRunner.TestType.All);
         } else if (validForquicktest(text)) {
            TestRunner.runTests(TestRunner.TestType.Specific);
         } else if (validForFps(text)) {
            com.tann.dice.Main.getCurrentScreen().push(TestUtils.fpsPanel(), 0.5F);
         }
      }
   }

   private static boolean validForFps(String text) {
      return text.hashCode() == -749783022 && (text + "a").hashCode() == -1768437105;
   }

   private static boolean validForCrash(String text) {
      return text.hashCode() == -140479674 && (text + "a").hashCode() == -59902501;
   }

   private static boolean validForTest(String text) {
      return text.hashCode() == 1780949763 && (text + "a").hashCode() == -625132098;
   }

   private static boolean validForResetSave3(String text) {
      return text.hashCode() == -576065020 && (text + "a").hashCode() == -678146339;
   }

   private static boolean validForAllTests(String text) {
      return text.hashCode() == -331798853 && (text + "a").hashCode() == -1695829754;
   }

   private static boolean validForquicktest(String text) {
      return text.hashCode() == 1225053491 && (text + "a").hashCode() == -678047346;
   }

   private static String generate(String name, String pass) {
      return "private static boolean validFor"
         + name
         + "(String text) {\n        return text.hashCode() == "
         + pass.hashCode()
         + "\n            && (text+\"a\").hashCode() == "
         + (pass + "a").hashCode()
         + ";\n    }";
   }
}
