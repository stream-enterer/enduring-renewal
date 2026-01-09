package com.tann.dice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectSet.ObjectSetIterator;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tann.dice.gameplay.battleTest.BattleTestUtils;
import com.tann.dice.gameplay.content.ent.die.Die;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.EntSidesLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.leaderboard.LeaderboardBlob;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.progress.MasterStats;
import com.tann.dice.gameplay.progress.UnlockManager;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.settings.Settings;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.platform.audio.SoundHandler;
import com.tann.dice.platform.control.Control;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.generalPanels.InventoryPanel;
import com.tann.dice.screens.pauseScreen.PauseScreen;
import com.tann.dice.screens.shaderFx.FXContainer;
import com.tann.dice.screens.splashScreen.SplashDraw;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.ImageUtils;
import com.tann.dice.statics.bullet.BulletStuff;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.statics.sound.music.MusicManager;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.FontWrapper;
import com.tann.dice.util.Noise;
import com.tann.dice.util.Pair;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStage;
import com.tann.dice.util.TimerUtil;
import com.tann.dice.util.VersionUtils;
import com.tann.dice.util.image.ImageFilter;
import com.tann.dice.util.image.Img64;
import com.tann.dice.util.saves.Prefs;
import com.tann.dice.util.translation.Translator;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends ApplicationAdapter {
   public static final boolean preppingForRelease = true;
   public static boolean demo = false;
   public static final String url = "https://tann.fun";
   public static final boolean trackBadFrames = false;
   public static int scale;
   public static int width;
   public static int height;
   private boolean isPaused;
   private int rotation;
   SpriteBatch bufferDrawer;
   public static TannStage stage;
   public OrthographicCamera orthoCam;
   public static TextureAtlas atlas;
   public static TextureAtlas atlas_big;
   public static TextureAtlas atlas_3d;
   private static boolean printCalls = false;
   Screen currentScreen;
   public static float secs;
   public static int frames;
   private static long lastResize;
   private final SoundHandler soundHandler;
   public final Control control;
   FrameBuffer fb;
   boolean settedUp;
   boolean invalidScale;
   private final Random r = Tann.makeStdRandom();
   private Thread forceRenderThread;
   public MasterStats masterStats;
   public Settings settings;
   public Translator translator;
   static float noiseFromTicks = 0.0F;
   static Vector2 cursor = new Vector2();
   SplashDraw splashScreen;
   GLProfiler glp;
   private int SCREEN_WIDTH;
   private int SCREEN_HEIGHT;
   private int prevScaleAdjust = -500;
   private int prevDiceAdjust = -500;
   private static final long SCALE_COOLDOWN = 300L;
   private long lastSetupScale = 0L;
   int badFrames;
   public static int bonusRenderCalls = 0;
   boolean fullscreening;
   String pausedKey;
   private boolean timerAlreadyCleared = false;
   int oldWidth;
   int oldHeight;
   public SpriteBatch backgroundBatch;
   public static boolean chadwick = false;
   public static boolean renderSwapChadwick = false;
   private static long previousTime;
   private static List<Pair<String, Long[]>> times = new ArrayList<>();
   private static int chadSamples = 20;
   private static Json json;
   private static Json jsonMini;

   public Main(SoundHandler soundHandler, Control control, boolean demo, boolean debug) {
      this.soundHandler = soundHandler;
      this.control = control;
      com.tann.dice.Main.demo = demo;
   }

   public static String resolutionString() {
      return width + ":" + height + ":" + scale + ":" + Gdx.graphics.getWidth() + ":" + Gdx.graphics.getHeight();
   }

   public static boolean justResized() {
      return System.currentTimeMillis() - lastResize < 100L;
   }

   public static void requestRendering() {
      Gdx.graphics.requestRendering();
   }

   public int notch(int direction) {
      return (int)((float)this.control.getNotches()[direction] / scale);
   }

   public Main(SoundHandler soundHandler, Control control, boolean demo) {
      this(soundHandler, control, demo, false);
   }

   public static Screen getCurrentScreen() {
      return self().currentScreen;
   }

   public static com.tann.dice.Main self() {
      return (com.tann.dice.Main)Gdx.app.getApplicationListener();
   }

   public static UnlockManager unlockManager() {
      return self().masterStats.getUnlockManager();
   }

   public static float getNoiseFromTicks() {
      return noiseFromTicks;
   }

   public static float pulsateFactor() {
      return pulsateFactor(1.0F);
   }

   public static float pulsateFactor(float factor) {
      return (float)(Math.sin(secs * 5.0F * factor) + 1.0) / 2.0F;
   }

   public static float getDeltaMultiple() {
      return 1.0F;
   }

   public static Vector2 getCursor() {
      cursor.set(Gdx.input.getX() / scale, height - Gdx.input.getY() / scale);
      return cursor;
   }

   public static boolean isPortrait() {
      return height > width * 0.9F;
   }

   public void create() {
      frames = 0;
      if (printCalls) {
         System.out.println("create");
      }

      this.splashScreen = new SplashDraw();
   }

   public void crManuallyAltered() {
      Gdx.graphics.setContinuousRendering(OptionUtils.isContinuous());
      if (OptionUtils.shouldForceRenderThread()) {
         this.startForceRenderThread();
      } else {
         this.interruptRenderThread();
      }

      requestRendering();
   }

   private void interruptRenderThread() {
      if (this.forceRenderThread != null) {
         this.forceRenderThread.interrupt();
      }
   }

   private void startForceRenderThread() {
      if (OptionUtils.shouldForceRenderThread()) {
         this.interruptRenderThread();
         this.forceRenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
               do {
                  Screen s = com.tann.dice.Main.getCurrentScreen();
                  if (s != null && s.needsExtraRender()) {
                     com.tann.dice.Main.requestRendering();
                  }

                  try {
                     Thread.sleep(1000L);
                  } catch (InterruptedException var3) {
                     return;
                  }
               } while (!Thread.currentThread().isInterrupted());
            }
         });
         this.forceRenderThread.start();
      }
   }

   private void load() {
      OptionUtils.setLocaleEn();
      TannLog.log("load1");
      Colours.init();
      OptionUtils.init();
      TannLog.log("load2");
      VersionUtils.VERSION_COL = Colours.secretCol(VersionUtils.versionName.hashCode());
      this.loadAtli();
      Mode.init();
      TannLog.log("load3");
      this.setupJson();
      String settingsJson = Prefs.getString("settings", null);
      TannLog.log("load4");
      if (settingsJson == null) {
         TannLog.log("load4.1");
         this.resetSettings();
         TannLog.log("load4.2");
      } else {
         try {
            TannLog.log("load4.3");
            this.settings = (Settings)getJson().fromJson(Settings.class, settingsJson);
         } catch (Exception var3) {
            var3.printStackTrace();
            TannLog.error("Failed to load settings: " + var3.getMessage());
            this.resetSettings();
         }
      }

      TannLog.log("load5");
      getSettings().loadUp();
      this.control.onStart();
      this.setLanguageFromOption();
      this.bufferDrawer = new SpriteBatch();
      TannLog.log("load6");
      Sounds.setup(this.soundHandler);
      TannLog.log("load7");
      Draw.setup();
      TextWriter.setup();
      KUtils.init();
      HeroTypeLib.init();
      MonsterTypeLib.init();
      ItemLib.init();
      ModifierLib.init();
      AbilityUtils.init();
      PipeUtils.init();
      TannLog.log("load8");
      MusicManager.initMusic();
      TannLog.log("load9");
      BattleTestUtils.init();
      ContextConfig.resetCache();
      LeaderboardBlob.setupLeaderboards();
      FXContainer.loadAllShaders();
      this.masterStats = new MasterStats();
      this.masterStats.init();
      this.setupScale();
      if (getCurrentScreen() == null) {
         this.setScreen(new TitleScreen());
      }

      if (OptionLib.SHOW_RENDERCALLS.c()) {
         this.setupProfiler();
      }

      this.settedUp = true;
      if (Gdx.app.getType() != ApplicationType.iOS) {
         this.control.checkPurchase();
      }

      Gdx.graphics.setContinuousRendering(OptionUtils.isContinuous());
      requestRendering();
      this.startForceRenderThread();
   }

   public void setLanguageFromOption() {
      try {
         this.translator = new Translator(Translator.getLanguageCodeFromOption());
      } catch (Exception var2) {
         OptionLib.LANGUAGE.setValue(1, false);
         this.translator = new Translator("en");
      }

      this.translator.init();
   }

   public void setupProfiler() {
      if (this.glp == null) {
         this.glp = new GLProfiler(Gdx.graphics);
         this.glp.enable();
      }
   }

   private void loadAtli() {
      atlas = new TextureAtlas(Gdx.files.internal("2d/atlas_image.atlas"));
      atlas_big = new TextureAtlas(Gdx.files.internal("2dBig/atlas_image.atlas"));
      atlas_3d = new TextureAtlas(Gdx.files.internal("3d/atlas_image.atlas"));
      ObjectSetIterator var1 = atlas_3d.getTextures().iterator();

      while (var1.hasNext()) {
         Texture t = (Texture)var1.next();
         t.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
      }
   }

   public void setupScale() {
      this.setupScale(false);
   }

   public void setupScale(boolean force) {
      this.lastSetupScale = System.currentTimeMillis();
      boolean wasInMenu = Tann.findByClass(stage, DungeonUtils.CogTag.class) != null;
      boolean wasInBook = Book.inBook();
      this.initBGBatch();
      int scaleAdjust = 0;
      int diceScale = 0;
      if (getSettings() != null) {
         scaleAdjust = getSettings().getScaleAdjust();
      }

      if (force
         || Gdx.app.getType() == ApplicationType.iOS
         || this.SCREEN_WIDTH != Gdx.graphics.getWidth()
         || this.SCREEN_HEIGHT != Gdx.graphics.getHeight()
         || this.prevScaleAdjust != scaleAdjust
         || this.prevDiceAdjust != diceScale) {
         this.prevScaleAdjust = scaleAdjust;
         this.prevDiceAdjust = diceScale;
         this.SCREEN_WIDTH = Gdx.graphics.getWidth();
         this.SCREEN_HEIGHT = Gdx.graphics.getHeight();
         boolean vert = this.SCREEN_HEIGHT > this.SCREEN_WIDTH;
         int maxD = Math.max(this.SCREEN_HEIGHT, this.SCREEN_WIDTH);
         int minD = Math.min(this.SCREEN_HEIGHT, this.SCREEN_WIDTH);
         int maxDiv = 290;
         int minDiv = vert ? 175 : 155;
         scale = Math.min(maxD / maxDiv, minD / minDiv);
         TannLog.log("Detected screen dimensions: " + this.SCREEN_WIDTH + "/" + this.SCREEN_HEIGHT + ", base scale: " + scale + "(" + scaleAdjust + ")");
         this.invalidScale = scale == 0;
         if (!this.invalidScale) {
            scale = Math.max(1, scale + scaleAdjust);
            width = this.SCREEN_WIDTH / scale;
            height = this.SCREEN_HEIGHT / scale;
            if (this.fb != null) {
               this.fb.dispose();
            }

            this.fb = new FrameBuffer(Format.RGBA8888, width, height, true);
            TannLog.log("FB init");
            ((Texture)this.fb.getColorBufferTexture()).setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
            if (stage != null) {
               stage.clear();
            }

            stage = new TannStage(new FitViewport(width, height));
            stage.getBatch().setBlendFunctionSeparate(770, 771, 1, 1);
            this.orthoCam = (OrthographicCamera)stage.getCamera();
            BulletStuff.init();
            InventoryPanel.resetSingleton();
            stage.addListener(new InputListener() {
               public boolean keyDown(InputEvent event, int keycode) {
                  Main.this.currentScreen.mainKeyPress(keycode);
                  return true;
               }
            });
            this.bufferDrawer = new SpriteBatch();
            TannLog.log("pre set screen");
            if (this.currentScreen != null) {
               this.setScreen(this.currentScreen.copy());
            } else {
               this.setScreen(new TitleScreen());
            }

            TannLog.log("post set screen");
            Gdx.input.setInputProcessor(new InputMultiplexer(new InputProcessor[]{this.makeSuperInput(), stage, this.makeDiceInput()}));
            if (wasInBook) {
               Book.openBook(false);
            }

            if (wasInMenu) {
               DungeonUtils.showCogMenu();
            }
         }
      }
   }

   public void render() {
      Gdx.gl.glClear(256);
      Gdx.gl.glClear(16384);
      if (this.invalidScale) {
         this.splashScreen.draw(SplashDraw.SplashType.InvalidResolution);
      } else {
         frames++;
         switch (frames) {
            case 1:
               requestRendering();
               this.splashScreen.draw(SplashDraw.SplashType.Loading);
               return;
            case 2:
               requestRendering();
               long t = System.currentTimeMillis();
               this.load();
               TannLog.log("Load: " + (System.currentTimeMillis() - t) + "ms");
               return;
            case 3:
               requestRendering();
               self().control.afterLoad();
            default:
               if (this.control.allowLongPress() && Gdx.input.isTouched()) {
                  requestRendering();
               }

               if (self().control.belayRescale()
                  && Gdx.graphics.getWidth() > 0
                  && Gdx.graphics.getHeight() > 0
                  && (this.SCREEN_WIDTH != Gdx.graphics.getWidth() || this.SCREEN_HEIGHT != Gdx.graphics.getHeight())
                  && System.currentTimeMillis() - this.lastSetupScale > 300L) {
                  this.setupScale();
               }

               if (!this.settedUp) {
                  throw new RuntimeException("Trying to render before finished setting up, frame: " + frames);
               } else {
                  resetTime();
                  if (frames >= 2) {
                     this.update(Gdx.graphics.getDeltaTime());
                  }

                  logTime("upd");
                  if (!this.fullscreening) {
                     this.actuallyRender();
                  }

                  this.fullscreening = false;
                  this.control.tick();
               }
         }
      }
   }

   private void actuallyRender() {
      bonusRenderCalls = 0;
      this.start2d(false);
      Gdx.gl.glClear(16384);
      stage.draw();
      if (OptionLib.SHOW_GRB.c()) {
         Batch b = stage.getBatch();
         b.begin();
         this.drawGRB(stage.getBatch());
         b.end();
      }

      this.stop2d(false);
      if (renderSwapChadwick) {
         System.out.println("foreground: " + ((SpriteBatch)stage.getBatch()).renderCalls);
      }

      logTime("frg");
      if (OptionLib.IMG_CREATIONS.c()) {
         this.debugDisplay("Images created: " + Img64.imgCounter);
      }

      if (this.glp != null && OptionLib.SHOW_RENDERCALLS.c()) {
         this.debugDisplay(
            "draw calls: "
               + this.glp.getDrawCalls()
               + "[n]tex bind: "
               + this.glp.getTextureBindings()
               + "[n]shad swit: "
               + this.glp.getShaderSwitches()
               + "[n]calls: "
               + this.glp.getCalls()
         );
         if (this.glp != null) {
            this.glp.reset();
         }
      }
   }

   private void debugDisplay(String s) {
      Actor tw = new Pixl(0, 3).border(Colours.orange).text(s).pix();
      Batch b = stage.getBatch();
      b.begin();
      tw.draw(b, 1.0F);
      b.end();
   }

   private void drawGRB(Batch batch) {
      Actor a = new Pixl(3).border(Colours.grey).text("[green]" + EntState.cnt).row().text("[red]" + EntSideState.cnt).pix();
      a.draw(batch, 0.0F);
   }

   public void start2d(boolean startBatch) {
      this.fb.begin();
      Gdx.gl.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      Gdx.gl.glClear(16384);
      if (startBatch) {
         stage.getBatch().begin();
      }
   }

   public void stop2d(boolean stopBatch) {
      if (stopBatch) {
         stage.getBatch().end();
      }

      this.fb.end();
      this.drawBufferToScreen();
      Gdx.gl.glClear(256);
   }

   private void drawBufferToScreen() {
      stage.getBatch().flush();
      this.bufferDrawer.begin();
      Draw.drawRotatedScaledFlipped(this.bufferDrawer, (Texture)this.fb.getColorBufferTexture(), 0.0F, 0.0F, scale, scale, 0.0F, false, true);
      this.bufferDrawer.end();
   }

   public void update(float delta) {
      if (!this.isPaused) {
         TimerUtil.tick(delta);
         int rotation = Gdx.input.getRotation();
         if (rotation != this.rotation) {
            this.rotation = rotation;
            this.setupScale();
         }

         delta = Math.min(0.033333335F, delta);
         noiseFromTicks = (float)Noise.noise(secs, 0.0);
         delta *= getDeltaMultiple();
         BulletStuff.update(delta);
         stage.act(delta);
         secs += delta;
         TannFont.bonusSin = 0.0F;
         if (!justResized()) {
            MusicManager.tick(delta);
         }
      }
   }

   public void pause() {
      super.pause();
      this.isPaused = true;
      if (printCalls) {
         System.out.println("pause");
      }

      if (this.control.unloadWhilePaused()) {
         if (this.forceRenderThread != null) {
            this.forceRenderThread.interrupt();
         }

         TannFont.GLOBAL_GLITCH = false;
         if (getCurrentScreen() instanceof DungeonScreen) {
            DungeonContext dc = ((DungeonScreen)getCurrentScreen()).getDungeonContext();
            this.pausedKey = dc.getContextConfig().getGeneralSaveKey();
            dc.getContextConfig().quitAction();
            this.setScreen(new PauseScreen());
         }

         this.clearCaches();
         BulletStuff.dispose();
      }
   }

   public void resume() {
      OptionUtils.setLocaleEn();
      requestRendering();
      this.isPaused = false;
      if (self().control.unloadWhilePaused()) {
         if (getSettings() == null) {
            this.resetSettings();
         }

         BulletStuff.init();
         BattleTestUtils.init();
         this.startForceRenderThread();
         if (this.pausedKey != null) {
            final String fPause = this.pausedKey;
            this.pausedKey = null;
            TimerUtil.clearStatics();
            this.timerAlreadyCleared = true;
            Tann.delayOneFrame(new Runnable() {
               @Override
               public void run() {
                  DungeonScreen.clearStaticReference();
                  SaveState toLoad = SaveState.load(fPause);
                  if (toLoad != null) {
                     toLoad.start();
                  } else {
                     TannLog.log("Failed to load save on resume for some reason: " + Main.this.pausedKey, TannLog.Severity.error);
                     TannLog.log(Prefs.getString(fPause, "null pref"), TannLog.Severity.error);
                  }

                  com.tann.dice.Main.getCurrentScreen().act(0.5F);
               }
            });
         }

         if (printCalls) {
            System.out.println("resume");
         }
      }
   }

   public void resize(int width, int height) {
      if (!this.timerAlreadyCleared) {
         TimerUtil.clearStatics();
      }

      lastResize = System.currentTimeMillis();
      this.timerAlreadyCleared = false;
      HdpiUtils.glViewport(0, 0, width, height);
      if (this.settedUp && !this.isPaused) {
         if (width != 0 || height != 0) {
            if (!self().control.unloadWhilePaused()) {
               if (width == this.oldWidth && height == this.oldHeight) {
                  return;
               }

               this.oldWidth = width;
               this.oldHeight = height;
            }

            if (!self().control.belayRescale()) {
               this.setupScale();
            }

            if (printCalls) {
               System.out.println("resize");
            }

            if (this.invalidScale) {
               this.setupScale();
            }
         }
      }
   }

   public void dispose() {
      System.out.println("starting dispose");
      super.dispose();
      this.control.onStop();
      this.disposeAll();
      this.clearAllStatics();
      if (printCalls) {
         System.out.println("dispose");
      }

      System.out.println("finished dispose");
   }

   private void initBGBatch() {
      if (this.backgroundBatch != null) {
         this.backgroundBatch.dispose();
      }

      this.backgroundBatch = new SpriteBatch();
   }

   public SpriteBatch startBackground() {
      this.backgroundBatch.begin();
      return this.backgroundBatch;
   }

   public void stopBackground() {
      this.backgroundBatch.end();
   }

   public void setFullScreen(boolean enabled) {
      this.fullscreening = true;
      this.backgroundBatch.begin();
      Draw.fillRectangle(this.backgroundBatch, 0.0F, 0.0F, 5000.0F, 5000.0F);
      this.backgroundBatch.end();
      if (enabled) {
         Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
      } else {
         Gdx.graphics.setWindowedMode(Tann.DG_WIDTH, Tann.DG_HEIGHT);
      }
   }

   public void setScreen(Screen screen) {
      if (this.currentScreen != null) {
         this.currentScreen.remove();
      }

      this.currentScreen = screen;
      stage.addActor(screen);
      screen.afterSet();
   }

   public static void logTime(String id) {
      if (chadwick) {
         long currentTime = System.currentTimeMillis();
         long time = currentTime - previousTime;
         previousTime = currentTime;
         boolean found = false;

         for (Pair<String, Long[]> p : times) {
            if (p.a.equals(id)) {
               p.b[frames % chadSamples] = time;
               found = true;
            }
         }

         if (!found) {
            times.add(new Pair<>(id, new Long[chadSamples]));
         }
      }
   }

   private static void resetTime() {
      previousTime = System.currentTimeMillis();
   }

   private void disposeAll() {
      Sounds.disposeAll();
      BulletStuff.dispose();
      FontWrapper.disposeAll();
   }

   private void clearCaches() {
      FXContainer.clearCaches();
      Sounds.clearCaches();
      ImageFilter.clearCaches();
      ImageUtils.clearCaches();
      PhaseManager.resetSingleton();
      InventoryPanel.resetSingleton();
      EntSide.clearCache();
      Img64.clearCache();
      Pipe.clearCache();
   }

   private void clearAllStatics() {
      if (printCalls) {
         TannLog.log("Clearing statics");
      }

      this.clearCaches();
      TimerUtil.clearStatics();
      Die.clearAllStatics();
      MusicManager.clearStatics();
      EntSidesLib.clearStatics();
      FontWrapper.clearAllStatics();
   }

   private InputProcessor makeDiceInput() {
      return new InputProcessor() {
         public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return DungeonScreen.get() != null && DungeonScreen.get().hasParent() ? BulletStuff.touchDown(screenX, screenY, pointer, button) : false;
         }

         public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            BulletStuff.touchUp(screenX, screenY, pointer, button);
            return false;
         }

         public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
         }

         public boolean keyDown(int keycode) {
            return false;
         }

         public boolean keyUp(int keycode) {
            return false;
         }

         public boolean keyTyped(char character) {
            return false;
         }

         public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
         }

         public boolean mouseMoved(int screenX, int screenY) {
            return false;
         }

         public boolean scrolled(float amountX, float amountY) {
            return false;
         }

         public boolean scrolled(int amount) {
            return false;
         }
      };
   }

   private InputProcessor makeSuperInput() {
      return new InputProcessor() {
         public boolean keyDown(int keycode) {
            switch (keycode) {
               case 31:
                  Colours.SHIFTER.set(Colours.random().cpy());
               case 30:
               case 41:
               case 47:
               default:
                  return false;
            }
         }

         public boolean keyUp(int keycode) {
            return false;
         }

         public boolean keyTyped(char character) {
            return false;
         }

         public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
         }

         public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
         }

         public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
         }

         public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
         }

         public boolean mouseMoved(int screenX, int screenY) {
            return false;
         }

         public boolean scrolled(float amountX, float amountY) {
            return false;
         }
      };
   }

   public static Json getJson() {
      return getJson(false);
   }

   public static Json getJson(boolean mini) {
      return mini ? jsonMini : json;
   }

   private void setupJson() {
      json = new Json();
      json.setTypeName(null);
      json.setUsePrototypes(false);
      json.setIgnoreUnknownFields(true);
      json.setOutputType(OutputType.json);
      jsonMini = new Json();
      jsonMini.setTypeName(null);
      jsonMini.setUsePrototypes(true);
      jsonMini.setIgnoreUnknownFields(true);
      jsonMini.setOutputType(OutputType.minimal);
   }

   public static Settings getSettings() {
      return self().settings;
   }

   public void resetSettings() {
      this.settings = new Settings();
      this.settings.reset();
   }

   public static boolean versionALower(String a, String b) {
      return a != null && b != null && a.compareTo(b) < 0;
   }

   public Random getR() {
      return this.r;
   }

   public static String t(String text) {
      return self().translator.translate(text);
   }

   public static String tOnce(String text) {
      String translated = t(text);
      return "[notranslate]" + translated.replace("[n]", "[n][notranslate]");
   }
}
