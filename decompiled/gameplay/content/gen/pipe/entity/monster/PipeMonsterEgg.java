package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EnSiBi;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.sound.Sounds;

public class PipeMonsterEgg extends PipeRegexNamed<MonsterType> {
   private static final PRNPart PREF = new PRNPref("egg");

   public PipeMonsterEgg() {
      super(PREF, ENTITY);
   }

   protected MonsterType internalMake(String[] groups) {
      return this.make(EntTypeUtils.byName(groups[0]));
   }

   public MonsterType example() {
      return this.make(MonsterTypeLib.randomWithRarity());
   }

   private MonsterType make(EntType src) {
      if (src.isMissingno()) {
         return null;
      } else {
         int hp = (int)Math.round(Math.pow(src.hp, 0.5));
         if (hp < 1) {
            return null;
         } else {
            EntSide hatchSide = new EnSiBi().size(EntSize.small).image("hatch").effect(new EffBill().summon(src.getName(), 1).keywords(Keyword.death)).val(1);
            EntSide blank = EntSidesBlobSmall.blank;
            MTBill mtb = new MTBill(EntSize.small);
            mtb.hp(hp);
            mtb.name(PREF + src.getName());
            mtb.death(Sounds.deathCute);
            mtb.arOverride(ImageUtils.loadArExt("portrait/monster/small/special/eggR1U1"));
            mtb.sides(hatchSide, hatchSide, blank, blank, hatchSide, blank);
            return mtb.bEntType();
         }
      }
   }

   @Override
   public boolean canGenerate(boolean wild) {
      return true;
   }

   protected MonsterType generateInternal(boolean wild) {
      return this.make(MonsterTypeLib.randomWithRarity());
   }

   @Override
   public float getRarity(boolean wild) {
      return 0.3F;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
