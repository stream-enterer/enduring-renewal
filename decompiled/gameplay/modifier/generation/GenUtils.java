package com.tann.dice.gameplay.modifier.generation;

import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.modifier.generation.tierMaker.TierMakerSet;
import com.tann.dice.gameplay.trigger.global.Global;
import java.util.ArrayList;
import java.util.List;

public class GenUtils {
   public static final int BASE = 16;

   private static List<Modifier> chain(final String name, int amt, TierMaker tm, ModMaker mm) {
      return chain(amt, new NameMaker() {
         @Override
         public String name(int i, List<Global> globals) {
            return ModifierUtils.makeName(name, i, globals);
         }
      }, tm, mm);
   }

   private static List<Modifier> chain(int amt, NameMaker nm, TierMaker tm, ModMaker mm) {
      List<Modifier> result = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         List<Global> globals = mm.make(i);
         float tier = tm.makeTier(i);
         String name = nm.name(i, globals);
         result.add(new Modifier(tier, name, mm.make(i)));
      }

      return result;
   }

   public static List<Modifier> bChain(String name, int amt, ModMaker mm) {
      return bChain(name, amt, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * 3;
         }
      }, mm);
   }

   public static List<Modifier> bChain(String name, int amt, TierMaker tm, ModMaker mm) {
      return chain(name, amt, tm, mm);
   }

   public static List<Modifier> bChain(String name, TierMakerSet tmp, ModMaker mm) {
      return chain(name, tmp.num(), tmp, mm);
   }

   public static List<Modifier> cChain(int amt, NameMaker nm, ModMaker mm) {
      return chain(amt, nm, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * -1;
         }
      }, mm);
   }

   public static List<Modifier> cChain(String name, int amt, ModMaker mm) {
      return cChain(name, amt, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * -1;
         }
      }, mm);
   }

   public static List<Modifier> mChain(String name, int amt, final int perTier, ModMaker mm) {
      return cChain(name, amt, new TierMaker() {
         @Override
         public float makeTier(int index) {
            return (index + 1) * perTier;
         }
      }, mm);
   }

   public static List<Modifier> cChain(String name, int amt, TierMaker tm, ModMaker mm) {
      return chain(name, amt, tm, mm);
   }

   public static List<Modifier> cChain(String name, TierMakerSet tmp, ModMaker mm) {
      return chain(name, tmp.num(), tmp, mm);
   }

   public static String hex(long input) {
      return Long.toString(input, 16);
   }

   public static long hex(String input) {
      try {
         return Long.parseLong(input, 16);
      } catch (Exception var2) {
         return -1L;
      }
   }

   public static String b64(long index) {
      try {
         long counter = index;
         String s = "";

         for (int i = 0; i < 100; i++) {
            long pow = (long)Math.pow(64.0, i);
            int amt = (int)(index / pow % 64L);
            counter -= amt * pow;
            s = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=".charAt(amt) + s;
            if (counter == 0L) {
               return s;
            }

            if (counter < 0L) {
               return "??b?";
            }
         }

         return "??c?";
      } catch (Exception var9) {
         return "??a?";
      }
   }

   public static long b64(String index) {
      try {
         String f = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ%=";
         char[] charArray = index.toCharArray();
         long total = 0L;

         for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            int ci = f.indexOf(c);
            if (ci == -1) {
               return -1L;
            }

            total = (long)(total + ci * Math.pow(64.0, charArray.length - i - 1));
         }

         return total;
      } catch (Exception var8) {
         return -1L;
      }
   }
}
