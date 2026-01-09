package com.tann.dice.screens.rollScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.panels.threeD.DieRenderer;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RollScreen extends Screen {
   List<Die> dice;
   int numDice = 1;
   private static RollScreen self;
   List<String> ratioStrings = new ArrayList<>();
   Map<Integer, Integer> resultMap;
   int total = 0;
   Actor ratioPanel;

   private RollScreen() {
      this.resetMap();
      this.layout();
   }

   private void resetMap() {
      this.resultMap = new HashMap<>();

      for (int i = 0; i < 6; i++) {
         this.resultMap.put(i, 0);
      }
   }

   public void resetWithNewNum(int newNumDice) {
      BulletStuff.reset();
      this.numDice = newNumDice;
      this.resetMap();
      this.rollDice();
   }

   public static RollScreen get() {
      if (self == null) {
         self = new RollScreen();
      }

      return self;
   }

   private void rollDice() {
      BulletStuff.reset();
      BulletStuff.setupWalls();
      this.dice = new ArrayList<>();
      int i = 0;

      while (i < this.numDice) {
         i++;
      }

      for (Die die : this.dice) {
         die.addToScreen();
      }

      BulletStuff.refreshDice(this.dice);

      for (final Die die : this.dice) {
         die.returnToPlay(new Runnable() {
            @Override
            public void run() {
               die.roll(true);
            }
         }, false, 0.1F);
      }
   }

   private void layout() {
      Actor a = new Pixl(0, 3).border(Colours.blue).text("testing").pix();
      this.addActor(a);
      Tann.center(a);
      DieRenderer dr = new DieRenderer();
      this.addActor(dr);
   }

   @Override
   public void preDraw(Batch batch) {
   }

   @Override
   public void postDraw(Batch batch) {
   }

   @Override
   public void preTick(float delta) {
   }

   @Override
   public void postTick(float delta) {
   }

   @Override
   protected void keyPress(int keycode) {
      int digit = Tann.getDigit(keycode) + 1;
      if (digit > 0 && digit < 10) {
         this.resetWithNewNum(digit);
      }

      switch (keycode) {
         case 46:
            com.tann.dice.Main.self().setScreen(new TitleScreen());
            break;
         case 62:
            this.rollDice();
      }
   }

   @Override
   public Screen copy() {
      return get();
   }

   public void logResult(int result) {
      this.resultMap.put(result, this.resultMap.get(result) + 1);
      this.total++;
      if (this.total % this.numDice == 0) {
         this.rollDice();
      }

      if (this.resultMap.get(0) >= 20 * this.numDice) {
         float zeros = this.resultMap.get(0).intValue();
         float ones = this.resultMap.get(1).intValue();
         float others = (this.resultMap.get(2) + this.resultMap.get(3) + this.resultMap.get(4) + this.resultMap.get(5)) / 4.0F;
         float ratioA = ones / zeros;
         float ratioB = others / zeros;
         String resultString = "R!-D" + this.numDice + "-" + Tann.floatFormat(ratioA) + "-" + Tann.floatFormat(ratioB);
         this.ratioStrings.add(resultString);
         System.out.println(resultString);
         this.addResultPanel();
         int newNumDice = this.numDice + 1;
         if (this.numDice == 5) {
            newNumDice = 1;
            this.resetMap();
         }

         this.resetWithNewNum(newNumDice);
      }
   }

   private void addResultPanel() {
      if (this.ratioPanel != null) {
         this.ratioPanel.remove();
      }

      Pixl p = new Pixl(2).border(Colours.green);

      for (String resultString : this.ratioStrings) {
         p.text(resultString).row();
      }

      this.ratioPanel = p.pix(8);
      this.ratioPanel = Tann.makeScrollpane(this.ratioPanel);
      this.addActor(this.ratioPanel);
      this.ratioPanel.setPosition(this.getWidth() - this.ratioPanel.getWidth(), this.getHeight() - this.ratioPanel.getHeight());
   }
}
