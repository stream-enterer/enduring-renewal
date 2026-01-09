package com.tann.dice.util.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class ClipboardUtils {
   public static final Color CLIPBOARD_COL = Colours.blue;
   public static final Color URN_COL = Colours.orange;

   public static void copyWithSoundAndToast(String copy) {
      Sounds.playSound(Sounds.slice);

      try {
         Gdx.app.getClipboard().setContents(copy);
      } catch (OutOfMemoryError var2) {
         com.tann.dice.Main.getCurrentScreen().showDialog("out of memory");
         return;
      }

      showClipboardToast(copy);
   }

   public static String pasteSafer() {
      String s = Gdx.app.getClipboard().getContents();
      return s == null ? null : PasteMode.genericPasteTagHandleCleanup(s);
   }

   public static void offerToCopy(String copy, String message) {
      offerToCopy(copy, message, new ArrayList<>());
   }

   public static void offerToCopy(final String copy, String message, List<Actor> extra) {
      ChoiceDialog choiceDialog = new ChoiceDialog(message, extra, ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().popSingleMedium();
            ClipboardUtils.copyWithSoundAndToast(copy);
            com.tann.dice.Main.getCurrentScreen().showDialog("Copied to clipboard");
         }
      }, new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().popSingleMedium();
         }
      });
      com.tann.dice.Main.getCurrentScreen().push(choiceDialog, true, true, false, 0.8F);
      Tann.center(choiceDialog);
   }

   public static void showClipboardToast(String copied) {
      final Actor showText = new TextWriter(TextWriter.rebracketTags(copied), true);
      boolean finalNeedsScroll = showText.getWidth() > com.tann.dice.Main.width * 0.8F;
      if (finalNeedsScroll) {
         showText = Tann.makeScrollpane(new Pixl().actor(showText).row(2).pix());
      }

      Actor a = new TextWriter(
         "[notranslate]" + TextWriter.getTag(CLIPBOARD_COL) + "[b]" + com.tann.dice.Main.t("clipboard") + ": " + Tann.makeEllipses(copied, 10)
      );
      a = new Pixl(3, 5).border(CLIPBOARD_COL).actor(a).pix();
      a.addListener(
         new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Actor ax = new Pixl(3, 3)
                  .border(ClipboardUtils.CLIPBOARD_COL)
                  .text("[grey]This text was copied to clipboard:")
                  .row()
                  .actor(showText)
                  .row()
                  .actor(OptionLib.SHOW_COPY.makeCogActor())
                  .pix();
               com.tann.dice.Main.getCurrentScreen().push(ax, true, true, false, 0.8F);
               Tann.center(ax);
               return true;
            }
         }
      );
      com.tann.dice.Main.getCurrentScreen().addPopup(a);
   }

   public static Actor makeSimpleCopyButton(final String toCopy) {
      StandardButton butt = new StandardButton(TextWriter.getTag(CLIPBOARD_COL) + "copy");
      butt.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               com.tann.dice.Main.getCurrentScreen().popSingleMedium();
               ClipboardUtils.copyWithSoundAndToast(toCopy);
               com.tann.dice.Main.getCurrentScreen()
                  .showDialog(TextWriter.getTag(ClipboardUtils.CLIPBOARD_COL) + "Copied [text]" + toCopy + "[cu] to clipboard", ClipboardUtils.CLIPBOARD_COL);
            }
         }
      );
      return butt;
   }
}
