package com.tann.dice.gameplay.content.ent.type.blob.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.global.linked.all.GlobalHeroes;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.AddKeyword;
import com.tann.dice.gameplay.trigger.personal.linked.TriggerPersonalToGlobal;
import com.tann.dice.gameplay.trigger.personal.util.CalcStats;
import com.tann.dice.util.lang.Words;
import java.util.Arrays;
import java.util.List;

public class MonsterTypeBlobNightmare {
   public static final String SIGIL = "sigil";

   public static List<MonsterType> make() {
      return Arrays.asList(makeTo(8, Keyword.death, 48.0F, 48.0F), makeTo(20, Keyword.pain, 18.0F, 18.0F), makeTo(12, Keyword.decay, 6.0F, 6.0F));
   }

   private static MonsterType makeTo(int hp, Keyword k, float csd, float cshp) {
      EntSide es = ESB.wandPoison.val(2);
      return new MTBill(EntSize.reg)
         .name(Words.capitaliseFirst(k.getName()) + "Sigil")
         .hp(hp)
         .sides(es, es, es, es, es, es)
         .texture("special/totem" + Words.capitaliseFirst(k.name()))
         .max(2)
         .trait(
            new Trait(
               new TriggerPersonalToGlobal(new GlobalHeroes(new AffectSides(new AddKeyword(k)).monsterPassivePriority()), "inflict/" + k.name()),
               new CalcStats(csd, cshp),
               true
            )
         )
         .bEntType();
   }
}
