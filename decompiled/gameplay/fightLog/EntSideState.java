package com.tann.dice.gameplay.fightLog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.HSL;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobBig;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobHuge;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.bullet.DieShader;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EntSideState {
   public static int cnt;
   private final EntState entState;
   private final EntSide original;
   private TextureRegion calculatedTexture;
   private Eff calculatedEffect;
   private HSL hsl;
   private List<TP<Keyword, Integer>> bonuses = new ArrayList<>();
   List<Keyword> cachedKeywords = null;
   Color bestColBonus;

   public EntSideState(EntState entState, EntSide original) {
      this(entState, original, -1);
   }

   public EntSideState(EntState entState, EntSide original, int calcUpTo) {
      this.original = original;
      this.entState = entState;
      this.hsl = original.getHSL();
      this.calculatedEffect = original.getBaseEffect().copy();
      this.calculatedTexture = original.getTexture();
      this.useTriggers(calcUpTo);
      if (OptionLib.SHOW_GRB.c()) {
         cnt++;
      }
   }

   public void changeTo(EntSide other) {
      this.changeTo(other.getBaseEffect().copy(), other.getTexture(), other.getHSL());
   }

   public void changeTo(EntSideState sideState) {
      sideState.removeStaticKeywordBonuses();
      this.changeTo(sideState.getCalculatedEffect().copy(), sideState.getCalculatedTexture(), sideState.hsl);
   }

   public void changeTo(Eff eff, TextureRegion tex) {
      this.changeTo(eff, tex, null);
   }

   public void changeTo(Eff eff, TextureRegion tex, HSL hsl) {
      this.hsl = hsl;
      this.calculatedEffect = eff.copy();
      int diff = tex.getRegionWidth() - this.calculatedTexture.getRegionWidth();
      if (Math.abs(diff) > 2) {
         this.fallbackToMissizeTexture(eff);
      } else {
         this.calculatedTexture = tex;
      }
   }

   private void useTriggers(int upToIndex) {
      try {
         this.usePreTriggers(upToIndex);

         for (Personal t : this.entState.getActivePersonals()) {
            t.affectSideFinal(this, this.entState);
         }
      } catch (Exception var4) {
         if (TestRunner.isTesting()) {
            throw var4;
         }

         TannLog.error(var4, "affecting side");
         this.enbug();
      }
   }

   private void usePreTriggers(int upToIndex) {
      List<Personal> activeTriggers = this.entState.getActivePersonals();

      for (int triggerIndex = 0; triggerIndex < activeTriggers.size(); triggerIndex++) {
         Personal t = activeTriggers.get(triggerIndex);
         if (triggerIndex == upToIndex || this.calculatedEffect.hasKeyword(Keyword.stasis)) {
            return;
         }

         int preVal = this.calculatedEffect.getValue();
         t.affectSide(this, this.entState, triggerIndex);
         int valDelta = this.calculatedEffect.getValue() - preVal;
         Keyword k = t.getStronglyAssociatedKeyword();
         if (k != null && valDelta != 0) {
            boolean found = false;

            for (int i = 0; i < this.bonuses.size(); i++) {
               if (this.bonuses.get(i).a == k) {
                  TP var10 = this.bonuses.get(i);
                  var10.b = (T2)(Integer)var10.b + valDelta;
                  found = true;
               }
            }

            if (!found) {
               this.bonuses.add(new TP<>(k, valDelta));
            }
         }
      }

      Snapshot s = this.entState.getSnapshot();
      if (s != null) {
         if (upToIndex == -1) {
            this.recurseMetaKeywords(s);
            this.precalculatedKeywords(s);
         }
      }
   }

   private void precalculatedKeywords(Snapshot s) {
      Eff e = this.getCalculatedEffect();
      if (e.getDoubleConditions() != null) {
         for (ConditionalRequirement doubleCondition : e.getDoubleConditions()) {
            if (doubleCondition.preCalculate() && doubleCondition.isValid(s, this.entState, null, e)) {
               e.setValue(e.getValue() * (e.hasKeyword(Keyword.treble) ? 3 : 2));
            }
         }
      }

      for (Keyword k : this.getCalculatedEffect().getKeywordForGameplay()) {
         switch (k) {
            case doubDiff:
            case revDiff:
               boolean doub = Keyword.doubDiff == k;
               if (this.original.getBaseEffect().hasValue() && this.calculatedEffect.hasValue()) {
                  int delta = this.calculatedEffect.getValue() - this.original.getBaseEffect().getValue();
                  if (delta != 0) {
                     this.addBonus(delta * (doub ? 1 : -2), k);
                  }
               }
               break;
            case lucky:
               if (s.turn != 0) {
                  Random r = Tann.makeStdRandom(s.getShifterSeed(this.getIndex(), this.entState));
                  float roll = r.nextFloat();
                  int bonus = (int)(-roll * (this.getCalculatedEffect().getValue() + 1));
                  if (bonus != 0) {
                     this.addBonus(bonus, k);
                  }
               }
               break;
            case critical:
               if (s.turn != 0) {
                  Random r = Tann.makeStdRandom(s.getShifterSeed(this.getIndex(), this.entState));
                  int bonus = r.nextBoolean() ? 0 : 1;
                  if (bonus != 0) {
                     this.addBonus(bonus, k);
                  }
               }
         }

         ConditionalBonus cb = k.getConditionalBonus();
         if (cb != null && cb.requirement.preCalculate()) {
            int bonus = cb.affectValue(this.getCalculatedEffect(), this.entState, null, this.calculatedEffect.getValue());
            if (bonus != 0) {
               this.addBonus(bonus, k);
            }
         }
      }
   }

   private void recurseMetaKeywords(Snapshot s) {
      if (KUtils.hasMetaKeyword(this.getCalculatedEffect())) {
         List<Keyword> alreadyProcessed = new ArrayList<>();

         for (int i = 0; i < 5; i++) {
            List<Keyword> cpy2 = new ArrayList<>(this.getCalculatedEffect().getKeywords());
            cpy2.removeAll(alreadyProcessed);
            if (cpy2.isEmpty()) {
               break;
            }

            alreadyProcessed.addAll(this.getCalculatedEffect().getKeywords());
            this.processMetaKeywords(cpy2, s);
         }
      }
   }

   private void processMetaKeywords(List<Keyword> kws, Snapshot s) {
      for (int ki = 0; ki < kws.size(); ki++) {
         Keyword k = kws.get(ki);
         switch (k) {
            case copycat:
            case echo:
            case resonate:
               EntSideState recentState = s.getMostRecentlyUsedDieCommandEffect();
               if (recentState != null) {
                  Eff recent = recentState.getCalculatedEffect();
                  switch (k) {
                     case copycat:
                        this.getCalculatedEffect().addKeywords(recent.getKeywords());
                        break;
                     case echo:
                        this.getCalculatedEffect().setValue(recent.hasValue() ? recent.getValue() : 0);
                        break;
                     case resonate:
                        int value = this.getCalculatedEffect().getValue();
                        ReplaceWith.replaceSide(this, recentState);
                        this.getCalculatedEffect().setValue(value);
                        this.getCalculatedEffect().addKeyword(Keyword.resonate);
                        break;
                     default:
                        TannLog.error("errorkw: " + k);
                  }
               }
               break;
            case dejavu:
               List<EntSideState> sss = s.getSideStatesFromTurnsAgoAndEnt(1, this.entState.getEnt());

               for (int ixx = 0; ixx < sss.size(); ixx++) {
                  this.getCalculatedEffect().addKeywords(sss.get(ixx).getCalculatedEffect().getKeywords());
               }
               break;
            case spy:
               EntSideState firstAttack = s.getFirstEnemyAttackState();
               if (firstAttack != null) {
                  this.getCalculatedEffect().addKeywords(firstAttack.getCalculatedEffect().getKeywords());
               }
               break;
            case shifter:
               if (s.turn == 0) {
                  break;
               }

               Random r = Tann.makeStdRandom(s.getShifterSeed(this.getIndex(), this.entState));
               int attempts = 20;

               for (int ix = 0; ix < attempts; ix++) {
                  Keyword possible = Tann.random(Keyword.values(), r);
                  if (!possible.abilityOnly()) {
                     int size = this.getCalculatedEffect().getKeywords().size();
                     this.getCalculatedEffect().addKeyword(possible);
                     if (size != this.getCalculatedEffect().getKeywords().size()) {
                        break;
                     }
                  }
               }
               break;
            case fumble:
               if (s.turn != 0 && Tann.makeStdRandom(s.getShifterSeed(this.getIndex(), this.entState)).nextBoolean()) {
                  ReplaceWith.replaceSide(this, ESB.blankFumble);
               }
               break;
            case fluctuate:
               if (s.turn != 0) {
                  Random r = Tann.makeStdRandom(s.getShifterSeed(this.getIndex(), this.entState));
                  EntSide fs = null;
                  int attempts = 50;
                  boolean bhv = this.getCalculatedEffect().hasValue();

                  for (int i = 0; i < attempts; i++) {
                     fs = EntSidesLib.random(r, this.entState.getEnt().getSize());
                     if (fs.getTexture() != this.getCalculatedTexture() && fs.getBaseEffect().hasValue() == bhv) {
                        break;
                     }
                  }

                  if (fs.getBaseEffect().hasValue() == bhv) {
                     int value = this.getCalculatedEffect().getValue();
                     List<Keyword> kws2 = this.getCalculatedEffect().getKeywords();
                     ReplaceWith.replaceSide(this, fs);
                     this.getCalculatedEffect().setValue(value);
                     this.getCalculatedEffect().addKeywords(kws2);
                  }
               }
         }
      }
   }

   private void addBonus(int bonus, Keyword keyword) {
      this.bonuses.add(new TP<>(keyword, bonus));
      Eff e = this.getCalculatedEffect();
      if (e.hasValue()) {
         e.setValue(e.getValue() + bonus);
      }
   }

   public EntSide getOriginal() {
      return this.original;
   }

   public TextureRegion getCalculatedTexture() {
      return this.calculatedTexture;
   }

   public Eff getCalculatedEffect() {
      return this.calculatedEffect;
   }

   public void setCalculatedEffect(Eff e) {
      this.calculatedEffect = e;
   }

   public int getIndex() {
      return this.entState == null ? -1 : this.entState.getSideIndex(this.getOriginal());
   }

   public List<Keyword> getBonusKeywords() {
      if (this.cachedKeywords == null) {
         this.cachedKeywords = new ArrayList<>();

         for (Keyword k : this.getCalculatedEffect().getBonusKeywords()) {
            if (!this.cachedKeywords.contains(k)) {
               this.cachedKeywords.add(k);
            }
         }
      }

      return this.cachedKeywords;
   }

   public int getBonusColIndex() {
      if (this.bestColBonus == null) {
         this.bestColBonus = Colours.pink;
         int bestBonus = 0;

         for (int i = 0; i < this.bonuses.size(); i++) {
            TP<Keyword, Integer> b = this.bonuses.get(i);
            if (b.b > bestBonus) {
               bestBonus = b.b;
               this.bestColBonus = b.a.getColour();
            }
         }
      }

      return DieShader.colIndex(this.bestColBonus);
   }

   public int getTotalBonus() {
      int bonus = 0;

      for (int i = 0; i < this.bonuses.size(); i++) {
         bonus += (Integer)this.bonuses.get(i).b;
      }

      return bonus;
   }

   public String getBonusString() {
      if (this.bonuses.size() == 0) {
         return "";
      } else {
         String result = "";

         for (TP<Keyword, Integer> bon : this.bonuses) {
            String tag = TextWriter.getTag(bon.a.getColour());
            if (!result.isEmpty()) {
               result = result + "[h]";
            }

            result = result + tag + Tann.delta(bon.b) + "[cu]";
         }

         return "(" + result + ")";
      }
   }

   public int getBonusFromGrowth() {
      for (TP<Keyword, Integer> bon : this.bonuses) {
         if (bon.a == Keyword.growth) {
            return bon.b;
         }
      }

      return 0;
   }

   public String describe() {
      Eff e = this.getCalculatedEffect();
      String result = e.describe(false);
      String bonusString = this.getBonusString();
      if (!bonusString.isEmpty()) {
         result = result + " " + bonusString;
      }

      return e.needsKeywordsBracketed() ? result : Eff.addKeywordsToString(result, this.getCalculatedEffect());
   }

   private void removeStaticKeywordBonuses() {
      Eff e = this.getCalculatedEffect();
      if (this.getCalculatedEffect().hasValue()) {
         for (TP<Keyword, Integer> i : this.bonuses) {
            ConditionalBonus cb = i.a.getConditionalBonus();
            if (cb != null && cb.requirement.preCalculate()) {
               e.setValue(e.getValue() - i.b);
            }
         }
      }
   }

   public void enbug() {
      switch (this.entState.getEnt().getSize()) {
         case small:
            this.changeTo(EntSidesBlobSmall.blankBug);
            break;
         case reg:
            this.changeTo(ESB.blankBug);
            break;
         case big:
            this.changeTo(EntSidesBlobBig.blankBug);
            break;
         case huge:
            this.changeTo(EntSidesBlobHuge.blankBug);
      }
   }

   private void fallbackToMissizeTexture(Eff e) {
      EntSize sz = this.entState.getEnt().getSize();
      TextureRegion missize = ImageUtils.loadExt3d(sz + "/face/special/missize");
      if (missize == null) {
         this.enbug();
      } else {
         this.calculatedTexture = missize;
      }
   }

   public HSL getHsl() {
      return this.hsl;
   }

   public boolean ownerDead() {
      return this.entState.isDead();
   }
}
