package com.tann.dice.util.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ConcisePanel;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;

public abstract class LiveText extends Group {
   String text;

   public LiveText(int width) {
      this.setSize(width, TannFont.font.getLineHeight());
      this.setTransform(false);
   }

   public abstract String fetchText();

   public void act(float delta) {
      String newText = this.fetchText();
      if (!newText.equals(this.text)) {
         this.setToNewText(newText);
      }

      super.act(delta);
   }

   private void setToNewText(String newText) {
      this.text = newText;
      this.clearChildren();
      Color fc = Colours.text;
      if (newText.startsWith("[")) {
         int end = newText.indexOf("]");
         if (end > 0) {
            String cs = newText.substring(1, end);
            Color c = TextWriter.getMapCol(cs);
            if (c != null) {
               newText = newText.substring(end + 1);
               fc = c;
            }
         }
      }

      Actor a = ConcisePanel.makeTitle(newText, fc, (int)this.getWidth(), false);
      this.addActor(a);
      Tann.center(a);
      a.setY(0.0F);
   }
}
