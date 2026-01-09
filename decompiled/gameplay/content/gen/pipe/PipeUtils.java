package com.tann.dice.gameplay.content.gen.pipe;

import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipeUtils {
   public static final String UNABALANCED_BRACKETS_MSG = "[orange]unbalanced brackets";
   private static Map<Integer, List<Pipe>> genPipesCache = new HashMap<>();
   private static Map<Integer, Float> innateRarityMap = new HashMap<>();

   public static void init() {
      genPipesCache = new HashMap<>();
      innateRarityMap = new HashMap<>();
   }

   public static <T> List<Pipe<T>> getGenPipes(List<Pipe<T>> pipes, boolean wild) {
      int key = pipes.hashCode() + (wild ? 1 : 0);
      if (genPipesCache.get(key) == null) {
         List<Pipe<T>> result = new ArrayList<>();

         for (int i = 0; i < pipes.size(); i++) {
            Pipe p = pipes.get(i);
            if (p.canGenerate(wild)) {
               result.add(p);
            }
         }

         genPipesCache.put(key, result);
      }

      return genPipesCache.get(key);
   }

   public static <T> Pipe<T> randomPipeForGen(List<Pipe<T>> gennablePipes, boolean wild) {
      float total = getInnateRarityTotal(gennablePipes, wild);
      float rnd = Tann.random(total);

      for (int i = 0; i < gennablePipes.size(); i++) {
         Pipe<T> p = gennablePipes.get(i);
         rnd -= p.getRarity(wild);
         if (rnd < 0.0F) {
            return p;
         }
      }

      TannLog.error("Error getting rarity pipe: " + wild);
      return gennablePipes.get(0);
   }

   private static <T> float getInnateRarityTotal(List<Pipe<T>> gennablePipes, boolean wild) {
      int key = gennablePipes.hashCode() + (wild ? 1 : 0);
      if (innateRarityMap.get(key) == null) {
         float result = 0.0F;

         for (Pipe<T> gennablePipe : gennablePipes) {
            result += gennablePipe.getRarity(wild);
         }

         innateRarityMap.put(key, result);
      }

      return innateRarityMap.get(key);
   }

   public static boolean unbalancedBrackets(String val) {
      return Tann.countCharsInString('(', val) != Tann.countCharsInString(')', val);
   }
}
