package com.tann.dice.gameplay.modifier;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModifierUtils {
   public static String describeList(List<Modifier> modifiers) {
      if (modifiers.size() == 0) {
         return "nothing??";
      } else {
         boolean onlyCurse = true;
         boolean onlyBlessing = true;

         for (Modifier m : modifiers) {
            onlyBlessing &= m.getMType() == ModifierType.Blessing;
            onlyCurse &= m.getMType() == ModifierType.Curse;
         }

         if (onlyCurse) {
            return "curse";
         } else {
            return onlyBlessing ? "blessing" : "modifier";
         }
      }
   }

   public static String describe(Boolean blessing) {
      if (blessing == null) {
         return "modifier";
      } else {
         return blessing ? "blessing" : "curse";
      }
   }

   public static String describe(int tier) {
      return describe(tier == 0 ? null : tier > 0);
   }

   public static Color colourFor(List<Modifier> modifiers) {
      if (modifiers.size() == 0) {
         return Colours.pink;
      } else {
         boolean onlyCurse = true;
         boolean onlyBlessing = true;

         for (Modifier m : modifiers) {
            onlyBlessing &= m.getMType() == ModifierType.Blessing;
            onlyCurse &= m.getMType() == ModifierType.Curse;
         }

         return !onlyCurse && !onlyBlessing ? Colours.text : modifiers.get(0).getBorderColour();
      }
   }

   public static String makeName(String base, int i) {
      return makeName(base, i, (List<Global>)null);
   }

   public static String makeName(String base, int i, Global g) {
      return makeName(base, i, Arrays.asList(g));
   }

   public static String makeName(String base, int i, List<Global> globals) {
      if (globals != null && !base.equalsIgnoreCase("wurst") && !base.equalsIgnoreCase("heavy sleeper")) {
         for (Global global : globals) {
            String s = global.hyphenTag();
            if (s != null) {
               return base + "^" + s;
            }
         }
      }

      return i == 0 ? base : base + "/" + (i + 1);
   }

   public static String afterItems() {
      return "[n][n][n][n][notranslate][grey](" + com.tann.dice.Main.t("this effect applies after items") + ")[cu]";
   }

   public static boolean hasMissingno(List<Modifier> mods) {
      for (Modifier mod : mods) {
         if (mod.isMissingno()) {
            return true;
         }
      }

      return false;
   }

   public static List<Modifier> deserialiseList(List<String> modifiers) {
      List<Modifier> result = new ArrayList<>();

      for (String s : modifiers) {
         Pipe.setupChecks();
         result.add(ModifierLib.byName(s));
         Pipe.disableChecks();
      }

      return result;
   }

   public static String hyphenTag(String s, String s1) {
      if (s != null) {
         return s1 != null ? s + "/" + s1 : s;
      } else {
         return s1;
      }
   }

   public static String hyphenTag(Eff eff) {
      return eff.getValue() == -999 ? null : "" + eff.getValue();
   }

   public static Modifier someNextInChain(Integer min, Integer max, Modifier existingModifier) {
      List<Modifier> mods = ModifierLib.findWithEssence(existingModifier);
      if (mods == null) {
         return null;
      } else {
         for (int i = 0; i < mods.size(); i++) {
            Modifier mod = mods.get(i);
            int delta = mod.getTier() - existingModifier.getTier();
            if (delta >= min && delta <= max) {
               return mod;
            }
         }

         return null;
      }
   }

   public static String extractEssence(Modifier m) {
      String n = m.name;
      return !n.contains("^") ? null : n.substring(0, n.indexOf(94));
   }

   public static String sumTiers(List<Modifier> mods, boolean col) {
      int total = 0;

      for (Modifier mod : mods) {
         total += mod.tier;
      }

      String s = total + "";
      if (col) {
         s = TextWriter.getTag(ModifierType.fromTier(total).c) + s + "[cu]";
      }

      return s;
   }
}
