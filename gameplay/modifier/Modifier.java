package com.tann.dice.gameplay.modifier;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Modifier implements Unlockable, Choosable {
   protected final int tier;
   protected final float floatTier;
   protected final String name;
   protected final List<Global> globals;
   protected float chance;
   private final String essence;
   public static final float UNRATED_TIER = -0.069F;

   public Modifier(String name, Global... globals) {
      this(-0.069F, name, Arrays.asList(globals));
   }

   public Modifier(float tier, String name, Global... globals) {
      this(tier, name, Arrays.asList(globals));
   }

   public Modifier(float tier, String name, List<Global> globals) {
      this.tier = Math.round(tier);
      this.floatTier = tier;
      this.name = name;
      this.globals = new ArrayList<>(globals);
      this.essence = ModifierUtils.extractEssence(this);
      if (this.isComplexChain()) {
         this.rarity(Rarity.THIRD);
      }

      this.calculateChance();
   }

   private boolean isComplexChain() {
      return this.name.contains("^") && this.name.contains("/") && !this.name.contains(".") && !this.name.contains("&");
   }

   public List<Global> getGlobals() {
      return this.globals;
   }

   public Color getBorderColour() {
      return this.getMType().c;
   }

   public ModifierType getMType() {
      if (this.floatTier == -0.069F) {
         return ModifierType.Unrated;
      } else if (this.tier > 0) {
         return ModifierType.Blessing;
      } else {
         return this.tier < 0 ? ModifierType.Curse : ModifierType.Tweak;
      }
   }

   public void playChooseSound() {
      switch (this.getMType()) {
         case Blessing:
            Sounds.playSound(Sounds.magic);
            break;
         case Curse:
            Sounds.playSound(Sounds.deboost);
            break;
         default:
            Sounds.playSound(Sounds.bats);
      }
   }

   public TextureRegion getLevelEndButtonIcon() {
      switch (this.getMType()) {
         case Blessing:
            return Images.phaseBlessingIcon;
         case Curse:
            return Images.phaseCurseIcon;
         case Tweak:
         default:
            return Images.phaseTweakIcon;
      }
   }

   @Override
   public String getName() {
      return this.getName(false);
   }

   public String getName(boolean display) {
      return display ? this.getDisplayName() : this.name;
   }

   private String getDisplayName() {
      String originalName = this.getName(false);
      String name = originalName;

      for (int i = 0; i < this.globals.size(); i++) {
         name = this.globals.get(i).overrideDisplayName(name);
      }

      if (name.equals(originalName)) {
         if (!originalName.contains(".")) {
            name = com.tann.dice.Main.t(name);
         } else if (com.tann.dice.Main.self().translator.shouldTranslate()) {
            name = translatePrefixes(name);
         }
      }

      return name;
   }

   private static String translatePrefixes(String input) {
      if (!input.contains(".")) {
         return com.tann.dice.Main.t(input);
      } else {
         String[] commonPrefixes = new String[]{
            "row",
            "col",
            "top",
            "bot",
            "mid",
            "topbot",
            "rightmost",
            "right2",
            "top2",
            "mid2",
            "2x",
            "3x",
            "4x",
            "5x",
            "i",
            "add",
            "bottom",
            "h",
            "hero",
            "monster",
            "11-20",
            "1st",
            "2nd",
            "3rd",
            "learn"
         };

         for (String prefix : commonPrefixes) {
            if (input.startsWith(prefix + ".")) {
               return com.tann.dice.Main.t(prefix) + "." + translatePrefixes(input.substring(prefix.length() + 1));
            }
         }

         return input;
      }
   }

   public ModifierPanel getPanel() {
      return new ModifierPanel(this, false);
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      return new ModifierPanel(this, big);
   }

   @Override
   public String getAchievementIconString() {
      return TextWriter.getTag(this.getBorderColour()) + (this.getMType() == ModifierType.Curse ? "C" : "B");
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      dc.addModifier(this);
   }

   @Override
   public void onReject(DungeonContext dc) {
      dc.getStatsManager().pickDelta(this, false);
   }

   public void onPickEffects(DungeonContext dc) {
      if (!TestRunner.isTesting()) {
         List<Global> globs = new ArrayList<>(this.getGlobals());
         Snapshot.addLinked(globs, dc.getCurrentLevelNumber(), dc, -1);

         for (Global gt : globs) {
            gt.onPick(dc);
         }

         DungeonScreen ds = DungeonScreen.get();
         if (ds != null) {
            FightLog f = ds.getFightLog();
            f.updateOutOfCombat();
            f.refreshPresentBaseStats();
            ds.refreshTopButtonsPanel();
         }
      }
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return new ModifierPanel(this, big);
   }

   @Override
   public int getTier() {
      return this.tier;
   }

   @Override
   public float getModTier() {
      return this.getFloatTier();
   }

   @Override
   public final String describe() {
      return TextWriter.getTag(this.getBorderColour()) + ModifierUtils.describe(this.tier);
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Modifier;
   }

   @Override
   public String getTierString() {
      return this.getMType() == ModifierType.Unrated ? "[orange]/[cu]" : Words.getTierString(this.tier, true);
   }

   @Override
   public long getCollisionBits() {
      long bit = 0L;

      for (Trigger t : this.globals) {
         bit |= t.getCollisionBits();
      }

      if ((bit & Collision.GENERIC_ALL_SIDES_HERO) > 0L) {
         bit |= Collision.ALL_SIDES_HERO_COMPOSITE;
      }

      return bit;
   }

   @Override
   public float chance() {
      return this.chance;
   }

   public boolean skipTest() {
      for (Global gt : this.getGlobals()) {
         if (gt.skipTest()) {
            return true;
         }
      }

      return false;
   }

   public List<Keyword> getReferencedKeywords() {
      List<Keyword> keywords = new ArrayList<>();

      for (Global gt : this.globals) {
         keywords.addAll(gt.getReferencedKeywords());
      }

      return keywords;
   }

   public String getFullDescription() {
      try {
         return Trigger.describeTriggers(new ArrayList<>(this.globals));
      } catch (Exception var2) {
         var2.printStackTrace();
         if (TestRunner.isTesting()) {
            throw var2;
         } else {
            return var2.getClass().getSimpleName();
         }
      }
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return true;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      String tag = PickStat.nameFor(this);
      Stat s = allMergedStats.get(tag);
      return s == null ? 0 : PickStat.val(s, reject);
   }

   @Override
   public String getSaveString() {
      if (DungeonScreen.tinyPasting) {
         String tiny = PipeMetaIndexed.tinyName(this);
         if (tiny != null && tiny.length() <= this.getName(false).length()) {
            return tiny;
         }
      }

      return this.getName();
   }

   @Override
   public boolean isPositive() {
      return this.tier >= 0;
   }

   @Override
   public Color getColour() {
      return this.getBorderColour();
   }

   public Modifier rarity(Rarity rarity) {
      if (rarity != null && rarity != Rarity.ONE) {
         GlobalRarity existing = null;

         for (int i = 0; i < this.globals.size(); i++) {
            Global g = this.globals.get(i);
            if (g instanceof GlobalRarity) {
               existing = (GlobalRarity)g;
               break;
            }
         }

         if (existing == null) {
            this.globals.add(GlobalRarity.fromRarity(rarity));
         } else {
            Rarity nr = Rarity.fromChance(rarity.getValue() * existing.chance());
            this.globals.set(this.globals.indexOf(existing), GlobalRarity.fromRarity(nr));
         }

         this.calculateChance();
         return this;
      } else {
         return this;
      }
   }

   private void calculateChance() {
      this.chance = GlobalRarity.listChance(this.globals);
   }

   public boolean isMissingno() {
      return this == ModifierLib.getMissingno();
   }

   public Eff getSingleEffOrNull() {
      for (int i = 0; i < this.globals.size(); i++) {
         Eff e = this.globals.get(i).getSingleEffOrNull();
         if (e != null) {
            return e;
         }
      }

      return null;
   }

   public Global getSingleGlobalOrNull() {
      Global result = null;
      List<Global> globs = this.getGlobals();

      for (int i = 0; i < globs.size(); i++) {
         Global g = globs.get(i);
         if (!g.metaOnly()) {
            if (result != null) {
               return null;
            }

            result = g;
         }
      }

      return result;
   }

   public float getFloatTier() {
      return this.floatTier == -0.069F ? 0.0F : this.floatTier;
   }

   public boolean hasFractionalTier() {
      return this.getFloatTier() != this.getTier();
   }

   public boolean isOnPick() {
      for (int i = 0; i < this.globals.size(); i++) {
         if (this.globals.get(i).isOnPick()) {
            return true;
         }
      }

      return false;
   }

   public boolean allTurnsOnly() {
      for (int i = 0; i < this.globals.size(); i++) {
         if (this.globals.get(i).allTurnsOnly()) {
            return true;
         }
      }

      return false;
   }

   public boolean allLevelsOnly() {
      for (int i = 0; i < this.globals.size(); i++) {
         if (this.globals.get(i).allLevelsOnly()) {
            return true;
         }
      }

      return false;
   }

   public boolean isMultiplable(boolean liberal) {
      return Trigger.checkMultiplability(this.globals, liberal);
   }

   public String getEssence() {
      return this.essence;
   }

   public boolean skipNotifyRandomReveal() {
      for (int i = 0; i < this.globals.size(); i++) {
         if (this.globals.get(i).skipNotifyRandomReveal()) {
            return true;
         }
      }

      return false;
   }

   public boolean isHidden() {
      for (int i = 0; i < this.globals.size(); i++) {
         if (this.globals.get(i).isHidden()) {
            return true;
         }
      }

      return false;
   }
}
