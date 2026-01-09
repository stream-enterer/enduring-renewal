package com.tann.dice.gameplay.content.ent.group;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroAdjust;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.HeroDeath;
import com.tann.dice.gameplay.save.PartyData;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.generalPanels.InventoryPanel;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.Flasher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Party {
   private List<Hero> heroes;
   private List<Item> itemList = new ArrayList<>();
   private PartyLayoutType plt;
   private boolean fid = false;

   public static Party generate(int rewardsGained) {
      return generate(rewardsGained, HeroGenType.Normal, new ArrayList<>());
   }

   public static Party generate(int rewardsGained, HeroGenType hgt, List<HeroType> avoid) {
      return generate(rewardsGained, hgt, getDefaultPLT(), avoid);
   }

   private static PartyLayoutType getDefaultPLT() {
      return PartyLayoutType.Basic;
   }

   public static Party generate(int rewardsGained, HeroGenType hgt, PartyLayoutType partyLayoutType, List<HeroType> avoid) {
      return new Party(generateHeroes(rewardsGained, hgt, partyLayoutType, avoid), partyLayoutType);
   }

   public Party(List<Hero> heroes) {
      this.heroes = heroes;
      this.itemList = new ArrayList<>();
   }

   public Party(List<Hero> heroes, PartyLayoutType plt) {
      this(heroes);
      this.plt = plt;
   }

   private static HeroType[] heroLevel(HeroGenType hgt, PartyLayoutType plt, List<HeroType> old, int... levels) {
      HeroCol[] cols = plt.getColsInstance();
      HeroType[] result = new HeroType[cols.length];
      int i = 0;
      List<HeroType> oldCopy = new ArrayList<>(old);

      for (HeroCol col : cols) {
         int lvl = levels[i];
         switch (hgt) {
            case Alternate:
               result[i] = PipeHeroAdjust.makeHeroAlternate(col, lvl);
               break;
            case Generate:
               result[i] = PipeHeroGenerated.generate(col, lvl);
               break;
            case Normal:
               result[i] = makeNormal(plt, col, lvl, oldCopy);
               break;
            default:
               throw new RuntimeException(hgt + "ppppp");
         }

         oldCopy.add(result[i]);
         i++;
      }

      return result;
   }

   private static HeroType makeNormal(PartyLayoutType plt, HeroCol col, int lvl, List<HeroType> oldCopy) {
      List<HeroType> types = HeroTypeUtils.getFilteredTypes(col, lvl, false);
      List<HeroType> empty = new ArrayList<>();
      HeroType missingno = PipeHero.getMissingno();

      for (int i = 0; i < 500; i++) {
         HeroType ht = Tann.getSelectiveRandom(types, 1, missingno, empty, oldCopy).get(0);
         if (!ht.isMissingno() && !HeroTypeUtils.bannedHeroTypeByCollision(ht, plt.getBannedCollisionBits(false))) {
            return ht;
         }
      }

      return PipeHeroGenerated.generate(col, lvl);
   }

   private static List<Hero> generateHeroes(int rewardsGained, HeroGenType hgt, PartyLayoutType partyLayoutType, List<HeroType> old) {
      int[] levels = new int[partyLayoutType.length()];
      Arrays.fill(levels, -1);
      int levelBudget = levels.length + Math.min(rewardsGained + 1, 20) / 2;

      for (int i = 0; i < levels.length; i++) {
         int thisLevel = levelBudget / (levels.length - i);
         levels[i] = thisLevel;
         levelBudget -= thisLevel;
      }

      Tann.shuffle(levels);
      List<Hero> result = new ArrayList<>();

      for (HeroType type : heroLevel(hgt, partyLayoutType, old, levels)) {
         if (PipeHeroGenerated.shouldAddGenerate()) {
            type = PipeHeroGenerated.generate(type.heroCol, type.getTier());
         }

         result.add(type.makeEnt());
      }

      return result;
   }

   public void onFirstInit(DungeonContext context) {
      if (this.fid) {
         TannLog.error("multi init");
      } else {
         this.fid = true;
         this.addItems(generateItems(context.getCurrentMod20LevelNumber(), context));

         for (int i = 0; i < this.heroes.size(); i++) {
            this.heroOnChooseStuff(context, this.heroes.get(i));
         }
      }
   }

   public void heroOnChooseStuff(DungeonContext context, Hero hero) {
      for (Trait trait : hero.traits) {
         Personal p = trait.personal;
         p.onChoose(context, hero.getHeroType());
      }
   }

   private static List<Item> generateItems(int rewardsGained, DungeonContext context) {
      List<Item> result = new ArrayList<>();

      for (int i = 3; i <= rewardsGained && i < 20; i += 2) {
         int quality = ItemLib.getStandardItemQualityFor(i, 0);
         List<Item> loot = ItemLib.randomWithExactQuality(2, quality, context, result);
         if (loot != null && loot.size() > 0) {
            Item single = loot.get(0);
            result.add(single);
         }
      }

      for (int ix = 20; ix < rewardsGained; ix++) {
         int quality = ItemLib.getStandardItemQualityFor(ix, 0);
         List<Item> loot = ItemLib.randomWithExactQuality(2, quality, context, result);
         if (loot != null && loot.size() > 0) {
            Item single = loot.get(0);
            result.add(single);
         }
      }

      return result;
   }

   public static Party deserialise(String data) {
      List<Hero> heroes = new ArrayList<>();

      for (String s : data.split(",")) {
         heroes.add(HeroTypeUtils.byName(s).makeEnt());
      }

      return new Party(heroes);
   }

   public List<Item> getItems() {
      return this.getItems(null);
   }

   public List<Item> getItems(Boolean equipped) {
      List<Item> result = new ArrayList<>();
      if (equipped == null) {
         result.addAll(this.getItems(true));
         result.addAll(this.getItems(false));
      } else if (equipped) {
         for (Hero h : this.heroes) {
            result.addAll(h.getItems());
         }
      } else {
         result.addAll(this.itemList);
      }

      return result;
   }

   public void addItems(List<Item> items) {
      for (Item e : items) {
         this.addItem(e);
      }
   }

   public void addItem(Item item) {
      this.addItem(item, this.itemList.size());
   }

   public void addItem(Item item, int index) {
      if (index == -1) {
         index = this.itemList.size();
      }

      index = Math.min(this.itemList.size(), Math.max(0, index));
      this.itemList.add(index, item);
      InventoryPanel.get().reset();

      for (Hero h : this.getHeroes()) {
         h.updateOutOfCombat();
      }
   }

   public void removeItem(Item item) {
      if (this.itemList.contains(item)) {
         this.itemList.remove(item);
         InventoryPanel.get().reset();
      }
   }

   public boolean unequip(Item selectedItem) {
      Ent equippee = this.getEquippee(selectedItem);
      if (equippee != null) {
         equippee.removeItem(selectedItem);
         this.refreshAllSlots();
         return true;
      } else {
         return false;
      }
   }

   public void refreshAllSlots() {
      for (Ent de : this.heroes) {
         List<Item> dropped = de.refreshItemSlots();
         if (dropped != null) {
            this.itemList.addAll(dropped);
         }
      }

      Tann.uniquify(this.itemList);
   }

   public void unequipAll() {
      for (Hero hero : this.heroes) {
         this.unequip(hero);
      }
   }

   public boolean unequip(Hero h) {
      boolean some = false;

      for (Item e : h.getItems()) {
         if (e != null && this.unequip(e)) {
            this.itemList.add(e);
            e.setNew(true);
            some = true;
         }
      }

      return some;
   }

   public Ent getEquippee(Item e) {
      for (Ent de : this.heroes) {
         if (de.getItemIndex(e) != null) {
            return de;
         }
      }

      return null;
   }

   public List<Hero> getHeroes() {
      return this.heroes;
   }

   public PartyData toSave() {
      List<String> heroData = new ArrayList<>();
      List<String> items = new ArrayList<>();

      for (int i = 0; i < this.heroes.size(); i++) {
         heroData.add(this.heroes.get(i).fullSaveString());
      }

      for (int i = 0; i < this.itemList.size(); i++) {
         items.add(this.itemList.get(i).getSaveString());
      }

      return new PartyData(heroData, items, this.plt);
   }

   public Party(PartyData partyData) {
      this.heroes = new ArrayList<>();
      this.plt = partyData.plt;

      for (String hd : partyData.h) {
         this.heroes.add(HeroTypeUtils.makeHeroFromString(hd));
      }

      this.itemList.addAll(ItemLib.deserialise(partyData.e));
   }

   public boolean hasAnyItems() {
      if (this.itemList.size() > 0) {
         return true;
      } else {
         for (Hero h : this.heroes) {
            if (h.getItems().size() > 0) {
               return true;
            }
         }

         return DungeonScreen.get().partyManagementPanel.isDragging();
      }
   }

   public List<Item> getForcedItems() {
      List<Item> result = new ArrayList<>();

      for (Item e : this.getItems(false)) {
         if (e.isForceEquip()) {
            result.add(e);
         }
      }

      return result;
   }

   public int getProbableLevel() {
      int result = 1;

      for (Hero h : this.getHeroes()) {
         result += h.getHeroType().level - 1;
      }

      return result + this.itemList.size();
   }

   public void discardItem(Item discarded) {
      for (Hero h : this.heroes) {
         boolean lost = h.removeItem(discarded) >= 0;
         if (lost) {
            this.refreshAllSlots();
         }
      }

      this.itemList.remove(discarded);
   }

   public TP<int[], int[]> getWholePartyHash() {
      int[] hps = new int[this.getHeroes().size() * 2];
      int[] sideHashes = new int[this.getHeroes().size() * 6];

      for (int heroIndex = 0; heroIndex < this.getHeroes().size(); heroIndex++) {
         Hero h = this.getHeroes().get(heroIndex);
         EntState es = h.getState(FightLog.Temporality.Present);
         hps[heroIndex * 2] = es.getHp();
         hps[heroIndex * 2 + 1] = es.getMaxHp();
         int[] hashes = h.getSideHashes();
         System.arraycopy(hashes, 0, sideHashes, heroIndex * 6, 6);
      }

      return new TP<>(hps, sideHashes);
   }

   public Hero getHeroFor(HeroType type, int index) {
      if (type.isMissingno()) {
         return this.getLowestHero();
      } else {
         List<Hero> options = null;
         boolean takeFirst = index == 0;
         if (!takeFirst) {
            options = new ArrayList<>();
         }

         for (Hero h : this.getHeroes()) {
            HeroType current = h.getHeroType();
            if (current.heroCol == type.heroCol && current.level == type.level - 1 && h.canLevelUp()) {
               if (takeFirst) {
                  return h;
               }

               options.add(h);
            }
         }

         return !takeFirst && options.size() != 0 ? options.get(index % options.size()) : this.getLowestHero();
      }
   }

   private Hero getLowestHero() {
      int lowestLevel = 5000;
      Hero current = null;

      for (Hero h : this.getHeroes()) {
         if (h.getLevel() < lowestLevel) {
            lowestLevel = h.getLevel();
            current = h;
         }
      }

      return current;
   }

   public void levelUpTo(HeroType heroType, int index) {
      Hero h = this.getHeroFor(heroType, index);
      if (h != null) {
         String oldHeroName = h.getName(true);
         h.levelUpTo(heroType, DungeonScreen.getCurrentContextIfInGame());
         h.getEntPanel().addActor(new Flasher(h.getEntPanel(), Colours.light, 0.5F));
         LevelEndPhase.unequipHero(this, h, oldHeroName);
         this.onHeroMakeupChange();
      } else {
         TannLog.log("Can't find hero for " + heroType, TannLog.Severity.error);
      }
   }

   public void addHero(Hero extraHero, HeroType underMe, DungeonContext dc) {
      int index = this.heroes.size();

      for (Hero h : this.heroes) {
         if (h.getHeroType() == underMe) {
            index = this.heroes.indexOf(h) + 1;
         }
      }

      this.heroes.add(index, extraHero);
      this.updateStatsAfterAdded(extraHero, index, dc);
      this.heroOnChooseStuff(dc, extraHero);
   }

   public void kill(Hero h, DungeonContext dc) {
      int index = this.heroes.indexOf(h);
      this.heroes.remove(h);
      this.addItems(h.getItems());
      this.updateStatsAfterKilled(h, index, dc);
   }

   private void updateStatsAfterKilled(Hero removed, int oldIndex, DungeonContext context) {
      if (!context.getContextConfig().mode.skipStats()) {
         Map<String, Stat> stats = context.getStatsManager().getStatsMap();

         for (int i = oldIndex; i < this.heroes.size(); i++) {
            Stat iStat = stats.get(HeroDeath.getNameFromIndex(i));
            Stat iPlusStat = stats.get(HeroDeath.getNameFromIndex(i + 1));
            iStat.setValue(iPlusStat.getValue());
         }

         Stat s = stats.get(HeroDeath.getNameFromIndex(this.heroes.size()));
         if (s == null) {
            TannLog.log("Something failed trying to update stats after killed hero: " + removed);
         } else {
            context.getStatsManager().removeStat(s);
         }
      }
   }

   private void updateStatsAfterAdded(Hero added, int atIndex, DungeonContext context) {
      if (!context.getContextConfig().mode.skipStats()) {
         HeroDeath heroDeath = new HeroDeath(this.heroes.size() - 1);
         context.getStatsManager().addStat(heroDeath);
         Map<String, Stat> stats = context.getStatsManager().getStatsMap();

         for (int i = this.heroes.size() - 1; i > atIndex; i--) {
            Stat iStat = stats.get(HeroDeath.getNameFromIndex(i));
            Stat iMinusStat = stats.get(HeroDeath.getNameFromIndex(i - 1));
            iStat.setValue(iMinusStat.getValue());
         }

         stats.get(HeroDeath.getNameFromIndex(atIndex)).setValue(0);
      }
   }

   private void updateStatsAfterSwapped(DungeonContext context, int swapA, int swapB) {
      Map<String, Stat> stats = context.getStatsManager().getStatsMap();
      Stat statA = stats.get(HeroDeath.getNameFromIndex(swapA));
      Stat statB = stats.get(HeroDeath.getNameFromIndex(swapB));
      if (statA != null && statB != null) {
         int aVal = statA.getValue();
         statA.setValue(statB.getValue());
         statB.setValue(aVal);
      } else {
         TannLog.log("stat swap error " + swapA + "/" + swapB, TannLog.Severity.error);
      }
   }

   public void swapHeroes(DungeonContext dc, int swapA, int swapB) {
      if (swapA != swapB && swapA < this.heroes.size() && swapB < this.heroes.size()) {
         Hero a = this.heroes.get(swapA);
         Hero b = this.heroes.get(swapB);
         this.heroes.set(swapA, b);
         this.heroes.set(swapB, a);
         this.updateStatsAfterSwapped(dc, swapA, swapB);
      } else {
         TannLog.log("error swapping " + swapA + " and " + swapB, TannLog.Severity.error);
      }
   }

   public void onHeroMakeupChange() {
      this.refreshAllSlots();
   }

   public Hero getByType(HeroType type) {
      for (int i = 0; i < this.heroes.size(); i++) {
         Hero h = this.heroes.get(i);
         if (h.entType == type) {
            return h;
         }
      }

      return null;
   }

   public void afterEquip() {
      com.tann.dice.Main.self().masterStats.getUnlockManager().afterEquip(this);
   }

   public boolean anyNewItems() {
      for (Item t : this.getItems()) {
         if (t.isNew()) {
            return true;
         }
      }

      return false;
   }

   public void clearBagItems() {
      this.itemList.clear();
   }

   public long getBannedCollisionBits() {
      return this.getBannedCollisionBits(true);
   }

   public long getBannedCollisionBits(boolean spellRequiresTwo) {
      List<HeroCol> cols = new ArrayList<>();

      for (int i = 0; i < this.heroes.size(); i++) {
         HeroCol hc = this.heroes.get(i).getHeroCol();
         cols.add(hc);
      }

      return getBannedCollisionBits(cols, spellRequiresTwo);
   }

   public static long getBannedCollisionBits(List<HeroCol> cols, boolean spellRequiresTwo) {
      boolean hasOrange = false;
      boolean hasYellow = false;
      boolean hasGrey = false;
      boolean hasRed = false;
      boolean hasBlue = false;
      boolean hasGreen = false;
      int numSpells = 0;
      float numPhysical = 0.0F;

      for (int i = 0; i < cols.size(); i++) {
         HeroCol hc = cols.get(i);
         if (hc != null && hc.isSpelly()) {
            numSpells++;
         }

         if (hc != null) {
            switch (hc) {
               case orange:
                  hasOrange = true;
                  break;
               case yellow:
                  hasYellow = true;
                  break;
               case grey:
                  hasGrey = true;
                  break;
               case red:
                  hasRed = true;
                  break;
               case blue:
                  hasBlue = true;
                  break;
               case green:
                  hasGreen = true;
            }

            switch (hc) {
               case orange:
               case yellow:
                  numPhysical++;
                  break;
               case green:
                  numPhysical += 0.5F;
            }
         }
      }

      long result = 0L;
      if (numSpells < 1 + (spellRequiresTwo ? 1 : 0)) {
         result |= Collision.SPELL;
      }

      if (numPhysical < 1 + (spellRequiresTwo ? 1 : 0)) {
         result |= Collision.PHYSICAL_DAMAGE;
      }

      if (!hasOrange) {
         result |= Collision.COL_ORANGE;
      }

      if (!hasYellow) {
         result |= Collision.COL_YELLOW;
      }

      if (!hasGrey) {
         result |= Collision.COL_GREY;
      }

      if (!hasRed) {
         result |= Collision.COL_RED;
      }

      if (!hasBlue) {
         result |= Collision.COL_BLUE;
      }

      if (!hasGreen) {
         result |= Collision.COL_GREEN;
      }

      if (!hasGrey) {
         result |= Collision.SHIELD;
      }

      if (!hasRed) {
         result |= Collision.HEAL;
      }

      return result;
   }

   public int colIndex(HeroCol heroCol) {
      for (int i = 0; i < this.heroes.size(); i++) {
         if (this.heroes.get(i).getHeroCol() == heroCol) {
            return i;
         }
      }

      return -1;
   }

   public boolean allowBurst() {
      return (this.getBannedCollisionBits(false) & Collision.SPELL) == 0L || this.anyHeroesWithBaseMana();
   }

   private boolean anyHeroesWithBaseMana() {
      for (int i = 0; i < this.heroes.size(); i++) {
         Hero h = this.heroes.get(i);
         EntSide[] sides = h.getSides();

         for (int j = 0; j < sides.length; j++) {
            if ((sides[j].getBaseEffect().getCollisionBits(true) & Collision.SPELL) != 0L) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean anyDuplicateCols() {
      List<HeroCol> cols = new ArrayList<>();

      for (Hero hero : this.heroes) {
         cols.add(hero.getHeroCol());
      }

      Tann.clearDupes(cols);
      return cols.size() != this.heroes.size();
   }

   public boolean randomiseEquipment() {
      this.unequipAll();
      if (this.itemList.isEmpty()) {
         return false;
      } else {
         Collections.shuffle(this.itemList);
         List<Hero> heroes = new ArrayList<>(this.getHeroes());

         for (Item item : new ArrayList<>(this.itemList)) {
            Collections.shuffle(heroes);

            for (Hero hero : heroes) {
               if (!hero.getBlankState().skipEquipScreen() && item.canEquip(hero) && hero.getNumberItemSlots() > hero.getItems().size()) {
                  hero.addItem(item);
                  this.removeItem(item);
                  break;
               }
            }
         }

         return true;
      }
   }

   public void curseReset(DungeonContext dc) {
      if (this.plt == null) {
         this.plt = PartyLayoutType.Basic;
      }

      this.fid = false;
      this.clearBagItems();
      this.heroes = generateHeroes(0, HeroGenType.Normal, this.plt, new ArrayList<>());
      this.onFirstInit(dc);

      for (Hero h : this.getHeroes()) {
         h.updateOutOfCombat();
      }
   }

   public PartyLayoutType getPLT() {
      return this.plt;
   }

   public int getMaxExtraHeroes() {
      return 20 - this.heroes.size();
   }
}
