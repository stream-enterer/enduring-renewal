package com.tann.dice.screens.titleScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.LanguageThing;
import com.tann.dice.screens.dungeon.panels.threeD.DieSpinner;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.DebugUtilsUseful;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TitleScreen extends Screen {
   public static final String VERSION_URL = "https://tann.fun/version/slice_and_dice";
   ImageActor logo;
   public static int LOGO_SPACE;
   Actor background;
   Actor oldBackground;
   private static final float BACKGROUND_TRANSITION_TIME = 0.5F;
   public static String version = null;
   Group leftButtonsGroup;
   private static final int LOGO_SLIDE_DIST = 5;
   boolean logoIn;
   public Mode currentMode;
   Actor modeActor;
   ModesPanel modesPanel;
   int dbc;

   public TitleScreen() {
      this(com.tann.dice.Main.getSettings().getLastMode());
   }

   public TitleScreen(Mode prefMode) {
      ContextConfig.resetCache();
      if (com.tann.dice.Main.demo || prefMode == null || UnUtil.isLocked(prefMode) || !UnUtil.getUnlockNotified().contains(Mode.CLASSIC)) {
         prefMode = (Mode)(com.tann.dice.Main.demo ? Mode.DEMO : Mode.CLASSIC);
      }

      this.layout(prefMode);
   }

   public static void showMode(Mode m) {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s instanceof TitleScreen) {
         ((TitleScreen)s).selectMode(m);
      }
   }

   @Override
   public void afterSet() {
      com.tann.dice.Main.getSettings().logVersion();
   }

   private static TextureRegion loadBackground(Mode mode) {
      return mode.getBackground();
   }

   public void layout(Mode prefMode) {
      if (prefMode == null) {
         prefMode = com.tann.dice.Main.getSettings().getLastMode();
      }

      this.currentMode = null;
      this.logoIn = false;
      this.clearChildren();
      this.resetPopupHolder();
      this.logo = new ImageActor(ImageUtils.loadExtBig("ui/logo"));
      this.logo.setTouchable(Touchable.disabled);
      LOGO_SPACE = (int)(this.logo.getHeight() + 10.0F + 2 * com.tann.dice.Main.self().notch(0));
      this.addActor(this.logo);
      Tann.center(this.logo);
      this.slideLogo(true);
      this.setupLeftButtons(null);
      this.modesPanel = new ModesPanel();
      this.modesPanel.setOnChangeMode(new Runnable() {
         @Override
         public void run() {
            TitleScreen.this.selectMode(TitleScreen.this.modesPanel.getSelectedMode());
         }
      });
      this.addActor(this.modesPanel);
      this.modesPanel.setX(com.tann.dice.Main.width - com.tann.dice.Main.self().notch(1));
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            TitleScreen.this.modesPanel.slide(false, true);
            return true;
         }
      });
      UnUtil.checkModeUnlocks(this);
      this.checkHerocolUnlocks(this);
      this.modesPanel.selectFirstUnlocked(prefMode);
      if (com.tann.dice.Main.self().control.checkVersion()) {
         if (version == null) {
            this.checkVersion();
         } else if (!version.equals("error")) {
            this.displayVersion(version);
         }
      }
   }

   private void addBackground(TextureRegion tr, Mode mode) {
      if (this.background != null && this.background instanceof ScaleRegionActor) {
         this.oldBackground = this.background;
         this.oldBackground.addAction(Actions.sequence(Actions.alpha(0.0F, 0.5F, Interpolation.pow2Out), Actions.removeActor()));
      }

      this.background = new ScaleRegionActor(tr);
      this.background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
   }

   @Override
   public void act(float delta) {
      super.act(delta);
      if (this.background != null) {
         this.background.act(delta);
      }

      if (this.oldBackground != null) {
         this.oldBackground.act(delta);
      }
   }

   private void displayVersion(String version) {
      TitleScreen.version = version;
      this.setupLeftButtons(version);
   }

   private void checkVersion() {
      Tann.thread(
         new Runnable() {
            @Override
            public void run() {
               HttpRequest request = new HttpRequestBuilder()
                  .newRequest()
                  .method("GET")
                  .timeout(8000)
                  .url("https://tann.fun/version/slice_and_dice/" + com.tann.dice.Main.self().control.getPlatformString())
                  .build();
               Gdx.net.sendHttpRequest(request, new HttpResponseListener() {
                  public void handleHttpResponse(HttpResponse httpResponse) {
                     final String result = httpResponse.getResultAsString().trim();
                     TannLog.log("Version check: " + result);
                     Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                           TitleScreen.this.displayVersion(result);
                        }
                     });
                  }

                  public void failed(Throwable t) {
                     TitleScreen.version = "error";
                     TannLog.log("Failed to get current version: " + t.getMessage(), TannLog.Severity.error);
                  }

                  public void cancelled() {
                     TitleScreen.version = "error";
                     TannLog.log("Version fetch cancelled");
                  }
               });
            }
         }
      );
   }

   private void setupLeftButtons(String result) {
      if (this.leftButtonsGroup != null) {
         this.leftButtonsGroup.remove();
      }

      List<Actor> butts = new ArrayList<>(Arrays.asList(DungeonUtils.makeCog(), Book.makeAlmanacButton(), LanguageThing.makeGlobeButton()));
      if (com.tann.dice.Main.getSettings().isBypass()) {
         butts.add(OptionUtils.makeLockButton());
      }

      if (OptionLib.SEARCH_BUTT.c()) {
         butts.add(DungeonUtils.makeSearchButton());
      }

      if (result != null) {
         boolean valid = result.startsWith("1") || result.startsWith("2") || result.startsWith("3");
         boolean newer = valid && com.tann.dice.Main.versionALower("3.2.13", result);
         if (newer) {
            Actor version = DungeonUtils.makeBasicButton(Images.versionNew);
            final String finalMsg = "Version: 3.2.13[n]Latest: " + result + "[n][green]New version available!";
            version.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  TitleScreen.this.showDialog(finalMsg, Colours.green);
                  return true;
               }
            });
            butts.add(version);
         }
      }

      if (this.currentMode != null && !com.tann.dice.Main.demo) {
         final Mode parent = this.currentMode.getParent();
         if (parent != null) {
            Actor backButton = DungeonUtils.makeBasicButton(Images.back);
            backButton.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  Sounds.playSound(Sounds.pop);
                  TitleScreen.this.selectMode(parent);
                  return true;
               }
            });
            butts.add(backButton);
         }
      }

      this.leftButtonsGroup = DungeonUtils.makeButtonsGroup(true, butts);
      this.leftButtonsGroup.setTouchable(Touchable.childrenOnly);
      this.addActor(this.leftButtonsGroup);
      DungeonUtils.placeButtonsGroup(this.leftButtonsGroup, true);
      this.leftButtonsGroup.toBack();
   }

   public void slideLogo(boolean in) {
      if (in != this.logoIn) {
         this.logoIn = in;
         this.logo.setX((int)(this.getWidth() / 2.0F - this.logo.getWidth() / 2.0F));
         if (com.tann.dice.Main.isPortrait()) {
            this.logo.setX(this.logo.getX() + 5.0F);
         }

         if (in) {
            this.logo.setY(com.tann.dice.Main.height - this.logo.getHeight() - 5.0F - com.tann.dice.Main.self().notch(0));
         } else {
            this.logo.setY(com.tann.dice.Main.height);
         }
      }
   }

   private void selectMode(Mode mode) {
      ContextConfig.resetCache();
      this.sdc(mode);
      if (mode != this.currentMode) {
         if (mode == Mode.DEBUG) {
            mode = Mode.CLASSIC;
         }

         this.modesPanel.selectedMode = mode;
         com.tann.dice.Main.getSettings().setLastMode(mode);
         com.tann.dice.Main.getSettings().save();
         this.currentMode = mode;
         if (this.modeActor != null) {
            this.modeActor.remove();
         }

         this.modeActor = mode.makeStartGameDisplay();
         boolean showLogo = com.tann.dice.Main.height - this.modeActor.getHeight() > 70.0F;
         this.slideLogo(showLogo);
         this.addActor(this.modeActor);
         this.modeActor.toBack();
         int middleX = com.tann.dice.Main.width / 2;
         this.modeActor.setX((int)(middleX - this.modeActor.getWidth() / 2.0F));
         int centerY = com.tann.dice.Main.height / 2;
         if (showLogo) {
            centerY = (com.tann.dice.Main.height - LOGO_SPACE) / 2;
         }

         if (mode.basicTitleBackground()) {
            centerY = (int)(this.modeActor.getHeight() * 3.0F / 4.0F);
         }

         this.modeActor.setY((int)(centerY - this.modeActor.getHeight() / 2.0F));
         this.addBackground(loadBackground(mode), mode);
         this.setupLeftButtons(null);
         TannStageUtils.ensureCR1(this, 0.6F);
      }
   }

   private void sdc(Mode mode) {
      if (mode == Mode.ROOT) {
         this.dbc++;
         if (this.dbc >= 60) {
            if (this.dbc % 5 == 0) {
               Sounds.playSound(Sounds.error);
            }

            return;
         }

         for (int i = 0; i < this.dbc / 10; i++) {
            Actor a = new DieSpinner(HeroTypeUtils.random().makeEnt().getDie(), 30.0F);
            a.setTouchable(Touchable.disabled);
            this.addActor(a);
            a.addAction(Actions.delay(Tann.random(1.0F, 7.0F), Actions.removeActor()));
            Tann.randomPos(a);
         }
      }
   }

   @Override
   public void preDraw(Batch batch) {
      batch.setColor(Colours.dark);
      Draw.fillActor(batch, this);
      if (this.currentMode.basicTitleBackground()) {
         this.background.draw(batch, 1.0F);
      } else {
         com.tann.dice.Main.self().stop2d(true);
         SpriteBatch bg = com.tann.dice.Main.self().startBackground();
         if (this.background != null) {
            this.background.draw(bg, 1.0F);
         }

         if (this.oldBackground != null) {
            this.oldBackground.draw(bg, 1.0F);
         }

         com.tann.dice.Main.self().stopBackground();
         com.tann.dice.Main.self().start2d(true);
      }
   }

   @Override
   public void keyPress(int keycode) {
      switch (keycode) {
         case 31:
            String f = "C:/code/games/Dicegeon/images/3d/extra/DogKisser/a";

            for (FileHandle fileHandle : Gdx.files.absolute(f).list()) {
               String s = f + "/z" + fileHandle.name();
               fileHandle.copyTo(Gdx.files.absolute(s));
               fileHandle.delete();
            }
            break;
         case 32:
            DebugUtilsUseful.debug();
      }
   }

   @Override
   public Screen copy() {
      return new TitleScreen(this.modesPanel.getSelectedMode());
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

   public ModesPanel getModesPanel() {
      return this.modesPanel;
   }

   @Override
   public String getReportString() {
      return "from title screen";
   }

   private void checkHerocolUnlocks(TitleScreen screen) {
      List<HeroCol> unl = new ArrayList<>();

      for (HeroCol c : HeroCol.basics()) {
         if (c != HeroCol.orange
            && c != HeroCol.yellow
            && c != HeroCol.grey
            && !UnUtil.isLocked(c)
            && !com.tann.dice.Main.getSettings().isUnlockNotified(c.colName)) {
            unl.add(c);
         }
      }

      UnUtil.colUnlocked(screen, unl);
   }
}
