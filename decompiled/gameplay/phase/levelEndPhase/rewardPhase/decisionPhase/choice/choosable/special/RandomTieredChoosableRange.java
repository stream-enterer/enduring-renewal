package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierPickContext;
import com.tann.dice.gameplay.modifier.ModifierPickUtils;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGeneratorLevelup;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RandomTieredChoosableRange implements Choosable {
   final int l;
   final int u;
   final int n;
   public final ChoosableType ty;

   public RandomTieredChoosableRange() {
      this(0, -1, -1, null);
   }

   public RandomTieredChoosableRange(int min, int max, int amount, ChoosableType choosableType) {
      this.l = min;
      this.u = max;
      this.n = amount;
      this.ty = choosableType;
   }

   @Override
   public boolean isPositive() {
      return this.l >= 0;
   }

   @Override
   public Color getColour() {
      return this.ty == ChoosableType.Levelup ? Colours.text : this.ty.getColour(this.l);
   }

   @Override
   public String getSaveString() {
      return this.l + "~" + this.u + "~" + this.n + "~" + this.ty.getTag();
   }

   public static Choosable byName(String saveString) {
      String[] parts = saveString.split("~");
      return new RandomTieredChoosableRange(
         Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), ChoosableType.fromTag(parts[3].charAt(0))
      );
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.RandomRange;
   }

   private int getRolledTier() {
      return Tann.randomInt(this.l, this.u);
   }

   private float getAvgTier() {
      return (this.u + this.l) / 2.0F;
   }

   private int getAvgTierInt() {
      return Math.round((this.u + this.l) / 2.0F);
   }

   private List<Choosable> generateChoosables(DungeonContext dc) {
      int t = this.getRolledTier();
      switch (this.ty) {
         case Item:
            List<Item> items = ItemLib.randomWithExactQuality(this.n, t, dc);
            return new ArrayList<>(items);
         case Modifier:
            List<Modifier> mods = ModifierPickUtils.generateModifiers(t, this.n, ModifierPickContext.Difficulty_But_Midgame, dc);
            return new ArrayList<>(mods);
         case Levelup:
            return Arrays.asList(PhaseGeneratorLevelup.getRandom(dc));
         case Hero:
            return new ArrayList<>(Arrays.asList(HeroTypeUtils.getRandom(Tann.random(HeroCol.basics()), t)));
         default:
            throw new RuntimeException("Unimplemented");
      }
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      List<Choosable> generatedChoosables = this.generateChoosables(dc);
      ModifierLib.getCache().decacheChoosables(generatedChoosables);

      for (Choosable actualChoosable : generatedChoosables) {
         actualChoosable.onChoose(dc, index);
      }

      if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
         PhaseManager.get().forceNext(new RandomRevealPhase(generatedChoosables));
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      return new Pixl(0, 2).border(this.getColour()).text(this.getName(), 55).pix();
   }

   @Override
   public int getTier() {
      return (this.l + this.u) * this.n / 2;
   }

   @Override
   public float getModTier() {
      float part = TierUtils.toModTier(this.ty, this.getAvgTierInt()) * this.n;
      return part * (part > 0.0F ? 0.95F : 1.1F);
   }

   @Override
   public String describe() {
      String start = (this.n == 1 ? "a" : this.n + "x") + " random";
      String tierString = "tier " + this.getTierString();
      String end = Words.plural(this.ty.niceName(this.getAvgTierInt()), this.n).toLowerCase();
      return start + " " + tierString + " " + end;
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   private boolean includeCol() {
      return this.ty != ChoosableType.Levelup;
   }

   @Override
   public String getTierString() {
      boolean col = this.includeCol();
      return Words.getTierString(this.l, col) + (col ? "-" : "-") + Words.getTierString(this.u, col);
   }

   @Override
   public String getName() {
      return this.describe();
   }

   @Override
   public long getCollisionBits() {
      return 0L;
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return false;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      return 0;
   }
}
