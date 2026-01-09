package com.tann.dice.gameplay.trigger.personal.weird;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.global.GlobalDescribeOnly;
import com.tann.dice.gameplay.trigger.personal.Personal;
import com.tann.dice.util.ui.TextWriter;

public class DescribeOnlyTrait extends Personal {
   final String text;
   String single;
   private boolean showBelowPanel;

   public DescribeOnlyTrait setShowBelowPanel(boolean showBelowPanel) {
      this.showBelowPanel = showBelowPanel;
      return this;
   }

   public DescribeOnlyTrait(String text) {
      this.text = text;
   }

   @Override
   protected boolean showInEntPanelInternal() {
      return this.showBelowPanel;
   }

   @Override
   public boolean skipNetAndIcon() {
      return this.showBelowPanel;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      return new GlobalDescribeOnly(this.rt2()).makePanelActorI(big);
   }

   private String rt2() {
      if (this.single != null) {
         return this.single;
      } else {
         this.single = this.richText();
         return this.single;
      }
   }

   private String richText() {
      if (this.text.isEmpty()) {
         return "?";
      } else if (this.text.charAt(0) == '[') {
         int end = this.text.indexOf("]");
         if (end == -1) {
            return "[";
         } else {
            for (int i = end; i < this.text.length(); i++) {
               TextWriter tw = new TextWriter(this.text.substring(0, i));
               if (tw.getWidth() != 0.0F) {
                  return tw.text;
               }
            }

            return "?";
         }
      } else {
         return this.text.charAt(0) + "";
      }
   }

   @Override
   public String describeForSelfBuff() {
      return this.text;
   }

   @Override
   public boolean skipCalc() {
      return true;
   }
}
