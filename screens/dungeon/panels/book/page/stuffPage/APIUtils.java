package com.tann.dice.screens.dungeon.panels.book.page.stuffPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHeroItem;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.item.facade.FacadeUtils;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeCache;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ConcisePanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.screens.dungeon.panels.threeD.DieSpinner;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.CopyButtonHolder;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.resolver.AnyDescResolver;
import com.tann.dice.util.ui.resolver.MetaResolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import com.tann.dice.util.ui.standardButton.StandardButtonStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class APIUtils {
   private static final StandardButtonStyle STYLE = StandardButtonStyle.SimpleSquare;
   private static final String[] PAGES = new String[]{"info", "api", "api-2", "search"};
   public static final String ANY = "[yellow]a[cu][orange]n[cu][grey]y[cu]";
   public static final int CONTENT_GEN_AMT_SMALL = 6;
   public static final int CONTENT_GEN_AMT_LARGE = 20;

   public static Actor makeMetaApiPage(BookPage junkPage, int contentWidth) {
      String s = com.tann.dice.Main.getSettings().getLastTextmodPage();
      if (s != null && !s.isEmpty()) {
         if (s.equalsIgnoreCase("1")) {
            return makeMetaApiPage(junkPage, 0, contentWidth);
         }

         if (s.equalsIgnoreCase("2")) {
            return makeMetaApiPage(junkPage, 1, contentWidth);
         }

         if (s.startsWith("3")) {
            if (s.length() > 1) {
               String pipeString = s.substring(1);
               return makeTMPageAPI2(contentWidth, junkPage, getPipe(pipeString));
            }

            return makeMetaApiPage(junkPage, 3, contentWidth);
         }
      }

      return makeMetaApiPage(junkPage, 0, contentWidth);
   }

   private static Pipe getPipe(String pipeString) {
      for (PipeType value : PipeType.values()) {
         for (Pipe content : value.contents) {
            if (content.getIdTag().equalsIgnoreCase(pipeString)) {
               return content;
            }
         }
      }

      return PipeType.Hero.getDefaultShownPipe();
   }

   public static Actor makeMetaApiPage(BookPage junkPage, int page, int contentWidth) {
      return makeApiPageIndex(page, contentWidth, junkPage);
   }

   private static Actor makeApiPageIndex(int pageIndex, int contentWidth, BookPage page) {
      switch (pageIndex) {
         case 0:
         default:
            return makeTMPageInfo(contentWidth, page);
         case 1:
            return makeTMPageAPI1(contentWidth, page);
         case 2:
            return makeTMPageAPI2(contentWidth, page, PipeType.Modifier.getDefaultShownPipe());
      }
   }

   private static Pixl makeTopPixl(int page, final BookPage junkPage, final int contentWidth) {
      boolean TINY = contentWidth < 160;
      int GAP = TINY ? 1 : 3;
      Pixl p = new Pixl();
      if (com.tann.dice.Main.self().translator.shouldTranslate()) {
         p.text("([red]TextMod[text] is only available in English.)").row(5);
      }

      for (int i = 0; i < PAGES.length; i++) {
         boolean selected = page == i;
         StandardButton sb = new StandardButton("[notranslate]" + (selected ? "[light]" : "[grey]") + PAGES[i]).setStyle(STYLE);
         if (TINY) {
            sb.makeTiny();
         }

         final int finalI = i;
         sb.setRunnable(new Runnable() {
            @Override
            public void run() {
               if (finalI == APIUtils.PAGES.length - 1) {
                  APIUtils.showSearch();
               } else {
                  Sounds.playSound(Sounds.pipSmall);
                  junkPage.showThing(APIUtils.makeMetaApiPage(junkPage, finalI, contentWidth));
               }
            }
         });
         p.actor(sb);
         if (i < PAGES.length - 1) {
            p.gap(GAP);
         }
      }

      Actor a = p.pix();
      int gap = (int)((contentWidth - a.getWidth()) / 2.0F);
      return new Pixl(2).gap(gap).actor(a).gap(gap).row();
   }

   public static void showDescSearch() {
      (new AnyDescResolver() {
         @Override
         public void resolve(Object o) {
            Actor a = new Pixl().text("??").pix();
            if (o instanceof Choosable) {
               Choosable ch = (Choosable)o;
               a = ch.makeChoosableActor(true, 0);
            } else if (o instanceof Keyword) {
               Keyword k = (Keyword)o;
               a = KUtils.makeActor(k, null);
            } else if (o instanceof MonsterType) {
               a = new EntPanelInventory(((MonsterType)o).makeEnt()).withoutDice();
            }

            com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.8F);
         }
      }).activate();
   }

   public static void showSearch() {
      (new MetaResolver() {
         @Override
         public void resolve(Object o) {
            Actor a = new Pixl().text("??").pix();
            if (o instanceof Choosable) {
               Choosable ch = (Choosable)o;
               a = ch.makeChoosableActor(true, 0);
            } else if (o instanceof Keyword) {
               Keyword k = (Keyword)o;
               a = KUtils.makeActor(k, null);
            } else if (o instanceof MonsterType) {
               a = new EntPanelInventory(((MonsterType)o).makeEnt()).withoutDice();
            }

            com.tann.dice.Main.getCurrentScreen().pushAndCenter(a, 0.8F);
         }
      }).activate();
   }

   public static Actor makeTMPageInfo(int contentWidth, BookPage page) {
      savePage("1");
      int pageIndex = 0;
      Pixl p = makeTopPixl(0, page, contentWidth);
      String exampleHeroString = "thief.hp.50.n.Rita.i.longbow.hue.70";
      String exampleHeroStringColoured = "[orange]thief[cu][red].hp.50[cu][green].n.Rita[cu][grey].i.longbow[cu][blue].hue.70";
      String colTag = "[text]";
      int textWidth = Math.min(110, (int)(contentWidth * 0.45F));
      List<Actor> actors = new ArrayList<>();
      actors.add(textIt(colTag + "[red]TextMod[cu]" + " is integrated cross-platform 'modding' by manipulating text.", textWidth));
      actors.add(
         new Pixl(1)
            .actor(textIt("Use your device's [light]clipboard[cu] to copy/paste/share.", textWidth))
            .row()
            .actor(TextUrl.make("online resource", "https://tann.fun/games/dice/textmod/"))
            .pix()
      );
      actors.add(
         new Pixl(1)
            .text("[notranslate][orange]thief[cu][red].hp.50[cu][green].n.Rita[cu][grey].i.longbow[cu][blue].hue.70", textWidth)
            .row()
            .actor(HeroTypeLib.byName("thief.hp.50.n.Rita.i.longbow.hue.70").makeEnt().getDiePanel().withoutDice())
            .pix()
      );
      actors.add(
         textIt(
            "It's a bit like code:[n][orange]thief[red].withHp(50)[n][green].withName('Rita')[n][grey].withItem('longbow')[n][blue].withHueShift(70)[text];",
            textWidth
         )
      );
      actors.add(textIt("You can [light]use[cu] textmod inventions into [blue]creative/[cu] modes. Or store them in Ledger-Pin.", textWidth));
      actors.add(textIt("For making complex things, it's recommended to use an external text-editor", textWidth));
      actors.add(
         textIt("[light]Warning: experimental[cu][n]It will be easy for you to crash the game. Be careful what you paste from the internet.", textWidth)
      );
      actors.add(FacadeUtils.makeCommunityEntryWithThanks(textWidth * 2));
      p.listActor(actors, 8, (int)(contentWidth * 0.98F));
      return p.pix(2);
   }

   private static void savePage(String extra) {
      com.tann.dice.Main.getSettings().setLastTextmodPage(extra);
   }

   private static Actor textIt(String s, int textWidth) {
      Actor a = new Pixl().text("[notranslateall]" + s, textWidth).pix();
      if (a.getHeight() < TannFont.font.getLineHeight() * 4) {
         a = new Pixl().text("[notranslateall]" + s, (int)(textWidth * 0.85F)).pix();
      }

      return a;
   }

   private static Actor makeTMPageAPI1(int contentWidth, BookPage page) {
      savePage("2");
      int pageIndex = 1;
      Pixl p = makeTopPixl(1, page, contentWidth);
      List<Actor> actors = Arrays.asList(
         makeDocumentation("modifier", Colours.purple, new ArrayList<>(PipeMod.pipes), page, contentWidth),
         makeDocumentation("hero", Colours.yellow, new ArrayList<>(PipeHero.pipes), page, contentWidth),
         makeDocumentation("monster", Colours.orange, new ArrayList<>(PipeMonster.pipes), page, contentWidth),
         makeDocumentation("item", Colours.grey, new ArrayList<>(PipeItem.pipes), page, contentWidth),
         makeDocumentation("texture", Colours.blue, Pipe.makeAllPipes(), page, contentWidth)
      );
      Group a = Tann.makeGroup(actors.get(0));
      int gap = 3;
      int ww = (int)(contentWidth - a.getWidth() - 15.0F);
      float ts = Tann.layoutMinArea(actors.subList(1, actors.size()), 3, ww, 9999, 10).getHeight();
      Group b = Tann.layoutMinArea(actors.subList(1, actors.size() - 1), 3, ww, 9999, 10);
      if (!(a.getHeight() > b.getHeight()) && ts != b.getHeight()) {
         a = new Pixl(3).actor(a).row().actor(actors.get(actors.size() - 1)).pix(18);
      } else {
         b = Tann.layoutMinArea(actors.subList(1, actors.size()), 3, ww, 9999, 10);
      }

      p.actor(a).gap(3).actor(b, contentWidth);
      String tap = com.tann.dice.Main.self().control.getSelectTapString();
      int pw = Math.min(110, (int)(contentWidth * 0.45F));
      Pixl inner = new Pixl(10);

      for (String s : Arrays.asList(
         "Each line is a way of creating something, grouped by the type of object it creates. [green]" + tap + " one to see examples.",
         "Chain together apis for complex creations. " + PipeRegexNamed.MODIFIER.getColDesc() + " can refer to any modifier, including ones you create",
         "[light]White text[cu] must be written exactly. Anything else is a token and should be replaced.",
         "- "
            + PipeRegexNamed.COLOUR.getColDesc()
            + " : hex color like [pink]d3e[cu][n]- "
            + PipeRegexNamed.ITEM.getColDesc()
            + " : any item[n]- "
            + PipeRegexNamed.ENTITY.getColDesc()
            + " : hero/monster[n]- "
            + PipeRegexNamed.UP_TO_FIFTEEN_HEX.getColDesc()
            + " : rng seed[n]- "
            + PipeRegexNamed.DIGIT.getColDesc()
            + " : 0-9",
         "Scroll up and use 'search' to test out ideas. Keep interesting things in Ledger-Pin to copy and tweak later.",
         new PipeHeroItem().document() + " is powerful, you can use custom items like [grey]'learn.harvest'[cu] or [grey]'t.slate'",
         "You can bracket things algebra-style if there are ordering issues. Many things just won't work though, try another way?",
         OptionLib.TEXTMOD_COMPLEX.c()
            ? "If you want to know about "
               + PipeRegexNamed.CHOOSABLE.getColDesc()
               + " or "
               + PipeRegexNamed.PHASE_STRING.getColDesc()
               + ", it's not explained here. Check textmod resources for community guide."
            : "This is only half of it, there's an option to enable the rest."
      )) {
         inner.actor(new TextWriter("[notranslateall]" + s, pw), contentWidth);
      }

      p.row().actor(inner.pix(2));
      p.row().actor(OptionLib.TEXTMOD_COMPLEX.makeCogActor());
      return p.pix(2);
   }

   public static Actor makeTMPageAPI2(final int contentWidth, final BookPage almanacPage, Pipe selectedPipe) {
      savePage("3" + selectedPipe.getIdTag());
      int pageIndex = 2;
      Pixl p = makeTopPixl(2, almanacPage, contentWidth);
      int maxWidth = (int)(contentWidth * 0.95F);
      PipeType type = selectedPipe.getPipeType();
      if (type == null) {
         com.tann.dice.Main.getSettings().setLastTextmodPage(null);
         return new Pixl().text("[notranslate][red]err: " + selectedPipe.getClass().getSimpleName()).pix();
      } else {
         for (final PipeType meta : PipeType.values()) {
            StandardButton sb = new StandardButton((meta == type ? "[light]" : "[grey]") + meta.name()).setStyle(STYLE);
            p.actor(sb, contentWidth);
            sb.setBorder(meta.col);
            if (meta != type) {
               sb.setRunnable(new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.pipSmall);
                     almanacPage.showThing(APIUtils.makeTMPageAPI2(contentWidth, almanacPage, meta.getDefaultShownPipe()));
                  }
               });
            }
         }

         p.row();
         Pixl ptmp = new Pixl(2);
         List<Pipe> contents = sortedPipesToView(type.contents);
         int index = 0;

         for (int i = 0; i < contents.size(); i++) {
            final Pipe pipe = contents.get(i);
            if (!shouldSkip(pipe)) {
               String preTag = "[grey]";
               String text = (pipe == selectedPipe ? "[light]" : (pipe.isComplexAPI() ? "[purple]" : "")) + Words.DOUBLE_ALPHABET.charAt(index);
               StandardButton tb = new StandardButton(preTag + text, type.col).setStyle(STYLE);
               tb.makeTiny();
               tb.setRunnable(new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.pipSmall);
                     almanacPage.showThing(APIUtils.makeTMPageAPI2(contentWidth, almanacPage, pipe));
                  }
               });
               ptmp.actor(tb, maxWidth);
               index++;
            }
         }

         p.actor(ptmp.pix(), contentWidth - 10);
         Actor ex = selectedPipe.getExtraActor();
         if (ex != null) {
            p.row().actor(ex);
         }

         p.row(3);
         p.actor(makeGenContent(selectedPipe, almanacPage, contentWidth));
         p.row(3).actor(OptionLib.SHOW_COPY.makeCogActor()).actor(OptionLib.TEXTMOD_COMPLEX.makeCogActor());
         return p.pix(2);
      }
   }

   private static List<Pipe> sortedPipesToView(List<Pipe> pipes) {
      List<Pipe> var1 = new ArrayList<>(pipes);
      if (!OptionLib.DISABLE_API_ORDER.c()) {
         Collections.sort(
            var1,
            new Comparator<Pipe>() {
               public int compare(Pipe o1, Pipe o2) {
                  if (o1.isTexturey() != o2.isTexturey()) {
                     return Boolean.compare(o1.isTexturey(), o2.isTexturey());
                  } else {
                     return o1.isComplexAPI() != o2.isComplexAPI()
                        ? Boolean.compare(o1.isComplexAPI(), o2.isComplexAPI())
                        : Boolean.compare(o2.showHigher(), o1.showHigher());
                  }
               }
            }
         );
      }

      return var1;
   }

   private static <T> Actor makeDocumentation(String title, Color col, List<Pipe> pipes, final BookPage page, final int contentWidth) {
      boolean texturey = title.equalsIgnoreCase("texture");
      int sectionMaxWidth = (int)(contentWidth * 0.5F) - 12;
      if (sectionMaxWidth < 50) {
         sectionMaxWidth = contentWidth;
      }

      int textWidth = sectionMaxWidth - 4;
      Pixl p = new Pixl(1, 2).border(col);
      p.text("[notranslate]" + TextWriter.getTag(col) + "[b]" + title).row(4);
      int actualIndex = 0;
      int startWidth = 10;
      pipes = sortedPipesToView(pipes);

      for (int i = 0; i < pipes.size(); i++) {
         final Pipe pipe = pipes.get(i);
         if (!shouldSkip(pipe) && pipe.isTexturey() == texturey && (!texturey || !pipe.document().contains("onster") && !pipe.document().contains("item"))) {
            Group a = new Pixl().text("[notranslate]" + (pipe.isComplexAPI() ? "[purple]" : "") + Words.DOUBLE_ALPHABET.charAt(actualIndex) + ")").pix();
            a.setWidth(10.0F);
            String doc = pipe.document();
            if (texturey) {
               doc = doc.replaceAll("hero", "[yellow]a[cu][orange]n[cu][grey]y[cu]");
            }

            Group b = new Pixl().actor(a).text("[notranslate][grey]" + doc.replace(".", ".[p][p][q]"), textWidth).pix();
            b.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  page.showThing(APIUtils.makeTMPageAPI2(contentWidth, page, pipe));
                  return true;
               }
            });
            if (b.getHeight() > 8.0F) {
               Color c = Colours.withAlpha(Colours.randomHashed(pipe.getClass().getSimpleName().hashCode() + pipe.document().hashCode()), 0.09F).cpy();
               Rectactor ra = new Rectactor(b, c);
               b.addActor(ra);
               ra.toBack();
            }

            p.actor(b).row();
            actualIndex++;
         }
      }

      return p.pix(8);
   }

   private static boolean shouldSkip(Pipe pipe) {
      return pipe.isHiddenAPI() || pipe instanceof PipeCache || pipe.isComplexAPI() && !OptionLib.TEXTMOD_COMPLEX.c();
   }

   private static Actor makeGenContent(Pipe pipe, BookPage almanacPage, int contentWidth) {
      return makeContent(pipe, almanacPage, contentWidth);
   }

   public static Actor makeContent(Pipe pipe, BookPage almanacPage, int contentWidth) {
      List<Object> l = pipe.examples(getContentGenAmt());
      if (l.isEmpty()) {
         return new Pixl().border(Colours.red).text("[notranslate]try again?").pix();
      } else {
         Object o = l.get(0);
         if (o instanceof HeroType) {
            return makeHeroes(pipe, pipe.examples(getContentGenAmt()), contentWidth);
         } else if (o instanceof MonsterType) {
            return makeMonsters(pipe, pipe.examples(getContentGenAmt()), contentWidth);
         } else if (o instanceof Item) {
            return makeItems(pipe, pipe.examples(getContentGenAmt()), contentWidth);
         } else {
            return (Actor)(o instanceof Modifier
               ? makeMods(pipe, pipe.examples(getContentGenAmt()), contentWidth)
               : new Pixl(5).border(Colours.pink).text("[notranslate]hmm").pix());
         }
      }
   }

   static Actor makeMods(Pipe pipe, List<Modifier> mods, int contentWidth) {
      List<Actor> actors = new ArrayList<>();

      for (int i = 0; i < mods.size(); i++) {
         Modifier m = mods.get(i);
         ConcisePanel cp = new ModifierPanel(m, false);
         actors.add(cp);
      }

      return makeGen(pipe, actors, contentWidth);
   }

   static Actor makeMonsters(Pipe pipe, List<MonsterType> ex, int contentWidth) {
      List<Actor> actors = new ArrayList<>();

      for (int i = 0; i < ex.size(); i++) {
         MonsterType mt = ex.get(i);
         EntPanelInventory dp = new EntPanelInventory(mt.makeEnt(), false);
         dp.removeDice();
         actors.add(dp);
      }

      return makeGen(pipe, actors, contentWidth);
   }

   static Actor makeHeroes(Pipe pipe, List<HeroType> ex, int contentWidth) {
      List<Actor> actors = new ArrayList<>();

      for (HeroType ht : ex) {
         if (ht == null) {
            TannLog.error("hmm ");
         } else {
            EntPanelInventory dp = new EntPanelInventory(ht.makeEnt(), false);
            dp.removeDice();
            actors.add(dp);
         }
      }

      return makeGen(pipe, actors, contentWidth);
   }

   static Actor makeItems(Pipe pipe, List<Item> ex, int contentWidth) {
      List<Actor> actors = new ArrayList<>();

      for (int i = 0; i < ex.size(); i++) {
         Item it = ex.get(i);
         ConcisePanel cp = new ItemPanel(it, false);
         actors.add(cp);
      }

      return makeGen(pipe, actors, contentWidth);
   }

   private static Actor makeGen(Pipe pipe, List<Actor> actors, int contentWidth) {
      Pixl p = new Pixl(1);
      p.text("[notranslate]" + pipe.document()).gap(4).row(5);

      for (int i = 0; i < actors.size(); i++) {
         if (actors.get(i) == null) {
            p.row(6);
         } else {
            p.actor(actors.get(i), contentWidth * 0.9F);
         }
      }

      return p.pix();
   }

   public static void addCopyButton(Group g, String s) {
      addCopyButton(g, s, null);
   }

   public static void addCopyButton(Group g, String toCopy, String extra) {
      addCopyButton(g, toCopy, extra, null);
   }

   public static void addCopyButton(Group g, final String toCopy, String extra, Vector2 delta) {
      if (!toCopy.equalsIgnoreCase("curse")) {
         final ImageActor a = new ImageActor(Images.copyButton);
         g.addActor(a);
         if (delta != null) {
            a.setPosition(delta.x, delta.y);
         }

         int sz = g.getChildren().size;
         int zi = sz;

         for (int i = 0; i < sz; i++) {
            if (g.getChild(i) instanceof DieSpinner) {
               zi = i;
               break;
            }
         }

         a.setZIndex(zi);
         a.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               if (x + y >= a.getWidth()) {
                  return false;
               } else {
                  ClipboardUtils.copyWithSoundAndToast(toCopy);
                  return true;
               }
            }
         });
         a.setName("copy");
      }
   }

   public static void refreshCopyButton() {
      List<Actor> copies = TannStageUtils.getActorsWithName("copy", com.tann.dice.Main.stage.getRoot());

      for (int i = copies.size() - 1; i >= 0; i--) {
         copies.get(i).remove();
      }

      if (OptionUtils.shouldShowCopy()) {
         List<Actor> all = TannStageUtils.getAllActors(com.tann.dice.Main.stage.getRoot());

         for (int i = 0; i < all.size(); i++) {
            Actor a = all.get(i);
            if (a instanceof CopyButtonHolder) {
               ((CopyButtonHolder)a).addCopyButton();
            }
         }
      }
   }

   public static int getContentGenAmt() {
      return OptionLib.GENERATE_50.c() ? 20 : 6;
   }
}
