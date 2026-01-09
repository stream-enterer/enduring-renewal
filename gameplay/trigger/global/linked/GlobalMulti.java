package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class GlobalMulti extends GlobalLinked {
   final Global global;
   final List<Global> lst = new ArrayList<>();

   public GlobalMulti(Global global, int amt) {
      super(global);

      for (int i = 0; i < amt; i++) {
         this.lst.add(global);
      }

      this.global = global;
   }

   @Override
   public String describeForSelfBuff() {
      return "x" + this.lst.size() + ": " + this.global.describeForSelfBuff();
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return makeMultiSidepanel(big, this.lst.size(), this.global);
   }

   public static Actor makeMultiSidepanel(boolean big, int size, Trigger global) {
      String txt = "[pink]x" + size;
      return DipPanel.makeSidePanelGroup(big, new TextWriter(txt), global, Colours.pink);
   }

   @Override
   public List<Global> getLinkedGlobalList(int level, DungeonContext context, int turn) {
      return this.lst;
   }

   @Override
   public boolean isMultiplable() {
      return false;
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new GlobalMulti(newCenter, this.lst.size());
   }
}
