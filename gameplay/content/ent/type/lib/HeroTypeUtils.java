package com.tann.dice.gameplay.content.ent.type.lib;

import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.blob.heroblobs.HeroTypeBlobPink;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroAdjust;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.SaveStateData;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;

public class HeroTypeUtils {
   public static final String BASIC_ORANGE = "Thief";
   public static final String BASIC_YELLOW = "Fighter";
   public static final String BASIC_GREY = "Defender";
   public static final String BASIC_RED = "Healer";
   public static final String BASIC_BLUE = "Mage";
   public static final String[] BASICS = new String[]{"Thief", "Fighter", "Defender", "Healer", "Mage"};
   public static final String TWIN_ORIGINAL = "Twin";
   public static final String TWIN_COPY = "Tw1n";
   public static final int MAX_LEVEL = 5;
   public static final int MIN_LEVEL = 0;
   public static final String HERO_SUMMONER_NAME_COFFIN = "Coffin";
   static Map<Integer, HeroType> defaultHeroCache = new HashMap<>();

   public static List<HeroType> getTierHeroes(int tier) {
      return getFilteredTypes(null, tier, false);
   }

   public static HeroType random() {
      return Tann.random(HeroTypeLib.getMasterCopy());
   }

   public static HeroType randomNonGreen() {
      return getRandom(Tann.pick(HeroCol.red, HeroCol.orange, HeroCol.yellow, HeroCol.blue, HeroCol.grey), Tann.pick(1, 2, 3));
   }

   public static HeroType random(Random r) {
      List<HeroType> t = HeroTypeLib.getMasterCopy();
      return t.get(r.nextInt(t.size()));
   }

   public static HeroType getRandom(HeroCol col, int level) {
      List<HeroType> base = getFilteredTypes(null, level, false);
      applyPool(base, col, level);
      Collections.shuffle(base);

      for (HeroType ht : base) {
         if (ht.level == level && ht.heroCol == col && !UnUtil.isLocked(ht) && (level <= 1 || !ht.isBannedFromLateStart())) {
            return ht;
         }
      }

      HeroType gen = PipeHeroGenerated.generate(col, level);
      return gen != null ? gen : getMissingno();
   }

   private static HeroType getMissingno() {
      return HeroTypeLib.getMissingno();
   }

   public static List<Hero> getHeroes(HeroType[] heroTypes) {
      List<Hero> result = new ArrayList<>();

      for (HeroType ht : heroTypes) {
         result.add(ht.makeEnt());
      }

      return result;
   }

   public static Map<HeroCol, Map<Integer, List<HeroType>>> getSortedHeroes() {
      Map<HeroCol, Map<Integer, List<HeroType>>> result = new HashMap<>();
      List<HeroType> sorted = HeroTypeLib.getMasterCopy();
      int added = 0;

      for (HeroCol col : HeroCol.values()) {
         Map<Integer, List<HeroType>> map = new HashMap<>();
         result.put(col, map);

         for (int i = 0; i <= 5; i++) {
            List<HeroType> types = new ArrayList<>();
            map.put(i, types);

            for (HeroType ht : sorted) {
               if (ht.heroCol == col && ht.level == i) {
                  types.add(ht);
                  added++;
               }
            }
         }
      }

      if (added != HeroTypeLib.getMasterCopy().size()) {
         throw new RuntimeException("uhoh something wrong here");
      } else {
         return result;
      }
   }

   public static List<String> serialise(List<HeroType> heroTypes) {
      List<String> result = new ArrayList<>();

      for (HeroType ht : heroTypes) {
         result.add(ht.getName(false));
      }

      return result;
   }

   public static List<HeroType> deserialise(List<String> heroTypeStrings) {
      List<HeroType> result = new ArrayList<>();

      for (String s : heroTypeStrings) {
         result.add(byName(s));
      }

      return result;
   }

   public static int getNumNormalHeroes() {
      return HeroTypeLib.getMasterCopy().size();
   }

