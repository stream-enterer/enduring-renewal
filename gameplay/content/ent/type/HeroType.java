package com.tann.dice.gameplay.content.ent.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import java.util.List;
import java.util.Map;

public class HeroType extends EntType implements Cloneable, Choosable {
   public final HeroCol heroCol;
   public final int level;

   public HeroType(
      String name, int hp, AtlasRegion portrait, EntSide[] sides, List<Trait> traits, EntSize size, HeroCol heroCol, int level, Map<String, Integer> offsets
   ) {
      super(name, hp, portrait, sides, traits, size, offsets);
      this.heroCol = heroCol;
      this.level = level;
      this.setupStats();
   }

   @Override
   public boolean skipStats() {
      return this.level == 1 || super.skipStats();
   }

   public float getHpBasedEffectTierAdjustment() {
      float targetHp = HeroTypeUtils.getHpFor(this.level);
      float hpDiff = this.getEffectiveHp() - targetHp;
      float hpDiffRatio = hpDiff / targetHp;
      float targetStrength = HeroTypeUtils.getEffectTierFor(this.level);
      return targetStrength * 0.42F * hpDiffRatio;
   }

   @Override
   public String describe() {
      return "hero";
   }

   @Override
   public float chance() {
      return this.chance;
   }

   public float getTotalEffectTier() {
      return this.getAvgEffectTier() + this.getHpBasedEffectTierAdjustment();
   }

   @Override
   public String getColourTag() {
      return "[" + this.heroCol.colName + "]";
   }

   public Hero makeEnt() {
      if (this.hp == 0 || this.getName(false) == null || this.sides == null || this.sides.length != 6) {
         TannLog.log("Uhoh, bad ent type: " + this.getName(false) + ". It will probably throw an error soon.", TannLog.Severity.error);
      }

      return new Hero(this);
   }

   @Override
   public TextureRegion getAchievementIcon() {
      return null;
   }

   @Override
   public String getAchievementIconString() {
      return TextWriter.getTag(this.heroCol.col) + "H";
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Hero;
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      Hero h = this.makeEnt();
      Party p = dc.getParty();
      if (p.getMaxExtraHeroes() > 0) {
         p.addHero(h, HeroTypeUtils.byName("Coffin"), dc);
         if (!this.isGenerated()) {
            dc.getStatsManager().pickDelta(this, true);
         }

         if (DungeonScreen.get() != null) {
            FightLog f = DungeonScreen.get().getFightLog();
            f.resetDueToFiddling();
         }
      }
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   public boolean isGenerated() {
      for (Trait t : this.traits) {
         if (t.personal.isGenerated()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      if (big) {
         return new EntPanelInventory(this.makeEnt(), false);
      } else {
         Actor a = new Pixl(0).image(Images.plusBig, this.getColour()).gap(3).actor(new Pixl(0, 2).border(this.getColour()).image(this.portrait)).pix();
         a.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               EntPanelInventory dp = new EntPanelInventory(HeroType.this.makeEnt(), false);
               com.tann.dice.Main.getCurrentScreen().push(dp, 0.8F);
               Tann.center(dp);
               return true;
            }
         });
         return a;
      }
   }

   public float getBalanceRatio() {
      return this.getTotalEffectTier() / HeroTypeUtils.getEffectTierFor(this.level);
   }

   public static Color getIdCol() {
      return Colours.yellow;
   }

   @Override
   public boolean isMissingno() {
      return this == PipeHero.getMissingno();
   }

   public boolean isBannedFromLateStart() {
      for (Trait t : this.traits) {
         if (t.personal.bannedFromLateStart()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return this.level == 1 && !UnUtil.isLocked(this) ? true : this.getPicks(allMergedStats, true) + this.getPicks(allMergedStats, false) > 0;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      String tag = PickStat.nameFor(this);
      Stat s = allMergedStats.get(tag);
      return s == null ? 0 : PickStat.val(s, reject);
   }

   @Override
   public long getCollisionBits() {
      long result = 0L;

      for (int i = 0; i < this.traits.size(); i++) {
         result |= this.traits.get(i).personal.getCollisionBits(true);
      }

      for (int i = 0; i < this.sides.length; i++) {
         long bit = this.sides[i].getBaseEffect().getCollisionBits(true);
         result |= bit;
      }

      return result;
   }

   @Override
   public int getTier() {
      return this.level;
   }

   @Override
   public float getModTier() {
      return TierUtils.extraHeroModTier(this.getTier());
   }

   @Override
   public boolean isPositive() {
      return true;
   }

   @Override
   public String getTierString() {
      return Words.getTierString(this.getTier(), false);
   }

   public String makeBadHashString() {
      StringBuilder result = new StringBuilder();
      result.append(this.hp);

      for (EntSide es : this.sides) {
         result.append("/").append(EntSide.badHash(es.getBaseEffect()));
      }

      return result.toString();
   }

   @Override
   public Color getColour() {
      return this.heroCol.col;
   }

   public Ability getAbility() {
      for (Trait t : this.traits) {
         Ability a = t.personal.getAbility();
         if (a != null) {
            return a;
         }
      }

      return null;
   }

   public Spell getSpell() {
      Ability a = this.getAbility();
      return a instanceof Spell ? (Spell)a : null;
   }

   public Tactic getTactic() {
      Ability a = this.getAbility();
      return a instanceof Tactic ? (Tactic)a : null;
   }

   @Override
   protected float getBaseHpForCalc() {
      return this.hp % 2 == 0 ? this.hp : this.hp - 0.13F;
   }

   private float getStdDev() {
      float avg = 0.0F;

      for (int i = 0; i < this.sides.length; i++) {
         EntSide side = this.sides[i];
         avg += this.affectSideCalcForStdDev(side.getApproxTotalEffectTier(this));
      }

      avg /= 6.0F;
      float totalDiff = 0.0F;

      for (int i = 0; i < this.sides.length; i++) {
         EntSide side = this.sides[i];
         totalDiff = (float)(totalDiff + Math.pow(this.affectSideCalcForStdDev(side.getEffectTier(this)) - avg, 2.0));
      }

      return (float)Math.sqrt(totalDiff / 6.0F);
   }

   private float affectSideCalcForStdDev(float v) {
      return v < 0.0F ? v * 0.05F : v;
   }

   @Override
   public float getVariance() {
      float r = this.getStdDev() / this.getFactorThing();
      if (Float.isNaN(r)) {
         return 0.0F;
      } else {
         r = (float)(Math.pow(Math.abs(r), 7.0) * 0.0067);
         return Math.min(r, HeroTypeUtils.getEffectTierFor(this.getTier()) * 0.2F);
      }
   }

   private float getFactorThing() {
      float avg = 0.0F;

      for (EntSide side : this.sides) {
         avg += this.affectSideCalcForStdDev(side.getApproxTotalEffectTier(this));
      }

      return avg / 6.0F;
   }
}
