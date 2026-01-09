package com.tann.dice.util.ui.choice;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.KeyListen;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class ChoiceDialog extends Group implements PostPop, KeyListen {
   private static final int MAX_WIDTH = 250;
   final CDChoice[] choices;
   private Runnable popRunnable;

   public ChoiceDialog(String s, Runnable onAccept, Runnable onDecline) {
      this(s, ChoiceDialog.ChoiceNames.YesNo, onAccept, onDecline);
   }

   public ChoiceDialog(List<Actor> actors, ChoiceDialog.ChoiceNames choiceNames, Runnable onAccept, Runnable onDecline) {
      this(null, actors, choiceNames, onAccept, onDecline);
   }

   public ChoiceDialog(String s, ChoiceDialog.ChoiceNames choiceNames, Runnable onAccept, Runnable onDecline) {
      this(s, new ArrayList<>(), choiceNames, onAccept, onDecline);
   }

   public ChoiceDialog(String description, List<Actor> actors, ChoiceDialog.ChoiceNames choiceNames, Runnable onAccept, Runnable onDecline) {
      this(description, actors, new CDChoice(choiceNames.decline, onDecline), new CDChoice(choiceNames.accept, onAccept));
   }

   public ChoiceDialog(String description, List<Actor> actors, CDChoice... choices) {
      this.choices = choices;
      Pixl p = new Pixl(5, 6);
      if (description != null) {
         p.text(description, 250).row();
      }

      for (Actor a : actors) {
         p.actor(a, 250.0F);
      }

      p.row();

      for (int i = 0; i < choices.length; i++) {
         CDChoice c = choices[i];
         p.actor(makeConfirm(c.name, c.onClick));
      }

      Actor a = p.pix();
      a = Tann.makeScrollpaneIfNecessary(a);
      Tann.become(this, a);
      this.setTransform(false);
   }

   public static boolean isTooBig(List<Actor> actors) {
      ChoiceDialog cd = new ChoiceDialog(actors, ChoiceDialog.ChoiceNames.YesCancel, null, null);
      return cd.tooBig();
   }

   public static boolean tooBig(Actor a, float threshold) {
      return a.getWidth() > com.tann.dice.Main.width * threshold || a.getHeight() > com.tann.dice.Main.height * threshold;
   }

   private boolean tooBig() {
      return tooBig(this, 0.9F);
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.green, 1);
      super.draw(batch, parentAlpha);
   }

   public static StandardButton makeConfirm(String text, Runnable runnable) {
      StandardButton tb = new StandardButton(text);
      if (runnable != null) {
         tb.setRunnable(runnable);
      }

      tb.setTouchable(runnable == null ? Touchable.disabled : Touchable.enabled);
      return tb;
   }

   public void setPopRunnable(Runnable popRunnable) {
      this.popRunnable = popRunnable;
   }

   @Override
   public void postPop() {
      if (this.popRunnable != null) {
         this.popRunnable.run();
      }
   }

   @Override
   public boolean keyPress(int keycode) {
      if (this.choices.length != 2) {
         return false;
      } else {
         int index = -1;
         switch (keycode) {
            case 66:
            case 160:
               index = 1;
               break;
            case 67:
               index = 0;
         }

         if (index >= 0 && index < this.choices.length && this.choices[index] != null) {
            this.choices[index].onClick.run();
            return true;
         } else {
            return false;
         }
      }
   }

   public static enum ChoiceNames {
      YesNo("[red]no", "[green]yes"),
      YesCancel("[grey]cancel", "[green]yes"),
      RedYes("[grey]cancel", "[red]yes"),
      PurpleYes("[grey]cancel", "[purple]yes"),
      AcceptDecline("[red]decline", "[green]accept"),
      HackyStartNow("[text]start of level", "[text]now");

      public final String decline;
      public final String accept;

      private ChoiceNames(String decline, String accept) {
         this.accept = accept;
         this.decline = decline;
      }
   }
}
