package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.lang.Words;
import java.util.List;

public class ChangeType extends AffectSideEffect {
   public final EnSiBi changeTo;
   public final Eff debug;
   final String newSideDescription;
   final int multiplier;

   public ChangeType(EnSiBi changeTo, String newSideDescription) {
      this(changeTo, newSideDescription, 1);
   }

   public ChangeType(EnSiBi changeTo, String newSideDescription, int multiplier) {
      this.changeTo = changeTo;
      this.newSideDescription = newSideDescription;
      this.multiplier = multiplier;
      this.debug = changeTo.val(1).getBaseEffect();
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String conditionString = "";
      boolean plural = true;
      boolean sayOther = false;

      for (AffectSideCondition condition : conditions) {
         conditionString = conditionString + condition.describe();
         plural &= condition.isPlural();
         if (condition instanceof TypeCondition) {
            conditionString = conditionString + " sides";
         }

         sayOther |= condition.sayOtherForSharpWitEtc();
      }

      if (conditions.size() == 0) {
         conditionString = "all sides";
      }

      String suffix = plural ? "[light]'" + this.newSideDescription + "'[cu] sides" : "a [light]'" + this.newSideDescription + "'[cu] side";
      String result = com.tann.dice.Main.t("Replace " + conditionString + " with ") + com.tann.dice.Main.t(suffix);
      String kString = sayOther ? "other keywords" : "keywords";
      String possessive = plural ? "their" : "its";
      return this.multiplier == 1
         ? "[notranslate]" + result + ", " + com.tann.dice.Main.t("retaining " + possessive + " original pips and " + kString)
         : "[notranslate]"
            + result
            + ", "
            + com.tann.dice.Main.t(
               "retaining "
                  + possessive
                  + " "
                  + kString
                  + " and [orange]"
                  + Words.multiple(this.multiplier)
                  + "[cu] "
                  + possessive
                  + " "
                  + Words.plural("pip", plural ? 2 : 1)
            );
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      if (conditions.size() == 1 && conditions.get(0) instanceof SpecificSidesCondition) {
         return null;
      } else if (conditions.isEmpty()) {
         return null;
      } else {
         GenericView view = new RandomSidesView(1);

         for (AffectSideCondition condition : conditions) {
            GenericView actor = condition.getActor();
            if (actor != null) {
               view = actor;
            }
         }

         for (AffectSideCondition conditionx : conditions) {
            EffectDraw a = conditionx.getAddDraw();
            if (a != null) {
               view.addDraw(a);
            }
         }

         if (view == null) {
            view = new RandomSidesView(1);
         }

         Pixl p = new Pixl(0);
         int gap = 2;
         return p.actor(view)
            .gap(2)
            .image(Images.arrowRight, Colours.light)
            .gap(2)
            .actor(this.changeTo.val(-999).makeBasicSideActor(this.multiplier == 1 ? -1 : this.multiplier, true, null))
            .pix();
      }
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      final EntSide es = this.changeTo.val(-999);
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            super.draw(batch, x, y);
            Color col = Colours.text;
            int bonus = ChangeType.this.multiplier == 1 ? -1 : ChangeType.this.multiplier;
            es.drawWithMultiplier(batch, x, y, col, bonus);
         }
      };
   }

   @Override
   public String describe() {
      return null;
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      Eff e = sideState.getCalculatedEffect();
      EffType originalType = e.getType();
      int newValue = e.hasValue() ? e.getValue() * this.multiplier : 0;
      EntSide newSide = this.changeTo.val(newValue);
      ReplaceWith.replaceSide(sideState, newSide);
      sideState.getCalculatedEffect().addKeywords(e.getKeywords());

      for (AffectSideCondition c : sourceTrigger.getConditions()) {
         if (c instanceof TypeCondition) {
            TypeCondition tc = (TypeCondition)c;

            for (EffType et : tc.types) {
               for (Keyword k : KUtils.getKeywordsFor(et)) {
                  if (k != null && !tc.getAllIncludingEquivs().contains(originalType)) {
                     sideState.getCalculatedEffect().removeKeyword(k);
                  }
               }
            }
         }
      }
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return this.debug.getKeywordsForDisplay(false);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return this.debug.getCollisionBits(player);
   }
}
