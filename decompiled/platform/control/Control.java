package com.tann.dice.platform.control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.OnscreenKeyboardType;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.OptionsMenu;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialItem;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialQuest;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class Control {
   public static final int NOTCH_TOP = 0;
   public static final int NOTCH_RIGHT = 1;
   public static final int NOTCH_BOTTOM = 2;
   public static final int NOTCH_LEFT = 3;

   public static String nothingToRestore() {
      return "[orange]nothing to restore[n][grey]if you recently purchased this on a different device, try waiting 24h";
   }

   public abstract Color getCol();

   public String getDoubleTapString() {
      return "Double-" + this.getSelectTapString().toLowerCase();
   }

   public abstract String getSelectTapString();

   public abstract String getInfoTapString();

   public abstract boolean allowLongPress();

   public abstract int getConfirmButtonThumbpadRadius();

   public List<Actor> getTipsSnippets(int contentWidth) {
      return new ArrayList<>();
   }

   public abstract Actor makePaymentRequestActor();

   public void onStart() {
   }

   public void onStop() {
   }

   public void tick() {
   }

   public abstract String getPlatformString();

   public boolean checkVersion() {
      return false;
   }

   public void textInput(final TextInputListener listener, String title, String existingText, String hint) {
      TextInputListener listenerWrapper = new TextInputListener() {
         public void input(String text) {
            listener.input(text);
            com.tann.dice.Main.requestRendering();
         }

         public void canceled() {
            listener.canceled();
            com.tann.dice.Main.requestRendering();
         }
      };
      Gdx.input.getTextInput(listenerWrapper, title, existingText, hint, this.getKeyboardType());
   }

   protected OnscreenKeyboardType getKeyboardType() {
      return OnscreenKeyboardType.Default;
   }

   public final void textInput(TextInputListener listener) {
      this.textInput(listener, "", "", "");
   }

   public List<Actor> getExtraSettingsDisplayActors() {
      return new ArrayList<>();
   }

   public String getStore() {
      return "";
   }

   public void setStore(String store) {
   }

   public boolean allowQuit() {
      return true;
   }

   public void afterLoad() {
   }

   public final Actor makeDefaultRequestActor() {
      String capitalism = "£$€¥£$€¥£";
      if (!FontWrapper.getFont().isTannFont()) {
         capitalism = "£$¥£$¥£$¥";
      }

      Actor textUrl = new TextWriter("[green]" + capitalism + "[n][light]buy full game[cu][n]" + capitalism);
      StandardButton sb = new StandardButton(textUrl, Colours.green, -1, -1);
      sb.setRunnable(new Runnable() {
         @Override
         public void run() {
            com.tann.dice.Main.getCurrentScreen().openUrl(Control.this.getFullVersionURL());
         }
      });
      return sb;
   }

   public String getFullVersionURL() {
      return "https://tann.fun/games/dice/";
   }

   public Actor makeDisplaySettings() {
      Pixl p = new Pixl(2, 0);

      for (Actor a : this.getExtraSettingsDisplayActors()) {
         p.actor(a).row();
      }

      p.actor(new Pixl(2).actor(OptionsMenu.makeScaleAdjust()).pix(16)).row();
      if (!com.tann.dice.Main.self().settings.displaySettingsSame()) {
         p.actor(this.makeSaveDisplaySettings());
      }

      Actor a = p.pix();
      return DipPanel.makeTopPanelGroup(new ImageActor(Images.esc_display), a, Colours.blue);
   }

   private Actor makeSaveDisplaySettings() {
      StandardButton store = new StandardButton("[green]Save Display").makeTiny();
      store.setRunnable(new Runnable() {
         @Override
         public void run() {
            boolean stored = com.tann.dice.Main.getSettings().storeDisplaySettings();
            String msg;
            if (stored) {
               msg = "Display settings saved for future launches";
               Sounds.playSound(Sounds.pip);
            } else {
               msg = "[red]error saving display settings";
               Sounds.playSound(Sounds.error);
            }

            com.tann.dice.Main.getCurrentScreen().showPopupDialog(msg, stored ? Colours.green : Colours.red);
         }
      });
      return store;
   }

   public abstract String getHighscorePlatformString();

   public void checkPurchase() {
   }

   public boolean allowsColourTextInput() {
      return false;
   }

   public boolean unloadWhilePaused() {
      return true;
   }

   public boolean belayRescale() {
      return false;
   }

   public boolean isPortrait() {
      return false;
   }

   public void changeOrientation(boolean portrait) {
   }

   public int[] getNotches() {
      return new int[]{0, 0, 0, 0};
   }

   public boolean disableMusic() {
      return false;
   }

   public boolean hasNotches() {
      int[] nots = this.getNotches();

      for (int not : nots) {
         if (not != 0) {
            return true;
         }
      }

      return false;
   }

   public static Actor makePaymentRequestIapActor(
      final PurchaseManager purchaseManager, boolean purchasesAvailable, final String purchaseIdentifier, Runnable restorePressed
   ) {
      if (!com.tann.dice.Main.demo) {
         TannLog.log("Game not in demo state but making payment request actor", TannLog.Severity.error);
         return new TextWriter("[pink]Already purchased???");
      } else if (!purchasesAvailable) {
         return new TextWriter("[red]Unable to access IAP[n][text]Try another internet connection?");
      } else {
         String price = purchaseManager.getInformation(purchaseIdentifier).getLocalPricing();
         if (price == null) {
            price = "Unknown Price";
         }

         TannLog.log("Price string: " + price);
         price = price.replaceAll("\\s", "");
         StandardButton purchaseButton = new StandardButton("[green]Unlock for " + price + "");
         purchaseButton.setRunnable(new Runnable() {
            @Override
            public void run() {
               purchaseManager.purchase(purchaseIdentifier);
            }
         });
         StandardButton restoreButton = new StandardButton("[blue]Restore purchase");
         restoreButton.setRunnable(restorePressed);
         return new Pixl(3).actor(purchaseButton).row().actor(restoreButton).pix();
      }
   }

   public List<TutorialItem> makeTutorialLevelEnd() {
      return new ArrayList<>();
   }

   public List<TutorialQuest> getExtraTargetingPhaseQuests() {
      return new ArrayList<>();
   }

   public void affectAdvertLines(List<String> lines) {
   }

   public Preferences makePrefs(String name) {
      return Gdx.app.getPreferences(name);
   }

   public String getMainFileString() {
      return "xsrvc.dll";
   }

   public boolean saveContentEncryption() {
      return true;
   }

   public boolean useBackups() {
      return false;
   }

   public String getMusicExtension() {
      return ".ogg";
   }

   public boolean stupidAboutLinks() {
      return false;
   }

   public boolean usesMouse() {
      return false;
   }

   public String getDeviceLanguage() {
      Locale l = Locale.getDefault();
      if (l == null) {
         return "en";
      } else {
         String lang = l.toString();
         return lang.length() > 2 ? lang.substring(0, 2) : lang;
      }
   }
}
