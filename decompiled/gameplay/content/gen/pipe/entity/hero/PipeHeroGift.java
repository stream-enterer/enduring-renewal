package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.gameplay.trigger.personal.choosable.AddChoosableOnPick;

public class PipeHeroGift extends PipeRegexNamed<HeroType> {
   public static final PRNPart SEP = new PRNMid("gift");

   public PipeHeroGift() {
      super(HERO, SEP, MODIFIER);
   }

   protected HeroType internalMake(String[] groups) {
      return this.make(groups[0], groups[1]);
   }

   private HeroType make(String heroName, String modName) {
      if (heroName != null && modName != null && !heroName.isEmpty() && !modName.isEmpty()) {
         HeroType ht;
         Modifier mod;
         if (heroName.length() < modName.length()) {
            if ((ht = HeroTypeLib.byName(heroName)).isMissingno() || (mod = ModifierLib.byName(modName)).isMissingno()) {
               return null;
            }
         } else if ((mod = ModifierLib.byName(modName)).isMissingno() || (ht = HeroTypeLib.byName(heroName)).isMissingno()) {
            return null;
         }

         Personal aiha = new AddChoosableOnPick(mod);
         String realHeroName = heroName + SEP + modName;
         return HeroTypeUtils.withPassive(ht, realHeroName, aiha, "[grey]on pick, gain: " + mod.getName());
      } else {
         return null;
      }
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random().getName(), ModifierLib.random().getName());
   }

   @Override
   public boolean showHigher() {
      return true;
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
