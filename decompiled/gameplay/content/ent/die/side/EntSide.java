package com.tann.dice.gameplay.content.ent.die.side;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.TargetingType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.trigger.personal.Dodge;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.Stunned;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.bullet.DieShader;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EntSide extends Side {
   private final TextureRegion tr;
   private final Eff baseEffect;
   public EntSize size;
   private final HSL hsl;
   private static ShaderProgram facadeProgram;
   static final Color MULT_COL = Colours.orange;
   List<Keyword> tmp = new ArrayList<>();
   static final Color pipCol = Colours.light;
   private boolean generic;

   public EntSide(TextureRegion tr, Eff effect, EntSize size) {
      this(tr, effect, size, null);
   }

   public EntSide(TextureRegion tr, Eff effect, EntSize size, HSL hsl) {
      if (tr == null) {
         tr = ImageUtils.loadExt3d(size.name() + "/face/placeholder");
      }

      if (hsl != null) {
         ensureFacadeProgramSet();
      }

      this.tr = tr;
      this.baseEffect = effect.copy();
      this.size = size;
      this.hsl = hsl;
   }

   private static void ensureFacadeProgramSet() {
      if (facadeProgram == null) {
         String vert = Gdx.files.internal("shader/fx/hsl/vertex.glsl").readString();
         String frag = Gdx.files.internal("shader/fx/hsl/fragment.glsl").readString();
         facadeProgram = new ShaderProgram(vert, frag);
      }
   }

   public EntSide copy() {
      return new EntSide(this.tr, this.baseEffect, this.size, this.hsl);
   }

   public static EntSide[] copy(EntSide[] sides) {
      EntSide[] result = new EntSide[sides.length];

      for (int i = 0; i < sides.length; i++) {
         result[i] = sides[i].copy();
      }

      return result;
   }

   private void drawTexture(Batch batch, float x, float y) {
      batch.setColor(Colours.z_white);
      if (facadeProgram != null && this.hsl != null) {
         batch.end();
         batch.setShader(facadeProgram);
         batch.begin();
         facadeProgram.setUniformf("u_hsl", this.hsl.a, this.hsl.b, this.hsl.c);
      }

      batch.draw(this.getTexture2D(), x, y);
      if (facadeProgram != null && this.hsl != null) {
         batch.end();
         batch.setShader(null);
         batch.begin();
      }
   }

   public void drawWithMultiplier(Batch batch, float x, float y, Color col, int multipler) {
      super.draw(batch, null, x, y, col, null);
      this.drawTexture(batch, x, y);
      batch.setColor(MULT_COL);

      for (int i = 0; i < multipler; i++) {
         batch.draw(Images.multiplier, x + this.getTexture2D().getRegionWidth() - 5.0F, y + 2.0F + i * 4);
      }

      if (Math.abs(multipler) == 1) {
         batch.setColor(MULT_COL);
         batch.draw(Images.keepKeywords, x + this.getTexture2D().getRegionWidth() - 4.0F, y + 2.0F);
      }
   }

   public void drawWithBonus(Batch batch, float x, float y, Color col, int add) {
      super.draw(batch, null, x, y, col, null);
      this.drawTexture(batch, x, y);
      batch.setColor(Colours.light);

      for (int i = 0; i < add; i++) {
         batch.draw(Images.plus, x + this.getTexture2D().getRegionWidth() - 5.0F, y + 2.0F + i * 4);
      }
   }

   public static void drawBonusKeyword(Batch batch, float x, float y, Keyword keyword, int index) {
      drawBonusKeyword(batch, x, y, keyword, index, EntSize.reg);
   }

   public static void drawBonusKeyword(Batch batch, float x, float y, Keyword keyword, int index, EntSize size) {
      TextureRegion image = ImageUtils.get2DIfPossible(keyword.getImage(size));
      int diffW = size.getPixels() - image.getRegionWidth();
      int diffH = size.getPixels() - image.getRegionHeight();
      int xFlip = index > 1 ? 1 : 0;
      int yFlip = index != 1 && index != 2 ? 0 : 1;
      batch.setColor(Colours.z_white);
      boolean flip = keyword.isFlipCorner() || size == EntSize.small;
      Draw.drawFlipped(batch, image, x + 1.0F + xFlip * (diffW - 2), y + 1.0F + yFlip * (diffH - 2), xFlip == 1 && flip, yFlip == 1 && flip);
   }

   @Override
   public void draw(Batch batch, Ent source, float x, float y, Color colour, TextureRegion lapel2D) {
      super.draw(batch, source, x, y, colour, lapel2D);
      Eff e = this.getBaseEffect();
      TextureRegion tex = this.getTexture2D();
      int bonus = 0;
      Color bc = Colours.purple;
      this.tmp.clear();
      List<Keyword> bonusKeywords = this.tmp;
      HSL tmpHsl = this.hsl;
      if (source != null) {
         EntSideState ss = this.findState(FightLog.Temporality.Visual, source);
         bonus = ss.getTotalBonus();
         bc = DieShader.bonusColDisplay(ss.getBonusColIndex());
         if (bc == Colours.dark) {
            bc = Colours.light;
         }

         e = ss.getCalculatedEffect();
         bonusKeywords.addAll(ss.getBonusKeywords());
         tex = ss.getCalculatedTexture();
         tex = ImageUtils.get2DIfPossible(tex);
         if (!ss.ownerDead()) {
            tmpHsl = ss.getHsl();
         }
      } else {
         bonusKeywords.addAll(this.getBaseEffect().getBonusKeywords());
      }

      Tann.clearDupes(bonusKeywords);
      batch.setColor(Colours.z_white);
      if (facadeProgram != null && tmpHsl != null) {
         batch.end();
         batch.setShader(facadeProgram);
         batch.begin();
         facadeProgram.setUniformf("u_hsl", tmpHsl.a, tmpHsl.b, tmpHsl.c);
      }

      int drawY = (int)y;
      drawY -= tex.getRegionHeight() - this.getSZ(source);
      batch.draw(tex, x, drawY);
      if (facadeProgram != null && tmpHsl != null) {
         batch.end();
         batch.setShader(null);
         batch.begin();
      }

      EntSize size;
      if (source != null) {
         size = source.getSize();
      } else {
         int px = tex.getRegionWidth();
         EntSize fromPx = EntSize.getFromPx(px);
         if (fromPx != null) {
            size = fromPx;
         } else {
            size = EntSize.reg;
         }
      }

      if (bonusKeywords != null) {
         for (int i = 0; i < bonusKeywords.size() && i < 4; i++) {
            Keyword k = bonusKeywords.get(i);
            drawBonusKeyword(batch, x, y, k, i, size);
         }
      }

      if (e.hasValue()) {
         drawPipsSinglePixelSquish(batch, size, e.getValue(), bonus, bc, (int)x, (int)y);
      }

      if (lapel2D != null) {
         batch.setColor(Colours.z_white);
         batch.draw(lapel2D, x, y);
      }
   }

   public static void drawPipsSinglePixelSquish(Batch batch, EntSize size, int value, int bonus, Color bc, int x, int y) {
      int PIP_WIDTH = size.getPipWidth();
      if (value <= 0) {
         batch.setColor(Colours.red);
         Draw.fillRectangle(batch, x + size.getPixels() - 2 - PIP_WIDTH, y + 2, 1.0F, 1.0F);
         Draw.fillRectangle(batch, x + size.getPixels() - 2 - PIP_WIDTH + 1, y + 3, 1.0F, 1.0F);
      }

      if (value > getMaxPips(size.getPixels() - 4)) {
         if (bonus == 0) {
            batch.setColor(Colours.light);
         } else if (bc == Colours.light) {
            batch.setColor(DieShader.getWhiteFlash());
         } else {
            batch.setColor(bc);
         }

         Draw.fillRectangle(batch, x + size.getPixels() - 2 - PIP_WIDTH, y + 2, PIP_WIDTH, size.getPixels() - 4);
      } else {
         Color bonusCol = bc;
         int groupSize = 5;
         int pixSize = 1;
         int blockSize = 3;
         int finalFive = value / 5 * 5;
         int pipY = y + 2;

         for (int i = 0; i < value; i++) {
            boolean partOfSingle = i >= finalFive - 1;
            if (partOfSingle) {
               batch.setColor(i >= value - bonus ? bonusCol : pipCol);
               Draw.fillRectangle(batch, x + size.getPixels() - 2 - PIP_WIDTH, pipY, PIP_WIDTH, 1.0F);
               pipY += 2;
            } else {
               batch.setColor(i >= value - bonus ? bonusCol : pipCol);
               Draw.fillRectangle(batch, x + size.getPixels() - 2 - PIP_WIDTH, pipY, 1.0F, 3.0F);
               batch.setColor(i + 5 - 1 >= value - bonus ? bonusCol : pipCol);
               Draw.fillRectangle(batch, x + 1 + size.getPixels() - 2 - PIP_WIDTH, pipY, PIP_WIDTH - 1, 3.0F);
               pipY += 4;
               i += 4;
            }
         }
      }
   }

   public static int getMaxPips(int availableSpace) {
      return (availableSpace - 3) / 4 * 5 + (availableSpace - 3) % 4 / 2 + 2;
   }

   @Override
   public TextureRegion getTexture() {
      return this.tr;
   }

   public TextureRegion getTexture2D() {
      return ImageUtils.get2DIfPossible(this.tr);
   }

   public Eff getBaseEffect() {
      return this.baseEffect;
   }

   public String toString(Ent source) {
      return source == null ? this.getBaseEffect().describe() : this.findState(FightLog.Temporality.Visual, source).describe();
   }

   public EntSideState findState(FightLog.Temporality temporality, Ent source) {
      return source.getState(temporality).getSideState(this);
   }

   public static int badHash(Eff e) {
      return badHash(e, 7);
   }

   public static int badHash(Eff e, int base) {
      if (e.getRestrictions().size() > 0) {
         return -1;
      } else if (e.getBuff() != null) {
         return -1;
      } else if (e.getSummonType() != null) {
         return -1;
      } else if (e.getType() == EffType.Or) {
         return -1;
      } else {
         int hash = base + 31 * base * e.getType().hashCode();
         hash += 33 * hash * e.getTargetingType().hashCode();
         int keywords = 1;

         for (Keyword k : e.getKeywords()) {
            keywords += k.hashCode() * k.hashCode() * 3;
         }

         return hash + 41 * hash * keywords;
      }
   }

   public Actor makeBasicSideActor(final int bonus, final boolean multiplier, final Keyword keyword) {
      return new Actor() {
         {
            this.setSize(EntSide.this.size.getPixels(), EntSide.this.size.getPixels());
         }

         public void draw(Batch batch, float parentAlpha) {
            Color col = Colours.AS_BORDER;
            if (keyword != null) {
               EntSide.drawBonusKeyword(batch, this.getX(), this.getY(), keyword, 0);
            }

            if (bonus != 0) {
               if (multiplier) {
                  EntSide.this.drawWithMultiplier(batch, this.getX(), this.getY(), col, bonus);
               } else {
                  EntSide.this.drawWithBonus(batch, this.getX(), this.getY(), col, bonus);
               }
            }

            if (bonus == 0 && keyword == null) {
               EntSide.this.draw(batch, null, this.getX(), this.getY(), col, null);
            }

            super.draw(batch, parentAlpha);
         }
      };
   }

   public float getEffectTier(EntType type) {
      boolean player = type instanceof HeroType;
      int tier = player ? ((HeroType)type).level : 1;
      return getValueFromEffect(this.getBaseEffect(), tier, type, player);
   }

   public static float getValueFromEffect(Eff e, int tier, EntType type, boolean player) {
      Float hackyOverride = checkForHackyOverride(e, tier, type, player);
      if (hackyOverride != null) {
         return hackyOverride;
      } else {
         float effectivePips = e.getValue();

         for (Keyword k : e.getKeywords()) {
            effectivePips = KUtils.affectBaseValue(k, e, tier, effectivePips, player);
         }

         float effValue = e.getType().getEffectTier(tier, effectivePips, player, e);
         effValue = e.getTargetingType().affectValue(e, player, effValue);

         for (Keyword k : e.getKeywords()) {
            float mult = KUtils.getValueMultiplier(k, e, player, tier);
            if (mult == -1.0F) {
               return -1.0F;
            }

            effValue *= mult;
         }

         if (!e.hasKeyword(Keyword.mandatory)) {
            effValue = Math.max(0.0F, effValue);
         }

         for (Keyword k : e.getKeywords()) {
            effValue = KUtils.getFinalEffectTierAdjustment(k, e, effValue, tier, type);
         }

         return effValue;
      }
   }

   private static Float checkForHackyOverride(Eff e, int tier, EntType type, boolean player) {
      if (e.getType() == EffType.Blank && e.hasKeyword(Keyword.stasis)) {
         return -HeroTypeUtils.getEffectTierFor(tier) * 0.75F;
      } else if (e.getType() == EffType.RedirectIncoming) {
         return 0.1F + EffType.Shield.getEffectTier(tier, e.getValue(), player, new EffBill().shield(e.getValue()).bEff());
      } else {
         if (e.getType() == EffType.Buff && e.getBuff().personal instanceof Stunned) {
            if (e.getRestrictions().size() > 0) {
               return (float)Math.pow(type.hp, 1.3) * 0.12F;
            }

            if (e.hasKeyword(Keyword.singleUse)) {
               return 1.5F + tier * 2.0F;
            }
         }

         if (e.hasKeyword(Keyword.death) && e.getType() == EffType.Summon) {
            MonsterType mt = MonsterTypeLib.byName(e.getSummonType());
            return e.getValue() * mt.getSummonValue() * 0.85F;
         } else {
            if (e.getType() == EffType.Buff) {
               Personal p = e.getBuff().personal;
               if (p instanceof AffectSides) {
                  List<Keyword> keywords = p.getReferencedKeywords();
                  if (keywords.contains(Keyword.cantrip) && keywords.contains(Keyword.death)) {
                     return 5.0F;
                  }
               }
            }

            return e.getType() == EffType.Kill && e.getTargetingType() == TargetingType.Top ? HeroTypeUtils.getHpFor(3) * 3.0F : null;
         }
      }
   }

   public float getApproxTotalEffectTier(EntType type) {
      return this.getEffectTier(type) + this.getExtraFlatEffectTier(type) * 4.0F;
   }

   public float getExtraFlatEffectTier(EntType type) {
      int tier = type instanceof HeroType ? ((HeroType)type).level : 1;
      Eff e = this.getBaseEffect();
      if (e.getType() == EffType.Blank) {
         return 0.0F;
      } else {
         if (e.hasKeyword(Keyword.death)) {
            if (e.hasKeyword(Keyword.enduring)) {
               return -HeroTypeUtils.getEffectTierFor(tier) * 0.025F;
            }

            float basePower = this.getEffectTier(type);
            if (basePower < 0.0F) {
               return basePower / 3.0F;
            }
         }

         if (e.hasKeyword(Keyword.cantrip)) {
            float baseValue = this.withKeyword(Keyword.mandatory).getEffectTier(type);
            float alreadyTakenMultiplier = KUtils.cantripTreatAsSingle(e, tier) ? 0.5F : 1.0F;
            float rollLikelinessMultiplier = 0.5F;
            float cantripMultiplier = e.getType().getCantripMultiplier();
            float multiMulti = rollLikelinessMultiplier * cantripMultiplier * alreadyTakenMultiplier;
            switch (e.getType()) {
               case Reroll:
                  return (tier * 0.25F + 0.57F) * e.getValue() * rollLikelinessMultiplier;
               case Heal:
               case Damage:
               case Shield:
                  return baseValue * multiMulti * 0.7F;
               case Mana:
                  return baseValue * multiMulti;
               case Buff:
                  if (e.getBuff().personal instanceof Dodge) {
                     return e.getBuff().getEffectTier(e.getValue(), tier) * multiMulti * 0.7F;
                  }

                  return Float.NaN;
               default:
                  return Float.NaN;
            }
         } else {
            return 0.0F;
         }
      }
   }

   public EntSide withKeyword(Keyword k) {
      EntSide copy = this.copy();
      copy.getBaseEffect().addKeyword(k);
      return copy;
   }

   public EntSide withKeyword(Keyword... ks) {
      EntSide copy = this.copy();
      copy.getBaseEffect().addKeywords(Arrays.asList(ks));
      return copy;
   }

   public EntSide withValue(int value) {
      EntSide copy = this.copy();
      copy.getBaseEffect().setValue(value);
      return copy;
   }

   @Override
   public String toString() {
      String sideName = ((AtlasRegion)this.getTexture()).name;

      while (sideName.contains("/")) {
         sideName = sideName.substring(sideName.indexOf("/") + 1);
      }

      String val = this.baseEffect.hasValue() ? " " + this.baseEffect.getValue() : "";
      return sideName + val;
   }

   public EntSide generic() {
      this.generic = true;
      return this;
   }

   public boolean isGeneric() {
      return this.generic;
   }

   public boolean isProbablyFromPlayer() {
      if (this.size != EntSize.reg) {
         return false;
      } else {
         for (EntType ht : HeroTypeLib.getMasterCopy()) {
            for (EntSide es : ht.sides) {
               if (es.getTexture() == this.getTexture()) {
                  return true;
               }
            }
         }

         for (EntType ht : MonsterTypeLib.getMasterCopy()) {
            for (EntSide esx : ht.sides) {
               if (esx.getTexture() == this.getTexture()) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public boolean sameTexture(EntSide es) {
      return this.getTexture() == es.getTexture();
   }

   public boolean same(EntSide es) {
      return this.sameTexture(es)
         && this.getBaseEffect().getValue() == es.getBaseEffect().getValue()
         && this.getBaseEffect().getBonusKeywords().equals(es.getBaseEffect().getBonusKeywords());
   }

   public HSL getHSL() {
      return this.hsl;
   }

   public static void clearCache() {
      facadeProgram = null;
   }

   public EntSide withHsl(HSL hsl) {
      return new EntSide(this.tr, this.baseEffect, this.size, hsl);
   }
}