   public static List<EntSide> getSidesWithColour(HeroCol col, boolean allowDuplicates, boolean allowBonused) {
      List<EntSide> result = new ArrayList<>();
      List<HeroType> filtered = getFilteredTypes(col, null, true);
      Collections.sort(filtered, new Comparator<HeroType>() {
         public int compare(HeroType o1, HeroType o2) {
            return o2.getName(false).compareTo(o1.getName(false));
         }
      });
      filtered.remove(HeroTypeLib.byName("mimic"));

      for (HeroType ht : filtered) {
         for (EntSide es : ht.sides) {
            boolean good = true;
            if (!allowDuplicates) {
               for (EntSide ex : result) {
                  if (es.sameTexture(ex) && es.getBaseEffect().getBonusKeywords().equals(ex.getBaseEffect().getBonusKeywords())) {
                     good = false;
                     break;
                  }
               }
            }

            if (!allowBonused && es.getBaseEffect().getBonusKeywords().size() > 0) {
               good = false;
            }

            if (good) {
               result.add(es.withValue(1));
            }
         }
      }

      return result;
   }

   public static List<HeroType> getFilteredTypes(HeroCol col, Integer tier, boolean allowLocked) {
      List<HeroType> result = new ArrayList<>();

      for (HeroType ht : HeroTypeLib.getMasterCopy()) {
         if ((col == null || ht.heroCol == col)
            && (tier == null || ht.level == tier)
            && (allowLocked || !UnUtil.isLocked(ht))
            && (tier == null || tier <= 0 || !ht.isBannedFromLateStart())) {
            result.add(ht);
         }
      }

      return result;
   }

   public static float getEffectTierFor(int level) {
      return level < 1 ? (float)(getEffectTierFor(1) / Math.pow(2.0, Math.abs(level - 1))) : (float)(1.09F * Math.pow(1.535, level));
   }

   public static int tierFromEffectTier(float effectTier) {
      int min = 0;
      int max = 9;

      for (int i = 0; i <= 9; i++) {
         if (getEffectTierFor(i) * 1.1F >= effectTier) {
            return i;
         }
      }

      return 9;
   }

   public static float getHpFor(int level) {
      switch (level) {
         case 1:
            return 5.0F;
         case 2:
            return 7.4F;
         case 3:
            return 9.0F;
         default:
            return (float)(0.4F * Math.pow(level - 1, 2.0) + 1.6F * (level - 1)) + 5.0F;
      }
   }

   public static EntSide[] makeTwinSides() {
      return new EntSide[]{ESB.wandSelfHeal.val(1), ESB.wandSelfHeal.val(1), ESB.dmgPain.val(1), ESB.dmgPain.val(1), ESB.blank, ESB.blank};
   }

   public static HeroType[] getAltT1s(int tier) {
      switch (tier) {
         case 0:
            return new HeroType[]{byName("lost"), byName("brigand"), byName("wallop"), byName("acolyte"), byName("student")};
         case 1:
            return new HeroType[]{byName("dabble"), byName("ruffian"), byName("squire"), byName("mystic"), byName("initiate")};
         case 2:
            return new HeroType[]{byName("clumsy"), byName("hoarder"), byName("alloy"), byName("gardener"), byName("cultist")};
         default:
            throw new RuntimeException("Unset alttst: " + tier);
      }
   }

   public static HeroType defaultHero(int tier) {
      if (defaultHeroCache.get(tier) == null) {
         defaultHeroCache.put(tier, new HTBill(HeroCol.red, tier).name("generated_default").hp((int)getHpFor(tier)).sides(ESB.blank).bEntType());
      }

      return defaultHeroCache.get(tier);
   }

   public static Hero makeHeroFromString(String raw) {
      List<String> data = Arrays.asList(raw.split("~"));
      int index = 0;
      Hero h = new Hero(byName(data.get(index)));
      index++;
      boolean dead = data.size() > index && data.get(index).equals(SaveStateData.deadHeroTag);
      if (dead) {
         index++;
      }

      List<Item> items = new ArrayList<>();

      for (int i = index; i < data.size(); i++) {
         items.add(ItemLib.byName(data.get(i)));
      }

      h.setDiedLastRound(dead);
      h.forceItems(items);
      return h;
   }

