package com.tann.dice.gameplay.content.gen.pipe.item.facade;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.HSL;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.ItBill;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.effect.ChangeArt;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class PipeItemFacade extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("facade");
   private static final PRNPart SEP2 = PipeRegexNamed.COLON;
   public static final String EXFO = "ite";

   public PipeItemFacade() {
      super(PREF, SIDE_ID, SEP2, TWO_DIGIT_DELTA, SEP2, TWO_DIGIT_DELTA, SEP2, TWO_DIGIT_DELTA);
   }

   protected Item internalMake(String[] groups) {
      String newSideData = groups[0];
      String h = groups[1];
      String s = groups[2];
      String l = groups[3];
      if (Tann.isInt(h) && Tann.isInt(s) && Tann.isInt(l) && newSideData.length() >= 4) {
         String folder = newSideData.substring(0, 3);
         String number = newSideData.substring(3);
         int numInt = Integer.parseInt(number);
         return this.make(numInt, folder, Integer.parseInt(h), Integer.parseInt(s), Integer.parseInt(l));
      } else {
         return null;
      }
   }

   private Item make(int newSideIndex, String folder, int h, int s, int l) {
      String name = PREF + folder + newSideIndex + SEP2 + h + SEP2 + s + SEP2 + l;
      return make(name, folder, newSideIndex, h, s, l);
   }

   public static Item make(String name, String folder, int newSideIndex, int h, int s, int l) {
      EntSize size = EntSize.reg;
      EntSide fafa = FacadeUtils.make(newSideIndex, folder, 0, size, new EffBill().specialAddKeyword(Keyword.heavy).bEff());
      if (fafa == null) {
         return null;
      } else {
         fafa = fafa.withHsl(new HSL((float)h, (float)s, (float)l));
         return new ItBill(name).prs(new AffectSides(SpecificSidesType.Middle, new ChangeArt(fafa))).bItem();
      }
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }

   public Item example() {
      return this.make(Tann.randomInt(300), "ite", Tann.randomInt(20), Tann.randomInt(20), Tann.randomInt(20));
   }

   public static Actor makeEx() {
      return new StandardButton("ids").makeTiny().setRunnable(new Runnable() {
         @Override
         public void run() {
            FacadeUtils.showTag("base");
         }
      });
   }

   @Override
   public Actor getExtraActor() {
      return makeEx();
   }
}
