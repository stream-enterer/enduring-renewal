package com.tann.dice.gameplay.trigger.global.scaffolding.levelRequirement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.gameplay.trigger.global.linked.GlobalLinked;
import com.tann.dice.util.Colours;

public class GlobalLevelRequirement extends GlobalLinked {
   final LevelRequirement requirement;
   final Global linked;
   public static final Color LEVEL_COL = Colours.yellow;
   static final String[] rms = new String[]{" to each fight", " each fight"};

   public GlobalLevelRequirement(LevelRequirement requirement, Global linked) {
      super(linked);
      this.requirement = requirement;
      this.linked = linked;
   }

   @Override
   public String describeForSelfBuff() {
      String start = this.requirement.describe();
      if (!start.toLowerCase().contains("after")) {
         start = (this.linked.isDescribedAsBeforeFight() ? "Before " : "During ") + start;
      }

      if (this.requirement.isDescribedAt()) {
         start = "At " + this.requirement.describe();
      }

      String end = this.linked.describeForSelfBuff();

      for (int i = 0; i < rms.length; i++) {
         if (end.contains(rms[i])) {
            end = end.replaceAll(rms[i], "");
         }
      }

      return "[notranslate]" + com.tann.dice.Main.t(start) + ": " + com.tann.dice.Main.t(end);
   }

   @Override
   public Global getLinkedGlobal(DungeonContext context, int turn) {
      return this.requirement.validFor(context) ? this.linked : super.getLinkedGlobal(context, turn);
   }

   @Override
   public long getCollisionBits(Boolean player) {
      return super.getCollisionBits(player) | Collision.SPECIFIC_LEVEL;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return DipPanel.makeSidePanelGroup(big, this.requirement.makePanelActor(), this.linked, LEVEL_COL);
   }

   @Override
   public boolean skipTest() {
      return this.linked.skipTest();
   }

   @Override
   public GlobalLinked splice(Global newCenter) {
      return new GlobalLevelRequirement(this.requirement, newCenter);
   }
}
