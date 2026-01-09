package com.tann.dice.gameplay.content.ent.type.lib;

import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.blob.monster.MonsterTypeBlob;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;

public class MonsterTypeLib {
   private static List<MonsterType> ALL_MONSTERS;
   static List<MonsterType> valids = new ArrayList<>();

   public static void init() {
      ALL_MONSTERS = MonsterTypeBlob.makeAll();
      test(ALL_MONSTERS);
      PipeMonster.init(ALL_MONSTERS);
      initStats();
   }

   private static void test(List<MonsterType> allMonsters) {
      List<MonsterType> cpy = new ArrayList<>(ALL_MONSTERS);
      Tann.uniquify(cpy);
      if (cpy.size() != allMonsters.size()) {
         TannLog.error("Invalid master monster list: " + ALL_MONSTERS);
      }
   }

   public static void initStats() {
      for (MonsterType m : ALL_MONSTERS) {
         m.setupStats();
      }
   }

   public static boolean isInit() {
      return ALL_MONSTERS != null && ALL_MONSTERS.size() > 0;
   }

   public static List<MonsterType> listName(String... names) {
      List<MonsterType> result = new ArrayList<>();

      for (String n : names) {
         result.add(byName(n));
      }

      return result;
   }

   @Nonnull
   public static MonsterType byName(String name) {
      return PipeMonster.fetch(name);
   }

   public static MonsterType[] byNames(String... names) {
      MonsterType[] result = new MonsterType[names.length];

      for (int i = 0; i < names.length; i++) {
         result[i] = byName(names[i]);
      }

      return result;
   }

   public static List<Monster> monsterList(MonsterType... monsters) {
      List<Monster> results = new ArrayList<>();

      for (MonsterType type : monsters) {
         results.add(type.makeEnt());
      }

      return results;
   }

   public static List<Monster> monsterList(List<MonsterType> monsters) {
      List<Monster> results = new ArrayList<>();

      for (MonsterType type : monsters) {
         Monster m;
         try {
            m = type.makeEnt();
         } catch (Exception var6) {
            m = PipeMonster.getMissingno().makeEnt();
         }

         results.add(m);
      }

      return results;
   }

   public static Map<EntSize, List<MonsterType>> getSortedMonsters() {
      Map<EntSize, List<MonsterType>> result = new HashMap<>();
      List<MonsterType> sortedAll = new ArrayList<>(ALL_MONSTERS);
      Collections.sort(sortedAll, new Comparator<MonsterType>() {
         public int compare(MonsterType o1, MonsterType o2) {
            return o1.hp - o2.hp;
         }
      });

      for (MonsterType mt : sortedAll) {
         List<MonsterType> list = result.get(mt.size);
         if (list == null) {
            list = new ArrayList<>();
            result.put(mt.size, list);
         }

         list.add(mt);
      }

      return result;
   }

   public static List<MonsterType> getMonsters(Zone zone, int playerTier, float rarityRandom) {
      valids.clear();
      List<MonsterType> validMonsters = zone.validMonsters;

      for (int i = 0; i < validMonsters.size(); i++) {
         MonsterType mt = validMonsters.get(i);
         if (!UnUtil.isLocked(mt) && mt.validForLevel(playerTier) && mt.validRarity(rarityRandom)) {
            valids.add(mt);
         }
      }

      if (valids.size() == 0) {
         throw new RuntimeException("No monsters valid for " + zone);
      } else {
         return valids;
      }
   }

   public static int getNumNormalMonsters() {
      return ALL_MONSTERS.size() - 1;
   }

   public static List<MonsterType> getMasterCopy() {
      return new ArrayList<>(ALL_MONSTERS);
   }

   public static List<String> serialise(List<MonsterType> types) {
      List<String> result = new ArrayList<>();

      for (MonsterType mt : types) {
         result.add(mt.getName(false));
      }

      return result;
   }

   public static MonsterType randomWithRarity() {
      int attempts = 50;

      for (int i = 0; i < attempts; i++) {
         MonsterType mt = random();
         if (mt.validRarity(Tann.random())) {
            return mt;
         }
      }

      return Tann.random(ALL_MONSTERS);
   }

   public static MonsterType random() {
      return Tann.random(ALL_MONSTERS);
   }

   public static MonsterType random(Random r) {
      return Tann.random(ALL_MONSTERS, r);
   }

   public static List<MonsterType> search(String text) {
      text = text.toLowerCase();
      List<MonsterType> result = new ArrayList<>();

      for (MonsterType mt : getMasterCopy()) {
         if (mt.getName(false).toLowerCase().contains(text)) {
            result.add(mt);
         }
      }

      return result;
   }

   public static List<MonsterType> getWithSize(EntSize size) {
      List<MonsterType> result = new ArrayList<>();

      for (int i = 0; i < ALL_MONSTERS.size(); i++) {
         MonsterType mt = ALL_MONSTERS.get(i);
         if (mt.size == size) {
            result.add(mt);
         }
      }

      return result;
   }

   public static List<MonsterType> getAllValidMonsters() {
      List<MonsterType> all = getMasterCopy();

      for (int i = all.size() - 1; i >= 0; i--) {
         MonsterType mt = all.get(i);
         if (mt.isMissingno()) {
            all.remove(mt);
         }
      }

      return all;
   }

   public static String[] stringArray(List<MonsterType> list) {
      String[] arr = new String[list.size()];

      for (int i = 0; i < list.size(); i++) {
         arr[i] = list.get(i).getName(false);
      }

      return arr;
   }

   public static List<MonsterType> getAllWith(String in) {
      List<MonsterType> result = new ArrayList<>();
      List<MonsterType> masterCopy = getMasterCopy();

      for (int i = masterCopy.size() - 1; i >= 0; i--) {
         if (masterCopy.get(i).getName(false).toLowerCase().contains(in.toLowerCase())) {
            result.add(masterCopy.get(i));
         }
      }

      return result;
   }

   public static MonsterType byPluralName(String monsterPlural) {
      if (monsterPlural.endsWith("s")) {
         MonsterType maybe = byName(monsterPlural.substring(0, monsterPlural.length() - 1));
         if (!maybe.isMissingno()) {
            return maybe;
         }
      }

      for (int i = 0; i < ALL_MONSTERS.size(); i++) {
         MonsterType potential = ALL_MONSTERS.get(i);
         if (Words.plural(potential.getName()).equalsIgnoreCase(monsterPlural)) {
            return potential;
         }
      }

      return null;
   }

   public static long getCollision(MonsterType[] types) {
      long result = 0L;

      for (int i = 0; i < types.length; i++) {
         result += types[i].getCollisionBits();
      }

      return result;
   }

   @Nonnull
   public static MonsterType safeByName(String name) {
      Pipe.setupChecks();
      MonsterType ht = byName(name);
      Pipe.disableChecks();
      return ht;
   }
}
