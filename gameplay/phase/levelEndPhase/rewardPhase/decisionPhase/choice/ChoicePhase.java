package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNRichText;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.LevelupHeroChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.OrChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.RandomTieredChoosableRange;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.ReplaceChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special.SkipChoosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.counter.ChoiceCounter;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.antiCheese.AntiCheeseRerollInfo;
import com.tann.dice.gameplay.save.antiCheese.AnticheeseData;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.background.PortBoard;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.action.PixAction;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoicePhase extends Phase {
   public static final int ALIGN = 1;
   final ChoiceType choiceType;
   final List<Choosable> options;
   final String topMessage;
   protected Group choiceGroup;
   protected Group toRemove;
   Map<Choosable, Actor> choiceActorMap = new HashMap<>();
   ChoiceCounter choiceCounter;
   private static int WEIRD_EDGE_GAP = 3;
   Group confSlide;
   boolean lastIn;
   List<Actor> highlights = new ArrayList<>();
   private static final Color LEVELUP_COL = Colours.make(88, 138, 32);

   public ChoicePhase(ChoiceType choiceType, List<Choosable> options) {
      this(choiceType, options, null);
   }

   public ChoicePhase(ChoiceType choiceType, List<Choosable> options, String topMessage) {
      this.choiceType = choiceType;
      this.options = options;
      this.topMessage = topMessage;
   }

   public ChoicePhase(String data) {
      String[] parts = data.split(";");
      if (parts.length == 3) {
         this.choiceType = new ChoiceType(parts[0]);
         this.options = ChoosableUtils.deserialiseList(parts[1]);
         this.topMessage = parts[2];
      } else {
         if (parts.length != 2) {
            throw new RuntimeException("Error making choice phase: " + data);
         }

         this.choiceType = new ChoiceType(parts[0]);
         this.options = ChoosableUtils.deserialiseList(parts[1]);
         this.topMessage = null;
      }
   }

   public static int getMaxHeight() {
      return com.tann.dice.Main.height - 20;
   }

   @Override
   public void activate() {
      if (this.options != null && !this.options.isEmpty()) {
         if (this.options.size() == 1 && this.choiceType.cs == ChoiceType.ChoiceStyle.Number) {
            Choosable c = this.options.get(0);
            ChoosableUtils.checkedOnChoose(c, this.getContext(), "trying to autochoose");
            if (!(c instanceof RandomTieredChoosable) && !(c instanceof RandomTieredChoosableRange) && !ChoosableUtils.skipRandomReveal(c)) {
               PhaseManager.get().pushPhaseNext(new RandomRevealPhase(c));
            }

            PhaseManager.get().popPhase(ChoicePhase.class);
         } else {
            Choosable first = this.options.get(0);
            this.playChooseSound(first, false);
            boolean heroes = this.isHeroes();
            boolean items = first instanceof Item;
            boolean hasBorder = !heroes && this.options.size() > 1;
            int edge = hasBorder ? 3 : 0;
            int gap = hasBorder ? 2 : 0;
            Pixl p = new Pixl(gap, edge);
            if (hasBorder) {
               p.border(Colours.grey);
            }

            int maxHeight = getMaxHeight();
            int available = maxHeight - edge * 2;
            Actor topText = this.makeTopText(heroes, this.topMessage);
            if (topText != null) {
               p.actor(topText).row(edge);
               available = (int)(available - (topText.getHeight() + edge));
            }

            List<Actor> choiceActors = this.makeChoiceActors(heroes);
            p.actor(this.makeChoiceOffer(heroes, items, choiceActors, available));
            Group g = p.pix();
            if (g.getHeight() > maxHeight && !com.tann.dice.Main.isPortrait() && heroes && com.tann.dice.Main.width > choiceActors.get(0).getWidth() * 2.05F) {
               Pixl ppp = new Pixl(2, 0);

               for (int i = 0; i < choiceActors.size(); i++) {
                  Actor a = choiceActors.get(i);
                  if (i == choiceActors.size() - 2) {
                     ppp.row();
                  }

                  ppp.actor(a, com.tann.dice.Main.width);
               }

               g = ppp.pix();
            }

            switch (this.choiceType.cs) {
               case UpToNumber:
                  StandardButton confirmButton = new StandardButton("Confirm");
                  confirmButton.setRunnable(new Runnable() {
                     @Override
                     public void run() {
                        ChoicePhase.this.choose(ChoicePhase.this.choiceType.currentChoices, true);
                     }
                  });
                  this.choiceGroup = new Pixl().actor(g).actor(confirmButton).pix();
                  break;
               case Optional:
                  ChoiceDialog cd = new ChoiceDialog(Arrays.asList(g), ChoiceDialog.ChoiceNames.AcceptDecline, new Runnable() {
                     @Override
                     public void run() {
                        ChoicePhase.this.choose(ChoicePhase.this.options, true);
                     }
                  }, new Runnable() {
                     @Override
                     public void run() {
                        ChoicePhase.this.endPhase();
                     }
                  });
                  this.choiceGroup = cd;
                  break;
               default:
                  this.choiceGroup = g;
            }

            this.toRemove = this.choiceGroup;
            boolean scrollpaned = this.choiceGroup.getHeight() > maxHeight;
            if (scrollpaned) {
               ScrollPane sp = Tann.makeScrollpane(this.choiceGroup);
               sp.setWidth(this.choiceGroup.getWidth() + 6.0F);
               sp.setHeight(maxHeight);
               com.tann.dice.Main.stage.setScrollFocus(sp);
               this.toRemove = sp;
            }

            if (!tutLevelled()) {
               Color c = Colours.withAlpha(Colours.dark, 0.85F).cpy();
               Rectactor r = new Rectactor((int)(com.tann.dice.Main.width * 2.2F), (int)(com.tann.dice.Main.height * 2.2F), null, c);
               this.toRemove = Tann.makeGroup(this.toRemove);
               this.toRemove.addActor(r);
               r.setZIndex(0);
               Tann.center(r);
               r.setTouchable(Touchable.disabled);
               this.toRemove.setTouchable(Touchable.childrenOnly);
            }

            DungeonScreen.get().addActor(this.toRemove);
            if (first instanceof LevelupHeroChoosable && !com.tann.dice.Main.isPortrait() && this.toRemove.getWidth() < com.tann.dice.Main.width * 0.38F) {
               this.toRemove.toBack();
            }

            Tann.center(this.toRemove);
            if (com.tann.dice.Main.isPortrait() && this.toRemove.getX() < 24.0F && treatAsLarge(this.toRemove)) {
               this.toRemove.setX(com.tann.dice.Main.width - this.toRemove.getWidth() - WEIRD_EDGE_GAP);
            }

            this.toRemove.setY(getShowY(this.toRemove));
            this.addRerollButtonMaybe(this.choiceGroup);
         }
      } else {
         boolean n = this.options == null;
         PhaseManager.get().pushPhaseNext(new MessagePhase(n ? "null choice" : "empty choice"));
         PhaseManager.get().popPhase(this.getClass());
         this.deactivate();
      }
   }

   private static boolean tutLevelled() {
      return com.tann.dice.Main.self().settings.hasAttemptedLevel();
   }

   private boolean isHeroes() {
      Choosable first = this.options.get(0);
      return first instanceof LevelupHeroChoosable;
   }

   private static boolean treatAsLarge(Actor a) {
      return a.getHeight() > com.tann.dice.Main.height * 0.45F;
   }

   public static int getShowY(Actor toRemove) {
      if (com.tann.dice.Main.isPortrait()) {
         int notch = com.tann.dice.Main.self().notch(0);
         if (treatAsLarge(toRemove)) {
            return toRemove.getHeight() < com.tann.dice.Main.height - notch
               ? (int)(com.tann.dice.Main.height - notch - toRemove.getHeight() - WEIRD_EDGE_GAP)
               : 0;
         } else {
            return (int)Math.min(
               (float)(com.tann.dice.Main.height - PortBoard.getPortboardHeight()),
               com.tann.dice.Main.height - toRemove.getHeight() - com.tann.dice.Main.self().notch(0)
            );
         }
      } else {
         return (int)(getMaxHeight() / 2 - toRemove.getHeight() / 2.0F) + 2;
      }
   }

   private void addRerollButtonMaybe(Group choiceGroup) {
      final DungeonContext dc = this.getContext();
      final ContextConfig cc = dc.getContextConfig();
      if (this.options.size() != 2) {
         if (dc.isFirstLevel() && dc.getContextConfig().usesAnticheese()) {
            final AnticheeseData acd = dc.getContextConfig().getAnticheese();
            final boolean rerollAvailable = acd == null || acd.canReroll();
            final boolean countsAsLoss = acd != null && acd.rerollCountsAsLoss();
            Color rerollColour = rerollAvailable ? Colours.light : Colours.grey;
            Actor sb = new Pixl(0, 3).border(Colours.grey).image(Images.flaff, rerollColour).pix();
            choiceGroup.addActor(sb);
            int rollGap = 0;
            sb.setPosition(choiceGroup.getWidth() - sb.getWidth() - 0.0F, choiceGroup.getHeight() - sb.getHeight() - 0.0F);
            sb.addListener(
               new TannListener() {
                  @Override
                  public boolean action(int button, int pointer, float x, float y) {
                     if (rerollAvailable) {
                        String message = "Reroll starting party and curse options?";
                        if (countsAsLoss) {
                           message = message + "[n][red]This will count as a loss because you have already rerolled once without playing";
                        }

                        AnticheeseData tmp = acd;
                        if (tmp == null) {
                           tmp = dc.getContextConfig().getAnticheese();
                        }

                        if (tmp == null) {
                           Sounds.playSound(Sounds.error);
                           return true;
                        }

                        final AntiCheeseRerollInfo acri = tmp.getRerollInfo();
                        if (GameStart.shouldUsePartyLayout(cc)) {
                           GameStart.startWithPLTChoice(dc.getContextConfig(), acri, countsAsLoss);
                        } else {
                           ChoiceDialog cd = new ChoiceDialog(Arrays.asList(new TextWriter(message, 120)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
                              @Override
                              public void run() {
                                 dc.getContextConfig().anticheeseReroll();
                                 DungeonScreen.get().restart(countsAsLoss, acri);
                              }
                           }, new Runnable() {
                              @Override
                              public void run() {
                                 com.tann.dice.Main.getCurrentScreen().popAllMedium();
                              }
                           });
                           com.tann.dice.Main.getCurrentScreen().push(cd, 0.8F);
                           Tann.center(cd);
                        }
                     } else {
                        String text = "Reach fight 3 on this mode or fight 10 on some other modes to reroll again.[n][n2]"
                           + (
                              com.tann.dice.Main.demo
                                 ? Mode.CUSTOM.getTextButtonName() + " mode available in full version"
                                 : (
                                    UnUtil.isLocked(Mode.CUSTOM)
                                       ? "Unlock " + Mode.CUSTOM.getTextButtonName() + " mode to pick any modifiers"
                                       : "You can play however you like in " + Mode.CUSTOM.getTextButtonName() + " mode."
                                 )
                           );
                        TextWriter tw = new TextWriter(text, 95);
                        Actor a = new Pixl(3, 3).border(Colours.grey).actor(tw).pix();
                        Sounds.playSound(Sounds.paper);
                        com.tann.dice.Main.getCurrentScreen().push(a, 0.6F);
                        Tann.center(a);
                     }

                     return true;
                  }
               }
            );
         }
      }
   }

   private List<Actor> makeChoiceActors(boolean heroes) {
      List<Actor> choiceActors = new ArrayList<>();

      for (int index = 0; index < this.options.size(); index++) {
         final Choosable c = this.options.get(index);
         boolean bigActor = this.options.size() == 1;
         Actor a = c.makeChoosableActor(bigActor, index);
         choiceActors.add(a);
         this.choiceActorMap.put(c, a);
         if (this.choiceType.cs != ChoiceType.ChoiceStyle.Optional) {
            Tann.addListenerFirst(a, new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  ChoicePhase.this.tapForChoiceToggle(c);
                  return true;
               }

               @Override
               public boolean info(int button, float x, float y) {
                  return true;
               }
            });
         }
      }

      return choiceActors;
   }

   private void tapForChoiceToggle(Choosable c) {
      if (this.isHeroes()) {
         com.tann.dice.Main.getSettings().setHasAttemptedLevel();
      }

      boolean validChoosable = this.choiceType.toggleChoice(c);
      this.updateCounter();
      if (this.choiceType.cs == ChoiceType.ChoiceStyle.PointBuy) {
         this.slidePNConfirm(validChoosable);
      } else if (validChoosable) {
         this.choose(this.choiceType.currentChoices, false);
      }

      this.sortHighlights();
   }

   private void updateCounter() {
      if (this.choiceCounter != null) {
         this.choiceCounter.setCurrent(this.getChosenValue());
      }
   }

   private int getChosenValue() {
      int total = 0;

      for (Choosable c : this.choiceType.currentChoices) {
         total += c.getTier();
      }

      return total;
   }

   private void slidePNConfirm(boolean in) {
      if (this.lastIn == in) {
         if (in) {
            if (this.confSlide != null) {
               this.confSlide.remove();
            }

            this.confSlide = this.makeConfSlide();
            com.tann.dice.Main.getCurrentScreen().addActor(this.confSlide);
            this.confSlide
               .setPosition(
                  (com.tann.dice.Main.width - this.confSlide.getWidth()) / 2.0F, Math.max(10.0F, this.toRemove.getY() - this.confSlide.getHeight() - 10.0F)
               );
         }
      } else {
         this.lastIn = in;
         Screen s = com.tann.dice.Main.getCurrentScreen();
         if (in) {
            if (this.confSlide != null) {
               this.confSlide.remove();
            }

            this.confSlide = this.makeConfSlide();
            this.confSlide.setPosition((com.tann.dice.Main.width - this.confSlide.getWidth()) / 2.0F, (int)(-this.confSlide.getHeight()));
         }

         this.confSlide.setTouchable(in ? Touchable.enabled : Touchable.disabled);
         int startX = (int)((com.tann.dice.Main.width - this.confSlide.getWidth()) / 2.0F);
         int startY = (int)(-this.confSlide.getHeight());
         int endY = (int)Math.max(10.0F, this.toRemove.getY() - this.confSlide.getHeight() - 10.0F);
         float dur = 0.22F;
         Interpolation terp = Interpolation.pow2Out;
         if (in) {
            s.addActor(this.confSlide);
            this.confSlide.setPosition(startX, startY);
            this.confSlide.addAction(Actions.sequence(PixAction.moveTo(startX, endY, dur, terp)));
         } else {
            this.confSlide.addAction(Actions.sequence(PixAction.moveTo(startX, startY, dur, terp), Actions.removeActor()));
         }
      }
   }

   private Group makeConfSlide() {
      StandardButton reset = new StandardButton(Images.ui_cross, Colours.grey);
      reset.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            ChoicePhase.this.clearChoices();
            ChoicePhase.this.slidePNConfirm(false);
            return true;
         }
      });
      StandardButton conf = new StandardButton(Images.tick, Colours.green);
      conf.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            ChoicePhase.this.choose(ChoicePhase.this.choiceType.currentChoices, true);
            ChoicePhase.this.slidePNConfirm(false);
            return true;
         }
      });
      return new Pixl(6, 6)
         .text("[b]" + Words.getTierString(this.getChosenValue(), true) + " [grey]/[cu] " + Words.getTierString(this.choiceType.v, true))
         .row()
         .border(Colours.dark, Colours.green, 2)
         .actor(reset)
         .actor(conf)
         .pix();
   }

   private Actor makeChoiceOffer(boolean heroes, boolean items, List<Actor> choiceActors, int maxHeight) {
      int margin = 3;
      int width = (int)Math.min((float)(com.tann.dice.Main.width - 40), com.tann.dice.Main.width * 0.8F);
      Pixl p;
      if (heroes) {
         p = new Pixl(2, 0);
         int sz = choiceActors.size();
         if (sz > 4) {
            p.actor(Tann.layoutMinArea(choiceActors.subList(0, choiceActors.size() - 2), 2));
            p.row().listActor(choiceActors.subList(choiceActors.size() - 2, choiceActors.size()), 2);
         } else if (sz == 4) {
            for (int i = 0; i < choiceActors.size(); i++) {
               Actor a = choiceActors.get(i);
               if (i != 0 && i != choiceActors.size() - 1) {
                  p.row();
               }

               p.actor(a);
            }
         } else {
            p.rowedActors(choiceActors);
         }
      } else if (items) {
         p = new Pixl(3);
         p.actor(Tann.layoutMinArea(choiceActors.subList(0, choiceActors.size() - 1), 3, width, maxHeight));
         p.row().actor(choiceActors.get(choiceActors.size() - 1));
      } else {
         p = new Pixl(0, margin);
         p.actor(Tann.layoutMinArea(choiceActors, 3, width, maxHeight));
      }

      return p.pix(1);
   }

   private Actor makeTopText(boolean heroes, String topMessage) {
      String start = "";
      if (topMessage != null) {
         start = "[notranslate]" + com.tann.dice.Main.t(topMessage) + "[cu]";
      }

      if (this.choiceType.cs == ChoiceType.ChoiceStyle.PointBuy) {
         this.choiceCounter = new ChoiceCounter(this.choiceType.v);
         if (start.isEmpty()) {
            start = "[text]Choose " + ChoosableUtils.describeList(this.options);
         }

         return new Pixl().text(start).gap(3).actor(this.choiceCounter).pix();
      } else {
         String ctd = this.choiceType.getDescription(this.options);
         if (!heroes && ctd != null) {
            if (!start.isEmpty()) {
               start = start + "  ";
            }

            return new Pixl().text(start + "[notranslate][text]" + com.tann.dice.Main.t(this.choiceType.getDescription(this.options))).pix();
         } else {
            return null;
         }
      }
   }

   private void sortHighlights() {
      for (Actor a : this.highlights) {
         a.remove();
      }

      this.highlights.clear();
      int border = 0;

      for (Choosable c : this.choiceType.currentChoices) {
         Actor a = this.choiceActorMap.get(c);
         Rectactor r = new Rectactor((int)(a.getWidth() + 0.0F), (int)(a.getHeight() + 0.0F), Colours.light);
         this.highlights.add(r);
         float x = a.getX();
         float y = a.getY();

         for (Group g = a.getParent(); g != null && g != this.choiceGroup; g = g.getParent()) {
            x += g.getX();
            y += g.getY();
         }

         r.setTouchable(Touchable.disabled);
         r.setPosition(x - 0.0F, y - 0.0F);
         this.choiceGroup.addActor(r);
         r.toFront();
      }
   }

   @Override
   public void deactivate() {
      if (this.toRemove != null) {
         this.toRemove.remove();
      }

      this.slidePNConfirm(false);
   }

   protected void choose(final List<Choosable> chosen, boolean confirm) {
      if (!this.choiceType.checkValid(confirm)) {
         Sounds.playSound(Sounds.flap);
      } else {
         if (chosen.size() == 1 && chosen.get(0) == ModifierLib.byName("skip")) {
            confirm = true;
         }

         if (!confirm) {
            List<Actor> actors = this.getConfirmActors(chosen);
            String msg = "[green]Confirm " + Words.plural("choice", chosen.size());
            if (chosen.size() == 1 && chosen.get(0) instanceof SkipChoosable && hasLevelTwoLevelups(this.options)) {
               msg = "[purple]Warning- not recommended";
            }

            ChoiceDialog choiceDialog = new ChoiceDialog(msg, actors, ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
               @Override
               public void run() {
                  ChoicePhase.this.choose(chosen, true);
                  com.tann.dice.Main.getCurrentScreen().popAllLight();
                  com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
                  Sounds.playSound(Sounds.pip);
               }
            }, new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.getCurrentScreen().popAllLight();
                  com.tann.dice.Main.getCurrentScreen().pop(ChoiceDialog.class);
                  Sounds.playSound(Sounds.pop);
               }
            });
            choiceDialog.setPopRunnable(new Runnable() {
               @Override
               public void run() {
                  ChoicePhase.this.clearChoices();
               }
            });
            Screen s = com.tann.dice.Main.getCurrentScreen();
            s.popAllLight();
            s.push(choiceDialog, true, true, false, 0.7F);
            Tann.center(choiceDialog);
         } else {
            if (chosen.size() > 0) {
               this.playChooseSound(chosen.get(0), true);
            } else {
               TannLog.error("Something weird with " + chosen);
            }

            FightLog f = this.getFightLog();
            DungeonContext dc = f.getContext();
            List<Choosable> rejected = new ArrayList<>(this.options);
            rejected.removeAll(chosen);

            for (Choosable c : rejected) {
               c.onReject(dc);
            }

            boolean gainedItem = false;
            int index = 0;
            if (chosen.size() == 1) {
               index = this.options.indexOf(chosen.get(0));
            }

            ChoosableUtils.checkedOnChoose(chosen, dc, "trying to regular choose", index);
            if (gainedItem) {
               PhaseManager pm = PhaseManager.get();
               boolean hasAlready = pm.has(LevelEndPhase.class);
               if (!hasAlready) {
                  if (pm.has(EnemyRollingPhase.class)) {
                     pm.pushPhaseBefore(new LevelEndPhase(true), EnemyRollingPhase.class);
                  } else {
                     pm.pushPhaseAfter(new LevelEndPhase(true), ChoicePhase.class);
                  }
               }
            }

            this.endPhase();
         }
      }
   }

   private static boolean hasLevelTwoLevelups(List<Choosable> options) {
      for (Choosable option : options) {
         if (option instanceof LevelupHeroChoosable && ((LevelupHeroChoosable)option).getHeroType().level == 2) {
            return true;
         }
      }

      return false;
   }

   private void clearChoices() {
      this.choiceType.clearChoices();
      this.sortHighlights();
      this.updateCounter();
   }

   private List<Actor> getConfirmActors(List<Choosable> chosen) {
      List<Actor> actors = new ArrayList<>();
      if (chosen.size() == 1) {
         Choosable c1 = chosen.get(0);
         if (ChoosableUtils.isDefinitelySingle(c1) && c1 instanceof LevelupHeroChoosable) {
            HeroType ht = ((LevelupHeroChoosable)c1).getHeroType();
            Hero h = DungeonScreen.get().getDungeonContext().getParty().getHeroFor(ht, this.options.indexOf(c1));
            Pixl p = new Pixl(2);
            p.actor(new EntPanelInventory(h, false));
            boolean vert = com.tann.dice.Main.isPortrait();
            if (vert) {
               p.row();
            }

            p.actor(new ImageActor(vert ? Images.arrowDown : Images.arrowRight, ht.heroCol.col));

            for (Choosable c : chosen) {
               if (vert) {
                  p.row();
               }

               p.actor(c.makeChoosableActor(true, this.options.indexOf(c)));
            }

            actors.add(p.pix());
            return actors;
         }
      }

      for (Choosable c : chosen) {
         actors.add(c.makeChoosableActor(true, this.options.indexOf(c)));
      }

      if (ChoiceDialog.isTooBig(actors)) {
         actors.clear();

         for (Choosable c : chosen) {
            actors.add(c.makeChoosableActor(false, this.options.indexOf(c)));
         }
      }

      return actors;
   }

   private void endPhase() {
      PhaseManager.get().popPhase(this.getClass());
      this.getContext().specialCachedAchievementCheck();
      DungeonScreen.get().save();
   }

   private void playChooseSound(Choosable ch, boolean choose) {
      if (ch instanceof LevelupHeroChoosable) {
         Sounds.playSound(Sounds.boost);
      }

      if (ch instanceof Item) {
         Sounds.playSound(Sounds.chooseItem);
      }

      if (ch instanceof Modifier) {
         ((Modifier)ch).playChooseSound();
      }

      if (ch instanceof ReplaceChoosable) {
         Sounds.playSound(Sounds.clink);
      }
   }

   @Override
   public String serialise() {
      return "c" + this.choiceType.toSaveString() + ";" + ChoosableUtils.serialiseList(this.options) + (this.topMessage == null ? "" : ";" + this.topMessage);
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return getLevelEndButtonInternal(this.getFirstCh());
   }

   private static StandardButton getLevelEndButtonInternal(Choosable ff) {
      if (ff == null) {
         return new StandardButton("?n?");
      } else if (ff instanceof LevelupHeroChoosable) {
         return new StandardButton(Images.phaseLevelupIcon, LEVELUP_COL, 53, 20);
      } else if (ff instanceof Item) {
         return new StandardButton(Images.phaseLootIcon, Colours.orange, 53, 20);
      } else if (ff instanceof Modifier) {
         Color col = ff.getColour();
         return new StandardButton(((Modifier)ff).getLevelEndButtonIcon(), col, 53, 20);
      } else if (ff instanceof RandomTieredChoosable) {
         Color c = ff.getTier() > 0 ? Colours.green : Colours.purple;
         return new StandardButton(Images.question, c, c, 53, 20);
      } else if (ff instanceof OrChoosable) {
         Color c = Colours.yellow;
         return new StandardButton(Images.reroll, Colours.z_white, c, 53, 20);
      } else {
         return ff instanceof ReplaceChoosable
            ? getLevelEndButtonInternal(((ReplaceChoosable)ff).gain)
            : new StandardButton("[purple]Choose " + Words.capitalsOnly(ff.getClass().getSimpleName()), Colours.purple, 53, 20);
      }
   }

   private Choosable getFirstCh() {
      return this.options.isEmpty() ? null : this.options.get(0);
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }

   @Override
   public Color getLevelEndColour() {
      if (this.options.size() == 0) {
         return Colours.pink;
      } else {
         Choosable c = this.options.get(0);
         if (c instanceof Modifier) {
            return ((Modifier)c).getBorderColour();
         } else {
            switch (c.getType()) {
               case Levelup:
                  return LEVELUP_COL;
               case Item:
               case Modifier:
                  return Colours.orange;
               default:
                  return Colours.pink;
            }
         }
      }
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean isPositive() {
      Choosable o = this.options.get(0);
      return o.isPositive();
   }

   public List<Choosable> getOptions() {
      return this.options;
   }

   @Override
   public boolean canFlee() {
      return true;
   }

   @Override
   public boolean keyPress(int keycode) {
      if (com.tann.dice.Main.getCurrentScreen().getTopActualActor() instanceof ChoiceDialog) {
         return false;
      } else {
         int index = Tann.getDigit(keycode);
         if (index >= 0 && index < this.options.size()) {
            Choosable chosen = this.options.get(index);
            this.tapForChoiceToggle(chosen);
            return true;
         } else {
            return super.keyPress(keycode);
         }
      }
   }

   @Override
   public boolean isInvalid() {
      for (Choosable option : this.options) {
         if (ChoosableUtils.isMissingno(option)) {
            return true;
         }
      }

      return PRNRichText.invalid(this.topMessage);
   }
}
