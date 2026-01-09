package com.tann.dice.gameplay.content.gen.pipe.entity.hero.side;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNSideMulti;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class PipeHeroSides extends PipeRegexNamed<HeroType> {
   static PRNPart MID = new PRNMid("sd");

   public PipeHeroSides() {
      super(HERO, MID, new PRNSideMulti());
   }

   public static Actor makeSidesWithLabels(EntSize size) {
      Pixl p = new Pixl(2, 2).border(Colours.grey);
      List<Object> objs = EntSidesLib.getSizedSides(size);
      int val = 1;

      for (int i = 0; i < objs.size(); i++) {
         Object obj = objs.get(i);
         final EntSide rl = FacadeUtils.maybeWithValue(obj, 1);
         Actor a = rl.makeBasicSideActor(0, false, null);
         final int finalI = i;
         a.addListener(
            new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  String n = "statue.sd." + finalI + "-" + Tann.randomInt(12);
                  HeroType ht = HeroTypeLib.byName(n);
                  com.tann.dice.Main.getCurrentScreen()
                     .pushAndCenter(FacadeUtils.example(FacadeUtils.getAttr("base", finalI), ht, rl.getTexture(), "" + finalI), 0.5F);
                  return true;
               }
            }
         );
         p.actor(a, com.tann.dice.Main.width * 0.5F);
      }

      return p.pix();
   }

   public static EntSide makeMechanicalOnly(int index, int val, EntSize size) {
      if (val <= 999 && val >= -999) {
         List<Object> objs = EntSidesLib.getSizedSides(size);
         return FacadeUtils.maybeWithValue(objs.get(index), val);
      } else {
         return null;
      }
   }

   public HeroType example() {
      List<String> parts = new ArrayList<>();
      int toAdd = Tann.randomInt(5) + 2;
      int var4 = 6;

      for (int i = 0; i < var4; i++) {
         parts.add(rBit());
      }

      String data = Tann.join(":", parts);
      return this.make(HeroTypeUtils.random(), data.split(":"), data);
   }

   public static String rBit() {
      return Tann.randomInt(20) + "-" + (Tann.randomInt(7) + 1);
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   protected HeroType internalMake(String[] groups) {
      HeroType ht = HeroTypeLib.byName(groups[0]);
      String[] data = groups[1].split(":");
      return this.make(ht, data, groups[1]);
   }

   private HeroType make(HeroType ht, String[] data, String unparsed) {
      if (ht.isMissingno()) {
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

            EntSide es = makeMechanicalOnly(index, str, ht.size);
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
               cpy[i] = ESB.blank;
            }
         }

         EntType.niceToReal(cpy);
         return HeroTypeUtils.copy(ht).name(ht.getName() + MID + unparsed).sidesRaw(cpy).bEntType();
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
}
