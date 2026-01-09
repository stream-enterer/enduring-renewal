package com.tann.dice.gameplay.trigger.personal.affectSideModular.condition;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.EffType;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.GenericView;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.RandomSidesView;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeCondition extends AffectSideCondition {
   public final List<EffType> types;
   final boolean basicOnly;
   final boolean allowKeywords;

   public TypeCondition(EffType type) {
      this(Arrays.asList(type), false);
   }

   public TypeCondition(EffType type, boolean basicOnly) {
      this(Arrays.asList(type), basicOnly);
   }

   public TypeCondition(EffType type, boolean basicOnly, boolean allowKeywords) {
      this(Arrays.asList(type), basicOnly, allowKeywords);
   }

   public TypeCondition(List<EffType> types, boolean basicOnly) {
      this(types, basicOnly, !basicOnly);
   }

   public TypeCondition(List<EffType> types, boolean basicOnly, boolean allowKeywords) {
      this.types = types;
      this.basicOnly = basicOnly;
      this.allowKeywords = allowKeywords;
   }

   @Override
   public GenericView getActor() {
      return new RandomSidesView(this.types.size());
   }

   @Override
   public boolean validFor(EntSideState sideState, EntState owner, int triggerAffectSides) {
      Eff eff = sideState.getCalculatedEffect();

      for (EffType t : this.types) {
         if (eff.hasType(t, this.basicOnly)) {
            return true;
         }

         if (this.allowKeywords) {
            if (KUtils.hasEquivalentKeyword(t, eff)) {
               return true;
            }

            EffType other = getEquivalentType(t);
            if (other != null && eff.hasType(other, false)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public EffectDraw getAddDraw() {
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y, int index) {
            batch.setColor(Colours.z_white);
            EntSide base = EntSidesLib.getSide(TypeCondition.this.types.get(index), TypeCondition.this.basicOnly);
            batch.draw(base.getTexture(), x, y);
            super.draw(batch, x, y);
         }
      };
   }

   @Override
   public boolean needsGraphic() {
      return this.basicOnly;
   }

   @Override
   public boolean hasSideImage() {
      return true;
   }

   @Override
   public boolean isPlural() {
      return true;
   }

   @Override
   public String describe() {
      if (this.basicOnly) {
         return "basic " + Tann.commaList(this.types).toLowerCase();
      } else {
         List<String> toList = new ArrayList<>();

         for (int i = 0; i < this.types.size(); i++) {
            EffType type = this.types.get(i);
            toList.add(type.name());
            if (this.allowKeywords) {
               Keyword k = KUtils.getKeywordsForSimple(type);
               if (k != null) {
                  toList.add(k.getColourTaggedString());
               }
            }
         }

         return Tann.commaList(toList, "/", "/").toLowerCase();
      }
   }

   @Override
   public boolean sayOtherForSharpWitEtc() {
      for (EffType et : this.types) {
         if (!KUtils.getKeywordsFor(et).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static EffType getEquivalentType(EffType t) {
      switch (t) {
         case Heal:
            return EffType.HealAndShield;
         case Shield:
            return EffType.HealAndShield;
         default:
            return null;
      }
   }

   public List<EffType> getAllIncludingEquivs() {
      List<EffType> result = new ArrayList<>();
      result.addAll(this.types);

      for (EffType t : this.types) {
         EffType equiv = getEquivalentType(t);
         if (equiv != null) {
            result.add(equiv);
         }
      }

      return result;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;

      for (EffType t : this.types) {
         bit |= t.getCollisionBits(player);
      }

      return bit;
   }
}
