package com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.trigger.personal.RenameHero;
import com.tann.dice.gameplay.trigger.personal.item.AsIfHasItem;
import com.tann.dice.gameplay.trigger.personal.weird.DescribeOnlyTrait;
import java.util.List;

public class DataSourceHero extends DataSource<HeroType> {
   public DataSourceHero() {
      super(PipeRegexNamed.HERO);
   }

   public HeroType combine(HeroType heroType, AtlasRegion ar, String realName) {
      return HeroTypeUtils.copy(heroType).arOverride(ar).name(realName).bEntType();
   }

   public HeroType makeT(String name) {
      return HeroTypeLib.byName(name);
   }

   public HeroType exampleBase() {
      return HeroTypeUtils.random();
   }

   public AtlasRegion getImage(HeroType heroType) {
      return heroType.portrait;
   }

   public HeroType upscale(HeroType heroType, int multiplier) {
      return multiplyHero(heroType, multiplier);
   }

   public HeroType rename(HeroType ht, String rename, String realName) {
      if (ht.isMissingno()) {
         return null;
      } else {
         RenameHero rh = new RenameHero(rename);
         return HeroTypeUtils.withPassive(ht, realName, rh, null);
      }
   }

   public HeroType document(HeroType heroType, String documentation, String realName) {
      return EntTypeUtils.copy(heroType).trait(new DescribeOnlyTrait(documentation)).name(realName).bEntType();
   }

   public HeroType retier(HeroType heroType, int newTier, String realName) {
      return newTier >= 0 && newTier <= 999 ? HeroTypeUtils.copy(heroType).name(realName).tier(newTier).bEntType() : null;
   }

   public HeroType makeIndexed(long val) {
      if (val < 0L) {
         return null;
      } else {
         List<HeroType> all = HeroTypeLib.getMasterCopy();
         return val < all.size() ? all.get((int)val) : null;
      }
   }

   public HeroType renameUnderlying(HeroType heroType, String rename) {
      return HeroTypeUtils.copy(heroType).name(rename).bEntType();
   }

   public HeroType withItem(HeroType ht, Item i, String s) {
      AsIfHasItem aiha = new AsIfHasItem(i);
      return HeroTypeUtils.withPassive(ht, s, aiha, "[grey]innate item: " + i.getName(true));
   }

   public static int reverseIndex(HeroType ht) {
      return HeroTypeLib.getMasterCopy().indexOf(ht);
   }

   private static HeroType multiplyHero(HeroType ht, int mult) {
      if (ht.getName(false).matches(".*x\\d.*")) {
         return null;
      } else if (mult <= 1) {
         return ht;
      } else {
         HTBill htb = HeroTypeUtils.copy(ht);
         htb.hp(ht.hp * mult);
         htb.name("x" + mult + "." + ht.getName());
         htb.tier(ht.level * mult);
         EntSide[] transformedSides = new EntSide[6];

         for (int si = 0; si < 6; si++) {
            EntSide src = ht.sides[si];
            Eff base = src.getBaseEffect();
            EntSide replace;
            if (base.hasValue()) {
               replace = src.withValue(src.getBaseEffect().getValue() * mult);
            } else {
               replace = src.copy();
            }

            transformedSides[si] = replace;
         }

         htb.sidesRaw(transformedSides);
         return htb.bEntType();
      }
   }
}
