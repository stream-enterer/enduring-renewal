package com.tann.dice.gameplay.effect.eff;

import com.badlogic.gdx.math.Interpolation;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Undying;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.Random;

public enum EffType {
   Damage(false),
   Shield(true),
   Heal(false),
   HealAndShield(true),
   Mana(true),
   Blank,
   Buff(true),
   Kill,
   Reroll,
   RedirectIncoming,
   Summon,
   Recharge,
   Resurrect,
   Or,
   MultiplyShields,
   SetToHp,
   Event,
   SnapshotEvent,
   JustTarget,
   Enchant;

   private boolean allowBadTargets = false;

   private EffType() {
   }

   private EffType(boolean allowBadTargets) {
      this.allowBadTargets = allowBadTargets;
   }

   public boolean doesAllowBadTargets() {
      return this.allowBadTargets;
   }

   public String describe(Eff source) {
      switch (this) {
         case Blank:
            return "blank";
         case Damage:
            String partx = source.getValue() + " damage";
            if (source.isFriendlyForce()) {
               switch (source.getTargetingType()) {
                  case Single:
                     return partx + " to " + Words.entName(source, false);
                  case Group:
                     return partx + " to all " + Words.entName(source, true);
                  case Self:
                     return source.getValue() + " self damage";
                  default:
                     return "weird unknown friend damage description: " + source.getTargetingType();
               }
            } else {
               switch (source.getTargetingType()) {
                  case Single:
                     return partx;
                  case Group:
                     String middlexx = "";
                     if (source.hasRestriction(StateConditionType.Damaged)) {
                        middlexx = middlexx + "damaged ";
                     }

                     if (source.hasRestriction(StateConditionType.Dying)) {
                        middlexx = middlexx + "dying ";
                     }

                     return partx + " to all " + middlexx + Words.entName(source, true);
                  case Self:
                  default:
                     return "ahh help damage targetingType" + source.getTargetingType();
                  case ALL:
                     return partx + " to all heroes and monsters";
                  case Top:
                     return partx + " to the top-most " + Words.entName(source, null);
                  case Bot:
                     return partx + " to the bottom-most " + Words.entName(source, null);
                  case Mid:
                     return partx + " to the middle " + Words.entName(source, null);
                  case TopAndBot:
                     return partx + " to the top and bottom " + Words.entName(source, true);
               }
            }
         case Shield:
            String part = "Shield " + source.getValue();
            if (!source.isFriendlyForce()) {
               switch (source.getTargetingType()) {
                  case Single:
                     return part + " to " + Words.entName(source, false);
                  case Group:
                     return part + " to all " + Words.entName(source, true);
                  default:
                     return "weird unknown friend shield description: " + source.getTargetingType();
               }
            } else {
               switch (source.getTargetingType()) {
                  case Single:
                     return part;
                  case Group:
                     String middlex = "";
                     String endx = "";
                     if (source.hasRestriction(StateConditionType.Damaged)) {
                        middlex = middlex + "damaged ";
                     }

                     if (source.hasRestriction(StateConditionType.FullHP)) {
                        middlex = middlex + "undamaged ";
                     }

                     if (source.hasRestriction(StateConditionType.Dying)) {
                        middlex = middlex + "dying ";
                     }

                     if (source.hasRestriction(StateConditionType.HasShields)) {
                        endx = " with shields";
                     }

                     return part + " to all " + middlex + Words.entName(source, true) + endx;
                  case Self:
                     return "Self-" + part.toLowerCase();
                  case ALL:
                  case Top:
                  case Bot:
                  case Mid:
                  case TopAndBot:
                  default:
                     return part + " to " + source.getTargetingType();
                  case SpellSource:
                     return part + " to the source of the spell";
               }
            }
         case Heal:
            String part = "Heal " + source.getValue();
            switch (source.getTargetingType()) {
               case Single:
                  return part;
               case Group:
                  String middle = "";
                  String end = "";
                  if (source.hasRestriction(StateConditionType.Damaged)) {
                     middle = middle + "damaged ";
                  }

                  if (source.hasRestriction(StateConditionType.Dying)) {
                     middle = middle + "dying ";
                  }

                  if (source.hasRestriction(StateConditionType.HasShields)) {
                     end = " with shields";
                  }

                  return part + " to all " + middle + Words.entName(source, true) + end;
               case Self:
                  return "Self-" + part.toLowerCase();
               case ALL:
               case Bot:
               case Mid:
               case SpellSource:
               default:
                  return " Need healing description: " + source.getTargetingType();
               case Top:
                  return part + " to the top " + Words.entName(true, true, null);
               case TopAndBot:
                  return part + " to the top and bottom " + Words.entName(source, true);
               case Untargeted:
                  return part;
            }
         case HealAndShield:
            return Heal.describe(source).replaceAll("Heal", "Heal and shield");
         case Buff:
            return source.getBuffAndCopy().toNiceString(source);
         case Kill:
            String base = "Kill";
            switch (source.getTargetingType()) {
               case Single:
                  String result = base + " " + Words.entName(source, false);
                  if (source.hasRestriction(StateConditionType.HalfOrLessHP)) {
                     result = result + " with half or less hp";
                  }

                  return result;
               case Group:
                  return base + " all " + Words.entName(source, true);
               case Self:
                  if (source.getVisual() == VisualEffectType.Flee) {
                     return "I flee";
                  }

                  return "I die";
               case ALL:
               default:
                  return base + " unknown for " + source.getTargetingType();
               case Top:
                  return base + " the top-most " + Words.entName(source, null);
            }
         case Reroll:
            return "Gain " + source.getValue() + " " + Words.plural("reroll", source.getValue());
         case RedirectIncoming:
            return "Redirect all damage and enemy effects from " + Words.entName(source, false) + " to me";
         case Recharge:
            switch (source.getTargetingType()) {
               case Single:
                  return "Another hero can use their dice again";
               default:
                  return "Recharge err";
            }
         case Summon:
            String summonName = EntTypeUtils.byName(source.getSummonType()).getName(true);
            if (com.tann.dice.Main.self().translator.shouldTranslate()) {
               if (source.getValue() == 1) {
                  return "Summon " + summonName;
               }

               return "Summon " + source.getValue() + " x " + summonName;
            }

            String amt;
            if (source.getValue() == 1) {
               amt = "a";
               if (Words.startsWithVowel(summonName)) {
                  amt = "an";
               }
            } else {
               amt = "" + source.getValue();
            }

            return "Summon " + amt + " " + Words.plural(summonName, source.getValue());
         case Resurrect:
            return "Revive the"
               + (source.getValue() == 1 ? "" : " " + source.getValue())
               + " top-most defeated "
               + Words.entName(source, source.getValue() == 1 ? null : true);
         case Enchant:
            Modifier m = source.getEnchantMod();
            return m.getFullDescription() + " this fight";
         case JustTarget:
            TargetingType tt = source.getTargetingType();
            String targetString = tt == TargetingType.Group ? "All" : "Target";
            String thingTargeted = Words.entName(source, tt == TargetingType.Group ? true : null);
            if (tt == TargetingType.Self) {
               thingTargeted = "self";
            } else if (tt == TargetingType.ALL) {
               thingTargeted = "[b]all[b]";
            }

            return targetString + " " + thingTargeted + (source.hasValue() ? " " + source.getValue() : "");
         case Mana:
            return "+[nbp][nbp]" + Words.manaString(source.getValue());
         case SetToHp:
            switch (source.getTargetingType()) {
               case Single:
                  return "Set a hero to " + source.getValue() + " hp";
               case Group:
                  return "Set all heroes to " + source.getValue() + " hp";
               default:
                  return "Set " + source.getValue() + " hp";
            }
         case MultiplyShields:
            return Words.capitaliseFirst(Words.multiple(source.getValue())) + " all friendly shields";
         case Or:
            return (source.getOr(false).describe(false) + " " + com.tann.dice.Main.t("or") + " " + source.getOr(true).describe(false).toLowerCase())
               .replaceAll("\\[n\\]", " ");
         default:
            return "no base for " + this;
      }
   }

