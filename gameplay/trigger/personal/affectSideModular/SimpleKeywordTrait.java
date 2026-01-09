package com.tann.dice.gameplay.trigger.personal.affectSideModular;

import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.bill.ETBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;

public class SimpleKeywordTrait extends AffectSides {
   final Keyword k;

   public SimpleKeywordTrait(Keyword k) {
      super(new AddKeyword(k));
      this.k = k;
   }

   @Override
   public float affectStrengthCalc(float total, float avgRawValue, EntType type) {
      ETBill withKeyword = EntTypeUtils.copyE(type);
      withKeyword.clearTraits();
      EntSide[] sides = withKeyword.getSides();

      for (int i = 0; i < sides.length; i++) {
         sides[i] = sides[i].withKeyword(this.k);
      }

      return total + withKeyword.bEntType().getAvgEffectTier(false) - type.getAvgEffectTier(false);
   }

   @Override
   public float affectTotalHpCalc(float hp, EntType entType) {
      return hp;
   }
}
