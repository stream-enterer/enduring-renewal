package com.tann.dice.gameplay.content.gen.pipe.item.facade;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNPref;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.util.Tann;

public class PipeItemFacadeMini extends PipeRegexNamed<Item> {
   private static final PRNPart PREF = new PRNPref("facade");
   private static final PRNPart SEP2 = PipeRegexNamed.COLON;

   public PipeItemFacadeMini() {
      super(PREF, SIDE_ID, SEP2, TWO_DIGIT_DELTA);
   }

   protected Item internalMake(String[] groups) {
      String newSideData = groups[0];
      String h = groups[1];
      String folder = newSideData.substring(0, 3);
      String number = newSideData.substring(3);
      int numInt = Integer.parseInt(number);
      return this.make(folder, numInt, Integer.parseInt(h));
   }

   private Item make(String folder, int newSideIndex, int h) {
      String name = PREF + folder + newSideIndex + SEP2 + h;
      return PipeItemFacade.make(name, folder, newSideIndex, h, 0, 0);
   }

   public Item example() {
      return this.make("ite", Tann.randomInt(300), Tann.randomInt(20));
   }

   @Override
   public Actor getExtraActor() {
      return PipeItemFacade.makeEx();
   }
}