   public float getEffectTier(int tier, float pips, boolean player, Eff eff) {
      switch (this) {
         case Blank:
            return 0.0F;
         case Damage:
            return pips * (eff != null && eff.isFriendly() ? -1 : 1);
         case Shield:
            if (!player) {
               switch (eff.getTargetingType()) {
                  case Single:
                     return pips * 1.1F;
                  case Self:
                     return pips * 0.3F;
                  default:
                     return -500.0F;
               }
            }

            return Tann.effectTierOnlySinTote(pips, 5.0F + (tier - 1) * 2.0F, 0.7F, 2.3F + (tier - 1) * 0.7F, 0.9F);
         case Heal:
            if (eff != null && eff.hasKeyword(Keyword.vitality)) {
               return pips * 0.84F;
            } else {
               if (player) {
                  return Tann.effectTierOnlySinTote(pips, 4.0F + Math.max(0, tier - 1) * 3, 0.5F, 1.4F + Math.max(0, tier - 1) * 0.85F, 0.9F);
               }

               return pips * 0.9F;
            }
         case HealAndShield:
            return (Heal.getEffectTier(tier, pips, player, eff) + Shield.getEffectTier(tier, pips, player, eff)) * 0.9F;
         case Buff:
            if (eff != null) {
               Buff buff = eff.getBuff();
               if (buff.personal instanceof Undying) {
                  return (float)(0.7 + Math.pow(1.57, tier - 1));
               }

               return buff.getEffectTier((int)pips, tier);
            }
         case RedirectIncoming:
         default:
            return Float.NaN;
         case Kill:
            return pips * Interpolation.pow2Out.apply(0.5F, 1.1F, Math.min(1.0F, (pips - 1.0F) / (HeroTypeUtils.getEffectTierFor(tier) * 6.0F)));
         case Reroll:
            return 0.0F;
         case Recharge:
            return HeroTypeUtils.getEffectTierFor(tier) * 1.05F + 0.55F;
         case Summon:
            MonsterType mt = MonsterTypeLib.byName(eff.getSummonType());
            return pips * mt.getSummonValue();
         case Resurrect:
            float pipFactor = Tann.effectTierOnlySinTote(pips, 4.0F, 1.1F, 2.2F, 1.1F);
            return pipFactor * (1.0F + HeroTypeUtils.getEffectTierFor(tier)) * 0.425F;
         case Enchant:
         case JustTarget:
            return 0.0F;
         case Mana:
            return (float)(pips * Math.pow(1.18F, tier * 0.81F + 0.19F));
      }
   }

