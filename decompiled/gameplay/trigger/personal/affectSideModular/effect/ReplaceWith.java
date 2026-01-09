package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.HSL;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.linked.GlobalNumberLimit;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TargetedCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.TypeCondition;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import java.util.ArrayList;
import java.util.List;

public class ReplaceWith extends AffectSideEffect {
   private final EntSide[] replaceSides;

   public ReplaceWith(EntSide... replaceSides) {
      this.replaceSides = replaceSides;
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            EntSide bon = FlatBonus.getFromThing(ReplaceWith.this.replaceSides, index);
            if (bon != null) {
               bon.draw(batch, ChangeArt.genericSource(), x, y, null, null);
            }
         }
      };
   }

   @Override
   public String getOverrideDescription(List<AffectSideCondition> conditions, List<AffectSideEffect> effects) {
      String result = "Replace ";
      boolean plural = false;
      boolean hasSST = false;
      if (conditions.size() == 0) {
         result = result + "all sides ";
      } else {
         for (AffectSideCondition asc : conditions) {
            hasSST |= asc instanceof SpecificSidesCondition;
            plural |= asc.isPlural();
            result = result + asc.describe() + " ";
         }

         if (!result.contains("side") && !hasSST) {
            result = result + Words.plural("side", plural) + " ";
         }
      }

      result = result + "with";
      return "[notranslate]" + com.tann.dice.Main.t(result) + " " + com.tann.dice.Main.t(this.describe());
   }

   @Override
   public String describe() {
      boolean allSame = true;

      for (int i = 1; i < this.replaceSides.length; i++) {
         allSame &= this.replaceSides[i - 1].same(this.replaceSides[i]);
      }

      if (allSame) {
         return this.wrapDesc(this.replaceSides[0].getBaseEffect().describe().replaceAll("\\[n\\]", " "));
      } else {
         List<String> strings = new ArrayList<>();

         for (EntSide es : this.replaceSides) {
            strings.add(this.wrapDesc(es.getBaseEffect().describe()));
         }

         return Tann.commaList(strings).replaceAll("\\[n\\]", " ");
      }
   }

   private String wrapDesc(String desc) {
      return "[grey]'" + desc + "'[cu]";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      EntSide rep;
      if (this.replaceSides.length != 1 && index != -1) {
         rep = this.replaceSides[index];
      } else {
         rep = this.replaceSides[0];
      }

      replaceSide(sideState, rep);
   }

   public static void replaceSide(EntSideState from, EntSide to) {
      replaceSide(from, to.getBaseEffect(), to.getTexture(), to.getHSL());
   }

   public static void replaceSide(EntSideState from, EntSideState to) {
      replaceSide(from, to.getCalculatedEffect(), to.getCalculatedTexture(), to.getHsl());
   }

   private static void replaceSide(EntSideState from, Eff to, TextureRegion toTex) {
      replaceSide(from, to, toTex, null);
   }

   private static void replaceSide(EntSideState from, Eff to, TextureRegion toTex, HSL hsl) {
      Eff original = from.getCalculatedEffect().copy();
      List<Keyword> kw = original.getKeywords();
      if (kw.contains(Keyword.dogma)) {
         int tv = to.getValue();
         if (tv == -999) {
            tv = 0;
         }

         from.getCalculatedEffect().setValue(tv);
      } else {
         from.changeTo(to, toTex, hsl);
      }

      Eff finalEff = from.getCalculatedEffect();
      if (kw.contains(Keyword.enduring)) {
         finalEff.addKeywords(kw);
      }

      if (kw.contains(Keyword.resilient)) {
         int newVal = finalEff.hasValue() ? original.getValue() : 0;
         finalEff.setValue(newVal);
         finalEff.addKeyword(Keyword.resilient);
      }
   }

   @Override
   public Actor getOverrideActor(List<AffectSideCondition> conditions) {
      for (AffectSideCondition asc : conditions) {
         if (asc instanceof TypeCondition || asc instanceof TargetedCondition) {
            return this.makeActorWithArrow(asc);
         }
      }

      return super.getOverrideActor(conditions);
   }

   private Actor makeActorWithArrow(AffectSideCondition asc) {
      Pixl p = new Pixl(0);
      int gap = 2;
      GenericView gv = asc.getActor();
      gv.addDraw(asc.getAddDraw());
      p.actor(gv).gap(2).image(Images.arrowRight, Colours.light).gap(2).actor(this.replaceSides[0].makeBasicSideActor(0, false, null));
      return p.pix();
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   public EntSide[] getReplaceSides() {
      return this.replaceSides;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      List<Keyword> result = new ArrayList<>();

      for (EntSide es : this.replaceSides) {
         for (Keyword k : es.getBaseEffect().getReferencedKeywords()) {
            if (!result.contains(k)) {
               result.add(k);
            }
         }
      }

      return result;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bits = 0L;

      for (int i = 0; i < this.replaceSides.length; i++) {
         bits |= this.replaceSides[i].getBaseEffect().getCollisionBits(player);
      }

      return Collision.ignored(bits, Collision.HEAL | Collision.SHIELD | Collision.PHYSICAL_DAMAGE);
   }

   @Override
   public boolean isIndexed() {
      return this.replaceSides.length > 1;
   }

   @Override
   public AffectSideEffect genMult(int mult) {
      if (this.replaceSides.length != 1) {
         return null;
      } else {
         EntSide rep = this.replaceSides[0];
         Eff base = rep.getBaseEffect();
         return base.hasValue() && base.getValue() != 0 ? new ReplaceWith(rep.withValue(GlobalNumberLimit.box(rep.getBaseEffect().getValue() * mult))) : null;
      }
   }
}
