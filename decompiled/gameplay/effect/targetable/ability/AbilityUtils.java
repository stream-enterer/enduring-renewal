package com.tann.dice.gameplay.effect.targetable.ability;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellLib;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilityUtils {
   public static final Color TACTIC_COL = Colours.yellow;
   private static List<Ability> all;
   private static Map<String, Ability> map;
   public static Map<Ability, Float> abilityStrengthMap;

   public static List<Ability> getAll() {
      List<Ability> result = new ArrayList<>();
      result.addAll(SpellLib.makeAllSpellsList());
      result.addAll(TacticUtils.makeAll(true));
      return result;
   }

   public static void init() {
      abilityStrengthMap = new HashMap<>();
      map = new HashMap<>();
      all = getAll();

      for (int i = 0; i < all.size(); i++) {
         map.put(all.get(i).getTitle().toLowerCase(), all.get(i));
      }

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         Ability s = ht.getAbility();
         if (s != null) {
            addAbility(s, ht.getTier());
         }
      }

      for (Item i : ItemLib.getMasterCopy()) {
         Ability s = i.getAbility();
         if (s != null) {
            addAbility(s, itemTierToHeroTierFactor(i.getTier()));
         }
      }

      List<Ability> abs = new ArrayList<>();
      abs.addAll(SpellLib.makeAllSpellsList());
      abs.addAll(TacticUtils.makeAll(true));

      for (Ability ab : abs) {
         if (!all.contains(ab)) {
            addAbility(ab, Float.NaN);
         }
      }
   }

   private static float itemTierToHeroTierFactor(float itemTier) {
      return (itemTier + 1.0F) / 2.0F;
   }

   public static float heroTierFactorToItemTier(float heroTier) {
      return heroTier * 2.0F - 1.0F;
   }

   public static Ability byName(String name) {
      String lc = name.toLowerCase();
      if (map.containsKey(lc)) {
         return map.get(lc);
      } else {
         if (name.startsWith("s") || name.startsWith("t")) {
            String rest = name.substring(1);
            HeroType ht = HeroTypeUtils.byName(rest);
            if (!ht.isMissingno()) {
               return ht.getAbility();
            }
         }

         return null;
      }
   }

   public static Spell spellByName(String name) {
      Ability a = byName(name);
      return a instanceof Spell ? (Spell)a : null;
   }

   public static Tactic tacticByName(String name) {
      Ability a = byName(name);
      return a instanceof Tactic ? (Tactic)a : null;
   }

   public static float likeFromHeroTier(Ability ab) {
      Float f = abilityStrengthMap.get(ab);
      return f == null ? Float.NaN : f;
   }

   private static void addAbility(Ability a, float v) {
      all.add(a);
      map.put(a.getTitle().toLowerCase(), a);
      abilityStrengthMap.put(a, v);
   }

   public static Ability random() {
      return Tann.random(all);
   }
}