   @Nonnull
   public static HeroType byName(String name) {
      return PipeHero.fetch(name);
   }

   public static List<HeroType> fromHeroes(List<Hero> heroes) {
      List<HeroType> result = new ArrayList<>();

      for (Hero h : heroes) {
         result.add(h.getHeroType());
      }

      return result;
   }

   public static HeroType getHeroFromSpell(Spell spell) {
      List<HeroType> l = HeroTypeLib.getMasterCopy();

      for (int i = 0; i < l.size(); i++) {
         if (l.get(i).getSpell() == spell) {
            return l.get(i);
         }
      }

      return null;
   }

   public static List<HeroType> getOptions(Hero h, HeroGenType hgt, DungeonContext dungeonContext, int amt) {
      List<HeroType> epu = new ArrayList<>();

      for (int i = 0; i < amt; i++) {
         epu.add(getOption(h, hgt, dungeonContext, epu, new ArrayList<>()));
      }

      return epu;
   }

   public static HeroType getOption(Hero h, HeroGenType hgt, DungeonContext dungeonContext, List<HeroType> currentPlusOffered, List<HeroType> tmpSeen) {
      HeroCol hc = h.getHeroCol();
      int newTier = h.getLevel() + 1;
      if (PipeHeroGenerated.shouldAddGenerate()) {
         return PipeHeroGenerated.generate(hc, newTier);
      } else {
         switch (hc == HeroCol.violet ? HeroGenType.Normal : hgt) {
            case Generate:
               return PipeHeroGenerated.generate(hc, newTier);
            case Alternate:
               return PipeHeroAdjust.makeHeroAlternate(hc, newTier);
            default:
               return getBasicLevelupOption(currentPlusOffered, h.getHeroType(), dungeonContext, tmpSeen, newTier);
         }
      }
   }

   private static boolean nOrM(HeroType ht) {
      return ht == null || ht.isMissingno();
   }

   public static boolean bannedHeroTypeByCollision(HeroType ht, DungeonContext dc) {
      return bannedHeroTypeByCollision(ht, dc.getBannedCollisionBits(false));
   }

   public static boolean bannedHeroTypeByCollision(HeroType ht, long bits) {
      return (bits & Collision.SPELL) != 0L && PipeHeroGenerated.hasOrSimilarKeyword(ht, EffType.Mana) && !isSpelly(ht.heroCol)
         ? true
         : (bits & Collision.MODIFIER) != 0L && (ht.getCollisionBits() & Collision.MODIFIER) != 0L;
   }

   public static HeroType getBasicLevelupOption(List<HeroType> existing, HeroType ht, DungeonContext dungeonContext, List<HeroType> tmpSeen, int newTier) {
      HeroCol hc = ht.heroCol;
      HeroType option = Tann.getSelectiveRandom(getAllLevelUpOptions(existing, ht, dungeonContext, newTier), 1, PipeHero.getMissingno(), existing, tmpSeen)
         .get(0);
      if (!nOrM(option)) {
         return option;
      } else {
         option = PipeHeroGenerated.generate(hc, newTier);
         if (!nOrM(option)) {
            return option;
         } else {
            if (!OptionLib.LEVELUP_HORUS_ONLY.c() && newTier == ht.getTier() + 1) {
               option = HeroTypeBlobPink.levelupExample(ht);
               if (!nOrM(option)) {
                  return option;
               }
            }

            String bonus = Tann.pick(".hp." + (ht.hp + 4));
            String oldName = ht.getName(true, false);
            String newName = oldName.contains(".") ? null : oldName + "X";
            HeroType fb = byName(ht.getName(false) + bonus + ".tier." + newTier + ".i.eye of horus" + (newName == null ? "" : ".n." + newName));
            return !fb.isMissingno() ? fb : PipeHero.getMissingno();
         }
      }
   }

