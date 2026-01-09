package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.StateConditionSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AffectSideEffect;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.Flip;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ReplaceWith;
import com.tann.dice.gameplay.trigger.personal.linked.stateCondition.GenericStateCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AffectSides extends Personal {
   public static final String TO_FROM_SKIP = "SKIP";
   final List<AffectSideCondition> conditions;
   final List<AffectSideEffect> effects;
   boolean combatPriority;
   boolean monsterPassivePriority;
   boolean heroPassivePriority;
   boolean mimicPriority;
   Boolean show;
   private List<Keyword> refKCache;

   @Override
   protected TextureRegion overrideImage() {
      for (AffectSideEffect effect : this.effects) {
         if (effect.overrideImage() != null) {
            return effect.overrideImage();
         }
      }

      return null;
   }

   public AffectSides(AffectSideEffect... affectSide) {
      this(new ArrayList<>(), Arrays.asList(affectSide));
   }

   public AffectSides(AffectSideCondition a, AffectSideCondition b, AffectSideEffect c) {
      this(Arrays.asList(a, b), Arrays.asList(c));
   }

   public AffectSides(AffectSideCondition affectSideCondition, AffectSideEffect... affectSide) {
      this(Arrays.asList(affectSideCondition), Arrays.asList(affectSide));
   }

   public AffectSides(GenericStateCondition stateCondition, AffectSideEffect... affectSide) {
      this(new StateConditionSideCondition(stateCondition), affectSide);
   }

   public AffectSides(SpecificSidesType specificSidesType, AffectSideEffect... affectSide) {
      this(Arrays.asList(new SpecificSidesCondition(specificSidesType)), Arrays.asList(affectSide));
   }

   public AffectSides(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      this.conditions = conditions;
      this.effects = effects;
   }

   @Override
   public String describeForSelfBuff() {
      List<String> effects = new ArrayList<>();
      String toFrom = "to";

      for (AffectSideEffect ase : this.effects) {
         String override = ase.getOverrideDescription(this.conditions, this.effects);
         if (override != null) {
            return override;
         }

         String desc = ase.describe();
         if (desc != null) {
            effects.add(desc);
         }

         if (ase.getToFrom() != null) {
            toFrom = ase.getToFrom();
         }
      }

      String result = "";

      for (AffectSideCondition con : this.conditions) {
         if (con.describeFirst()) {
            result = result + con.describe();
         }
      }

      result = result + Tann.commaList(effects);
      if (toFrom != null && !toFrom.equals("SKIP")) {
         result = result + " " + toFrom;
      }

      int conditionsCount = this.conditions.size();
      if (conditionsCount == 0) {
         return result + " all sides";
      } else {
         effects.clear();
         boolean hasSpecificSides = false;
         boolean single = false;

         for (AffectSideCondition asc : this.conditions) {
            if (!asc.isAfterSides() && !asc.describeFirst()) {
               if (asc instanceof SpecificSidesCondition) {
                  hasSpecificSides = true;
                  SpecificSidesCondition ssc = (SpecificSidesCondition)asc;
                  single = ssc.specificSidesType.sideIndices.length == 1;
               }

               String descx = asc.describe();
               if (descx != null) {
                  effects.add(descx);
               }
            }
         }

         if (!hasSpecificSides) {
            result = result + " all";
         }

         if (effects.size() > 0) {
            result = result + " " + Tann.commaList(effects, ", ", ", ");
         }

         if (!hasSpecificSides) {
            result = result + " side" + (single ? "" : "s");
         }

         effects.clear();

         for (AffectSideCondition ascx : this.conditions) {
            if (ascx.isAfterSides() && !ascx.describeFirst()) {
               String descx = ascx.describe();
               if (descx != null) {
                  effects.add(descx);
               }
            }
         }

         if (effects.size() > 0) {
            result = result + " " + Tann.commaList(effects);
         }

         return result;
      }
   }

   private boolean hasAnyAddGraphics() {
      for (AffectSideEffect ase : this.effects) {
         if (ase.getAddDraw(false, this.conditions) != null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      boolean notAlone = false;

      for (AffectSideEffect ase : this.effects) {
         Actor overrideActor = ase.getOverrideActor(this.conditions);
         if (overrideActor != null) {
            return overrideActor;
         }
      }

      if (!this.hasAnyAddGraphics()) {
         return super.makePanelActorI(big);
      } else {
         boolean needsGraphic = false;
         boolean hasSideImage = false;
         boolean needsArrow = false;

         for (AffectSideCondition asc : this.conditions) {
            needsGraphic |= asc.needsGraphic();
            hasSideImage |= asc.hasSideImage();
            needsArrow |= asc.needsArrow();
         }

         for (AffectSideEffect asex : this.effects) {
            needsGraphic |= asex.needsGraphic();
         }

         if (!needsGraphic) {
            return super.makePanelActorI(big);
         } else {
            GenericView base = null;

            for (AffectSideCondition asc : this.conditions) {
               GenericView testBase = asc.getActor();
               if (testBase != null) {
                  base = testBase;
                  break;
               }
            }

            if (base == null) {
               base = new RandomSidesView(1);
            }

            List<Actor> preconditions = new ArrayList<>();

            for (AffectSideCondition ascx : this.conditions) {
               EffectDraw addDraw = ascx.getAddDraw();
               Actor precon = ascx.getPrecon();
               if (addDraw != null) {
                  base.addDraw(addDraw);
               } else if (precon != null) {
                  preconditions.add(precon);
               } else if (!ascx.overrideDoesntNeedPrecon()) {
                  preconditions.add(this.makeUnknownPrecondition(ascx));
               }
            }

            for (AffectSideEffect asex : this.effects) {
               EffectDraw addDraw = asex.getAddDraw(hasSideImage, this.conditions);
               if (addDraw != null) {
                  notAlone |= addDraw.isNotAlone();
                  base.addDraw(addDraw);
               }
            }

            Actor result;
            if (preconditions.size() > 0) {
               int gap = 2;
               Pixl p = new Pixl(0);

               for (Actor a : preconditions) {
                  p.actor(a).gap(2);
               }

               if (needsArrow) {
                  p.image(Images.arrowRight, Colours.light).gap(2);
               } else {
                  p.text(":").gap(2);
               }

               p.actor(base);
               result = p.pix();
            } else {
               result = base;
            }

            if (notAlone) {
               result.setName("notalone");
            }

            return result;
         }
      }
   }

   private Actor makeUnknownPrecondition(AffectSideCondition asc) {
      return new TextWriter(asc.describe(), 40);
   }

   @Override
   public void affectSide(EntSideState sideState, EntState owner, int triggerIndex) {
      int index = -1;

      for (AffectSideCondition asc : this.conditions) {
         if (!asc.validFor(sideState, owner, triggerIndex)) {
            return;
         }

         if (index == -1) {
            index = asc.indexValid(sideState, owner);
         }
      }

      for (AffectSideEffect affectSideEffect : this.effects) {
         affectSideEffect.affect(sideState, owner, index, this, triggerIndex);
      }
   }

   public AffectSides buffPriority() {
      this.combatPriority = true;
      return this;
   }

   public AffectSides mimicPriority() {
      this.mimicPriority = true;
      return this;
   }

   public AffectSides monsterPassivePriority() {
      this.monsterPassivePriority = true;
      return this;
   }

   public AffectSides heroPassivePriority() {
      this.heroPassivePriority = true;
      return this;
   }

   public AffectSides show(boolean show) {
      this.show = show;
      return this;
   }

   @Override
   public float getPriority() {
      if (this.mimicPriority) {
         return -11.0F;
      } else if (this.heroPassivePriority) {
         return -11.0F;
      } else if (this.monsterPassivePriority) {
         return -9.0F;
      } else if (this.combatPriority) {
         return 0.0F;
      } else {
         return this.buff != null ? 0.0F : -10.0F;
      }
   }

   @Override
   public boolean showInEntPanelInternal() {
      if (this.show != null) {
         return this.show;
      } else {
         for (AffectSideEffect ase : this.effects) {
            if (ase.showInPanel()) {
               return true;
            }
         }

         for (AffectSideCondition asc : this.conditions) {
            if (asc.showInPanel()) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public String getImageName() {
      for (AffectSideCondition asc : this.conditions) {
         String img = asc.getImageName();
         if (img != null) {
            return img;
         }
      }

      for (AffectSideEffect ase : this.effects) {
         String img = ase.getImageName();
         if (img != null) {
            return img;
         }
      }

      return super.getImageName();
   }

   @Override
   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      if (targetPresent.isUsed()) {
         return false;
      } else {
         EntSideState ess = targetPresent.getCurrentSideState();
         if (ess == null) {
            return false;
         } else {
            boolean effectValid = false;

            for (AffectSideEffect ase : this.effects) {
               effectValid |= ase.isRecommended(sourceState, targetPresent, targetFuture);
            }

            if (!effectValid) {
               return false;
            } else {
               for (AffectSideCondition c : this.conditions) {
                  if (!c.validFor(ess, targetFuture, 0)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;
      boolean replaces = false;
      boolean hasPosition = false;
      boolean hasIndexed = false;

      for (AffectSideEffect ase : this.effects) {
         bit |= ase.getCollisionBits(player);
         replaces |= ase instanceof ReplaceWith;
         hasIndexed |= ase.isIndexed();
      }

      for (AffectSideCondition asc : this.conditions) {
         hasPosition |= asc instanceof SpecificSidesCondition;
         bit |= asc.getCollisionBits(player);
      }

      if (!hasIndexed) {
         if (!hasPosition && replaces) {
            bit |= Collision.allSides(player);
         }

         if (this.conditions.isEmpty()) {
            bit |= Collision.allSides(player);
         }
      }

      return bit;
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      for (AffectSideEffect ase : this.effects) {
         float val = ase.getEffectTier(pips, tier);
         if (!Float.isNaN(val)) {
            return val;
         }
      }

      return Float.NaN;
   }

   public List<AffectSideEffect> getEffects() {
      return this.effects;
   }

   public List<AffectSideCondition> getConditions() {
      return this.conditions;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      if (this.refKCache == null) {
         this.refKCache = new ArrayList<>();

         for (AffectSideEffect ase : this.effects) {
            List<Keyword> extras = ase.getReferencedKeywords();
            if (extras != null) {
               this.refKCache.addAll(extras);
            }
         }
      }

      return this.refKCache;
   }

   @Override
   public String describeForGiveBuff(Eff source) {
      boolean allKeyword = this.effects.size() > 0;

      for (AffectSideEffect ase : this.effects) {
         allKeyword |= ase instanceof AddKeyword;
      }

      return allKeyword ? this.describeForSelfBuff().replaceAll("all", "target's") : super.describeForGiveBuff(source);
   }

   @Override
   public String[] getSound() {
      if (this.conditions.size() == 1 && this.conditions.get(0) instanceof TypeCondition) {
         return Sounds.boost;
      } else if (this.effects.size() == 2 && this.effects.get(0) instanceof AddKeyword) {
         return Sounds.boost;
      } else {
         return this.effects.get(0) instanceof Flip ? Sounds.flap : null;
      }
   }

   @Override
   public Personal genMult(int mult) {
      List<AffectSideEffect> newEffects = new ArrayList<>();

      for (AffectSideEffect effect : this.effects) {
         AffectSideEffect nASE = effect.genMult(mult);
         if (nASE == null) {
            return null;
         }

         newEffects.add(nASE);
      }

      return new AffectSides(this.conditions, newEffects);
   }

   @Override
   public boolean isMultiplable() {
      for (AffectSideEffect effect : this.effects) {
         if (!effect.isMultiplable()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public String hyphenTag() {
      if (this.conditions.size() == 1) {
         String s = this.conditions.get(0).hyphenTag();
         if (s != null) {
            return s;
         }
      }

      return this.effects.size() != 1 ? null : this.effects.get(0).hyphenTag();
   }

   @Override
   public Actor getTraitActor() {
      if (this.conditions.isEmpty() && this.effects.size() == 1) {
         AffectSideEffect ase = this.effects.get(0);
         if (ase instanceof AddKeyword) {
            List<Keyword> list = ((AddKeyword)ase).getKeywordList();
            if (list.size() == 1) {
               return new ImageActor(list.get(0).getImage());
            }
         }
      }

      return super.getTraitActor();
   }

   @Override
   public boolean skipMultiplable() {
      for (AffectSideEffect effect : this.effects) {
         if (effect.skipMultipliable()) {
            return true;
         }
      }

      return false;
   }

   public Personal splice(AffectSides newInner) {
      return new AffectSides(this.conditions, newInner.effects);
   }

   public static AffectSides mrgEffects(AffectSides aa, AffectSides bb) {
      AffectSides ab = new AffectSides(aa.getConditions(), Tann.combineLists(aa.getEffects(), bb.getEffects()));
      if (ab.getConditions().size() == 1 && ab.getConditions().get(0) instanceof SpecificSidesCondition) {
         SpecificSidesCondition ssc = (SpecificSidesCondition)ab.getConditions().get(0);
         if (ab.getEffects().size() != 1 && ab.getEffects().size() != ssc.specificSidesType.sideIndices.length) {
            return null;
         }
      }

      ab.squishKeywords();
      return ab;
   }

   private void squishKeywords() {
      List<AddKeyword> skw = new ArrayList<>();

      for (int i = 0; i < this.effects.size(); i++) {
         AffectSideEffect ase = this.effects.get(i);
         if (ase instanceof AddKeyword) {
            skw.add((AddKeyword)ase);
         }
      }

      if (skw.size() > 1) {
         int index = this.effects.indexOf(skw.get(0));
         this.effects.removeAll(skw);
         List<Keyword> kw = new ArrayList<>();

         for (AddKeyword addKeyword : skw) {
            kw.addAll(addKeyword.getKeywordList());
         }

         this.effects.add(index, new AddKeyword(kw));
      }
   }
}
