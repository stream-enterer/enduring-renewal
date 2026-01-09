package com.tann.dice.gameplay.content.gen.pipe.item;

import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeMaster;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.content.gen.pipe.item.dataFromHero.PipeItemOnHit;
import com.tann.dice.gameplay.content.gen.pipe.item.dataFromHero.PipeItemTriggerHP;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.PipeItemFacade;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.PipeItemFacadeMini;
import com.tann.dice.gameplay.content.gen.pipe.item.sideReally.PipeItemCast;
import com.tann.dice.gameplay.content.gen.pipe.item.sideReally.PipeItemSticker;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeCache;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaBracketed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaDocument;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRandomTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRename;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaSetTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaX;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceItem;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.PipeMetaTexture;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.trigger.personal.OnOverheal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogEft;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogFriend;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogKey;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogMult;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogOrf;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogPips;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogRes;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogResCh;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogResCombine;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogTarg;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogTime;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogUnt;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.TogVis;
import com.tann.dice.gameplay.trigger.personal.hp.HpBonusLetter;
import com.tann.dice.gameplay.trigger.personal.hp.MaxHP;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.ColLink;
import com.tann.dice.gameplay.trigger.personal.weird.ClearDescription;
import com.tann.dice.gameplay.trigger.personal.weird.ClearIcons;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PipeItem {
   private static Item MISSINGNO_REAL;
   private static final String MISSINGNO_NAME = "err";
   public static final int QUALITY_MISSINGNO = -50;
   private static PipeCache<Item> pmc;
   public static List<Pipe<Item>> pipes;
   private static final int FALLBACK_ATTEMPTS = 50;

   public static void init(List<Item> all) {
      DataSource<Item> ds = new DataSourceItem();
      pipes = new ArrayList<>();
      pipes.add(new PipeMaster<>(all));
      pipes.add(pmc = new PipeCache<>());
      pipes.add(new PipeItemKeyword());
      pipes.add(new PipeItemPosition());
      pipes.add(new PipeItemMultiplyValues());
      pipes.addAll(PipeMetaX.makeAll(ds));
      pipes.addAll(PipeMetaSetTier.makeAll(ds));
      pipes.addAll(PipeMetaRename.makeAll(ds));
      pipes.addAll(PipeMetaDocument.makeAll(ds));
      pipes.add(new PipeItemPerTier());
      pipes.add(new PipeItemPart());
      pipes.add(new PipeMetaRandomTier<>(ds));
      pipes.addAll(PipeMetaTexture.makeAll(ds));
      pipes.addAll(PipeMetaIndexed.makeAll(ds));
      pipes.addAll(PipeMetaBracketed.makeAll(ds));
      pipes.add(new PipeItemUnpack());
      pipes.add(new PipeItemGeneratedTiered());
      pipes.add(new PipeItemGenerated());
      pipes.add(new PipeItemSideDesc());
      pipes.add(new PipeItemSpliceItem());
      pipes.add(new PipeItemSelf());
      pipes.add(new PipeItemAbility());
      pipes.add(new PipeItemOnHit());
      pipes.add(new PipeItemTriggerHP());
      pipes.add(new PipeItemHat());
      pipes.add(new PipeItemFacade());
      pipes.add(new PipeItemFacadeMini());
      pipes.add(new PipeItemTrait());
      pipes.add(new PipeItemCombined());
      pipes.add(new PipeItemMerged());
      pipes.add(new PipeItemSticker());
      pipes.add(new PipeItemEnchant());
      pipes.add(new PipeItemCast());
      pipes.add(new PipeMaster<>(makeHiddenItems()));
      MISSINGNO_REAL = new ItBill(-50, "err", "special/bug").hidden().prs(new OnOverheal(new EffBill().summon("error", 1))).bItem();
   }

   private static List<Item> makeHiddenItems() {
      return Arrays.asList(
         new ItBill("togtime").prs(new AffectSides(new TogTime())).bItem(),
         new ItBill("togtarg").prs(new AffectSides(new TogTarg())).bItem(),
         new ItBill("togfri").prs(new AffectSides(new TogFriend())).bItem(),
         new ItBill("togvis").prs(new AffectSides(new TogVis())).bItem(),
         new ItBill("togres").prs(new AffectSides(new TogRes())).bItem(),
         new ItBill("togresn").prs(new AffectSides(new TogResCh(0))).bItem(),
         new ItBill("togress").prs(new AffectSides(new TogResCh(1))).bItem(),
         new ItBill("togresa").prs(new AffectSides(new TogResCombine(0))).bItem(),
         new ItBill("togreso").prs(new AffectSides(new TogResCombine(1))).bItem(),
         new ItBill("togresx").prs(new AffectSides(new TogResCombine(2))).bItem(),
         new ItBill("togresm").prs(new AffectSides(new TogMult())).bItem(),
         new ItBill("togeft").prs(new AffectSides(new TogEft())).bItem(),
         new ItBill("togpip").prs(new AffectSides(new TogPips())).bItem(),
         new ItBill("togkey").prs(new AffectSides(new TogKey())).bItem(),
         new ItBill("togunt").prs(new AffectSides(new TogUnt())).bItem(),
         new ItBill("togorf").prs(new AffectSides(new TogOrf())).bItem(),
         new ItBill("cleardesc").prs(new ClearDescription()).bItem(),
         new ItBill("clearicon").prs(new ClearIcons()).bItem(),
         new ItBill("rgreen").prs(new ColLink(HeroCol.green, new MaxHP(1))).bItem(),
         new ItBill(6, "Idol of Chrzktx").prs(new HpBonusLetter(1, HpBonusLetter.CONSONANTS)).bItem(),
         new ItBill(0, "Idol of Pythagoras").prs(new HpBonusLetter(1, HpBonusLetter.NUMBERS)).bItem(),
         new ItBill(5, "Idol of Aiiu").prs(new HpBonusLetter(1, HpBonusLetter.VOWELS)).bItem(),
         new ItBill(0, "False Idol").prs(new HpBonusLetter(1, 'z')).bItem()
      );
   }

   @Nonnull
   public static Item fetch(String name) {
      return !Pipe.DANGEROUS_NONMODIFIER_PIPE_CHARS.matcher(name).matches() ? getMissingno() : Pipe.checkPipes(pipes, name, pmc, getMissingno());
   }

   public static Item getMissingno() {
      return MISSINGNO_REAL;
   }

   public static Item makeGen() {
      boolean wild = true;
      int attempts = 20;
      List<Pipe<Item>> gennablePipes = getGenPipes(wild);

      for (int i = 0; i < 20; i++) {
         Pipe<Item> pm = randomPipeForGen(gennablePipes, wild);
         Item item = pm.generate(wild);
         if (item != null && !item.isMissingno() && item.getTier() != 0) {
            return item;
         }
      }

      return getMissingno();
   }

   private static boolean roundtrip(Item item) {
      Item rounded = ItemLib.byName(item.getName());
      return rounded.getName().equalsIgnoreCase(item.getName())
         && rounded.getTier() == item.getTier()
         && rounded.getDescription().equalsIgnoreCase(item.getDescription());
   }

   private static Pipe<Item> randomPipeForGen(List<Pipe<Item>> gennablePipes, boolean wild) {
      return PipeUtils.randomPipeForGen(gennablePipes, wild);
   }

   private static List<Pipe<Item>> getGenPipes(boolean wild) {
      return PipeUtils.getGenPipes(pipes, wild);
   }

   public static Item makeTieredMaybeNull(int tier, int attempts, long bannedBits) {
      for (int i = 0; i < attempts; i++) {
         Item maybe = makeGen();
         if (!maybe.isMissingno() && maybe.getTier() == tier && (bannedBits & maybe.getCollisionBits()) == 0L) {
            return maybe;
         }
      }

      return null;
   }

   public static Item makeTieredMaybeNull(int tier, long bannedBits) {
      return makeTieredMaybeNull(tier, 100, bannedBits);
   }

   public static Item makeFallback(int itemTier) {
      return makeFallback(itemTier, 0L);
   }

   public static Item makeFallback(int itemTier, long bannedCollisionBits) {
      Item pregen = makeTieredMaybeNull(itemTier, 30, bannedCollisionBits);
      if (pregen != null && !pregen.isMissingno()) {
         return cacheReturn(pregen);
      } else if (itemTier > 30) {
         return getMissingno();
      } else {
         int minTier = itemTier > 0 ? 1 : itemTier + 1;
         int maxTier = itemTier > 0 ? itemTier - 1 : -1;

         for (int i = 0; i < 50; i++) {
            if (itemTier % 2 == 0) {
               int attempts = 5;

               for (int i1 = 0; i1 < 5; i1++) {
                  Item half = ItemLib.randomWithExactQuality(1, itemTier / 2, 0L).get(0);
                  if (!half.isMissingno() && !ChoosableUtils.collides(half, bannedCollisionBits)) {
                     Item multid = DataSourceItem.multiMake(half, 2);
                     if (multid != null) {
                        return cacheReturn(multid);
                     }
                  }
               }
            }

            int tierA = Tann.randomInt(minTier, maxTier);
            int tierB = itemTier - tierA;
            Item a = ItemLib.randomWithExactQuality(1, tierA, 0L).get(0);
            Item b = ItemLib.randomWithExactQuality(1, tierB, 0L).get(0);
            if (!a.isMissingno()
               && !b.isMissingno()
               && !ChoosableUtils.collides(a, bannedCollisionBits)
               && !ChoosableUtils.collides(b, bannedCollisionBits)
               && (a.getCollisionBitsIncludingGenericTransformed() & b.getCollisionBitsIncludingGenericTransformed()) == 0L) {
               Item comb = PipeItemCombined.makeChecked(a, b);
               if (comb != null) {
                  return cacheReturn(comb);
               }
            }
         }

         return getMissingno();
      }
   }

   @Nonnull
   public static Item cacheReturn(Item i) {
      return ItemLib.byName(i.getName());
   }

   public static Item byCache(String text) {
      return pmc.get(text);
   }

   public static void clearCache() {
      pmc.cc();
   }
}
