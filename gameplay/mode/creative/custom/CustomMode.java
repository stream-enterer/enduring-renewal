package com.tann.dice.gameplay.mode.creative.custom;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.group.PartyLayoutType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.ModifierUtils;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.OptionsMenu;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.screens.titleScreen.TitleScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.resolver.ModifierResolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomMode extends Mode {
   private static final int MAX_MODS = 500;
   static float storedY = 500000.0F;
   final String cpct = "[yellow]";
   final String slct = "[blue]";

   public CustomMode() {
      super("Custom");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{Mode.CLASSIC.getTextButtonName() + " " + Difficulty.Normal.getColourTaggedName() + " but you can choose modifiers"};
   }

   @Override
   protected List<Actor> getLeftOfTitleActors() {
      return Arrays.asList(this.makeModifierPickGroup());
   }

   public static List<Modifier> getCustomModifiers() {
      return ModifierUtils.deserialiseList(com.tann.dice.Main.getSettings().getCustomModifiers());
   }

   private void clearStored() {
      storedY = 500000.0F;
   }

   private Actor makeModifierPickGroup() {
      final List<Modifier> existing = getCustomModifiers();
      Pixl modifiersPix = new Pixl(2, 2);

      for (int i = 0; i < existing.size(); i++) {
         final Modifier m = existing.get(i);
         StandardButton minus = new StandardButton("[minus]").makeTiny();
         minus.setRunnable(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pipSmall);
               CustomMode.this.removeModifier(existing.indexOf(m));
            }
         });
         modifiersPix.actor(minus);
         if (existing.size() > 1 && OptionLib.CUSTOM_REARRANGE.c()) {
            boolean active = i > 0;
            StandardButton up = new StandardButton(active ? "[hp-arrow_up]" : "[grey][hp-arrow_up]").makeTiny();
            if (active) {
               final int finalI = i;
               up.setRunnable(new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.pipSmall);
                     existing.add(finalI - 1, existing.remove(finalI));
                     CustomMode.this.saveAndRefresh(existing);
                  }
               });
            }

            modifiersPix.actor(up);
         }

         SmallModifierPanel sm = new SmallModifierPanel(m);
         sm.addBasicListener();
         modifiersPix.actor(sm);
         modifiersPix.row();
      }

      Actor inner = modifiersPix.pix(8);
      int MAX_SCROLLPANE_HEIGHT = com.tann.dice.Main.height / 3;
      Pixl p = new Pixl(3, 2).border(Colours.purple);
      boolean needsScroll = inner.getHeight() > MAX_SCROLLPANE_HEIGHT;
      if (needsScroll) {
         final ScrollPane scroll = Tann.makeScrollpane(inner);
         scroll.setHeight(Math.min((float)MAX_SCROLLPANE_HEIGHT, inner.getHeight()));
         scroll.setWidth(Math.max(inner.getWidth() + (needsScroll ? 6 : 0), scroll.getWidth()));
         scroll.layout();
         scroll.setScrollY(storedY);
         scroll.updateVisualScroll();
         p.actor(scroll);
         p.gap(0).actor(new Actor() {
            public void act(float delta) {
               CustomMode.storedY = scroll.getScrollY();
            }
         });
      } else {
         p.actor(inner);
      }

      p.row().actor(this.makeButtStuff(existing));
      return p.pix();
   }

   private Pixl makeButtStuff(List<Modifier> existing) {
      int buttGap = 2;
      Pixl buttStuff = new Pixl(buttGap, buttGap);
      buttStuff.actor(this.makePlus()).actor(this.makePlusRand());
      if (existing.size() > 2) {
         buttStuff.actor(this.makeMag());
      }

      buttStuff.actor(this.makeReset());
      buttStuff.row();
      if (!existing.isEmpty()) {
         buttStuff.actor(this.makeCopy());
      }

      buttStuff.actor(this.makePaste());
      if (!existing.isEmpty()) {
         buttStuff.actor(this.makeSave());
      }

      if (!com.tann.dice.Main.getSettings().getCustomPresets().isEmpty()) {
         buttStuff.actor(this.makeLoad());
      }

      return buttStuff;
   }

   private Actor makeReset() {
      StandardButton plus = new StandardButton("clear");
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            CustomMode.this.clearModifiers(true);
         }
      });
      return plus;
   }

   private Actor makePlus() {
      StandardButton plus = new StandardButton(" [plus] ");
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            CustomMode.this.makeMr().activate();
         }
      });
      return plus;
   }

   private ModifierResolver makeMr() {
      return new ModifierResolver() {
         @Override
         protected List<Modifier> failSearch(String text) {
            Item i = ItemLib.checkedByName(text);
            if (!i.isMissingno()) {
               return AlternateModifier.showItemMods(i);
            } else {
               HeroType ht = HeroTypeLib.safeByName(text);
               if (!ht.isMissingno()) {
                  return AlternateModifier.showHeroMods(ht);
               } else {
                  MonsterType mt = MonsterTypeLib.safeByName(text);
                  return !mt.isMissingno() ? AlternateModifier.showMonsterMods(mt) : makeBlank();
               }
            }
         }

         public void resolve(Modifier modifier) {
            CustomMode.this.addModifier(modifier);
         }
      };
   }

   private Actor makePlusRand() {
      StandardButton plus = new StandardButton("[plus][p]rng");
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.addModifier(ModifierLib.random());
         }
      });
      return plus;
   }

   private Actor makeMag() {
      StandardButton plus = new StandardButton(Images.magnifyingGlass, Colours.light);
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            Pixl p = new Pixl(3, 3).border(Colours.purple);

            for (Modifier customModifier : CustomMode.getCustomModifiers()) {
               Actor mp = new ModifierPanel(customModifier, false);
               p.actor(mp, com.tann.dice.Main.width * 0.6F);
            }

            Actor a = p.pix();
            if (a.getHeight() > com.tann.dice.Main.height) {
               ScrollPane scrollPane = Tann.makeScrollpane(a);
               a = scrollPane;
            }

            com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
            Tann.center(a);
         }
      });
      return plus;
   }

   private Actor makeCopy() {
      StandardButton cpy = new StandardButton("[yellow]copy");
      styleButton(cpy);
      cpy.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            String result = "=" + Tann.commaList(com.tann.dice.Main.getSettings().getCustomModifiers(), ",", ",");
            CustomMode.this.offerToCopy(PasteMode.encloseBackticks(result));
         }
      });
      return cpy;
   }

   private void buttonSound() {
      Sounds.playSound(Sounds.pipSmall);
   }

   private Actor makePaste() {
      StandardButton paste = new StandardButton("[yellow]paste");
      styleButton(paste);
      paste.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            CustomMode.this.pasteStuff(true);
            return true;
         }

         @Override
         public boolean action(int button, int pointer, float x, float y) {
            CustomMode.this.pasteStuff(false);
            return true;
         }
      });
      return paste;
   }

   private void pasteStuff(boolean longtap) {
      Pipe.setupChecks();
      String contents = ClipboardUtils.pasteSafer();
      String genericError = PasteMode.getPasteErrorGeneric(contents);
      if (contents != null && contents.length() + this.getTextLengthOfCurrent() > 2000000) {
         genericError = "too much total text in modifiers, maximum is 2000000";
      }

      if (genericError != null) {
         com.tann.dice.Main.getCurrentScreen().showDialog(genericError, Colours.red);
      } else {
         contents = PasteMode.genericPasteTagHandleCleanup(contents);
         if (contents.startsWith("{")) {
            com.tann.dice.Main.getCurrentScreen().showDialog(PasteMode.looksLikeFor(Mode.PASTE));
         } else {
            boolean replace = false;
            if (contents.startsWith("=")) {
               contents = contents.substring(1);
               replace = !longtap;
            }

            String[] modNames = contents.split(",");
            if (getCustomModifiers().size() + modNames.length > 500) {
               com.tann.dice.Main.getCurrentScreen().showDialog("Too many modifiers", Colours.red);
            } else {
               List<Modifier> mods = ModifierUtils.deserialiseList(Arrays.asList(modNames));
               if (!replace && mods.size() > 1 && !longtap) {
                  com.tann.dice.Main.getCurrentScreen().showDialog("modifier lists must start with =");
               } else {
                  String exFail = "";
                  int fails = 0;

                  for (int i = mods.size() - 1; i >= 0; i--) {
                     if (mods.get(i).isMissingno()) {
                        mods.remove(i);
                        if (mods.size() > 1) {
                           exFail = modNames[i];
                        }

                        fails++;
                     }
                  }

                  if (mods.size() == 0) {
                     Pipe.setupChecks();
                     if (!this.checkOtherThings(contents)) {
                        String err = "No modifiers found";
                        com.tann.dice.Main.getCurrentScreen().showDialog(err, Colours.red);
                        this.buttonSound();
                     }

                     Pipe.disableChecks();
                  } else {
                     float successRatio = 1.0F - (float)fails / (fails + mods.size());
                     if (successRatio < 0.8F) {
                        String msg = "[red]" + fails + "/" + (fails + mods.size()) + " failed to paste";
                        com.tann.dice.Main.getCurrentScreen().showDialog(msg + ", cancelling[n][grey]" + Tann.makeEllipses(exFail, 50));
                     } else {
                        if (replace) {
                           this.clearModifiers(false);
                        }

                        this.addModifiers(mods);
                        if (fails > 0) {
                           com.tann.dice.Main.getCurrentScreen()
                              .showDialog(
                                 "[red]" + fails + "/" + (fails + mods.size()) + " failed to paste[n][blue](only a few failed, so the rest are accepted)"
                              );
                        }

                        Pipe.disableChecks();
                     }
                  }
               }
            }
         }
      }
   }

   private int getTextLengthOfCurrent() {
      int total = 0;

      for (Modifier customModifier : getCustomModifiers()) {
         total += customModifier.getName(false).length();
      }

      return total;
   }

   private Actor makeSave() {
      StandardButton plus = new StandardButton("[blue]save");
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            com.tann.dice.Main.self().control.textInput(new TextInputListener() {
               public void input(String modeName) {
                  CustomMode.this.buttonSound();
                  CustomMode.this.savePreset(modeName, CustomMode.getCustomModifiers());
                  CustomMode.this.refreshScreen();
                  com.tann.dice.Main.getCurrentScreen().showDialog("[blue]" + modeName + " saved");
               }

               public void canceled() {
               }
            }, "Name this stored custom mode", null, null);
         }
      });
      return plus;
   }

   private Actor makeLoad() {
      StandardButton plus = new StandardButton("[blue]load");
      styleButton(plus);
      plus.setRunnable(new Runnable() {
         @Override
         public void run() {
            CustomMode.this.buttonSound();
            Pixl p = new Pixl(3, 3).border(Colours.blue);
            p.text("[text]Load a stored custom mode").row();

            for (final CustomPreset customPreset : com.tann.dice.Main.getSettings().getCustomPresets()) {
               StandardButton sb = new StandardButton("[text]" + customPreset.getTitle());
               sb.addListener(new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     CustomMode.this.clearModifiers(false);
                     CustomMode.this.addModifiers(customPreset.getContentAsModifiers());
                     com.tann.dice.Main.getCurrentScreen().popAllLight();
                     return true;
                  }

                  @Override
                  public boolean info(int button, float x, float y) {
                     CustomMode.this.buttonSound();
                     ChoiceDialog cd = new ChoiceDialog("Delete " + customPreset.getTitle() + "?", ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                        @Override
                        public void run() {
                           CustomMode.this.buttonSound();
                           com.tann.dice.Main.getSettings().removePreset(customPreset);
                           CustomMode.this.refreshScreen();
                        }
                     }, new Runnable() {
                        @Override
                        public void run() {
                           com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                        }
                     });
                     com.tann.dice.Main.getCurrentScreen().push(cd);
                     Tann.center(cd);
                     return true;
                  }
               });
               p.actor(sb, 150.0F);
            }

            Actor a = p.pix();
            if (a.getHeight() > com.tann.dice.Main.height) {
               a = Tann.makeScrollpane(a);
            }

            com.tann.dice.Main.getCurrentScreen().push(a, 0.8F);
            Tann.center(a);
         }
      });
      return plus;
   }

   private void savePreset(String name, List<Modifier> customModifiers) {
      CustomPreset cp = new CustomPreset(name, customModifiers);
      com.tann.dice.Main.getSettings().addPreset(cp);
   }

   private boolean checkOtherThings(String contents) {
      return this.makeMr().debugSearch(contents);
   }

   private static void styleButton(StandardButton sb) {
      sb.makeTiny();
   }

   private void offerToCopy(String result) {
      ClipboardUtils.offerToCopy(result, "Copy modifiers to clipboard?");
   }

   private void clearModifiers(boolean setScreen) {
      com.tann.dice.Main.getSettings().setCustomModifiers(new ArrayList<>());
      if (setScreen) {
         this.refreshScreen();
      }
   }

   private void addModifiers(List<Modifier> list) {
      Sounds.playSound(Sounds.blocks);
      this.clearStored();
      List<Modifier> mods = ModifierUtils.deserialiseList(com.tann.dice.Main.getSettings().getCustomModifiers());
      mods.addAll(list);
      this.saveAndRefresh(mods);
   }

   private void removeModifier(int index) {
      List<Modifier> mods = ModifierUtils.deserialiseList(com.tann.dice.Main.getSettings().getCustomModifiers());
      mods.remove(index);
      this.saveAndRefresh(mods);
   }

   private void saveAndRefresh(List<Modifier> mods) {
      this.saveModifiers(mods);
      this.refreshScreen();
   }

   private void addModifier(Modifier m) {
      this.addModifiers(Arrays.asList(m));
   }

   private void refreshScreen() {
      com.tann.dice.Main.self().setScreen(new TitleScreen());
   }

   private void saveModifiers(List<Modifier> mods) {
      com.tann.dice.Main.getSettings().setCustomModifiers(ModifierLib.serialiseToStringList(mods));
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   @Override
   public Color getColour() {
      return Colours.blue;
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new CustomMode.CustomConfig());
   }

   @Override
   public String getSaveKey() {
      return "custom";
   }

   public static List<Global> naiveGlobalExtract(List<Modifier> customModifiers) {
      List<Global> res = new ArrayList<>();

      for (Modifier customModifier : customModifiers) {
         res.addAll(customModifier.getGlobals());
      }

      return res;
   }

   private static HeroType makeNormal(PartyLayoutType plt, HeroCol col, int lvl, List<HeroType> oldCopy) {
      List<HeroType> types = HeroTypeUtils.getFilteredTypes(col, lvl, false);
      HeroTypeUtils.globalAffect(types, naiveGlobalExtract(getCustomModifiers()), col, lvl);
      List<HeroType> empty = new ArrayList<>();
      HeroType missingno = PipeHero.getMissingno();

      for (int i = 0; i < 500; i++) {
         HeroType ht = Tann.getSelectiveRandom(types, 1, missingno, empty, oldCopy).get(0);
         if (!ht.isMissingno() && !HeroTypeUtils.bannedHeroTypeByCollision(ht, plt.getBannedCollisionBits(false))) {
            return ht;
         }
      }

      return PipeHeroGenerated.generate(col, lvl);
   }

   @Override
   public boolean disablePartyLayout() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.creative;
   }

   @Override
   protected List<Actor> extraDescActors() {
      return Arrays.asList(new StandardButton("[grey]options").makeTiny().setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            Pixl p = new Pixl(3, 3).border(Colours.red);
            int w = (int)Math.min(200.0F, com.tann.dice.Main.width * 0.5F);
            p.text(OptionsMenu.getCheckboxExp(), w).row();

            for (Option option : OptionUtils.EscBopType.Modding.getOptions()) {
               p.actor(option.makeCogActor(), w);
            }

            Actor a = p.pix(8);
            com.tann.dice.Main.getCurrentScreen().push(a, true, true, false, 0.3F);
            Tann.center(a);
         }
      }), new StandardButton("[grey]api").makeTiny().setRunnable(new Runnable() {
         @Override
         public void run() {
            Book.openBook("ledger-textmod");
         }
      }), TextUrl.make(com.tann.dice.Main.t("[grey]resources"), "https://tann.fun/games/dice/textmod/"));
   }

   public static class CustomConfig extends ContextConfig {
      public CustomConfig() {
         super(Mode.CUSTOM);
      }

      @Override
      public Collection<Global> getSpecificModeAddPhases() {
         return super.getSpecificModeAddPhases();
      }

      @Override
      public DungeonContext makeContext(AntiCheeseRerollInfo info) {
         DungeonContext dc = super.makeContext(info);
         dc.addModifiers(CustomMode.getCustomModifiers());
         return dc;
      }

      @Override
      public Party getStartingParty(PartyLayoutType chosen, AntiCheeseRerollInfo info) {
         List<Hero> heroes = new ArrayList<>();
         PartyLayoutType plt = PartyLayoutType.Basic;
         HeroCol[] cols = plt.getColsInstance();
         List<HeroType> oldCopy = new ArrayList<>();
         int lvl = 1;

         for (HeroCol col : cols) {
            heroes.add(CustomMode.makeNormal(plt, col, lvl, oldCopy).makeEnt());
         }

         return new Party(heroes);
      }
   }
}
