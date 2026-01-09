package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNRichText;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;

public class MessagePhase extends Phase {
   final String msg;
   final String conf;
   Actor messageActor;

   public MessagePhase(String msg) {
      String[] parts = msg.split(";");
      this.msg = parts[0].replace(",", "");
      if (parts.length > 1) {
         this.conf = parts[1];
      } else {
         this.conf = "ok";
      }
   }

   public MessagePhase(Exception e, String ctx) {
      this(getMsg(e, ctx));
   }

   public static String getMsg(Exception e, String ctx) {
      return e == null ? "null exception" : e.getClass().getSimpleName() + ":" + e.getMessage() + ": " + ctx;
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.grey;
   }

   @Override
   public String serialise() {
      return '4' + this.msg + (this.conf == null ? "" : ";" + this.conf);
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.paper);
      Pixl p = new Pixl(4, 4);
      StandardButton sb = new StandardButton(this.conf);
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            MessagePhase.this.clickOk();
         }
      });
      p.border(Colours.grey).actor(this.makeSmallestMessageBox("[text]" + this.msg)).row().actor(sb);
      this.messageActor = p.pix();
      DungeonScreen.get().addActor(this.messageActor);
      Tann.center(this.messageActor);
      Tann.slideIn(this.messageActor, Tann.TannPosition.Bot, (int)(com.tann.dice.Main.height / 2 - this.messageActor.getHeight() / 2.0F));
   }

   private void clickOk() {
      PhaseManager.get().popPhase(this.getClass());
   }

   private Actor makeSmallestMessageBox(String s) {
      int MIN_WITH = 90;
      int MAX_WIDTH = (int)(com.tann.dice.Main.width * 0.8F);
      int INCREMENT = 5;
      Actor best = new TextWriter("??");
      int minArea = 50000;

      for (int width = 90; width <= MAX_WIDTH; width += 5) {
         Actor a = new TextWriter(s, width);
         int area = (int)(a.getWidth() + a.getHeight() * 0.8F);
         if (area < minArea) {
            minArea = area;
            best = a;
         }
      }

      return best;
   }

   @Override
   public void deactivate() {
      Sounds.playSound(Sounds.pop);
      Tann.slideAway(this.messageActor, Tann.TannPosition.Bot, true);
      DungeonScreen.get().save();
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton(Images.phaseMessageIcon, Colours.grey, 53, 20);
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 62:
         case 66:
         case 67:
         case 160:
            this.clickOk();
            return true;
         default:
            return false;
      }
   }

   @Override
   public boolean isInvalid() {
      return PRNRichText.invalid(this.msg);
   }
}
