package com.tann.dice.gameplay.content.ent.type;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterJinx;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.chance.GlobalRarity;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntType implements Unlockable {
   private final String name;
   public final int hp;
   public final AtlasRegion portrait;
   public final EntSide[] sides;
   public final List<Trait> traits;
   public final EntSize size;
   protected float chance;
   public final Map<String, Integer> offsets;
   private float effectiveHp;
   protected float avgEffectTier;
   protected float avgEffectTierWithPassives;
   private int calcBackRow;
   private float withOneRollRemaining;

   public EntType(String name, int hp, AtlasRegion portrait, EntSide[] sides, List<Trait> traits, EntSize size, Map<String, Integer> offsets) {
      this.name = name;
      this.hp = hp;
      this.sides = sides;
      this.traits = traits;
      this.size = size;
      this.portrait = portrait;
      this.offsets = offsets;
   }

   public void setupStats() {
      this.effectiveHp = this.getHpForCalc();

      for (Trait t : this.traits) {
         this.calcBackRow = Math.max(this.calcBackRow, t.personal.calcBackRowTurn());
      }

      this.setupAvg();
      this.chance = this.calculateChance();
   }

   protected float calculateChance() {
      List<Global> globals = new ArrayList<>();

      for (Trait t : this.traits) {
         Global gt = t.personal.getGlobalFromPersonalTrigger();
         if (gt != null) {
            globals.add(gt);
         }
      }

      return GlobalRarity.listChance(globals);
   }

   public float getEffectiveHp() {
      return this.effectiveHp;
   }

   public float getAvgEffectTier() {
      return this.getAvgEffectTier(true);
   }

   public float getAvgEffectTier(boolean includePassives) {
      return includePassives ? this.avgEffectTierWithPassives : this.avgEffectTier;
   }

   public boolean calcBackRow(int turn) {
      return this.calcBackRow > turn;
   }

   public EntSide[] getNiceSides() {
      EntSide[] result = new EntSide[6];
      System.arraycopy(this.sides, 0, result, 0, 6);
      realToNice(result);
      return result;
   }

   public static void niceToReal(EntSide[] sides) {
      Tann.swap(sides, 0, 2);
      Tann.swap(sides, 1, 4);
      Tann.swap(sides, 1, 3);
   }

   public static void realToNice(EntSide[] sides) {
      Tann.swap(sides, 1, 3);
      Tann.swap(sides, 1, 4);
      Tann.swap(sides, 0, 2);
   }

   public boolean skipStats() {
      return false;
   }

   protected abstract String getColourTag();

   protected void setupAvg() {
      float[] vals = new float[this.sides.length];

      for (int i = 0; i < this.sides.length; i++) {
         vals[i] = this.sides[i].getEffectTier(this);
      }

      if (this instanceof HeroType) {
         float v3 = this.getRollResult(vals, 3);
         float v2 = this.withOneRollRemaining;
         this.avgEffectTier = Math.min(v3, v2 * 1.1F);
      } else {
         this.avgEffectTier = this.getRollResult(vals, 1);
      }

      for (int i = 0; i < this.sides.length; i++) {
         this.avgEffectTier = this.avgEffectTier + this.sides[i].getExtraFlatEffectTier(this);
      }

      float avgPips = this.getAveragePips();
      this.avgEffectTierWithPassives = this.avgEffectTier;

      for (Trait tt : this.traits) {
         this.avgEffectTierWithPassives = tt.personal.getStrengthCalc(this.avgEffectTierWithPassives, avgPips, this);
      }
   }

   public float getVariance() {
      return 0.0F;
   }

   private float getRollResult(float[] sideValues, int rolls) {
      return this.getRollResult(sideValues, rolls, Float.NEGATIVE_INFINITY);
   }

   private float getRollResult(float[] sideValues, int remainingRolls, float currentValue) {
      if (remainingRolls == 1) {
         this.withOneRollRemaining = currentValue;
      }

      if (remainingRolls == 0) {
         return currentValue;
      } else {
         float total = 0.0F;

         for (float val : sideValues) {
            total += Math.max(val, currentValue);
         }

         float avg = total / sideValues.length;
         return this.getRollResult(sideValues, remainingRolls - 1, avg);
      }
   }

   private float getAveragePips() {
      float result = 0.0F;

      for (EntSide s : this.sides) {
         result += s.getBaseEffect().getValue();
      }

      float var6;
      return var6 = result / 6.0F;
   }

   private float getHpForCalc() {
      float result = this.getBaseHpForCalc();

      for (Trait t : this.traits) {
         result = t.personal.getTotalHpCalc(result, this);
      }

      return result;
   }

   protected float getBaseHpForCalc() {
      return this.hp;
   }

   @Override
   public String toString() {
      return this.getName(false);
   }

   public abstract Ent makeEnt();

   public String getName() {
      return this.getName(false);
   }

   public String getName(boolean forDisplay, boolean andCol) {
      if (!forDisplay) {
         return this.name;
      } else {
         String workingName = this.name;
         boolean shouldTranslate = !this.name.contains(".");

         for (Trait t : this.traits) {
            String traitOverride = t.personal.getDisplayName(workingName);
            if (traitOverride != null) {
               workingName = traitOverride;
               shouldTranslate = false;
            }
         }

         if (shouldTranslate) {
            workingName = com.tann.dice.Main.t(workingName);
         }

         return andCol ? this.getColourTag() + workingName + "[cu]" : workingName;
      }
   }

   public String getName(boolean forDisplay) {
      return this.getName(forDisplay, forDisplay);
   }

   public String getSaveString() {
      if (DungeonScreen.tinyPasting) {
         String tiny = PipeMetaIndexed.tinyName(this);
         if (tiny != null && tiny.length() <= this.getName(false).length()) {
            return tiny;
         }
      }

      return this.getName(false);
   }

   public float getChance() {
      return this.chance;
   }

   public boolean sameForStats(EntType type) {
      return type == this || PipeMonsterJinx.isJinx(type) && PipeMonsterJinx.isJinx(this);
   }

   @Override
   public Actor makeUnlockActor(boolean big) {
      if (big) {
         return new EntPanelInventory(this.makeEnt());
      } else {
         Actor a = new Pixl(0, 2).border(this.getColour()).image(this.portrait, this instanceof MonsterType).pix();
         a.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               Actor dp = EntType.this.makeUnlockActor(true);
               com.tann.dice.Main.getCurrentScreen().push(dp, 0.7F);
               Tann.center(dp);
               Sounds.playSound(Sounds.pip);
               return true;
            }
         });
         return a;
      }
   }

   public abstract Color getColour();

   public boolean hasArt() {
      return !this.portrait.name.contains("placeholder");
   }

   public abstract boolean isMissingno();

   public abstract long getCollisionBits();
}
