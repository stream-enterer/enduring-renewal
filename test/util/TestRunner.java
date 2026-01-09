package com.tann.dice.test.util;

import com.badlogic.gdx.Gdx;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.TestAbilities;
import com.tann.dice.test.TestBannedCombos;
import com.tann.dice.test.TestBasicEff;
import com.tann.dice.test.TestBattleSim;
import com.tann.dice.test.TestBook;
import com.tann.dice.test.TestBugRepro;
import com.tann.dice.test.TestBugReproIgnored;
import com.tann.dice.test.TestCleanse;
import com.tann.dice.test.TestCollision;
import com.tann.dice.test.TestComplexEff;
import com.tann.dice.test.TestFiles;
import com.tann.dice.test.TestHeroes;
import com.tann.dice.test.TestItem;
import com.tann.dice.test.TestKeyword;
import com.tann.dice.test.TestKeywordSpell;
import com.tann.dice.test.TestModding;
import com.tann.dice.test.TestModifierOffer;
import com.tann.dice.test.TestMusic;
import com.tann.dice.test.TestParty;
import com.tann.dice.test.TestPipe;
import com.tann.dice.test.TestRandomBits;
import com.tann.dice.test.TestScattershot;
import com.tann.dice.test.TestStates;
import com.tann.dice.test.TestStrangeScenarios;
import com.tann.dice.test.TestTriggerOrdering;
import com.tann.dice.test.TestUniqueness;
import com.tann.dice.test.TestValidation;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.TextWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {
   private static final Class[] TEST_CLASSES = new Class[]{
      TestStrangeScenarios.class,
      TestBasicEff.class,
      TestItem.class,
      TestComplexEff.class,
      TestRandomBits.class,
      TestKeyword.class,
      TestKeywordSpell.class,
      TestParty.class,
      TestTriggerOrdering.class,
      TestBugRepro.class,
      TestBugReproIgnored.class,
      TestScattershot.class,
      TestBannedCombos.class,
      TestModifierOffer.class,
      TestUniqueness.class,
      TestValidation.class,
      TestCleanse.class,
      TestFiles.class,
      TestCollision.class,
      TestPipe.class,
      TestHeroes.class,
      TestMusic.class,
      TestBattleSim.class,
      TestAbilities.class,
      TestBook.class,
      TestModding.class,
      TestStates.class
   };
   private static boolean runningTests = false;

   public static void runTests(TestRunner.TestType testType) {
      setupDunScreen();
      runningTests = true;
      Sounds.setSoundEnabled(false);
      long startTime = System.currentTimeMillis();
      int successes = 0;
      int fails = 0;
      int skips = 0;
      List<String> failedTests = new ArrayList<>();
      boolean isTann = Gdx.files.absolute("C:/code").exists();
      String slowest = "unset";
      long slowestTime = 0L;

      for (Class c : TEST_CLASSES) {
         for (Method m : c.getMethods()) {
            List<Class> annotationTypes = new ArrayList<>();
            boolean skip = false;

            for (Annotation anno : m.getAnnotations()) {
               annotationTypes.add(anno.annotationType());
               if (anno instanceof SkipNonTann && !isTann) {
                  skip = true;
               }

               if (anno instanceof TestPlat) {
                  TestPlat p = (TestPlat)anno;
                  if (!p.platformClass().equals(com.tann.dice.Main.self().control.getClass())) {
                     skip = true;
                  }
               }
            }

            if ((!skip || testType == TestRunner.TestType.Specific)
               && annotationTypes.contains(Test.class)
               && (testType != TestRunner.TestType.Specific || annotationTypes.contains(Specific.class))) {
               boolean slowAnnotation = annotationTypes.contains(Slow.class);
               if (testType != TestRunner.TestType.Quick || !slowAnnotation) {
                  if (annotationTypes.contains(Skip.class) && testType != TestRunner.TestType.Specific) {
                     skips++;
                  } else {
                     try {
                        long before = System.currentTimeMillis();
                        m.invoke(null);
                        if (DungeonScreen.get() == null) {
                           TannLog.error("DunScreen nulled by " + m.getName());
                           setupDunScreen();
                        }

                        long taken = System.currentTimeMillis() - before;
                        if (taken > 1000L && !slowAnnotation) {
                           System.err.println("slow needed: " + c.getSimpleName() + ":" + m.getName() + " - took " + taken + "ms");
                        }

                        if (taken < 100L && slowAnnotation) {
                           System.err.println("unnecessary slow: " + c.getSimpleName() + ":" + m.getName() + " - took " + taken + "ms");
                        }

                        if (taken > slowestTime) {
                           slowestTime = taken;
                           slowest = m.getName();
                        }

                        successes++;
                     } catch (InvocationTargetException var26) {
                        fails++;
                        failedTests.add("{" + c.getSimpleName() + ":" + m.getName() + "} " + var26.getTargetException());
                        var26.printStackTrace();
                     } catch (IllegalAccessException var27) {
                        System.out.println(var27.getMessage());
                        System.exit(0);
                     } catch (Throwable var28) {
                        fails++;
                        failedTests.add("{" + c.getSimpleName() + ":" + m.getName() + "}");
                        var28.printStackTrace();
                     }
                  }
               }
            }
         }
      }

      long duration = System.currentTimeMillis() - startTime;
      TannLog.log("______________");
      TannLog.log("TEST RESULTS");
      TannLog.log(successes + "/" + (successes + fails) + " passed");
      if (skips > 0) {
         TannLog.log(skips + " skipped");
      }

      TannLog.log("Took " + duration + "ms");
      TannLog.log("Slowest: " + slowest + " (" + slowestTime + "ms)");
      TannLog.log("______________");
      String message = (fails == 0 ? "[green]" : "[red]") + "Test results: " + successes + "/" + (successes + fails);
      if (fails > 0) {
         message = message + "[n][n][grey]";

         for (String s : failedTests) {
            TannLog.log(s);
            message = message + Tann.makeEllipses(TextWriter.rebracketTags(s), 100) + "[n]";
         }
      }

      Sounds.setSoundEnabled(true);
      runningTests = false;
      com.tann.dice.Main.getCurrentScreen().showDialog(message);
      DungeonScreen.clearStaticReference();
   }

   private static void setupDunScreen() {
      new DungeonScreen(DebugUtilsUseful.dummyContext());
   }

   public static boolean isTesting() {
      return runningTests;
   }

   public static void assertTrue(String message, boolean test) {
      if (!test) {
         throw new RuntimeException(message);
      }
   }

   public static <T> void assertEquals(String message, T expected, T actual) {
      if (!Tann.equals(expected, actual)) {
         throw new RuntimeException(message + " expected " + expected + " and got " + actual);
      }
   }

   public static enum TestType {
      Specific,
      Quick,
      All;
   }
}
