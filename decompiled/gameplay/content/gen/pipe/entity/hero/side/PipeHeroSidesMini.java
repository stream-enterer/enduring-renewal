package com.tann.dice.gameplay.content.gen.pipe.entity.hero.side;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.List;

public class PipeHeroSidesMini extends PipeRegexNamed<HeroType> {
   static PRNPart MID = new PRNMid("sd");
   static PRNPart SEP = prnS(":");

   public PipeHeroSidesMini() {
      super(HERO, MID, SIDE_POSITION, SEP, SIDE_SINGLE);
   }

   public HeroType example() {
      return this.make(
         HeroTypeUtils.random(), SpecificSidesType.getNiceSidesType(com.tann.dice.Main.self().getR()), Tann.randomInt(100) + "-" + Tann.randomInt(10)
      );
   }

   public static String rBit() {
      return Tann.randomInt(20) + "-" + (Tann.randomInt(7) + 1);
   }

   protected HeroType internalMake(String[] groups) {
      HeroType ht = HeroTypeLib.byName(groups[0]);
      SpecificSidesType sst = SpecificSidesType.byName(groups[1]);
      String data = groups[2];
      return this.make(ht, sst, data);
   }

   private HeroType make(HeroType src, SpecificSidesType sst, String datum) {
      if (src.isMissingno()) {
         return null;
      } else {
         String[] parts = datum.split("-");
         if (!Tann.isInt(parts[0])) {
            return null;
         } else {
            int index = Integer.parseInt(parts[0]);
            int str = 0;
            if (parts.length == 2) {
               if (!Tann.isInt(parts[1])) {
                  return null;
               }

               str = Integer.parseInt(parts[1]);
            }

            EntSide es = PipeHeroSides.makeMechanicalOnly(index, str, src.size);
            if (es == null) {
               return null;
            } else {
               List<EntSideState> srcStates = src.makeEnt().getBlankState().getAllSideStates();
               EntSide[] newSides = new EntSide[6];

               for (int i = 0; i < 6; i++) {
                  EntSideState existing = srcStates.get(i);
                  Eff ecf = existing.getCalculatedEffect().innatifyKeywords();
                  if (Tann.contains(sst.sideIndices, i)) {
                     newSides[i] = es;
                  } else {
                     newSides[i] = new EntSide(existing.getCalculatedTexture(), ecf, EntSize.reg, existing.getHsl());
                  }
               }

               EntType.realToNice(newSides);
               String realHeroName = src.getName(false) + MID + sst.getShortName() + SEP + datum;
               return HeroTypeUtils.copy(src).sides(newSides).name(realHeroName).bEntType();
            }
         }
      }
   }

   @Override
   public Actor getExtraActor() {
      return new StandardButton("ids").makeTiny().setRunnable(new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().pushAndCenter(Tann.makeScrollpaneIfNecessary(PipeHeroSides.makeSidesWithLabels(EntSize.reg)), 0.5F);
         }
      });
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
