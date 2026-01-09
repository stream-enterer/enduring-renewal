package com.tann.dice.platform.control.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.save.settings.option.ChOption;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.platform.control.Control;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpPage;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextInput;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DesktopControl extends Control {
   private int[] notches = new int[]{0, 0, 0, 0};
   public static final ChOption SCREEN_MODE = new ChOption(
      "screen mode",
      "w: [blue]windowed[cu][n]fs: [blue]fullscreen[cu][n]fs2: [blue]fullscreen 2[cu][n][n][n][n][n]'Save Display' to store for next launch[n][n][n][n][n][grey]3rd fullscreen option: maximise the window",
      "[notranslate]w",
      "[notranslate]fs",
      "[notranslate]fs2"
   ) {
      @Override
      protected void manualSelectAction() {
         DesktopControl.SCREEN_ACTION();
         if (DesktopControl.SCREEN_MODE.c() == 3) {
            com.tann.dice.Main.getCurrentScreen().showDialog("The 3rd fullscreen option is[n]'[yellow]you maximise the window[cu]'[n]if OS allows");
         }
      }
   };

   @Override
   public String getSelectTapString() {
      return "Click";
   }

   @Override
   public String getInfoTapString() {
      return "Right-click";
   }

   @Override
   public boolean allowLongPress() {
      return OptionLib.SMARTPHONE_CONTROLS.c();
   }

   @Override
   public int getConfirmButtonThumbpadRadius() {
      return -1;
   }

   @Override
   public List<Actor> getTipsSnippets(int contentWidth) {
      int tw = contentWidth - 10;
      return Arrays.asList(
         new TextWriter("[b][grey]desktop hotkeys"),
         this.makeHotkey("esc", "menu"),
         this.makeHotkey("space/enter", "end turn"),
         HelpPage.makeSection(
            "targeting",
            Colours.red,
            new Pixl(HelpPage.HELP_CONTENT_GAP)
               .rowedActors(
                  Arrays.asList(
                     this.makeHotkey("1-5", "select heroes"),
                     this.makeHotkey("QWERTY", "select ability"),
                     this.makeHotkey("1-9 [grey](+shift)[cu]", "target"),
                     this.makeHotkey("tab", "show targeting"),
                     this.makeHotkey("z", "undo")
                  )
               )
               .pix(8)
         ),
         HelpPage.makeSection(
            "rolling",
            Colours.yellow,
            new Pixl(HelpPage.HELP_CONTENT_GAP)
               .rowedActors(Arrays.asList(this.makeHotkey("1-5", "lock/unlock dice"), this.makeHotkey("r", "roll dice")))
               .pix(8)
         ),
         HelpPage.makeSection(
            "phases", Colours.orange, new Pixl(HelpPage.HELP_CONTENT_GAP).rowedActors(Arrays.asList(new TextWriter("Usually 1-5, enter, bksp, R, I"))).pix(8)
         ),
         HelpPage.makeSection(
            "text input",
            Colours.light,
            new Pixl(HelpPage.HELP_CONTENT_GAP)
               .rowedActors(Arrays.asList(new TextWriter("Some expected functions are usable, like 'ctrl+v', home, end, del, bksp", tw)))
               .pix(8)
         )
      );
   }

   private Actor makeHotkey(String key, String eff) {
      return new TextWriter("[notranslate][light]" + com.tann.dice.Main.t(key) + ":[cu] " + com.tann.dice.Main.t(eff));
   }

   @Override
   public Actor makePaymentRequestActor() {
      return this.makeDefaultRequestActor();
   }

   @Override
   public void onStart() {
      System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
   }

   @Override
   public String getPlatformString() {
      return "desktop";
   }

   @Override
   public void textInput(final TextInputListener listener, String title, String existingText, String hint) {
      final TextInput ti = new TextInput(listener);
      ti.setText(existingText);
      com.tann.dice.Main.stage.setKeyboardFocus(ti);
      Pixl p = new Pixl(3, 2).border(Colours.grey);
      StandardButton clr = new StandardButton("[text]clear");
      clr.setRunnable(new Runnable() {
         @Override
         public void run() {
            ti.clearTextInput();
         }
      });
      StandardButton ok = new StandardButton("[green]ok");
      ok.setRunnable(new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().popSingleMedium();
            listener.input(ti.getText());
         }
      });
      Group tmp = p.text("[text]" + title + (hint != null && hint.length() > 0 ? " [text](" + hint + ")[cu]" : "")).row().actor(ti).pix();
      Actor a = new Pixl().actor(tmp).row(-1).actor(clr).actor(ok).pix();
      com.tann.dice.Main.getCurrentScreen().push(a, true, true, false, 0.7F);
      Tann.center(a);
      a.setTouchable(Touchable.childrenOnly);
   }

   @Override
   public List<Actor> getExtraSettingsDisplayActors() {
      List<Actor> result = new ArrayList<>();
      result.add(SCREEN_MODE.makeCogActor());
      return result;
   }

   private static void SCREEN_ACTION() {
      switch (SCREEN_MODE.c()) {
         case 0:
            com.tann.dice.Main.self().setFullScreen(false);
            break;
         case 1:
            com.tann.dice.Main.self().setFullScreen(true);
            break;
         case 2:
            DisplayMode dm = Gdx.graphics.getDisplayMode();
            int w = dm.width;
            int h = dm.height;
            Gdx.graphics.setWindowedMode(w + 1, h + 1);
            break;
         case 3:
            com.tann.dice.Main.self().setFullScreen(false);
      }
   }

   @Override
   public String getStore() {
      return SCREEN_MODE.cString();
   }

   @Override
   public void setStore(String store) {
      SCREEN_MODE.setValue(store, false);
   }

   @Override
   public boolean allowQuit() {
      return true;
   }

   @Override
   public void afterLoad() {
      boolean windowed = SCREEN_MODE.c() == 0;
      if (windowed) {
         com.tann.dice.Main.getSettings().resizeWindow();
      } else {
         SCREEN_ACTION();
      }
   }

   @Override
   public String getHighscorePlatformString() {
      return "desktop";
   }

   @Override
   public boolean allowsColourTextInput() {
      return true;
   }

   @Override
   public boolean belayRescale() {
      return true;
   }

   @Override
   public int[] getNotches() {
      return this.notches;
   }

   @Override
   public boolean useBackups() {
      return true;
   }

   @Override
   public boolean usesMouse() {
      return true;
   }
}
