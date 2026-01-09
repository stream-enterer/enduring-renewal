package com.tann.dice.gameplay.effect.eff;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.effect.Buff;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.ConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.GSCConditionalRequirement;
import com.tann.dice.gameplay.effect.eff.conditionalBonus.conditionalRequirement.TargetingRestriction;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.event.entState.StateEvent;
import com.tann.dice.gameplay.fightLog.event.snapshot.SnapshotEvent;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.weirdTextmod.Sidesc;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.StateConditionType;
import com.tann.dice.screens.shaderFx.DeathType;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Eff implements Cloneable {
   public static final int DEBUG_EFF_VALUE = -999;
   private final EffType type;
   private final TargetingType targetingType;
   private final String overrideDescription;
   private final VisualEffectType visualEffect;
   private final List<ConditionalRequirement> restrictions;
   private final String summonType;
   private final boolean friendly;
   private final boolean basic;
   private final boolean hasValue;
   private final StateEvent event;
   private final SnapshotEvent snapshotEvent;
   private final List<ConditionalRequirement> doubleConditions;
   private List<Keyword> keywords;
   private Buff buff;
   private Eff orFriendly;
   private Eff orEnemy;
   private final Eff bonusUntargetedEffect;
   private int value;
   static final String EXTRA_EFF_SEP = ", then ";
   static final List<Keyword> empty = Collections.unmodifiableList(new ArrayList<>());
   private List<Keyword> bonusKeywords = new ArrayList<>();

   public Eff(
      EffType type,
      TargetingType targetingType,
      VisualEffectType visualEffect,
      List<ConditionalRequirement> restrictions,
      int value,
      Buff buff,
      String summonType,
      boolean friendly,
      boolean hasValue,
      List<Keyword> keywords,
      Eff orFriendly,
      Eff orEnemy,
      Eff bonusUntargetedEffect,
      boolean basic,
      StateEvent event,
      SnapshotEvent snapshotEvent,
      String overrideDescription,
      List<ConditionalRequirement> doubleConditions
   ) {
      this.type = type;
      this.targetingType = targetingType;
      this.visualEffect = visualEffect;
      this.restrictions = restrictions;
      this.value = GlobalNumberLimit.box(value);
      this.buff = buff;
      this.summonType = summonType;
      this.friendly = friendly;
      this.keywords = keywords;
      this.orFriendly = orFriendly;
      this.orEnemy = orEnemy;
      this.bonusUntargetedEffect = bonusUntargetedEffect;
      this.hasValue = hasValue;
      this.basic = basic;
      this.event = event;
      this.snapshotEvent = snapshotEvent;
      this.overrideDescription = overrideDescription;
      if (bonusUntargetedEffect != null && bonusUntargetedEffect.needsTarget()) {
         throw new RuntimeException("Targeted untargeted");
      } else {
         this.doubleConditions = doubleConditions;
      }
   }

   @Override
   public String toString() {
      return this.describe();
   }

   private String getRestrictionsString() {
      if (this.getRestrictions().size() == 0) {
         return "";
      } else {
         StringBuilder result = new StringBuilder();
         int times = 0;

         for (ConditionalRequirement restriction : this.getRestrictions(false)) {
            if (restriction instanceof GSCConditionalRequirement) {
               boolean skip;
               skip = false;
               GSCConditionalRequirement gc = (GSCConditionalRequirement)restriction;
               StateConditionType sct = gc.getGsc().getStateConditionType();
               label33:
               switch (this.targetingType) {
                  case Group:
                     switch (sct) {
                        case Damaged:
                        case FullHP:
                        case Dying:
                        case HasShields:
                           skip = true;
                        default:
                           break label33;
                     }
                  case Single:
                     skip |= sct == StateConditionType.HalfOrLessHP;
               }

               if (skip) {
                  continue;
               }
            }

            if (times > 0) {
               result.append(" and");
            }

            String restr = restriction.describe(this);
            if (restr != null) {
               result.append(" ").append(restr);
               times++;
            }
         }

         return result.toString();
      }
   }

   public Eff copy() {
      Eff e = null;

      try {
         e = (Eff)this.clone();
      } catch (CloneNotSupportedException var3) {
         throw new RuntimeException(var3);
      }

      if (this.getBuff() != null) {
         e.setBuff(this.getBuff().copy());
      }

      if (this.getType() == EffType.Or) {
         e.orEnemy = e.orEnemy.copy();
         e.orFriendly = e.orFriendly.copy();
      }

      e.keywords = new ArrayList<>(this.keywords);
      e.bonusKeywords = new ArrayList<>(this.bonusKeywords);
      return e;
   }

   public String describe() {
      return this.describe(true);
   }

   public String describe(boolean includeKeywords, boolean withThisTurn) {
      try {
         return this.describeInternal(includeKeywords, withThisTurn);
      } catch (Exception var4) {
         return "err";
      }
   }

   public String describe(boolean includeKeywords) {
      return this.describe(includeKeywords, true);
   }

   private String describeInternal(boolean includeKeywords, boolean withThisTurn) {
      if (this.getOverrideDescription() != null) {
         return this.getOverrideDescription()
            .replace(Sidesc.NOKEYWORD, "")
            .replace("[pips]", this.getValue() + "")
            .replace("[pipsk]", KUtils.getValue(this) + "");
      } else if (this.skipDescription()) {
         return "";
      } else {
         String restrictions = this.getRestrictionsString();
         String description = this.getType().describe(this);
         if (this.getType().equals(EffType.Buff)) {
            description = this.getBuffAndCopy().toNiceString(this, withThisTurn, restrictions.equals(""));
         }

         String result = com.tann.dice.Main.tOnce(description + restrictions);
         if (this.bonusUntargetedEffect != null) {
            if (this.needsKeywordsBracketed()) {
               return this.kd(result, this)
                  + com.tann.dice.Main.t(", then ")
                  + this.kd(com.tann.dice.Main.t(this.bonusUntargetedEffect.describe(false)), this.bonusUntargetedEffect);
            }

            result = result + com.tann.dice.Main.t(", then ");
            result = result + com.tann.dice.Main.t(this.bonusUntargetedEffect.describe(false).toLowerCase());
         }

         if (this.doubleConditions != null) {
            for (ConditionalRequirement doubleCondition : this.doubleConditions) {
               result = result + "[n]x2 if " + doubleCondition.describe(this);
            }
         }

         if (includeKeywords) {
            result = addKeywordsToString(result, this);
         }

         return result;
      }
   }

   public boolean needsKeywordsBracketed() {
      if (this.bonusUntargetedEffect == null) {
         return false;
      } else {
         List<Keyword> cpyWithoutSpell = new ArrayList<>(this.getKeywords());

         for (int i = cpyWithoutSpell.size() - 1; i >= 0; i--) {
            Keyword k = cpyWithoutSpell.get(i);
            if (k.abilityOnly()) {
               cpyWithoutSpell.remove(k);
            }
         }

         List<Keyword> other = this.bonusUntargetedEffect.getKeywords();
         return !other.equals(cpyWithoutSpell) && !cpyWithoutSpell.isEmpty();
      }
   }

   private String kd(String result, Eff eff) {
      boolean hk = eff.getKeywords().size() > 0;
      return hk ? "(" + result + " " + eff.makeKeywordsString() + ")" : result;
   }

   public static String addKeywordsToString(String current, Eff eff) {
      String keywords = eff.makeKeywordsString();
      if (keywords != null) {
         if (!keywords.isEmpty() && !current.isEmpty()) {
            current = current + " ";
         }

         current = current + keywords;
      }

      return current;
   }

   public static String hyphenInsteadOfNewline(String current) {
      return current.replaceAll("\\[n\\]", " - ");
   }

   private boolean skipDescription() {
      return this.getType() == EffType.Event || this.getType() == EffType.SnapshotEvent;
   }

   private String makeKeywordsString() {
      List<Keyword> keywords = this.getKeywordsForDisplay(false);
      return keywords.size() == 0 ? null : KUtils.describeKeywords(keywords);
   }

   public VisualEffectType getVisual() {
      return this.visualEffect;
   }

   public int getValue() {
      return this.value;
   }

   public String getNoTargetsString() {
      return this.isUnusableBecauseNerfed() ? "Does nothing due to curses!" : "No valid targets";
   }

   public Buff getBuffAndCopy() {
      return this.getBuff().copy();
   }

   public void playSound() {
      String[] s = this.getSound();
      if (s != null) {
         Sounds.playSound(s);
      }
   }

   public String[] getSound() {
      for (Keyword k : this.keywords) {
         switch (k) {
            case boost:
            case permaBoost:
               return Sounds.boost;
            case smith:
               return Sounds.smith;
            case weaken:
               return Sounds.deboost;
            case repel:
               return Sounds.clangs;
         }
      }

      switch (this.getType()) {
         case Recharge:
            return Sounds.boost;
         case HealAndShield:
            return Sounds.heals;
         case Shield:
            if (this.getTargetingType() == TargetingType.Group) {
               return Sounds.song;
            }

            return Sounds.blocks;
         case Resurrect:
            return Sounds.resurrect;
         case Buff:
            return this.getBuff().personal.getSound();
         case RedirectIncoming:
            return Sounds.whistle;
         case Mana:
            return Sounds.magic;
         case Reroll:
            return Sounds.onRoll;
         case Kill:
            return Sounds.summonImp;
         case MultiplyShields:
            return Sounds.clink;
         case JustTarget:
            return Sounds.arrowWobble;
         case Enchant:
            return Sounds.summonBones;
         default:
            return null;
      }
   }

   public boolean isFriendly() {
      return this.friendly ^ this.hasKeyword(Keyword.possessed);
   }

   public boolean isFriendlyForce() {
      return this.friendly;
   }

   public boolean needsTarget() {
      return this.getTargetingType().requiresTarget;
   }

   public void setValue(int value) {
      if (this.hasValue) {
         this.value = GlobalNumberLimit.box(value);
      }
   }

   public List<Ent> getSummons() {
      if (this.getType() != EffType.Summon) {
         return new ArrayList<>();
      } else {
         List<Ent> summons = new ArrayList<>();

         for (int i = 0; i < this.getValue(); i++) {
            EntType et = EntTypeUtils.byName(this.getSummonType());
            summons.add(et.makeEnt());
         }

         return summons;
      }
   }

   public List<ConditionalRequirement> getRestrictions() {
      return this.getRestrictions(true);
   }

   public List<ConditionalRequirement> getRestrictions(boolean includeKeywords) {
      List<ConditionalRequirement> result = new ArrayList<>(this.restrictions);
      if (includeKeywords) {
         for (Keyword k : this.getKeywordForGameplay()) {
            ConditionalRequirement cr = k.getTargetingConditionalRequirement();
            if (cr != null) {
               result.add(cr);
            }
         }
      }

      return result;
   }

   public boolean hasKeyword(Keyword keyword) {
      return this.keywords.contains(keyword);
   }

   public boolean hasRestriction(TargetingRestriction restriction) {
      return this.getRestrictions().contains(restriction);
   }

   public boolean hasRestriction(StateConditionType restriction) {
      for (ConditionalRequirement cr : this.getRestrictions()) {
         if (cr instanceof GSCConditionalRequirement) {
            GSCConditionalRequirement gcr = (GSCConditionalRequirement)cr;
            if (gcr.getGsc().getStateConditionType() == restriction) {
               return true;
            }
         }
      }

      return false;
   }

   public List<Keyword> getKeywords() {
      return this.keywords;
   }

   public List<Keyword> getKeywordForGameplay() {
      return this.keywords;
   }

   public List<Keyword> getReferencedKeywords() {
      if (this.type == EffType.Buff) {
         List<Keyword> result = new ArrayList<>();
         result.addAll(this.keywords);
         result.addAll(this.buff.personal.getReferencedKeywords());
         return result;
      } else {
         return this.keywords;
      }
   }

   public List<Keyword> getKeywordsForDisplay(boolean explain) {
      List<Keyword> all = new ArrayList<>(this.getKeywords());
      String ord = this.getOverrideDescription();
      if (ord != null && ord.contains(Sidesc.NOKEYWORD)) {
         all = new ArrayList<>(this.getBonusKeywords());
      }

      if (explain && this.getType() == EffType.Buff && this.getBuff() != null && this.getBuff().personal != null) {
         all.addAll(this.getBuff().personal.getReferencedKeywords());
      }

      return all;
   }

   public Eff getOr(boolean friendly) {
      return friendly ? this.orFriendly : this.orEnemy;
   }

   public boolean hasType(EffType targetType, boolean basicOnly, Ent target) {
      if (basicOnly && !this.isBasic()) {
         return false;
      } else if (this.getType() == targetType) {
         return true;
      } else if (this.getType() != EffType.HealAndShield || targetType != EffType.Heal && targetType != EffType.Shield) {
         if (this.getType() != EffType.Or) {
            return false;
         } else {
            return target != null
               ? this.getOr(target.isPlayer()).hasType(targetType, basicOnly)
               : this.getOr(true).hasType(targetType, basicOnly) || this.getOr(false).hasType(targetType, basicOnly);
         }
      } else {
         return true;
      }
   }

   public boolean hasType(EffType targetType, boolean basicOnly) {
      return this.hasType(targetType, basicOnly, null);
   }

   public void addKeyword(Keyword newKeyword) {
      if (KUtils.allowAddingKeyword(newKeyword, this)) {
         int priority = newKeyword.getSortPriority();
         boolean added = false;

         for (int i = 0; i < this.keywords.size(); i++) {
            int sp = this.keywords.get(i).getSortPriority();
            if (priority < sp) {
               this.keywords.add(i, newKeyword);
               added = true;
               break;
            }
         }

         if (!added) {
            this.keywords.add(newKeyword);
         }

         this.bonusKeywords.add(newKeyword);
         if (this.getType() == EffType.Or) {
            this.orEnemy.addKeyword(newKeyword);
            this.orFriendly.addKeyword(newKeyword);
         }
      }
   }

   public List<Keyword> getBonusKeywords() {
      return this.bonusKeywords;
   }

   public DeathType getDeathType() {
      return this.visualEffect.deathType;
   }

   public boolean allowBadTargets() {
      if (this.hasKeyword(Keyword.growth)) {
         return true;
      } else if (this.hasKeyword(Keyword.selfShield)) {
         return true;
      } else {
         return this.getType() != EffType.Or ? this.getType().doesAllowBadTargets() : this.getOr(false).allowBadTargets() || this.getOr(true).allowBadTargets();
      }
   }

   public boolean isUnusableBecauseNerfed() {
      return this.hasValue && this.getValue() <= 0 && !this.canStillUseWithValueZero();
   }

   public boolean canStillUseWithValueZero() {
      return false;
   }

   public void removeKeyword(Keyword keyword) {
      this.keywords.remove(keyword);
      this.bonusKeywords.remove(keyword);
   }

   public StateEvent getEvent() {
      return this.event;
   }

   public SnapshotEvent getSnapshotEvent() {
      return this.snapshotEvent;
   }

   public void addKeywords(List<Keyword> keywordList) {
      for (Keyword k : keywordList) {
         this.addKeyword(k);
      }
   }

   public void clearKeywords() {
      this.keywords.clear();
   }

   public Actor getBasicImage() {
      return this.getBasicImage(this.getValue() + "");
   }

   public Actor getBasicImage(String inner) {
      Actor a = this.getSimpleBasicImage(inner);
      Color col = this.isFriendly() ? Colours.green : Colours.red;
      switch (this.targetingType) {
         case Group:
            return new Pixl(2, 2).border(col).actor(a).pix();
         case Single:
         default:
            return a;
         case Top:
            return new Pixl().image(Images.arrowUp, col).gap(2).actor(a).pix();
         case TopAndBot:
            return new Pixl(1).actor(a).image(Images.arrowUp, col).image(Images.arrowDown, col).pix();
      }
   }

   private Actor getSimpleBasicImage(String text) {
      Color borderCol = null;
      Color textCol;
      switch (this.getTargetingType()) {
         case Self:
            textCol = this.getType() == EffType.Damage ? Colours.dark : Colours.light;
            break;
         default:
            textCol = Colours.light;
      }

      String displayText = TextWriter.getTag(textCol) + text;
      switch (this.getType()) {
         case HealAndShield:
            return Tann.imageWithText(Images.eq_iconHealShield, displayText, borderCol);
         case Shield:
            return Tann.imageWithText(Images.eq_iconShield, displayText, borderCol);
         case Resurrect:
         case RedirectIncoming:
         case Reroll:
         case MultiplyShields:
         case JustTarget:
         case Enchant:
         default:
            return Trigger.unknown();
         case Buff:
            return this.getBuff().personal.makePanelActor(true);
         case Mana:
            return Tann.imageWithText(Images.eq_iconMana, displayText, borderCol);
         case Kill:
            return new ImageActor(Images.eq_skullWhite, Colours.light);
         case Damage:
            switch (this.getTargetingType()) {
               case Group:
                  textCol = Colours.yellow;
                  break;
               case Self:
                  textCol = Colours.dark;
            }

            Actor a = Tann.imageWithText(Images.eq_iconDamage, displayText, borderCol);
            if (this.hasKeyword(Keyword.petrify)) {
               a = new Pixl().image(EntSidesBlobSmall.petrify.val(1).getTexture()).tannText(displayText).pix();
            }

            return a;
         case Heal:
            return Tann.imageWithText(Images.eq_iconHeal, displayText, borderCol);
         case Summon:
            if (this.getValue() == 1) {
               return new Pixl(2).text("+").image(EntTypeUtils.byName(this.getSummonType()).portrait, true).pix();
            }

            return new Pixl(2)
               .text("+")
               .actor(Tann.actorWithText(new ImageActor(EntTypeUtils.byName(this.getSummonType()).portrait, true), this.getValue() + "", Colours.dark))
               .pix();
         case Event:
            return null;
      }
   }

   public String getOverrideDescription() {
      return this.overrideDescription;
   }

   public boolean hasValue() {
      return this.hasValue;
   }

   public boolean allowAutoskip() {
      if (this.type == EffType.Damage && this.targetingType == TargetingType.Self) {
         return true;
      } else if (this.type == EffType.RedirectIncoming && !this.hasKeyword(Keyword.selfShield)) {
         return true;
      } else {
         for (Keyword k : this.getKeywordForGameplay()) {
            if (k.getMetaKeyword() != null && KUtils.allowAutoskip(k.getMetaKeyword())) {
               return true;
            }

            if (KUtils.allowAutoskip(k)) {
               return true;
            }
         }

         return false;
      }
   }

   public int hashEff() {
      int result = 13;
      result += this.getValue();
      result += this.getTargetingType().hashCode() * 2;
      result += this.getType().hashCode() * 3;

      for (Keyword k : this.getKeywords()) {
         result += k.hashCode() * 5;
      }

      return result + this.describe().hashCode() * 7;
   }

   public EffType getType() {
      return this.type;
   }

   public TargetingType getTargetingType() {
      return this.targetingType;
   }

   public String getSummonType() {
      return this.summonType;
   }

   public boolean isBasic() {
      return this.basic;
   }

   public Buff getBuff() {
      return this.buff;
   }

   public void setBuff(Buff buff) {
      this.buff = buff;
   }

   public boolean canKill() {
      return this.type == EffType.Damage || this.type == EffType.Kill || this.hasKeyword(Keyword.damage);
   }

   public boolean isSpecialAddKeyword() {
      return this.type == EffType.Buff
         && this.buff.personal instanceof AffectSides
         && ((AffectSides)this.buff.personal).getEffects().get(0) instanceof AddKeyword;
   }

   public long getCollisionBits(Boolean player) {
      long result = 0L;
      if (player == null) {
         player = true;
      }

      if (!this.isFriendly()) {
         player = !player;
      }

      for (int i = 0; i < this.keywords.size(); i++) {
         result |= this.keywords.get(i).getCollisionBits();
      }

      switch (this.getType()) {
         case Buff:
            result |= this.buff.personal.getCollisionBits(player);
            return result - (result & Collision.GENERIC_ALL_SIDES_HERO);
         case Damage:
            if (this.targetingType == TargetingType.Self) {
               return result;
            }
         default:
            return result | this.getType().getCollisionBits(player);
      }
   }

   public Eff getBonusUntargetedEffect() {
      return this.bonusUntargetedEffect;
   }

   public boolean canBeUsedUntargeted(Snapshot present) {
      switch (this.type) {
         case Resurrect:
            List<EntState> es = present.getStates(true, true);

            for (int i = 0; i < es.size(); i++) {
               if (es.get(i).canResurrect()) {
                  return true;
               }
            }

            return false;
         default:
            return true;
      }
   }

   public boolean isMultiplable() {
      switch (this.type) {
         case HealAndShield:
         case Shield:
         case Resurrect:
         case Mana:
         case Reroll:
         case Damage:
         case Heal:
         case Summon:
            return true;
         case Buff:
         case RedirectIncoming:
         case Kill:
         case MultiplyShields:
         case JustTarget:
         case Enchant:
         default:
            return false;
      }
   }

   public boolean sameAs(Eff eff) {
      return this.hashEff() == eff.hashEff();
   }

   public Eff innatifyKeywords() {
      this.bonusKeywords.clear();
      return this;
   }

   public Modifier getEnchantMod() {
      return ModifierLib.byName(this.getSummonType());
   }

   public List<ConditionalRequirement> getDoubleConditions() {
      return this.doubleConditions;
   }
}
