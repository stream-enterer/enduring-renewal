package com.tann.dice.gameplay.mode.creative.pastey;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.gameplay.save.SaveStateData;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PasteMode extends Mode {
   public static final int MAX_PASTE_LENGTH = 2000000;

   public PasteMode() {
      super("Paste");
   }

   public static Actor makeEscButton() {
      StandardButton paste = new StandardButton("[pink]Copy");
      paste.setRunnable(
         new Runnable() {
            @Override
            public void run() {
               Phase p = PhaseManager.get().getPhase();
               if (!p.isPastey()) {
                  Sounds.playSound(Sounds.error);
                  com.tann.dice.Main.getCurrentScreen().showDialog("[red]wrong phase: " + p.getClass().getSimpleName(), Colours.pink);
               } else {
                  final boolean hasEnd = PhaseManager.get().has(LevelEndPhase.class);
                  ChoiceDialog cd = new ChoiceDialog(
                     "Copy state to clipboard for " + Mode.PASTE.getName() + "?",
                     hasEnd ? ChoiceDialog.ChoiceNames.YesCancel : ChoiceDialog.ChoiceNames.HackyStartNow,
                     new Runnable() {
                        @Override
                        public void run() {
                           String tc;
                           if (OptionLib.TINY_PASTE.c()) {
                              tc = DungeonScreen.get().tryTinyPaste();
                           } else {
                              tc = DungeonScreen.get().reportStringSave(true);
                           }

                           PasteMode.escCopyString(tc);
                        }
                     },
                     new Runnable() {
                        @Override
                        public void run() {
                           if (hasEnd) {
                              com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                           } else {
                              PasteMode.escCopyString(PasteMode.encloseBackticks(PasteMode.getStringFromStartOfLevel(DungeonScreen.get().getDungeonContext())));
                           }
                        }
                     }
                  );
                  com.tann.dice.Main.getCurrentScreen().push(cd);
                  Tann.center(cd);
               }
            }
         }
      );
      return paste;
   }

   public static String encloseBackticks(String s) {
      return "`" + s + "`";
   }

   private static void escCopyString(String toClip) {
      ClipboardUtils.copyWithSoundAndToast(toClip);
      com.tann.dice.Main.getCurrentScreen().popAllMedium();
   }

   private static String getStringFromStartOfLevel(DungeonContext dc) {
      int numEntities = dc.getParty().getHeroes().size() + dc.getCurrentLevel().getMonsterList().size();
      List<String> l = new ArrayList<>();

      for (int i = 0; i < numEntities; i++) {
         l.add("0");
      }

      SaveStateData ssd = new SaveStateData(
         dc.toData(), new ArrayList<>(), Tann.commaList(l, ",", ","), Arrays.asList(new LevelEndPhase().serialise(), new EnemyRollingPhase().serialise())
      );
      ssd.trimContextDataForReport();
      return ssd.toState().getSaveString();
   }

   public static String genericPasteTagHandleCleanup(String s) {
      s = s.trim();
      if (Tann.countCharsInString('`', s) >= 2) {
         s = s.substring(s.indexOf("`") + 1, s.lastIndexOf("`"));
      }

      s = s.replaceAll("`", "");
      return removeNewlines(s);
   }

   public static String removeNewlines(String s) {
      return s.replaceAll("\\r|\\n", "");
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      Pixl p = new Pixl(3);
      StandardButton paste = new StandardButton("[pink]Paste!");
      paste.setRunnable(new Runnable() {
         @Override
         public void run() {
            Pipe.setupChecks();
            this.doInput();
            Pipe.disableChecks();
         }

         private void doInput() {
            String content = ClipboardUtils.pasteSafer();
            if (content == null || !content.startsWith("=") && ModifierLib.byName(content).isMissingno()) {
               PasteMode.attemptToStartFromString(content, new PasteConfig());
               PasteMode.this.specialPasteSave();
            } else {
               com.tann.dice.Main.getCurrentScreen().showDialog(PasteMode.looksLikeFor(Mode.CUSTOM));
            }
         }
      });
      StandardButton store = new StandardButton("[text]Store");
      store.setRunnable(new Runnable() {
         @Override
         public void run() {
            final String clip = ClipboardUtils.pasteSafer();

            try {
               SaveState result = SaveState.loadPasteModeString(clip, true);
               if (!result.validForPaste()) {
                  com.tann.dice.Main.getCurrentScreen().showDialog("invalid scenario paste");
               } else {
                  com.tann.dice.Main.self().control.textInput(new TextInputListener() {
                     public void input(String text) {
                        com.tann.dice.Main.getSettings().saveScenario(new Scenario(text, clip));
                        com.tann.dice.Main.self().setScreen(com.tann.dice.Main.getCurrentScreen().copy());
                        com.tann.dice.Main.getCurrentScreen().showDialog("scenario paste saved");
                     }

                     public void canceled() {
                        com.tann.dice.Main.getCurrentScreen().popAllMedium();
                     }
                  }, "[notranslate]" + com.tann.dice.Main.t("Scenario title"), "", "title");
               }
            } catch (Exception var3) {
               var3.printStackTrace();
               com.tann.dice.Main.getCurrentScreen().showDialog("invalid scenario paste (error)");
            }
         }
      });
      p.actor(paste).actor(store).row(6);
      Pixl scen = new Pixl(1);

      for (final Scenario sc : this.getScenarios()) {
         StandardButton tb = new StandardButton("[notranslate]" + sc.getTitle());
         tb.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               PasteMode.attemptToStartFromString(sc.getContent(), new PasteConfig());
               return true;
            }

            @Override
            public boolean info(int button, float x, float y) {
               String title = com.tann.dice.Main.t("Delete [mode]?");
               title = title.replace("[mode]", sc.getTitle());
               ChoiceDialog cd = new ChoiceDialog("[notranslate]" + title, ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                  @Override
                  public void run() {
                     boolean removed = com.tann.dice.Main.getSettings().deleteScenario(sc);
                     if (!removed) {
                        com.tann.dice.Main.getCurrentScreen().popAllMedium();
                        com.tann.dice.Main.getCurrentScreen().showDialog("Failed to delete", Colours.red);
                     } else {
                        com.tann.dice.Main.self().setScreen(com.tann.dice.Main.getCurrentScreen().copy());
                        com.tann.dice.Main.getCurrentScreen().showDialog("Deleted " + sc.getTitle(), Colours.green);
                     }
                  }
               }, new Runnable() {
                  @Override
                  public void run() {
                     com.tann.dice.Main.getCurrentScreen().popAllMedium();
                  }
               });
               com.tann.dice.Main.getCurrentScreen().push(cd, 0.8F);
               Tann.center(cd);
               return true;
            }
         });
         scen.actor(tb, (int)(com.tann.dice.Main.width * 0.75F));
      }

      Actor scenarios = scen.pix();
      Actor sp = Tann.makeScrollpane(scenarios);
      sp.setWidth(scenarios.getWidth() + 6.0F);
      sp.setHeight(Math.min(40.0F, scenarios.getHeight() + 6.0F));
      p.row().actor(sp);
      Actor loadButton = SaveState.getLoadButton(this.getConfigs().get(0).getGeneralSaveKey());
      if (loadButton != null) {
         p.row().actor(loadButton);
      }

      return p.pix();
   }

   private void specialPasteSave() {
      Screen s = com.tann.dice.Main.getCurrentScreen();
      if (s instanceof DungeonScreen) {
         DungeonScreen ds = (DungeonScreen)s;
         ds.save();
      }
   }

   private List<Scenario> getScenarios() {
      List<Scenario> all = new ArrayList<>(com.tann.dice.Main.getSettings().getScenarios());
      return all;
   }

   public static String getPasteErrorGeneric(String paste) {
      if (paste == null) {
         return "[grey]Empty clipboard";
      } else if (paste.length() > 2000000) {
         return "[red]Clipboard too long[n][text](" + paste.length() + " characters!)";
      } else {
         String rep = paste.replaceAll("[a-zA-Z0-9#+=.,_ /'()@?%$&!~{}`;:|\\^\\\\\\[\\]\"\r\n\\-]", "");
         if (PipeUtils.unbalancedBrackets(paste)) {
            return "[orange]unbalanced brackets";
         } else if (!rep.isEmpty()) {
            List<Character> chars = Tann.toCharList(rep);
            Tann.uniquify(chars);
            TannLog.error("illegal chars: " + chars.toString());
            TannLog.error("illegal string: " + paste);
            System.out.println(chars);
            return "[red]Illegal characters in clipboard: [cu]" + chars.toString().replaceAll("[\\[\\]]", "") + "[n][grey]Try an alternate text editor?";
         } else {
            return null;
         }
      }
   }

   public static String looksLikeFor(Mode m) {
      return "this is likely for " + m.getTextButtonName() + " mode";
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new PasteConfig());
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"paste a save from anywhere", "cog -> [pink]copy[cu] in any fight to copy"};
   }

   public static void attemptToStartFromString(String content, ContextConfig blankConfig) {
      String genericError = getPasteErrorGeneric(content);
      if (genericError != null) {
         com.tann.dice.Main.getCurrentScreen().showDialog(genericError);
      } else {
         try {
            SaveState result = SaveState.loadPasteModeString(content, blankConfig == null);
            if (!result.validForPaste()) {
               throw new Exception("wrong");
            }

            result.dungeonContext.setContextConfig(blankConfig);
            result.start();
         } catch (Exception var5) {
            var5.printStackTrace();
            if (content == null) {
               content = "null";
            }

            String extra = "";
            if (var5.getMessage() != null) {
               extra = ":[n][text]" + var5.getMessage().substring(0, Math.min(var5.getMessage().length(), 20)) + "...";
            } else {
               extra = ":[n][text]" + var5.getClass().getSimpleName();
            }

            com.tann.dice.Main.getCurrentScreen().showDialog("Failed to load fight from clipboard" + extra, Colours.red);
         }
      }
   }

   @Override
   public String getSaveKey() {
      return "paste";
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   @Override
   protected String getExtraDescription() {
      return "You can copy a save state from most places using menu - system - copy. This is just text which can be pasted into this mode or shared online";
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.creative;
   }
}
