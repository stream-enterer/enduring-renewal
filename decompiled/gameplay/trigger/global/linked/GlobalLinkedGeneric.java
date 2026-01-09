package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ui.TextWriter;

public class GlobalLinkedGeneric extends GlobalLinked {
   final GlobalLinkedGeneric.GenCon condition;
   final Global g;

   public GlobalLinkedGeneric(Global link, GlobalLinkedGeneric.GenCon condition) {
      super(link);
      this.g = link;
      this.condition = condition;
   }

   @Override
   public Global getLinkedGlobal(DungeonContext context, int turn) {
      return this.condition.holdsFor(context, turn) ? this.g : super.getLinkedGlobal(context, turn);
   }

   @Override
   public String describeForSelfBuff() {
      return "[notranslate]" + com.tann.dice.Main.t("If " + this.condition.describe()) + ": " + this.g.describeForSelfBuff();
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, new TextWriter("[text]" + this.condition.describe(), 50), this.g, Colours.pink);
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new GlobalLinkedGeneric(newCenter, this.condition);
   }

   public interface GenCon {
      String describe();

      boolean holdsFor(DungeonContext var1, int var2);
   }
}
