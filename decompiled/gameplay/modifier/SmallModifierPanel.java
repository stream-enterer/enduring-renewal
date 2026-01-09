package com.tann.dice.gameplay.modifier;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextMarquee;
import com.tann.dice.util.ui.TextWriter;

public class SmallModifierPanel extends Group {
   static final int MAX_WIDTH = 110;
   final Modifier modifier;

   public SmallModifierPanel(Modifier modifier) {
      this.modifier = modifier;
      Group tw = makeSmallTitleMarquee(modifier);
      this.setSize(tw.getWidth(), tw.getHeight());
      this.addActor(tw);
      this.setTransform(false);
   }

   public static Group makeSmallTitleMarquee(Modifier modifier) {
      String start = "[text]" + TextWriter.rebracketTags(modifier.getName(true));
      String tierString = "(" + modifier.getTierString() + ")";
      String fullString;
      if (modifier.getMType() == ModifierType.Unrated) {
         fullString = start;
      } else {
         fullString = start + " " + tierString;
      }

      Group tw = new TextWriter("[notranslate]" + fullString, 5000, modifier.getBorderColour(), 2);
      if (tw.getWidth() > 110.0F) {
         Actor a = TextMarquee.marqueeOrDots(TextWriter.stripTags(modifier.getName(true)), Colours.text, 110);
         tw = new Pixl(1, 2).border(modifier.getColour()).actor(a).text("[text]" + tierString).pix();
      }

      return tw;
   }

   public SmallModifierPanel addBasicListener() {
      this.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            ModifierPanel panel = new ModifierPanel(SmallModifierPanel.this.modifier, true);
            Sounds.playSound(Sounds.pip);
            com.tann.dice.Main.getCurrentScreen().push(panel);
            Tann.center(panel);
            return true;
         }
      });
      return this;
   }
}
