package com.tann.dice.gameplay.content.item;

import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItemGeneratedTiered;
import com.tann.dice.gameplay.content.item.blob.ItemBlob;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.item.GlobalStartWithItem;
import com.tann.dice.util.Tann;
import com.tann.dice.util.bsRandom.Supplier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;

public abstract class ItemLib {
   public static final int MIN_QUALITY = -7;
   public static final int MAX_QUALITY = 20;
   private static List<Item> all = new ArrayList<>();

   public static void init() {
      all = new ArrayList<>();
      addAll(ItemBlob.makeAll());
      PipeItem.init(all);
   }

   private static void addAll(List<ItBill> bill) {
      for (ItBill itBill : bill) {
         add(itBill);
      }
   }

   private static void add(ItBill bill) {
      add(bill.bItem());
   }

   private static void add(Item add) {
      String name = add.getName(false).toLowerCase();

      for (Item e : all) {
         if (e.getName(false).toLowerCase().equals(name)) {
            System.err.println("Wuhoh, duplicate item with name " + add.getName(false));
            return;
         }
      }

      all.add(add);
   }

   public static boolean collides(List<Item> att) {
      for (int i = 0; i < att.size(); i++) {
         for (int j = i + 1; j < att.size(); j++) {
            if (ChoosableUtils.collides(att.get(i), att.get(j))) {
               return true;
            }
         }
      }

      return false;
   }

   public static List<Item> randomWithExactQuality(int numItems, int itemTier, DungeonContext dc) {
      return randomWithExactQuality(numItems, itemTier, dc, null);
   }

   public static List<Item> randomWithExactQuality(int numItems, int itemTier, long onlyBits) {
      return randomWithExactQuality(numItems, itemTier, new ArrayList<>(), new ArrayList<>(), onlyBits);
   }

   public static List<Item> randomWithExactQuality(int numItems, int itemTier, DungeonContext dc, List<Item> bannedItems) {
      List<Item> ownedItems = dc.getParty().getItems(null);
      if (bannedItems != null) {
         ownedItems.addAll(bannedItems);
      }

      return randomWithExactQuality(numItems, itemTier, ownedItems, dc);
   }

   private static List<Item> randomWithExactQuality(int numItems, int itemTier, List<Item> owned, DungeonContext dc) {
      long bannedBitsFromContext = dc.getBannedCollisionBits();
      List<Item> base = getAllItemsWithQuality(itemTier, false, bannedBitsFromContext);

      for (Global modifierGlobal : dc.getModifierGlobalsIncludingLinked()) {
         modifierGlobal.affectItemOptions(base, itemTier);
      }

      List<Item> picked = Tann.getSelectiveRandom(base, numItems, PipeItem.getMissingno(), owned, dc.makeSeenItems());
      return rweqInternalFinalTransformationsAfterPicked(itemTier, bannedBitsFromContext, picked);
   }

   private static List<Item> randomWithExactQuality(int numItems, int itemTier, List<Item> owned, List<Item> alreadySeen, long bannedBitsFromContext) {
      List<Item> base = getAllItemsWithQuality(itemTier, false, bannedBitsFromContext);
      List<Item> picked = Tann.getSelectiveRandom(base, numItems, PipeItem.getMissingno(), owned, alreadySeen);
      return rweqInternalFinalTransformationsAfterPicked(itemTier, bannedBitsFromContext, picked);
   }

   private static List<Item> rweqInternalFinalTransformationsAfterPicked(int itemTier, long bannedBitsFromContext, List<Item> result) {
      for (int i = 0; i < result.size(); i++) {
         if (result.get(i).isMissingno()) {
            result.set(i, PipeItem.makeFallback(itemTier, bannedBitsFromContext));
         }

         if (PipeItemGeneratedTiered.shouldGenerateRandom()) {
            Item attempt = PipeItem.makeTieredMaybeNull(itemTier, bannedBitsFromContext);
            if (attempt != null) {
               result.set(i, attempt);
            }
         }
      }

      if (itemTier >= 10) {
         for (int i = 0; i < result.size() / 2; i++) {
            result.set(i, PipeItem.makeFallback(itemTier, bannedBitsFromContext));
         }
      }

      return result;
   }

