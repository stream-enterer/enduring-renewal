package com.tann.dice.gameplay.modifier;

import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.modifier.bless.BlessingLib;
import com.tann.dice.gameplay.modifier.generation.CurseLib;
import com.tann.dice.gameplay.modifier.tweak.TweakLib;
import com.tann.dice.util.bsRandom.Supplier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class ModifierLib {
   private static List<Modifier> all;
   private static List<Modifier> curses;
   private static List<Modifier> blessings;
   private static List<Modifier> tweaks;
   private static ModCache modCache;
   private static Map<String, List<Modifier>> hyphenCache;

   public static Modifier getMissingno() {
      return PipeMod.getMissingno();
   }

   public static boolean isWithin(Modifier m, Integer min, Integer max) {
      return (min == null || m.getTier() >= min) && (max == null || m.getTier() <= max);
   }

   @Nonnull
   public static Modifier byName(String modifierName) {
      return PipeMod.fetch(modifierName);
   }

   public static List<Modifier> getAll() {
      return all;
   }

   public static List<Modifier> getAll(Boolean blessing) {
      return blessing == null ? getAll() : getAll(blessing ? ModifierType.Blessing : ModifierType.Curse);
   }

   public static List<Modifier> getAll(ModifierType type) {
      if (type == null) {
         return getAll();
      } else {
         switch (type) {
            case Blessing:
               return blessings;
            case Curse:
               return curses;
            case Tweak:
               return tweaks;
            default:
               throw new RuntimeException("eip " + type);
         }
      }
   }

   public static void init() {
      all = new ArrayList<>();
      all.addAll(tweaks = ul(TweakLib.makeAll()));
      all.addAll(curses = ul(CurseLib.makeAll()));
      all.addAll(blessings = ul(BlessingLib.makeAll()));
      all = ul(all);
      validate(tweaks, 0);
      validate(curses, -1);
      validate(blessings, 1);
      PipeMod.init(all);
      modCache = new ModCache();
      hyphenCache = new HashMap<>();

      for (int i = 0; i < all.size(); i++) {
         Modifier m = all.get(i);
         String essence = m.getEssence();
         if (essence != null) {
            if (hyphenCache.get(essence) == null) {
               hyphenCache.put(essence, new ArrayList<>());
            }

            hyphenCache.get(essence).add(m);
         }
      }
   }

   private static void validate(List<Modifier> mods, int tierSign) {
      for (int i = 0; i < mods.size(); i++) {
         Modifier m = mods.get(i);
         if (Math.signum((float)m.tier) != tierSign) {
            throw new RuntimeException("Tweak with bad tier: " + m.getTier() + "/" + m.getName());
         }
      }
   }

   private static <T> List<T> ul(List<T> in) {
      return Collections.unmodifiableList(in);
   }

   public static List<Modifier> getAllStartingWith(String start) {
      start = start.toLowerCase();
      List<Modifier> result = new ArrayList<>();

      for (Modifier m : getAll()) {
         if (!m.getName().contains("11-20") && m.getName().toLowerCase().startsWith(start)) {
            result.add(m);
         }
      }

      return result;
   }

   public static List<Modifier> search(String search) {
      search = search.toLowerCase();
      List<Modifier> result = new ArrayList<>();
      List<Modifier> modifiers = getAll();

      for (int i = 0; i < modifiers.size(); i++) {
         Modifier m = modifiers.get(i);
         if (m.getName().toLowerCase().contains(search)) {
            result.add(m);
         }
      }

      return result;
   }

   public static List<String> serialiseToStringList(List<Modifier> originals) {
      List<String> strings = new ArrayList<>();

      for (Modifier m : originals) {
         strings.add(m.getName(false));
      }

      return strings;
   }

   public static List<Modifier> getByNames(String... names) {
      List<Modifier> result = new ArrayList<>();

      for (String n : names) {
         result.add(byName(n));
      }

      return result;
   }

   public static Modifier random() {
      return PipeMod.randomDesigned();
   }

   public static ModCache getCache() {
      return modCache;
   }

   public static List<Modifier> findWithEssence(Modifier existingModifier) {
      return findWithEssence(existingModifier.getEssence());
   }

   public static List<Modifier> findWithEssence(String part) {
      return hyphenCache.get(part);
   }

   public static Supplier<Modifier> makeSupplier() {
      return new Supplier<Modifier>() {
         public Modifier supply() {
            return ModifierLib.random();
         }
      };
   }

   public static Modifier safeByName(String s) {
      Pipe.setupChecks();
      Modifier m = byName(s);
      Pipe.disableChecks();
      return m;
   }
}
