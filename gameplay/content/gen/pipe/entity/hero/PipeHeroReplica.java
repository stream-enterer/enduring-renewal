package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.trigger.personal.RenameHero;
import java.util.List;

public class PipeHeroReplica extends PipeRegexNamed<HeroType> {
   public static final PRNPref PREF = new PRNPref("replica");

   public PipeHeroReplica() {
      super(PREF, HERO);
   }

   protected HeroType internalMake(String[] groups) {
      String heroName = groups[0];
      return this.make(HeroTypeLib.byName(heroName));
   }

   private HeroType make(HeroType src) {
      if (src.isMissingno()) {
         return null;
      } else {
         String name = PREF + src.getName();
         HTBill htb = EntTypeUtils.copy(src).clearTraits().name(name).sides(sidesFromHero(src.makeEnt()));
         String disp = src.getName(true, false);
         if (!disp.equals(src.getName())) {
            htb.trait(new RenameHero(disp));
         }

         return htb.bEntType();
      }
   }

   public static EntSide[] sidesFromHero(Ent h) {
      List<EntSideState> srcStates = h.getBlankState().getAllSideStates();
      EntSide[] newSides = new EntSide[6];

      for (int i = 0; i < 6; i++) {
         EntSideState existing = srcStates.get(i);
         Eff ecf = existing.getCalculatedEffect();
         newSides[i] = new EntSide(existing.getCalculatedTexture(), ecf, EntSize.reg, existing.getHsl());
      }

      EntType.realToNice(newSides);
      return newSides;
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random());
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
