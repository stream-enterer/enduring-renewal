package com.tann.dice.gameplay.trigger.global.linked;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.TextWriter;

public class DipPanel {
   public static Actor makeSidePanelGroup(boolean big, Actor exception, Choosable contained, Color border) {
      return makeSidePanelGroup(exception, contained.makeChoosableActor(big, 0), border);
   }

   public static Actor makeSidePanelGroup(boolean big, Actor exception, Trigger contained, Color border) {
      return makeSidePanelGroup(exception, contained.makePanelActor(big), border);
   }

   public static Actor makeSidePanelGroup(Actor exception, String contained, Color border) {
      return makeSidePanelGroup(exception, new TextWriter(contained), border);
   }

   public static Actor makeSidePanelGroup(Actor exception, Actor contained, Color border) {
      Actor var6 = new Pixl(0, 2).border(Colours.dark, border, 1).actor(exception).pix();
      if (contained == null) {
         return null;
      } else {
         int ep = 3;
         int gap = (int)((var6.getWidth() + ep * 2) / 2.0F);
         Group g = new Pixl(2, ep).border(border).gap(-gap).actor(var6).actor(contained).pix();
         g = new Pixl().gap(gap - ep).actor(g).pix();
         if (!"notalone".equalsIgnoreCase(contained.getName())) {
            g.setName(contained.getName());
         }

         return g;
      }
   }

   public static Actor makeTopPanelGroup(Actor exception, Actor contained, Color border) {
      return makeTopPanelGroup(exception, contained, border, 4);
   }

   public static Group makeTopPanelGroup(Actor exception, Actor contained, Color border, int padding) {
      Actor var7 = new Pixl(0, 2).border(Colours.dark, border, 1).actor(exception).pix();
      if (contained == null) {
         return TannStageUtils.errorActor("tpg");
      } else {
         int gap = Math.round(var7.getHeight() / 2.0F);
         int igg = padding - 1;
         Group g = new Pixl(0, igg).border(border).row(gap - 1).actor(contained).pix();
         g = new Pixl().actor(var7).row(-gap).actor(g).pix();
         var7.toFront();
         if (!"notalone".equalsIgnoreCase(contained.getName())) {
            g.setName(contained.getName());
         }

         return g;
      }
   }
}