   @Override
   public String toString() {
      return this.name();
   }

   public float getCantripMultiplier() {
      switch (this) {
         case Damage:
            return 0.6F;
         case Shield:
            return 0.6F;
         case Heal:
            return 0.3F;
         case HealAndShield:
         case Kill:
         case Reroll:
         case RedirectIncoming:
         case Recharge:
         case Summon:
         case Resurrect:
         case Enchant:
         case JustTarget:
         default:
            return Float.NaN;
         case Buff:
            return 0.5F;
         case Mana:
            return 1.0F;
      }
   }

   public long getCollisionBits(Boolean player) {
      if (player == null) {
         player = true;
      }

      switch (this) {
         case Blank:
            return Collision.BLANK_SIDE;
         case Damage:
            return Collision.PHYSICAL_DAMAGE;
         case Shield:
            if (player != null && !player) {
               return Collision.ENEMY_SHIELD;
            }

            return Collision.SHIELD;
         case Heal:
            return Collision.HEAL;
         case HealAndShield:
            return Heal.getCollisionBits(player) | Shield.getCollisionBits(player);
         case Buff:
         case Kill:
         case Reroll:
         case RedirectIncoming:
         case Recharge:
         case Summon:
         case Resurrect:
         case Enchant:
         case JustTarget:
         default:
            return 0L;
         case Mana:
            return Collision.SPELL;
      }
   }

   public static EffType niceRandom(Random r) {
      return Tann.random(Arrays.asList(Damage, Shield, Heal, HealAndShield, Kill), r);
   }
}