   public static Item random() {
      return Tann.random(all);
   }

   public static Item random(Random r) {
      return all.get(r.nextInt(all.size()));
   }

   public static int getStandardItemQualityFor(int levelNumber) {
      return getStandardItemQualityFor(levelNumber, 0);
   }

   public static int getStandardItemQualityFor(int levelNumber, int bonusQuality) {
      int extra = 0;
      int above20 = levelNumber - 21;
      if (above20 >= 0) {
         extra = above20 / 2;
      }

      return levelNumber / 2 + bonusQuality + extra;
   }

   @Nonnull
   public static Item byName(String name) {
      return PipeItem.fetch(name);
   }

   public static Item[] byeNames(String... items) {
      Item[] rslt = new Item[items.length];

      for (int i = 0; i < items.length; i++) {
         rslt[i] = byName(items[i]);
      }

      return rslt;
   }

   public static List<Item> deserialise(List<String> strings) {
      List<Item> result = new ArrayList<>();

      for (String s : strings) {
         result.add(byName(s));
      }

      return result;
   }

   public static List<String> serialise(List<Item> items) {
      List<String> result = new ArrayList<>();

      for (Item e : items) {
         result.add(e.getName(false));
      }

      return result;
   }

   public static List<Item> getAllItemsWithQuality(int quality) {
      return getAllItemsWithQuality(quality, false);
   }

   public static List<Item> getAllItemsWithQuality(int quality, boolean allowLocked) {
      return getAllItemsWithQuality(quality, allowLocked, 0L);
   }

   public static List<Item> getAllItemsWithQuality(int quality, boolean allowLocked, long bannedBits) {
      List<Item> results = new ArrayList<>();

      for (Item e : all) {
         if (!ChoosableUtils.collides(e, bannedBits) && e.getTier() == quality && (allowLocked || !UnUtil.isLocked(e))) {
            results.add(e);
         }
      }

      return results;
   }

   public static int getNumNormalItems() {
      int total = 0;

      for (Item e : all) {
         if (!e.isHidden()) {
            total++;
         }
      }

      return total;
   }

   public static List<Item> getMasterCopy() {
      return new ArrayList<>(all);
   }

   public static Modifier makeBlessingMulti(String name, Item... eqs) {
      float tier = 0.0F;

      for (Item e : eqs) {
         tier += TierUtils.itemModTier(e.getTier());
      }

      tier *= 0.9F;
      return new Modifier(tier, name, new GlobalStartWithItem(eqs));
   }

   public static List<Item> search(String start) {
      start = start.toLowerCase();
      List<Item> result = new ArrayList<>();

      for (Item i : all) {
         if (i.getName(false).toLowerCase().contains(start)) {
            result.add(i);
         }
      }

      return result;
   }

   public static Item[] getAllPotions() {
      List<Item> result = new ArrayList<>();

      for (Item i : all) {
         if (i.isPotion()) {
            result.add(0, i);
         }
      }

      return result.toArray(new Item[0]);
   }

   public static List<Keyword> getAllKeywordRefrences() {
      List<Keyword> ks = new ArrayList<>();

      for (Item item : all) {
         ks.addAll(item.getReferencedKeywords());
      }

      return ks;
   }

   public static List<String> names(List<Item> toulouse) {
      List<String> strings = new ArrayList<>();

      for (Item item : toulouse) {
         strings.add(item.getName());
      }

      return strings;
   }

   public static Supplier<Item> makeSupplier() {
      return new Supplier<Item>() {
         public Item supply() {
            return ItemLib.random();
         }
      };
   }

   public static Item checkedByName(String text) {
      Pipe.setupChecks();
      Item i = byName(text);
      Pipe.disableChecks();
      return i;
   }
}
