package com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.RenameUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;
import com.tann.dice.gameplay.trigger.personal.weird.DescribeOnlyTrait;
import java.util.List;

public class DataSourceMonster extends DataSource<MonsterType> {
   public DataSourceMonster() {
      super(PipeRegexNamed.MONSTER);
   }

   public MonsterType combine(MonsterType monType, AtlasRegion ar, String realName) {
      MTBill bill = EntTypeUtils.copy(monType);
      bill.arOverride(ar);
      bill.name(realName);
      return bill.bEntType();
   }

   public MonsterType makeT(String name) {
      return MonsterTypeLib.byName(name);
   }

   public MonsterType exampleBase() {
      return MonsterTypeLib.randomWithRarity();
   }

   public AtlasRegion getImage(MonsterType heroType) {
      return heroType.portrait;
   }

   public MonsterType upscale(MonsterType mt, int multN) {
      if (mt.getName(false).matches(".*x\\d.*")) {
         return null;
      } else if (multN <= 1) {
         return mt;
      } else {
         int mult = multN;
         MTBill mtb = new MTBill(mt.size);
         String newName = "x" + multN + "." + mt.getName(false);
         mtb.name(newName);
         mtb.hp(mt.hp * multN);

         for (Trait t : mt.traits) {
            mtb.trait(t.copy());
         }

         EntSide[] transformedSides = new EntSide[6];

         for (int si = 0; si < 6; si++) {
            EntSide src = mt.sides[si];
            Eff base = src.getBaseEffect();
            EntSide replace;
            if (base.hasValue()) {
               replace = src.withValue(base.getValue() * mult);
            } else {
               replace = src.copy();
            }

            transformedSides[si] = replace;
         }

         mtb.arOverride(mt.portrait);
         mtb.sidesRaw(transformedSides);
         return mtb.bEntType();
      }
   }

   public MonsterType rename(MonsterType src, String rename, String realName) {
      return src != null && !src.isMissingno() && realName != null && !realName.isEmpty() && rename != null && !rename.isEmpty()
         ? RenameUtils.make(src, realName, rename)
         : null;
   }

   public MonsterType document(MonsterType src, String documentation, String realName) {
      return EntTypeUtils.copy(src).trait(new DescribeOnlyTrait(documentation).setShowBelowPanel(true)).name(realName).bEntType();
   }

   public MonsterType retier(MonsterType monsterType, int newTier, String realName) {
      return null;
   }

   public MonsterType makeIndexed(long val) {
      if (val < 0L) {
         return null;
      } else {
         List<MonsterType> all = MonsterTypeLib.getMasterCopy();
         return val < all.size() ? all.get((int)val) : null;
      }
   }

   public MonsterType renameUnderlying(MonsterType monsterType, String rename) {
      return EntTypeUtils.copy(monsterType).name(rename).bEntType();
   }

   public MonsterType withItem(MonsterType mt, Item i, String s) {
      return EntTypeUtils.copy(mt).name(s).trait(new Trait(new AsIfHasItem(i))).bEntType();
   }

   public static int reverseIndex(MonsterType mt) {
      return MonsterTypeLib.getMasterCopy().indexOf(mt);
   }
}