   private static List<HeroType> getAllLevelUpOptions(List<HeroType> existing, HeroType ht, DungeonContext dungeonContext, int newTier) {
      List<HeroType> results = getAllLevelupsFor(ht, newTier);
      results.removeAll(existing);
      List<Global> globs = dungeonContext.getModifierGlobalsIncludingLinked();

      for (int i = results.size() - 1; i >= 0; i--) {
         HeroType potential = results.get(i);
         if (bannedHeroTypeByCollision(potential, dungeonContext)) {
            results.remove(i);
         }
      }

      globalAffect(results, globs, ht.heroCol, newTier);
      return results;
   }

   public static List<HeroType> globalAffect(List<HeroType> results, List<Global> globs, HeroCol base, Integer newTier) {
      for (int i = 0; i < globs.size(); i++) {
         Global g = globs.get(i);
         g.affectLevelupOptions(results);
         List<HeroType> extras = g.getExtraLevelupOptions(base, newTier);
         if (extras != null) {
            results.addAll(extras);
         }
      }

      return results;
   }

   public static boolean isSpelly(HeroCol heroCol) {
      return heroCol == HeroCol.red || heroCol == HeroCol.blue;
   }

   private static List<HeroType> getAllLevelupsFor(HeroType ht, int newTier) {
      List<HeroType> results = new ArrayList<>();

      for (HeroType type : HeroTypeLib.getMasterCopy()) {
         if (type.heroCol == ht.heroCol && type.level == newTier && !UnUtil.isLocked(type)) {
            results.add(type);
         }
      }

      return results;
   }

   public static List<HeroType> heroList(String... names) {
      List<HeroType> result = new ArrayList<>();

      for (String name : names) {
         result.add(byName(name));
      }

      return result;
   }

   public static Unlockable[] fullTacticUnlock() {
      HeroType[] hts = ttl(getNonGreenHeroesWithTactics());
      Unlockable[] us = new Unlockable[hts.length + 1];
      us[0] = Feature.TACTICS;
      System.arraycopy(hts, 0, us, 1, hts.length);
      return us;
   }

   public static HeroType[] ttl(List<HeroType> in) {
      return in.toArray(new HeroType[0]);
   }

   public static List<HeroType> getNonGreenHeroesWithTactics() {
      List<HeroType> result = new ArrayList<>();

      for (HeroType heroType : HeroTypeLib.getMasterCopy()) {
         if (heroType.heroCol != HeroCol.green && heroType.getTactic() != null) {
            result.add(heroType);
         }
      }

      return result;
   }

   public static HTBill copy(HeroType src) {
      HTBill htb = new HTBill(src.heroCol, src.level);
      EntTypeUtils.finishInit(htb, src);
      return htb;
   }

   public static HeroType withPassive(HeroType src, String name, Personal traitP) {
      return withPassive(src, name, traitP, null);
   }

   public static HeroType withPassive(HeroType src, String name, Personal traitP, String traitN) {
      if (src.isMissingno()) {
         return null;
      } else {
         HTBill htb = copy(src);
         htb.name(name);
         if (traitN == null) {
            htb.trait(traitP, false);
         } else {
            htb.trait(traitP);
         }

         return htb.bEntType();
      }
   }

   public static MonsterType withPassive(MonsterType src, String monsterName, Personal traitP, String traitN) {
      if (src.isMissingno()) {
         return null;
      } else if (src.getName(false).startsWith("c.")) {
         return src;
      } else {
         MTBill mtb = EntTypeUtils.copy(src);
         mtb.name(monsterName);
         if (traitN == null) {
            mtb.trait(traitP, false);
         } else {
            mtb.trait(traitP);
         }

         return mtb.bEntType();
      }
   }

   public static String safeHeroName(String text) {
      return text.replaceAll("[^a-zA-Z0-9 ]", "");
   }

   private static List<Global> findGlobsHackier() {
      if (DungeonScreen.get() == null) {
         return null;
      } else {
         FightLog f = DungeonScreen.get().getFightLog();
         return f == null ? null : f.getSnapshot(FightLog.Temporality.Present).getGlobals();
      }
   }

   public static void applyPool(List<HeroType> base, HeroCol col, Integer level) {
      List<Global> globs = findGlobsHackier();
      if (globs != null) {
         globalAffect(base, globs, col, level);
      }
   }
}
