package com.tann.dice.gameplay.mode.creative.custom;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeModAllItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeModPermaItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeModSpirit;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.global.eff.GlobalSummonMonsterStartTurn;
import com.tann.dice.gameplay.trigger.global.level.GlobalAddMonster;
import java.util.ArrayList;
import java.util.List;

public class AlternateModifier {
   public static List<Modifier> showMonsterMods(MonsterType mt) {
      List<Modifier> result = new ArrayList<>();
      result.add(GlobalSummonMonsterStartTurn.makeGenerated(mt, true));
      result.add(GlobalAddMonster.makeGenerated(mt));
      result.add(PipeModSpirit.make(mt));

      for (int i = result.size() - 1; i >= 0; i--) {
         if (result.get(i) == null || result.get(i).isMissingno()) {
            result.remove(i);
         }
      }

      return result;
   }

   public static List<Modifier> showHeroMods(HeroType ht) {
      List<Modifier> result = new ArrayList<>();
      result.add(ModifierLib.safeByName("add." + ht.getName()));
      result.add(ModifierLib.safeByName("party." + ht.getName() + "+" + ht.getName() + "+" + ht.getName() + "+" + ht.getName() + "+" + ht.getName()));

      for (int i = result.size() - 1; i >= 0; i--) {
         if (result.get(i) == null || result.get(i).isMissingno()) {
            result.remove(i);
         }
      }

      return result;
   }

   public static List<Modifier> showItemMods(Item item) {
      List<Modifier> result = new ArrayList<>();
      result.add(PipeModPermaItem.make(item));
      result.add(PipeModAllItem.makeItemAll(item, true));

      for (int i = result.size() - 1; i >= 0; i--) {
         if (result.get(i) == null || result.get(i).isMissingno()) {
            result.remove(i);
         }
      }

      return result;
   }
}
