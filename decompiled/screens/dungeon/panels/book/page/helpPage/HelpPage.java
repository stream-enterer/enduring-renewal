package com.tann.dice.screens.dungeon.panels.book.page.helpPage;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.TacticCostType;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.progress.chievo.unlock.Feature;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.SpecificSidesType;
import com.tann.dice.platform.control.Control;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.book.TopTab;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.Incantations;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Discord;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.HpGrid;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpPage extends BookPage {
   Map<HelpType, List<Actor>> snippets;
   public static int HELP_CONTENT_GAP = 4;

   public HelpPage(int width, int height) {
      super("[green]help", null, width, height);
   }

   @Override
   protected List<TopTab> getAllListItems() {
      List<TopTab> results = new ArrayList<>();

      for (HelpType type : HelpType.values()) {
         TopTab sbi = new TopTab(type, type.getColouredString(), getSideWidth());
         results.add(sbi);
      }

      return results;
   }

   @Override
   protected Actor getContentActorFromSidebar(Object typee, int contentWidth) {
      HelpType type = (HelpType)typee;
      if (this.snippets == null) {
         this.snippets = new HashMap<>();
      }

      List<Actor> snips = this.snippets.get(type);
      int padding = 0;
      int availableWidth = contentWidth - 0;
      if (snips == null) {
         snips = new ArrayList<>();
         this.snippets.put(type, snips);
         this.populateList(type, snips, availableWidth);
      }

      Pixl p = new Pixl(HELP_CONTENT_GAP, 0);

      for (int i = 0; i < snips.size(); i++) {
         Actor a = snips.get(i);
         if (i > 0) {
            p.row();
         }

         p.actor(a);
      }

      return p.pix(8);
   }

   public static String getClickTut() {
      Control cont = com.tann.dice.Main.self().control;
      return Words.capitaliseFirst(cont.getSelectTapString()) + " to select, " + cont.getInfoTapString().toLowerCase() + " for info";
   }

   private static StandardButton makeTutorialButton(final boolean reset) {
      StandardButton tutorialButton = new StandardButton(reset ? "[green]Reset Tutorial" : "[pink]Tut-sk");
      tutorialButton.setRunnable(new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pip);
            String msg;
            if (reset) {
               msg = "[green]Tutorial Reset";
               com.tann.dice.Main.getSettings().resetTutorial();
            } else {
               msg = "[orange]Tutorial Skipped";
               com.tann.dice.Main.getSettings().skipTutorial();
            }

            if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
               DungeonScreen.get().getTutorialManager().reset();
            }

            com.tann.dice.Main.getCurrentScreen().popAllMedium();
            com.tann.dice.Main.getCurrentScreen().showDialog(msg, Colours.yellow);
         }
      });
      return tutorialButton;
   }

   private static Actor makeTutActorIf() {
      return com.tann.dice.Main.getSettings().isTutorialEnabled() && !(com.tann.dice.Main.getSettings().getTutorialProgress() > 0.0F)
         ? null
         : makeTutorialButton(true);
   }

   private void populateList(HelpType type, List<Actor> snips, int contentWidth) {
      String selectTap = com.tann.dice.Main.self().control.getSelectTapString();
      String infoTap = com.tann.dice.Main.self().control.getInfoTapString();
      HelpSnippet.setWIDTH(contentWidth);
      switch (type) {
         case Basics:
            snips.add(new HelpSnippet("Roll dice, do battle, upgrade heroes, equip items"));
            snips.add(new HelpSnippet("Heroes and monsters are dice"));
            snips.add(new HelpSnippet(getClickTut()));
            snips.add(
               new HelpSnippet(Words.capitaliseFirst(com.tann.dice.Main.self().control.getSelectTapString()) + " outside of menus or popups to close them")
            );
            snips.add(new HelpSnippet("Defeated heroes return next fight with 1/2 hp"));
            snips.add(new HelpSnippet("[red]All heroes defeated[cu] -> [purple]You lose"));
            snips.add(new HelpSnippet("Try [green]easy[cu], it only adds a starting [green]blessing[cu]"));
            snips.add(new HelpSnippet("Use [light]undo[cu] to optimise your turns"));
            snips.add(new HelpSnippet("Focus on defeating enemies first"));
            snips.add(
               new HelpSnippet(
                  "To bypass unlocks, there is a checkbox for it in stuff->options->gameplay"
                     + (com.tann.dice.Main.demo ? " (other modes available only in full game)" : "")
               )
            );
            Pixl p = new Pixl(0);
            p.row(0).actor(new HelpSnippet("Stuck? maybe try learning by watching someone play:")).row(2);
            Pixl youtubers = new Pixl(1);

            for (Actor a : YoutuberPanel.makeAll()) {
               youtubers.actor(a, (int)(contentWidth * 0.999F), 1).gap(1);
            }

            p.actor(youtubers.pix(10));
            Actor a = p.pix(10);
            snips.add(a);
            snips.add(Discord.makeBadge());
            Tann.addIfNotNull(snips, makeTutActorIf());
            break;
         case Dice:
            snips.add(new HelpSnippet("Each hero and monster is represented by a dice"));
            snips.add(new HelpSnippet("If a hero is defeated during combat, their dice is not usable until next fight"));
            snips.add(new HelpSnippet("All dice have 6 sides which represent abilities that hero or monster can use"));
            snips.add(new HelpSnippet("You can levelup heroes into better heroes with different dice"));
            snips.add(new HelpSnippet("Or equip items to heroes to change their sides"));
            snips.add(new HelpSnippet("A hero's leftmost sides are usually better than their rightmost sides"));
            snips.add(new HelpSnippet("[light]Pips[cu] refers to the white bars showing the value of each side ([light][p][img][p][cu]).", Images.pips));
            snips.add(new HelpSnippet("[light]N[cu] refers to the number of pips on the side"));
            snips.add(this.makeDicePositionExplain());
            break;
         case Rolling:
            snips.add(new HelpSnippet(selectTap + " [white][image][cu] in the bottom-left to reroll your dice", Images.reroll));
            snips.add(new HelpSnippet("You have 2 rerolls each round"));
            snips.add(new HelpSnippet(selectTap + " a dice to lock it while you reroll the others"));
            snips.add(new HelpSnippet(infoTap + " a dice to learn what it does"));
            snips.add(new HelpSnippet("Tap [grey][image][cu] in the bottom-right to accept all dice and continue", Images.tick));
            snips.add(new HelpSnippet("Dice auto-accept after your last reroll"));
            snips.add(new HelpSnippet(com.tann.dice.Main.self().control.getDoubleTapString() + " near dice to align them"));
            snips.add(new HelpSnippet("You can still undo back to the roll phase after confirming"));
            break;
         case Combat:
            snips.add(HpGrid.makeTutorial(999, 0));
            snips.add(new HelpSnippet("Incoming damage can be shielded, poison cannot"));
            snips.add(new HelpSnippet("You can kill " + Words.entName(false, false) + " to cancel their attack"));
            snips.add(
               new HelpSnippet(
                  "Use "
                     + Keyword.ranged.getColourTaggedString()
                     + " damage and area-of-effect abilities to kill "
                     + Words.entName(false, true)
                     + " at the back"
               )
            );
            snips.add(new HelpSnippet(infoTap + " a hero to see who is attacking them"));
            snips.add(new HelpSnippet("Undefeated heroes fully-heal between fights"));
            snips.add(new HelpSnippet("Defeated heroes return on half health next fight [grey](even if they revive)"));
            snips.add(new HelpSnippet("Enemies attempt to flee if your total hp is 10x theirs"));
            snips.add(new HelpSnippet("Allowing enemies to flee has no gameplay impact"));
            break;
         case Abilities:
            snips.addAll(
               Arrays.asList(
                  new HelpSnippet("[blue]Spells[cu] and [yellow]Tactics[cu] are both types of [blue]Ability"),
                  new HelpSnippet("If a hero is defeated, you can't use their abilities"),
                  makeSection(
                     "spells",
                     Colours.blue,
                     new Pixl(HELP_CONTENT_GAP)
                        .rowedActors(
                           Arrays.asList(
                              new HelpSnippet("Spells cost mana ([white][p][image][p][cu])", Images.mana),
                              new HelpSnippet("Gain mana mostly from dice"),
                              new HelpSnippet("You always have [purple]burst[cu] available"),
                              new HelpSnippet("The other spells come from your [red]red[cu] and [blue]blue[cu] heroes"),
                              new HelpSnippet("You can save " + Words.manaString(3) + " between turns")
                           )
                        )
                        .pix(8)
                  )
               )
            );
            if (UnUtil.isLocked(Feature.TACTICS)) {
               snips.add(new HelpSnippet("[grey]You haven't unlocked tactics yet, come back later"));
            } else {
               List<Actor> top = new ArrayList<>();
               List<Actor> icons = new ArrayList<>();
               List<Actor> bot = new ArrayList<>();
               top.addAll(
                  Arrays.asList(
                     new HelpSnippet("Tactics cost unused dice sides"),
                     new HelpSnippet("Heroes are used when they contribute to a tactic"),
                     new HelpSnippet("Eligible heroes are checked from top to bottom")
                  )
               );
               bot.addAll(
                  Arrays.asList(
                     new HelpSnippet("'heal pip' counts anything, like selfheal sides"),
                     new HelpSnippet("'3-pip side' means the side must have exactly 3 pips"),
                     new HelpSnippet("A single side can fulfil multiple costs")
                  )
               );
               Pixl over = new Pixl(10);
               List<Actor> acs = new ArrayList<>();

               for (TacticCostType value : TacticCostType.values()) {
                  Actor a = value.makeExpl();
                  if (value == TacticCostType.pips2) {
                     Actor img = ((Group)a).getChild(0);
                     img.addListener(new TannListener() {
                        @Override
                        public boolean action(int button, int pointer, float x, float y) {
                           return true;
                        }

                        @Override
                        public boolean info(int button, float x, float y) {
                           com.tann.dice.Main.self().control.textInput(new TextInputListener() {
                              public void input(String text) {
                                 Incantations.input(text);
                              }

                              public void canceled() {
                              }
                           });
                           return true;
                        }
                     });
                  }

                  acs.add(a);
               }

               over.listActor(acs.subList(0, acs.size() / 2), 2, 2, 8).listActor(acs.subList(acs.size() / 2, acs.size()), 2, 2, 8);
               icons.add(over.pix(10));
               snips.add(
                  makeSection(
                     "tactics",
                     Colours.yellow,
                     new Pixl(HELP_CONTENT_GAP)
                        .rowedActors(top)
                        .row()
                        .actor(new Pixl(2, 3).border(Colours.grey).text("Cost icons:").row().rowedActors(icons))
                        .row()
                        .rowedActors(bot)
                        .pix(8)
                  )
               );
            }
            break;
         case Tips:
            snips.addAll(com.tann.dice.Main.self().control.getTipsSnippets(contentWidth));
            snips.addAll(
               Arrays.asList(
                  new HelpSnippet("Items apply in order [grey](1 then 2)[cu]- try swapping two items on a hero"),
                  new HelpSnippet("Sometimes it's useful to keep dice early, try to play the turn, then undo and decide what to reroll"),
                  new HelpSnippet(selectTap + " the undo button in the bottom-left to undo any combat action"),
                  new HelpSnippet("If there's something you don't understand, try to investigate with undo"),
                  new HelpSnippet("Remember to check enemy passives"),
                  new HelpSnippet("You can swap around your [grey]items[cu] after each fight"),
                  new HelpSnippet("You can rename heroes if you " + selectTap.toLowerCase() + " on their name in 'inventory'"),
                  new HelpSnippet("All tier " + Words.getTierString(1) + " items are pretty bad, don't worry if you don't get a good one"),
                  new HelpSnippet("The best defence is killing a monster"),
                  new HelpSnippet("This game has a lot of random elements so sometimes a game is unwinnable"),
                  new HelpSnippet("Hexia OP")
               )
            );
            break;
         case Advanced:
            snips.add(this.makeLayering(contentWidth));
            snips.add(new HelpSnippet("For things like pain+cleave+engage, the pain only gets doubled if all targets are valid for engage"));
            snips.add(new HelpSnippet("Difficulty only affects starting modifier choice and how complex the level generation can be"));
            snips.add(new HelpSnippet("There is no levelup path. [grey]grey t1 hero[cu] -> [grey]grey t2 hero[cu]."));
            snips.add(new HelpSnippet("Enemies target randomly, but prefer targets who are not already dying"));
            snips.add(new HelpSnippet("Enemies with " + Keyword.heavy + " and " + Keyword.eliminate + " take into account incoming damage"));
            snips.add(new HelpSnippet("Some monsters and curses are rarer. This is mostly because they're strange."));
            snips.add(
               new HelpSnippet(
                  "Heroes/Items in the ledger are sorted based on pick %, except it's assumed you picked and rejected +3 times. This is so that e.g. 1/0 doesn't appear above 99/1"
               )
            );
            break;
         case Glossary:
            snips.add(glossary("[yellow]Character", "A hero or monster"));
            snips.add(glossary("[yellow]Hero", "Any character on the left, you control these"));
            snips.add(glossary("[yellow]Monster", "Any character on the right, defeat these to win"));
            snips.add(
               glossary("Dice", "Rolley cubes representing a hero/monster. This game doesn't use the singular 'die' because it's confusing with all the death.")
            );
            snips.add(glossary("Side", "Each dice has 6 sides"));
            snips.add(glossary("Pips", "The white bars on the right of each side showing its value ([light][p][img][p][cu]).", Images.pips));
            snips.add(glossary("Pips IRL", "[white][img]", Images.realPips));
            snips.add(glossary("[notranslate]N", "The number of pips on this side"));
            snips.add(glossary("[pink]Keyword", "A static ability that can be added to a side or spell, eg " + Keyword.poison.getColourTaggedString()));
            snips.add(glossary("[blue]Ability", "A spell or a tactic"));
            snips.add(glossary("[blue]Spell/Tactic", "A type of ability [grey](see help-abilities)"));
            snips.add(glossary("[orange]On-Hit", "An effect that triggers when attacked by a hero [grey](upon receiving unblocked non-ranged damage)"));
            snips.add(glossary("[purple]Modifier", "A global effect that changes the game rules"));
            snips.add(glossary("[purple]Blessing/Curse", "Positive/negative modifier"));
            snips.add(glossary("[purple]Difficulty", "Difficulties only affect your starting modifiers. Harder difficulties can generate more-complex enemies"));

            for (Difficulty d : Difficulty.values()) {
               if (!UnUtil.isLocked(d)) {
                  snips.add(glossary(d.getColourTaggedName(), d.getRules()));
               }
            }
      }
   }

   private Actor makeLayering(int contentWidth) {
      int PADDING = 2;
      int GAP = 5;
      contentWidth++;
      String layer3Sep = "/";
      if (com.tann.dice.Main.self().translator.getLanguageCode().equals("ru")) {
         layer3Sep = " и ";
      }

      String[] parts = new String[]{
         com.tann.dice.Main.t("[text]Base side[cu]"),
         com.tann.dice.Main.t("[yellow]My traits[cu]"),
         com.tann.dice.Main.t("[green]Blessings[cu]") + layer3Sep + com.tann.dice.Main.t("[purple]Curses[cu]"),
         com.tann.dice.Main.t("[grey]Item1[cu]"),
         com.tann.dice.Main.t("[grey]Item2...[cu]"),
         com.tann.dice.Main.t("[orange]Enemy traits[cu]"),
         com.tann.dice.Main.t("[blue]In-combat buffs[cu]"),
         com.tann.dice.Main.t("[light]Static keywords[cu]"),
         com.tann.dice.Main.t("[red]Targeted keywords")
      };
      List<Actor> acs = new ArrayList<>();

      for (int i = 0; i < parts.length; i++) {
         String part = parts[i];
         acs.add(new TextWriter("[notranslate]" + (i + 1) + ": " + part, contentWidth / 2));
      }

      int split = acs.size() / 2 + 1;
      return new Pixl(5, 2)
         .border(Colours.withAlpha(Colours.purple, 0.2F).cpy(), null, 0)
         .text("Effects layering for dice sides:")
         .row()
         .actor(new Pixl(2).rowedActors(acs.subList(0, split)).pix(8))
         .actor(new Pixl(2).rowedActors(acs.subList(split, acs.size())).pix(8))
         .row()
         .actor(new HelpSnippet("For example, buckle's [grey]shield 2 [cu][light]pristine[cu] with [grey]eye of horus[cu] uses layers 1, 4, 8 to reach 6 pips"))
         .row()
         .actor(new HelpSnippet("All sides are recalculated from layers 1-8 whenever anything happens (sort of)"))
         .pix(10);
   }

   public static Actor makeSection(String title, Color col, Actor contained) {
      String text = "[notranslate][b]" + TextWriter.getTag(col) + com.tann.dice.Main.t(title);
      TextWriter t = new TextWriter(text);
      int w = (int)Math.max(t.getWidth(), contained.getWidth());
      return new Pixl(3).actor(t).row().actor(makeSeparator(w, col)).row().actor(contained).row().actor(makeSeparator(w, col)).pix(8);
   }

   private static Actor makeSeparator(int width, Color col) {
      return new Rectactor(width, 1, col);
   }

   private Actor makeDicePositionExplain() {
      SpecificSidesType sst = SpecificSidesType.All;
      Group g = Tann.makeGroup(new ImageActor(sst.templateImage, Colours.light));
      List<TP<String, String>> sideShortLong = new ArrayList<>();
      sideShortLong.add(new TP<>("L", "leftmost"));
      sideShortLong.add(new TP<>("M", "middle"));
      sideShortLong.add(new TP<>("T", "top"));
      sideShortLong.add(new TP<>("B", "bottom"));
      sideShortLong.add(new TP<>("r", "right"));
      sideShortLong.add(new TP<>("R", "rightmost"));
      int px = EntSize.reg.getPixels();

      for (int i = 0; i < sst.sidePositions.length; i++) {
         TextWriter tw = new TextWriter("[notranslate][text]" + (String)sideShortLong.get(i).a);
         tw.setPosition((int)(sst.sidePositions[i].x + px / 2 - tw.getWidth() / 2.0F), (int)(sst.sidePositions[i].y + px / 2 - tw.getHeight() / 2.0F));
         g.addActor(tw);
      }

      Pixl tp = new Pixl(2);

      for (int i = 0; i < sideShortLong.size(); i++) {
         TP<String, String> t = sideShortLong.get(i);
         tp.text("[notranslate][text]" + t.a + ": " + com.tann.dice.Main.t(t.b)).row();
      }

      Actor texts = tp.pix(8);
      Pixl p = new Pixl();
      return p.gap(2).actor(g).gap(2).actor(texts).pix();
   }

   private static Actor glossary(String name, String def) {
      return glossary(name, def, null);
   }

   private static Actor glossary(String name, String def, TextureRegion image) {
      return new Pixl(2)
         .actor(new TextWriter("[notranslate][light]" + com.tann.dice.Main.t(name) + "[text]: " + com.tann.dice.Main.t(def), image, HelpSnippet.WIDTH - 4))
         .pix();
   }

   public void setDefault(Phase phase) {
      HelpType requested = phase.getHelpType();
      if (requested != null) {
         for (TopTab sbi : this.sideBar.getItems()) {
            if (sbi.getIdentifier() == requested) {
               this.openSidebar(sbi);
               break;
            }
         }
      }
   }
}
