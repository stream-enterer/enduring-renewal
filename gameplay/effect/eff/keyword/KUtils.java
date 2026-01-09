package com.tann.dice.gameplay.effect.eff.keyword;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonus;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.ConditionalBonusType;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.CombinedRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ParamCondition;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.XORRequirement;
import com.tann.dice.gameplay.modifier.modBal.ModTierUtils;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.screens.dungeon.panels.DieSidePanel;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KUtils {
   public static final String NSQ_PLACEHOLDER = "!N2!";
   public static final String TWON_PLACEHOLDER = "!2N!";
   public static final String NP1_PLACEHOLDER = "!NP1!";
   public static final String N_PLACEHOLDER = "!N!";
   private static List<Keyword> ke;
   private static List<Keyword> kh;
   private static List<Keyword> ks;
   private static List<Keyword> kd;
   private static List<Keyword> km;
   static final List<Keyword> META = Arrays.asList(
      Keyword.copycat, Keyword.echo, Keyword.resonate, Keyword.fluctuate, Keyword.fumble, Keyword.shifter, Keyword.spy, Keyword.dejavu
   );

   public static String describeAdding(Keyword other) {
      return "add " + other.getColourTaggedString() + " to target's sides for a turn";
   }

   public static String describeGroupActivate(Keyword act, boolean side) {
      return "The keyword '" + act.getColourTaggedString() + "' activates for all allies" + (side ? " (on this side)" : "");
   }

   public static String describeHavingSameValueAsLastNDice(int N) {
      String part;
      if (N == 1) {
         part = "";
      } else if (N < 1) {
         part = N + "? ";
      } else {
         part = N + " ";
      }

      return "this has the same pips as the " + part + describePreviousDice();
   }

   public static String describePreviousDice() {
      return describePreviousDice(false);
   }

   public static String describeStraightDice() {
      return "dice used before it this turn";
   }

   public static String describePreviousDice(boolean allowSpell) {
      String name = allowSpell ? "dice/spell" : "dice";
      return "previous " + name + " this turn";
   }

   public static String describeN() {
      return describeN(null);
   }

   public static String describe2N() {
      return describeN().replace("!N!", "!2N!");
   }

   public static String describeNP1() {
      return describeN().replace("!N!", "!NP1!");
   }

   public static String describeNSQ() {
      return describeN().replace("!N!", "!N2!");
   }

   public static String describeThree() {
      return describeN().replace("!N!", "3");
   }

   public static String describeOne() {
      return describeN().replace("!N!", "1");
   }

   public static String describeN(Boolean plus) {
      return plus == null ? "!N!" : (plus ? "+" : "-") + "[nbp]" + "!N!";
   }

   public static String describeOneTurn() {
      return describeThisTurn();
   }

   public static String describeThisTurn() {
      return "this turn";
   }

   public static String describeThisFight() {
      return "this fight";
   }

   public static String describeKeywords(List<Keyword> keywords) {
      String keywordResult = "";

      for (Keyword k : keywords) {
         if (keywordResult.length() > 0) {
            keywordResult = keywordResult + " ";
         }

         keywordResult = keywordResult + com.tann.dice.Main.t(k.getColourTaggedString());
      }

      return keywordResult.length() > 0 ? keywordResult : "";
   }

   public static Actor makeActor(Keyword keyword, Eff source) {
      int width = 110;
      int padding = 2;
      Pixl p = new Pixl(2, padding).border(Colours.grey);
      p.actor(
         new TextWriter(
            "[notranslateall]" + com.tann.dice.Main.t(keyword.getColourTaggedString()) + "[text]: " + com.tann.dice.Main.t(keyword.getRules(source)),
            width - padding * 2
         )
      );
      return p.pix(10);
   }

   public static List<Actor> makeExampleSides(Keyword k) {
      List<Actor> result = new ArrayList<>();
      if (k.abilityOnly()) {
         return result;
      } else {
         List<EntSide> realValid = new ArrayList<>();
         List<EntSide> validTmp = EntSidesLib.exampleKeywordSides(k);
         int setVal = 0;

         for (int i = 0; i < validTmp.size(); i++) {
            EntSide check = validTmp.get(i).copy();
            if (check.getBaseEffect().getType() != EffType.Blank) {
               setVal++;
            }

            check.getBaseEffect().setValue(setVal);
            realValid.add(check);
         }

         for (EntSide es : realValid) {
            final EntSide wk = es.withKeyword(k);
            DieSidePanel dsp = new DieSidePanel(wk, es.size.getExampleEntity());
            dsp.addListener(new TannListener() {
               @Override
               public boolean info(int button, float x, float y) {
                  Actor a = new Explanel(wk, null);
                  com.tann.dice.Main.getCurrentScreen().push(a, true, true, true, 0.0F);
                  Tann.center(a);
                  return true;
               }
            });
            result.add(dsp);
         }

         return result;
      }
   }

   public static Actor makeExplanationActor(Keyword k, int maxWidth) {
      Pixl textStuff = new Pixl(2);
      textStuff.text(k.getColourTaggedString(), maxWidth);
      textStuff.row().text("[text]" + k.getRules(), maxWidth);
      if (k.getExtraRules() != null) {
         textStuff.row().text("[purple]" + k.getExtraRules(), maxWidth);
      }

      return textStuff.pix(8);
   }

   public static float affectBaseValue(Keyword k, Eff e, int tier, float pips, boolean player) {
      float tstr = HeroTypeUtils.getEffectTierFor(tier);
      switch (k) {
         case critical:
            return pips + 0.5F;
         case bloodlust:
            return pips + 2.05F;
         case steel:
            if (!player) {
               return pips;
            }

            float extraExpected = 0.5F + Math.max((float)tier, 1.9F) * 0.46F;
            if (e.getType() == EffType.Shield || e.getType() == EffType.Heal) {
               extraExpected *= 0.5F;
            }

            if (e.getType() == EffType.Mana) {
               extraExpected *= 1.52F;
            }

            return pips + extraExpected;
         case growth:
            return pips + 0.5F;
         case era:
            return pips + 1.0F;
         case lucky:
            return pips * 0.5F;
         case charged:
            float bonusx;
            switch (e.getType()) {
               case Shield:
               case Heal:
                  bonusx = tier * 0.6F + 1.1F;
                  break;
               case Mana:
               default:
                  bonusx = tier * 0.5F + 1.2F;
                  break;
               case Damage:
                  if (e.hasKeyword(Keyword.singleUse)) {
                     bonusx = tier * 0.4F + 1.2F;
                  } else {
                     bonusx = tier * 0.8F + 1.4F;
                  }
            }

            return pips + bonusx;
         case hoard:
            float bonus = (float)Math.pow(1.4, tier);
            return pips + bonus;
         case decay:
            return pips - 0.3F;
         case vigil:
            return pips + 1.0F;
         case plague:
            float doseBonus = 0.5F + tier / 4.0F;
            return pips + doseBonus;
         case duel:
            return pips * 1.43F;
         case underdog:
            return pips * 1.35F;
         case focus:
            return e.isFriendly() ? pips * 1.5F : pips * 1.7F;
         case ego:
            switch (e.getType()) {
               case Shield:
                  return pips * 1.15F;
               default:
                  return Float.NaN;
            }
         case chain: {
            int numKeywords = e.getKeywords().size();
            float chance = Math.min(1.0F, numKeywords / 7.0F);
            return pips * (1.0F + chance);
         }
         case patient:
            return pips * 1.1F;
         case pristine:
            return pips * 1.7F;
         case engage:
            return pips * 1.5F;
         case uppercut:
            return pips * 1.25F;
         case fierce:
            return pips * 1.7F;
         case serrated:
            if (player) {
               return pips * 1.98F;
            }

            return pips * 1.25F;
         case cruel:
            if (e.needsTarget()) {
               return pips * 1.25F;
            }

            return pips * 1.15F;
         case deathwish:
            return pips * 1.33F;
         case step:
            return 1.85F;
         case inspired: {
            float TIER_VAL_MULT = 0.75F;
            float RATIO_PW = 2.0F;
            int thisVal = e.getValue();
            float avgTierValue = HeroTypeUtils.getEffectTierFor(tier) * 0.75F;
            float ratio = avgTierValue / thisVal;
            float chance = (float)Math.min(0.8F, Math.pow(ratio, 2.0));
            return pips * (1.0F + chance);
         }
         case pair:
            switch (tier) {
               case 1:
               case 2:
                  return pips * Math.max(1.5F, 2.0F - e.getValue() * 0.1F);
               default:
                  return pips * 1.5F;
            }
         case trio:
            int originalValue = e.getValue();
            float tierStrength = HeroTypeUtils.getEffectTierFor(tier);
            if (originalValue < tierStrength) {
               return pips + pips * 2.0F * 0.25F;
            }

            return pips + pips * 2.0F * 0.1F;
         case quin:
            return pips * 1.01F;
         case inflictPain:
            if (e.isFriendly()) {
               return pips;
            } else {
               if (player) {
                  return pips + HeroTypeUtils.getEffectTierFor(tier) * 1.0F;
               }

               return pips + 1.5F;
            }
         case evil:
         case guilt: {
            float str4T = HeroTypeUtils.getEffectTierFor(tier);
            float ratio = pips / str4T;
            float reduction = 0.05F * ratio;
            return pips * (1.0F - reduction);
         }
         case boned:
            return pips - 1.8F;
         case hyperBoned:
            return pips - e.getValue() * 1.8F;
         case echo:
            return tstr * 1.3F + 1.0F;
         case paxin:
            return pips * 1.8F;
         case fizz:
            return pips + 1.1F;
         case flurry:
         case sprint:
            return pips * 1.01F;
         case hyperGrowth:
            return pips * 3.0F;
         case defy:
            return pips + 1.0F + tier * 0.7F;
         case dejavu:
            return pips * 1.2F;
         default:
            return pips;
      }
   }

   public static float getValueMultiplier(Keyword k, Eff e, boolean player, int tier) {
      if (k.getInflict() != null) {
         return 1.0F;
      } else {
         switch (k) {
            case critical:
            case bloodlust:
            case steel:
            case growth:
            case era:
            case lucky:
            case charged:
            case hoard:
            case decay:
            case vigil:
            case plague:
            case duel:
            case underdog:
            case focus:
            case ego:
            case chain:
            case patient:
            case pristine:
            case engage:
            case uppercut:
            case fierce:
            case serrated:
            case cruel:
            case deathwish:
            case step:
            case inspired:
            case pair:
            case trio:
            case quin:
            case evil:
            case guilt:
            case boned:
            case hyperBoned:
            case echo:
            case paxin:
            case fizz:
            case flurry:
            case sprint:
            case hyperGrowth:
            case defy:
            case dejavu:
            case repel:
            case cleanse:
            case pain:
            case boost:
            case permaBoost:
            case smith:
            case selfHeal:
            case selfShield:
            case manaGain:
            case duplicate:
            case death:
            case sept:
            case manacost:
            case regen:
            case sticky:
            case mandatory:
            case doubleUse:
            case quadUse:
            case petrify:
            case hypnotise:
            case wither:
            case groooooowth:
            case resilient:
            case dispel:
            case enduring:
            case damage:
            case shield:
            case heal:
            case selfCleanse:
            case fluctuate:
               return 1.0F;
            case inflictPain:
            default:
               return Float.NaN;
            case ranged:
               if (!e.hasKeyword(Keyword.poison) && !e.hasKeyword(Keyword.cleave)) {
                  return 1.14F;
               }

               return 1.05F;
            case poison:
               return player ? 2.1F : 3.2F;
            case copycat:
               return getValueMultiplier(Keyword.cleave, e, player, tier);
            case spy:
               return 2.0F;
            case cleave:
               if (!player) {
                  return 2.85F;
               }

               float bonus = 0.0F;
               if (e.getType() == EffType.Damage) {
                  bonus = 0.1F;
               }

               return 2.1F + bonus;
            case descend:
               return 1.0F + (getValueMultiplier(Keyword.cleave, e, player, tier) - 1.0F) * 0.53F;
            case cantrip:
               if (cantripTreatAsSingle(e, tier)) {
                  return 1.0F + e.getType().getCantripMultiplier() + 0.2F;
               }

               return 1.0F;
            case heavy:
               if (player) {
                  return 0.73F;
               }

               return 0.93F;
            case vulnerable:
               if (tier == 1) {
                  return 1.9F;
               }

               return 1.9F + tier * 0.2F;
            case singleUse:
               if (e.hasKeyword(Keyword.poison)) {
                  return 0.88F;
               }

               return 0.75F;
            case rampage:
               if (e.getType() == EffType.Heal) {
                  return 1.0F;
               }

               return 1.78F;
            case vitality:
               if (e.getType() == EffType.Heal) {
                  return 1.0F;
               }

               return 1.1F;
            case rescue:
               float flatAdd = (tier - 1) * 0.2F;
               switch (e.getType()) {
                  case Shield:
                     return 1.95F + flatAdd;
                  case Heal:
                     return 2.0F + flatAdd;
                  default:
                     return 1.7F + flatAdd;
               }
            case weaken:
               if (!player) {
                  return 2.35F;
               }

               return tier * 0.3F + 2.5F;
            case eliminate:
               if (player) {
                  return e.isFriendly() ? 0.7F : 0.93F;
               }

               return 1.25F;
            case exert:
               return 0.65F;
            case shifter:
               return 1.25F;
         }
      }
   }

   public static boolean cantripTreatAsSingle(Eff e, int tier) {
      float baseValue = e.getType().getEffectTier(tier, e.getValue(), true, null);
      return baseValue * 1.2F > HeroTypeUtils.getEffectTierFor(tier);
   }

   public static float getFinalEffectTierAdjustment(Keyword k, Eff e, float val, int tier, EntType type) {
      boolean hero = type instanceof HeroType;
      switch (k) {
         case inflictPain:
            if (e.isFriendly()) {
               return val - HeroTypeUtils.getEffectTierFor(tier) * 0.2F;
            }

            return val;
         case poison:
            if (e.isFriendly()) {
               return val - e.getValue() * 1.1F;
            } else {
               if (e.getType() == EffType.JustTarget) {
                  return val + e.getValue() * 1.1F;
               }

               return val;
            }
         case weaken:
            if (e.hasKeyword(Keyword.poison)) {
               val /= getValueMultiplier(Keyword.poison, null, false, 0);
               val /= getValueMultiplier(Keyword.weaken, null, false, 0);
               float var11 = val + (val * getValueMultiplier(Keyword.poison, null, false, 0) - val);
               val = var11 + (val * getValueMultiplier(Keyword.weaken, null, false, 0) - val);
            }
         case evil:
         case guilt:
         case boned:
         case hyperBoned:
         case echo:
         case paxin:
         case fizz:
         case flurry:
         case sprint:
         case hyperGrowth:
         case defy:
         case dejavu:
         case ranged:
         case copycat:
         case spy:
         case cleave:
         case descend:
         case cantrip:
         case heavy:
         case vulnerable:
         case singleUse:
         case rampage:
         case vitality:
         case rescue:
         case eliminate:
         case exert:
         case shifter:
         case sept:
         case sticky:
         case mandatory:
         case wither:
         case enduring:
         default:
            return val;
         case repel:
            return val + e.getValue() * 1.5F;
         case cleanse:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            float maxPips = 2.0F + (tier - 1) * 1.25F;
            float valAtOne = 0.7F + (tier - 1) * 0.15F;
            float maxVal = 1.1F + (tier - 1) * 0.4F;
            return val + Tann.effectTierOnlySinTote(e.getValue(), maxPips, valAtOne, maxVal, 0.8F);
         case pain:
            float painReduction = e.getValue();
            float max = hero ? type.hp * 1.2F : type.hp;
            painReduction = Math.min(painReduction, max);
            if (e.hasKeyword(Keyword.rampage)) {
               painReduction *= 2.5F;
            }

            if (hero) {
               painReduction *= 0.55F;
            } else {
               painReduction *= 0.258F;
            }

            if (!hero) {
            }

            return val - painReduction;
         case boost:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + (tier * 0.1F + 1.7F) * e.getValue();
         case permaBoost:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + (getFinalEffectTierAdjustment(Keyword.boost, e, val, tier, type) - val) * 2.1F;
         case smith:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + (getFinalEffectTierAdjustment(Keyword.boost, e, val, tier, type) - val) * 0.8F;
         case selfHeal:
            if (type instanceof MonsterType && type.hp > 12) {
               return val + 0.6F * e.getValue();
            }

            return val + 0.3F * e.getValue();
         case selfShield:
            return val + 0.3F * e.getValue();
         case manaGain:
            float extra = EffType.Mana.getEffectTier(tier, e.getValue(), true, e);
            if (e.getType() == EffType.Resurrect) {
               extra *= 0.25F;
            }

            return val + extra;
         case duplicate:
            float base = HeroTypeUtils.getEffectTierFor(tier) * 0.9F;
            float extra = (float)Math.pow(val / base, 3.0);
            float extraMult;
            switch (e.getType()) {
               case Shield:
               case Heal:
                  extraMult = 0.2F;
                  break;
               case Mana:
                  extraMult = 1.1F;
                  break;
               case Damage:
                  extraMult = 1.0F;
                  break;
               default:
                  extraMult = 500.0F;
            }

            return val * 1.3F + extra * extraMult;
         case death:
            if (e.hasKeyword(Keyword.mandatory) && e.hasKeyword(Keyword.sticky)) {
               return val - HeroTypeUtils.getHpFor(tier) * 1.0F;
            }

            return Math.max(val * 0.26F, val * 0.9F + getFinalEffectTierAdjustment(Keyword.pain, new EffBill().damage(type.hp).bEff(), 0.0F, tier, type));
         case manacost:
            return val - EffType.Mana.getEffectTier(tier, e.getValue(), true, e);
         case regen:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + e.getValue() * 1.3F;
         case doubleUse:
            return val * 2.05F;
         case quadUse:
            return val * Interpolation.linear.apply(4.2F, 4.4F, Math.min(1.0F, tier / 4.0F));
         case petrify:
            if (e.isFriendly()) {
               return val - e.getValue() * 1.0F * (1.0F + tier * 0.2F);
            } else {
               if (hero) {
                  return val * (1.8F + tier * 0.8F);
               }

               return val + Tann.effectTierOnlySinTote(e.getValue(), 6.0F, 1.3F, 14.0F, 1.0F);
            }
         case hypnotise:
            return val + 2.0F;
         case groooooowth:
            return val + 1.2F;
         case resilient:
            return val;
         case dispel:
            if (hero && !e.isFriendly()) {
               return val + tier * 0.8F;
            }

            return Float.NaN;
         case damage:
            if (e.isFriendly()) {
               return Float.NaN;
            }

            return val + EffType.Damage.getEffectTier(tier, e.getValue(), true, e);
         case shield:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + EffType.Shield.getEffectTier(tier, e.getValue(), true, e);
         case heal:
            if (!e.isFriendly()) {
               return Float.NaN;
            }

            return val + EffType.Heal.getEffectTier(tier, e.getValue(), true, e);
         case selfCleanse:
            return val + (getFinalEffectTierAdjustment(Keyword.cleanse, e, val, tier, type) - val) * 0.24F;
         case fluctuate:
            return e.hasValue() ? e.getValue() * 1.32F : tier * 0.33F + 0.66F;
         case inflictDeath:
            if (hero) {
               return val + HeroTypeUtils.getEffectTierFor(((HeroType)type).getTier()) * 3.0F;
            } else {
               if (e.isFriendly()) {
                  return Float.NaN;
               }

               return val + 3.2F;
            }
         case inflictExert:
            if (e.isFriendly()) {
               return Float.NaN;
            }

            return type instanceof HeroType
               ? val + 0.9F * (e.hasKeyword(Keyword.cleave) ? 2.5F : 1.0F)
               : val + 1.9F * (e.hasKeyword(Keyword.cleave) ? 2.8F : 1.0F);
         case inflictSingleUse:
            return e.isFriendly() ? Float.NaN : val + 0.3F;
      }
   }

   public static boolean allowAddingKeyword(Keyword k, Eff e) {
      EffType et = e.getType();
      if (et == EffType.Event) {
         return false;
      } else if (e.getKeywords().contains(k)) {
         return false;
      } else if (e.hasKeyword(Keyword.permissive)) {
         return true;
      } else if (k.getAllowType() == null) {
         TannLog.error("Unimplemented: " + k + "/" + k.getAllowType());
         return false;
      } else {
         return k == Keyword.descend && e.hasKeyword(Keyword.cleave) ? false : k.getAllowType().check(e);
      }
   }

   public static int getValue(Eff e) {
      return getValue(e, e.getValue());
   }

   public static int getValue(Eff e, int value) {
      int v = value;

      for (int i = 0; i < e.getKeywords().size(); i++) {
         Keyword k = e.getKeywords().get(i);
         switch (k) {
            case fault:
               v = -1;
               break;
            case zeroed:
               v = 0;
               break;
            case onesie:
               v = 1;
               break;
            case threesy:
               v = 3;
               break;
            case plus:
               v++;
               break;
            case doubled:
               v *= 2;
               break;
            case squared:
               v *= v;
         }
      }

      return GlobalNumberLimit.box(v);
   }

   public static String describePipBonus(String bonusDescription) {
      return bonusDescription.startsWith("equal to") ? "bonus pips " + bonusDescription : describeBeingIncreased() + " for each " + bonusDescription;
   }

   public static List<Keyword> getColourKeywords(Color col) {
      List<Keyword> result = new ArrayList<>();
      Keyword[] vals = Keyword.values();

      for (int i = 0; i < vals.length; i++) {
         Keyword k = vals[i];
         if (k.getColour() == col) {
            result.add(k);
         }
      }

      return result;
   }

   public static boolean allowAutoskip(Keyword k) {
      switch (k) {
         case boned:
         case hyperBoned:
         case exert:
         case pain:
         case death:
         case manacost:
         case potion:
            return true;
         default:
            return false;
      }
   }

   public static float getModTierAll(Keyword k, boolean hero) {
      return hero ? getModTierAllHero(k) : getModTierAllMonster(k);
   }

   public static float getModTierAllMonster(Keyword k) {
      switch (k) {
         case growth:
            return -2.0F;
         case era:
            return -13.0F;
         case decay:
            return 2.0F;
         case vigil:
            return -11.0F;
         case pristine:
            return -4.0F;
         case poison:
            return ModTierUtils.monsterPlus(2);
         case singleUse:
            return 9.0F;
         case exert:
            return 15.0F;
         case selfHeal:
            return -8.0F;
         case inflictDeath:
            return -10.0F;
         case inflictBoned:
            return -10.0F;
         default:
            return 0.0F;
      }
   }

   public static float getModTierAllHero(Keyword k) {
      switch (k) {
         case critical:
            return ModTierUtils.heroBonusAllSides(0.5F) * 5.0F;
         case bloodlust:
            return 20.0F;
         case steel:
            return 12.0F;
         case growth:
            return 8.0F;
         case era:
            return ModTierUtils.heroBonusAllSides(5.5F);
         case lucky:
         case hoard:
         case plague:
         case chain:
         case uppercut:
         case serrated:
         case step:
         case quin:
         case inflictPain:
         case evil:
         case paxin:
         case fizz:
         case flurry:
         case sprint:
         case hyperGrowth:
         case defy:
         case dejavu:
         case poison:
         case spy:
         case cleave:
         case descend:
         case vulnerable:
         case rampage:
         case vitality:
         case weaken:
         case eliminate:
         case shifter:
         case cleanse:
         case boost:
         case permaBoost:
         case smith:
         case sept:
         case manacost:
         case regen:
         case sticky:
         case doubleUse:
         case quadUse:
         case petrify:
         case hypnotise:
         case wither:
         case resilient:
         case damage:
         case shield:
         case heal:
         case selfCleanse:
         case inflictDeath:
         case inflictExert:
         case inflictSingleUse:
         case fault:
         case zeroed:
         case onesie:
         case threesy:
         case plus:
         case doubled:
         case squared:
         case potion:
         default:
            return 0.0F;
         case charged:
            return 35.0F;
         case decay:
            return -4.0F;
         case vigil:
            return 10.0F;
         case duel:
            return das(0.15F);
         case underdog:
            return das(0.32F);
         case focus:
            return das(0.15F);
         case ego:
            return das(0.06F);
         case patient:
            return das(0.06F);
         case pristine:
            return das(0.6F);
         case engage:
            return das(0.42F);
         case fierce:
            return 7.0F;
         case cruel:
            return das(0.21F);
         case deathwish:
            return das(0.38F);
         case inspired:
            return das(0.6F);
         case pair:
         case trio:
            return das(0.26F);
         case guilt:
            return -4.0F;
         case boned:
            return -14.0F;
         case hyperBoned:
            return -30.0F;
         case echo:
            return 22.0F;
         case ranged:
            return 3.0F;
         case copycat:
            return 17.0F;
         case cantrip:
            return 30.0F;
         case heavy:
            return -9.0F;
         case singleUse:
            return -9.0F;
         case rescue:
            return 9.0F;
         case exert:
            return -12.0F;
         case repel:
            return 14.0F;
         case pain:
            return ModTierUtils.painKeyword(5.0F);
         case selfHeal:
            return 7.0F;
         case selfShield:
            return 10.0F;
         case manaGain:
            return 30.0F;
         case duplicate:
            return 10.0F;
         case death:
            return ModTierUtils.deathKeyword(5.0F);
         case mandatory:
            return -1.0F;
         case groooooowth:
            return 26.0F;
         case dispel:
            return 4.0F;
         case enduring:
            return 4.0F;
         case fluctuate:
            return 20.0F;
         case inflictBoned:
            return -6.0F;
         case fumble:
            return -12.0F;
         case unusable:
            return -20.0F;
         case halveEngage:
            return -6.0F;
         case halveDuel:
            return -2.0F;
         case groupExert:
            return -13.0F;
         case generous:
            return -1.0F;
         case sixth:
            return 3.0F;
         case undergrowth:
            return 5.0F;
         case groupGrowth:
            return 22.0F;
         case flesh:
            return ModTierUtils.heroBonusAllSides(20.0F);
         case reborn:
            return das(0.02F);
         case terminal:
            return das(0.07F);
         case overdog:
            return das(0.32F);
      }
   }

   public static Rarity getRarity(Keyword k) {
      if (k.name().startsWith("group")) {
         return Rarity.HUNDREDTH;
      } else {
         switch (k) {
            case underdog:
            case evil:
            case guilt:
            case mandatory:
            case overdog:
               return Rarity.TENTH;
            case ego:
            case patient:
            case inspired:
               return Rarity.FIFTH;
            case exert:
            case generous:
               return Rarity.THIRD;
            case inflictDeath:
            case inflictBoned:
            case unusable:
               return Rarity.HUNDREDTH;
            default:
               return null;
         }
      }
   }

   private static float das(float m) {
      return ModTierUtils.doubleSidesAllHeroes(m);
   }

   public static String describeBeingIncreased() {
      return "+1 pip";
   }

   public static String describeBeingDecreased() {
      return "-1 pip";
   }

   public static String describeOthersSeeingNPips(String newAs) {
      return "Other keywords see coloured " + describeN() + " as " + newAs;
   }

   public static String describedRemainingWhenReplaced(String keywords) {
      return keywords + " remain when the side is replaced (if possible)";
   }

   public static StandardButton makeKeywordButton(Keyword k) {
      return new StandardButton(k.getColourTaggedString());
   }

   public static String makeName(String name, Keyword condition, Keyword bonus) {
      int split = (int)Math.ceil(name.length() / 2.0F);
      String a = name.substring(0, split);
      String b = name.substring(split);
      return TextWriter.getTag(condition.getColour()) + a + "[cu]" + TextWriter.getTag(bonus.getColour()) + b + "[cu]";
   }

   public static TextureRegion makePlaceholderCorner(Keyword keyword) {
      Color col = keyword.getColour();
      if (col != null) {
         String name = TextWriter.getNameForColour(col);
         if (name != null) {
            TextureRegion tr = ImageUtils.loadExt3d("keyword/special/" + name);
            if (tr != null) {
               return tr;
            }
         }
      }

      return ImageUtils.loadExt3d("keyword/special/placeholder");
   }

   public static String describeCombination(Keyword a, Keyword b, KeywordCombineType kct) {
      if (kct == KeywordCombineType.TC4X) {
         return "x4 if the conditions of both " + a.getColourTaggedString() + " and " + b.getColourTaggedString() + " are met";
      } else if (kct == KeywordCombineType.ConditionBonus) {
         return "If " + a.getColourTaggedString() + "'s condition is met, gain the effects of " + b.getColourTaggedString();
      } else {
         return kct == KeywordCombineType.XOR
            ? "x3 if the conditions of " + a.getColourTaggedString() + " xor " + b.getColourTaggedString() + " is met"
            : "unk: " + kct;
      }
   }

   public static ConditionalBonus getConditionalBonus(Keyword a, Keyword b, KeywordCombineType kct) {
      if (kct == KeywordCombineType.TC4X) {
         return new ConditionalBonus(
            new CombinedRequirement(a.getConditionalBonus().requirement, b.getConditionalBonus().requirement), ConditionalBonusType.Multiply, 4
         );
      } else if (kct == KeywordCombineType.ConditionBonus) {
         return new ConditionalBonus(a.getConditionalBonus().requirement, b.getConditionalBonus().bonusType, b.getConditionalBonus().bonusAmount);
      } else if (kct == KeywordCombineType.XOR) {
         return new ConditionalBonus(
            new XORRequirement(a.getConditionalBonus().requirement, b.getConditionalBonus().requirement), ConditionalBonusType.Multiply, 3
         );
      } else {
         throw new RuntimeException("ep: " + kct);
      }
   }

   public static KeywordAllowType getSwapRequirement(Keyword toSwap) {
      KeywordAllowType kat = toSwap.getAllowType();
      if (kat == KeywordAllowType.TARG_PIPS) {
         return KeywordAllowType.PIPS_ONLY;
      } else {
         return kat == KeywordAllowType.PIPS_ONLY ? KeywordAllowType.TARG_PIPS : kat;
      }
   }

   public static String swapRules(Keyword toSwap) {
      return toSwap.getRules().replaceAll("I am", "target is").replaceAll("vs targets on", "if I have").replaceAll("vs targets with", "if I have");
   }

   public static ConditionalBonus getSwapBonus(Keyword toSwap) {
      String er = "Failed to swap " + toSwap;
      ConditionalBonus cb = toSwap.getConditionalBonus();
      if (cb == null) {
         return errorNull(er);
      } else {
         ConditionalRequirement req = cb.requirement;
         if (req == null) {
            return errorNull(er);
         } else {
            ConditionalRequirement swapped = getSwapped(req);
            return swapped == null ? errorNull(er) : new ConditionalBonus(swapped, cb.bonusType, cb.bonusAmount);
         }
      }
   }

   public static ConditionalRequirement getSwapped(ConditionalRequirement req) {
      if (req instanceof ParamCondition) {
         ParamCondition pc = (ParamCondition)req;
         return ((ParamCondition)req).getSwapped();
      } else if (req instanceof GSCConditionalRequirement) {
         GSCConditionalRequirement gcgsccr = (GSCConditionalRequirement)req;
         return gcgsccr.getSwapped();
      } else {
         return null;
      }
   }

   private static ConditionalBonus errorNull(String msg) {
      throw new RuntimeException(msg);
   }

   public static KeywordAllowType getKATFromTargetingRequirement(ConditionalRequirement targetingRequirement) {
      if (targetingRequirement != TargetingRestriction.ExactlyValue && targetingRequirement != TargetingRestriction.ExactlyValuePicky) {
         return targetingRequirement == TargetingRestriction.NotMe ? KeywordAllowType.ALLY_TARG : KeywordAllowType.SINGLE_TARGET;
      } else {
         return KeywordAllowType.TARG_PIPS;
      }
   }

   public static List<Color> getKeywordColours() {
      return Arrays.asList(Colours.red, Colours.blue, Colours.green, Colours.light, Colours.pink, Colours.yellow, Colours.orange, Colours.grey, Colours.purple);
   }

   public static boolean hasEquivalentKeyword(EffType t, Eff eff) {
      return Tann.anySharedItems(getKeywordsFor(t), eff.getKeywords());
   }

   public static void init() {
      ke = new ArrayList<>();
      kh = Arrays.asList(Keyword.heal, Keyword.selfHeal);
      ks = Arrays.asList(Keyword.shield, Keyword.selfShield);
      kd = Arrays.asList(Keyword.damage);
      km = Arrays.asList(Keyword.manaGain);
   }

   public static List<Keyword> getKeywordsFor(EffType t) {
      switch (t) {
         case Shield:
            return ks;
         case Heal:
            return kh;
         case Mana:
            return km;
         case Damage:
            return kd;
         default:
            return ke;
      }
   }

   public static Keyword getKeywordsForSimple(EffType t) {
      switch (t) {
         case Shield:
            return Keyword.selfShield;
         case Heal:
            return Keyword.selfHeal;
         case Mana:
            return Keyword.manaGain;
         default:
            return null;
      }
   }

   public static String descStraight(int i) {
      return "this forms a straight of length " + i + "+ with the " + describeStraightDice();
   }

   public static boolean isMeta(Keyword k) {
      return META.contains(k);
   }

   public static boolean hasMetaKeyword(Eff e) {
      return Tann.anySharedItems(e.getKeywords(), META);
   }

   public static List<Keyword> getAbilityOnlyKeywords() {
      List<Keyword> result = new ArrayList<>();

      for (Keyword value : Keyword.values()) {
         if (value.abilityOnly()) {
            result.add(value);
         }
      }

      return result;
   }

   public static boolean isNonLinearWhenConvertingToAllHeroes(Keyword k) {
      return k == Keyword.duel || k == Keyword.ranged;
   }
}
