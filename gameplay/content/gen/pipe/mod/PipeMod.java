package com.tann.dice.gameplay.content.gen.pipe.mod;

import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeMaster;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.content.gen.pipe.mod.keyword.PipeModAllKeyword;
import com.tann.dice.gameplay.content.gen.pipe.mod.keyword.PipeModKeywordSide;
import com.tann.dice.gameplay.content.gen.pipe.mod.level.PipeModLevelRangeRegex;
import com.tann.dice.gameplay.content.gen.pipe.mod.level.PipeModLevelRegex;
import com.tann.dice.gameplay.content.gen.pipe.mod.level.PipeModNthFight;
import com.tann.dice.gameplay.content.gen.pipe.mod.level.PipeModNthFightShifted;
import com.tann.dice.gameplay.content.gen.pipe.mod.meta.PipeModPerBoss;
import com.tann.dice.gameplay.content.gen.pipe.mod.meta.PipeModPerLevel;
import com.tann.dice.gameplay.content.gen.pipe.mod.meta.PipeModPerTurn;
import com.tann.dice.gameplay.content.gen.pipe.mod.meta.PipeModSplice;
import com.tann.dice.gameplay.content.gen.pipe.mod.meta.PipeModSpliceItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.phase.PipeModDifficulty;
import com.tann.dice.gameplay.content.gen.pipe.mod.phase.PipeModPhaseHardcoded;
import com.tann.dice.gameplay.content.gen.pipe.mod.phase.PipeModPhaseIndexed;
import com.tann.dice.gameplay.content.gen.pipe.mod.phase.PipeModPhaseModPick;
import com.tann.dice.gameplay.content.gen.pipe.mod.pool.PipeModHeroPool;
import com.tann.dice.gameplay.content.gen.pipe.mod.pool.PipeModItemPool;
import com.tann.dice.gameplay.content.gen.pipe.mod.pool.PipeModMonsterPool;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaBracketed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaDocument;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRandomTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRename;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaSetTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaX;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceModifier;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.trigger.global.GlobalAddFight;
import com.tann.dice.gameplay.trigger.global.GlobalDescribeOnly;
import com.tann.dice.gameplay.trigger.global.GlobalWishEnabled;
import com.tann.dice.gameplay.trigger.global.changeHero.GlobalChangeHeroAll;
import com.tann.dice.gameplay.trigger.global.changeHero.effects.KillHero;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.global.phase.GlobalSkipPhase;
import com.tann.dice.gameplay.trigger.global.scaffolding.CurseLevelModulus;
import com.tann.dice.gameplay.trigger.global.weird.GlobalHidden;
import com.tann.dice.gameplay.trigger.global.weird.GlobalTemporary;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PipeMod {
   public static final float GEN_FRACTION = 0.21F;
   public static List<Pipe<Modifier>> pipes;
   private static Modifier missingno;
   private static PipeCache<Modifier> pmc;

   @Nonnull
   public static Modifier fetch(String modifierName) {
      return Pipe.checkPipes(pipes, modifierName, pmc, getMissingno());
   }

   public static Modifier fetchDesigned(String modifierName) {
      return pipes.get(0).get(modifierName);
   }

   public static void init(List<Modifier> designed) {
      DataSource<Modifier> ds = new DataSourceModifier();
      pipes = new ArrayList<>();
      pipes.add(new PipeMaster<>(designed));
      pipes.add(pmc = new PipeCache<>());
      pipes.addAll(PipeMetaDocument.makeAll(ds));
      pipes.addAll(PipeMetaRename.makeAll(ds));
      pipes.add(new PipeModAllKeyword(true));
      pipes.add(new PipeModAllKeyword(false));
      pipes.add(new PipeModKeywordSide());
      pipes.add(new PipeModHeroPos());
      pipes.add(new PipeModDifficulty());
      pipes.add(new PipeModSidePos());
      pipes.add(new PipeModPhaseHardcoded());
      pipes.add(new PipeModPhaseIndexed());
      pipes.add(new PipeModPhaseModPick());
      pipes.addAll(PipeMetaSetTier.makeAll(ds));
      pipes.addAll(PipeMetaBracketed.makeAll(ds));
      pipes.add(new PipeModLevelRangeRegex());
      pipes.add(new PipeModLevelRegex());
      pipes.add(new PipeModSplice());
      pipes.add(new PipeModSpliceItem());
      pipes.add(new PipeModNthFight());
      pipes.add(new PipeModNthFightShifted());
      pipes.add(new PipeModTurn());
      pipes.add(new PipeModTurnEvery());
      pipes.add(new PipeModInverted());
      pipes.add(new PipeModPart());
      pipes.add(new PipeModZone());
      pipes.add(new PipeModDelivery());
      pipes.add(new PipeModRandom());
      pipes.add(new PipeModPerLevel());
      pipes.add(new PipeModPerBoss());
      pipes.add(new PipeModPerTurn());
      pipes.addAll(PipeMetaX.makeAll(ds));
      pipes.add(new PipeModUnpack());
      pipes.add(new PipeModEndTurnSpell());
      pipes.add(new PipeModHeroPool());
      pipes.add(new PipeModItemPool());
      pipes.add(new PipeModMonsterPool());
      pipes.add(new PipeModPermaItem());
      pipes.add(new PipeModAddMonsterMeta());
      pipes.add(new PipeModAllItem(true));
      pipes.add(new PipeModAllItem(false));
      pipes.add(new PipeModBecome());
      pipes.add(new PipeModAllItemsAre());
      pipes.add(new PipeModAddHero());
      pipes.add(new PipeModCombined());
      pipes.add(new PipeModSpirit());
      pipes.add(new PipeModSetFight());
      pipes.add(new PipeModSetParty());
      pipes.add(new PipeModGainChoosable());
      pipes.addAll(PipeMetaIndexed.makeAll(ds));
      pipes.add(new PipeMetaRandomTier<>(ds));
      pipes.add(new PipeMaster<>(makeHiddenModifiers()));
      missingno = makeMissingnoCurse();
   }

   private static List<Modifier> makeHiddenModifiers() {
      return Arrays.asList(
         new Modifier("Skip", new GlobalDescribeOnly("[b][orange]skip")),
         new Modifier("Wish", new GlobalWishEnabled()),
         new Modifier("Clear Party", new GlobalChangeHeroAll(new KillHero())),
         new Modifier("Temporary", new GlobalTemporary()),
         new Modifier("Hidden", new GlobalHidden()),
         new Modifier("Skip All", new GlobalSkipPhase(true)),
         new Modifier("Add Fight", new GlobalAddFight(1)),
         new Modifier("Add 10 Fights", new GlobalAddFight(10)),
         new Modifier("Add 100 Fights", new GlobalAddFight(100)),
         new Modifier("Minus Fight", new GlobalAddFight(-1)),
         new Modifier("Cursemode Loopdiff", new CurseLevelModulus(20)),
         new Modifier(ModTierUtils.missingHero(5.0F), "Missing", new GlobalChangeHeroAll(new KillHero()))
      );
   }

   private static Modifier makeMissingnoCurse() {
      return new Modifier(-6.0F, "BUG", new GlobalHeroes(new AffectSides(SpecificSidesType.Top, new AddKeyword(Keyword.nothing))));
   }

   public static Modifier getMissingno() {
      return missingno;
   }

   public static Modifier makeGen(boolean wild) {
      int attempts = 20;
      List<Pipe<Modifier>> gennablePipes = getGenPipes(wild);

      for (int i = 0; i < 20; i++) {
         Pipe<Modifier> pm = randomPipeForGen(gennablePipes, wild);
         Modifier m = pm.generate(wild);
         if (m != null && !m.isMissingno() && m.getTier() != 0) {
            return fetch(m.getName());
         }
      }

      return getMissingno();
   }

   private static Pipe<Modifier> randomPipeForGen(List<Pipe<Modifier>> gennablePipes, boolean wild) {
      return PipeUtils.randomPipeForGen(gennablePipes, wild);
   }

   public static boolean roundtrip(Modifier m) {
      Modifier rounded = ModifierLib.byName(m.getName());
      return m.getFloatTier() == rounded.getFloatTier() && m.getFullDescription().equals(rounded.getFullDescription());
   }

   public static List<Pipe<Modifier>> getGenPipes(boolean wild) {
      return PipeUtils.getGenPipes(pipes, wild);
   }

   public static Modifier randomDesigned() {
      return pipes.get(0).example();
   }

   public static Integer getMin(Boolean blessing) {
      if (blessing == null) {
         return -20;
      } else {
         return blessing ? 1 : null;
      }
   }

   public static Integer getMax(Boolean blessing) {
      if (blessing == null) {
         return 20;
      } else {
         return blessing ? null : -1;
      }
   }

   public static List<Modifier> makeGenerated(int total, Boolean blessing, boolean wild) {
      return makeGenerated(total, getMin(blessing), getMax(blessing), wild);
   }

   public static List<Modifier> makeGenerated(int total, Integer min, Integer max, boolean wild) {
      List<Modifier> mods = new ArrayList<>();

      for (int i = 0; i < total; i++) {
         int attempts = 10;

         for (int i1 = 0; i1 < 10; i1++) {
            Modifier m = makeGen(wild);
            if (m != null && !m.isMissingno() && ModifierLib.isWithin(m, min, max)) {
               mods.add(m);
               break;
            }
         }
      }

      return mods;
   }

   public static Modifier byCache(String text) {
      return pmc.get(text);
   }

   public static void clearCache() {
      pmc.cc();
   }
}
