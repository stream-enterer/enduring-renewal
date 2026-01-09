package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.side.PipeHeroSides;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSideMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.List;

public class PipeMonsterSides extends PipeRegexNamed<MonsterType> {
   static PRNPart MID = new PRNMid("sd");

   public PipeMonsterSides() {
      super(MONSTER, MID, new PRNSideMulti());
   }

   public MonsterType example() {
      List<String> parts = new ArrayList<>();
      int toAdd = Tann.randomInt(5) + 2;

      for (int i = 0; i < toAdd; i++) {
         parts.add(PipeHeroSides.rBit());
      }

      String data = Tann.join(":", parts);
      return this.make(MonsterTypeLib.random(), data.split(":"), data);
   }

   protected MonsterType internalMake(String[] groups) {
      MonsterType ht = MonsterTypeLib.byName(groups[0]);
      String[] data = groups[1].split(":");
      return this.make(ht, data, groups[1]);
   }

   private MonsterType make(MonsterType mt, String[] data, String unparsed) {
      if (mt.isMissingno()) {
         return null;
      } else {
         List<EntSide> replacedSides = new ArrayList<>();

         for (String datum : data) {
            String[] parts = datum.split("-");
            if (!Tann.isInt(parts[0])) {
               return null;
            }

            int index = Integer.parseInt(parts[0]);
            int str = 0;
            if (parts.length == 2) {
               if (!Tann.isInt(parts[1])) {
                  return null;
               }

               str = Integer.parseInt(parts[1]);
            }

            EntSide es = this.make(index, str, mt);
            if (es == null) {
               return null;
            }

            replacedSides.add(es);
         }

         EntSide[] cpy = new EntSide[6];
         EntType.realToNice(cpy);

         for (int i = 0; i < 6; i++) {
            if (i < replacedSides.size()) {
               cpy[i] = replacedSides.get(i);
            } else {
               cpy[i] = mt.size.getBlank();
            }
         }

         EntType.niceToReal(cpy);
         return EntTypeUtils.copy(mt).name(mt.getName() + MID + unparsed).sidesRaw(cpy).bEntType();
      }
   }

   private EntSide make(int index, int val, MonsterType monsterType) {
      if (val <= 999 && val >= -999) {
         List<Object> objs = EntSidesLib.getSizedSides(monsterType.size);
         if (objs.size() <= index) {
            return null;
         } else {
            Object o = objs.get(index);
            if (o instanceof EntSide) {
               return (EntSide)o;
            } else if (o instanceof EnSiBi) {
               EnSiBi esb = (EnSiBi)o;
               return esb.val(val);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
