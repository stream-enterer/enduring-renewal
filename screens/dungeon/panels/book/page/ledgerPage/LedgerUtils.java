package com.tann.dice.screens.dungeon.panels.book.page.ledgerPage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.EntTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.item.PipeItem;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeMod;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.mode.creative.pastey.PasteMode;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.ModifierLib;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.KillsStat;
import com.tann.dice.gameplay.progress.stats.stat.endOfFight.monsters.tracker.MonsterTrackerStat;
import com.tann.dice.gameplay.progress.stats.stat.pickRate.PickStat;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.DieSidePanel;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.book.page.BookPage;
import com.tann.dice.screens.dungeon.panels.book.views.EntityLedgerView;
import com.tann.dice.screens.dungeon.panels.book.views.HeroLedgerView;
import com.tann.dice.screens.dungeon.panels.book.views.ItemLedgerView;
import com.tann.dice.screens.dungeon.panels.book.views.MonsterLedgerView;
import com.tann.dice.screens.dungeon.panels.entPanel.AbilityPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.tp.TTP;
import com.tann.dice.util.ui.ClipboardUtils;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.resolver.AnyDescResolver;
import com.tann.dice.util.ui.resolver.MetaResolver;
import com.tann.dice.util.ui.resolver.Resolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LedgerUtils {
   public static final int BGG_FACTOR = 3;
   private static final int CONTENT_GAP = 10;
   private static final int GAP_AFTER_FOUND = 5;

   public static Group makeHeroGroup(Map<String, Stat> allMergedStats, int contentWidth) {
      Pixl heroesPix = new Pixl(0).forceWidth(contentWidth);
      Map<HeroCol, Map<Integer, List<HeroType>>> map = HeroTypeUtils.getSortedHeroes();
      int total = 0;
      int seen = 0;
      int maxLevel = 3;
      List<HeroType> seenHeroes = new ArrayList<>();
      long bannedColl = 0L;
      if (com.tann.dice.Main.getCurrentScreen() instanceof DungeonScreen) {
         DungeonContext dc = DungeonScreen.get().getDungeonContext();
         seenHeroes = dc.makeSeenHeroTypes(null);
         bannedColl = dc.getParty().getBannedCollisionBits(false);
      }

      for (HeroCol col : HeroCol.basics()) {
         Pixl colPixl = new Pixl();

         for (int level = 1; level <= maxLevel; level++) {
            List<HeroType> colHeroes = new ArrayList<>(map.get(col).get(level));
            Collections.sort(colHeroes, makeSorter(allMergedStats));

            for (int heroIndex = 0; heroIndex < colHeroes.size(); heroIndex++) {
               final HeroType ht = colHeroes.get(heroIndex);
               final boolean banned = HeroTypeUtils.bannedHeroTypeByCollision(ht, bannedColl);
               final boolean seenThisRun = seenHeroes.contains(ht);
               final int rejected;
               final int chosen;
               if (ht.skipStats()) {
                  chosen = 0;
                  rejected = 0;
               } else {
                  Stat s = allMergedStats.get(PickStat.nameFor(ht));
                  rejected = PickStat.val(s, true);
                  chosen = PickStat.val(s, false);
               }

               boolean visible = !UnUtil.isLocked(ht) && (chosen + rejected > 0 || level == 1);
               if (visible) {
                  seen++;
               }

               total++;
               HeroLedgerView hav = new HeroLedgerView(ht, visible);
               if (!addStandardListener(hav, !visible, ht)) {
                  hav.addListener(new TannListener() {
                     @Override
                     public boolean info(int button, float x, float y) {
                        Hero h = new Hero(ht);
                        EntPanelInventory dp = new EntPanelInventory(h);
                        if (ht.level > 1) {
                           TextWriter chosenWriter = new TextWriter(BookPage.getChosenString(chosen, rejected), 999, h.getColour(), 2);
                           dp.addActor(chosenWriter);
                           chosenWriter.setPosition((int)(dp.getWidth() / 2.0F - chosenWriter.getWidth() / 2.0F), (int)(dp.getHeight() - 1.0F));
                        }

                        if (seenThisRun) {
                           Actor tw = new Pixl(0, 3).border(Colours.purple).text("[purple]Seen this run").pix();
                           dp.addActor(tw);
                           Tann.center(tw);
                           tw.setY(-tw.getHeight() - 1.0F);
                        }

                        if (banned) {
                           Actor tw = new Pixl(0, 3).border(Colours.purple).text("Banned this run [blue](mana)").pix();
                           dp.addActor(tw);
                           Tann.center(tw);
                           tw.setY(-tw.getHeight() - 1.0F);
                        }

                        BookPage.push(dp);
                        return true;
                     }
                  });
               }

               colPixl.actor(hav, contentWidth - 10, 1).gap(1);
               if (hav.getWidth() * colHeroes.size() > contentWidth * 0.9F && heroIndex == colHeroes.size() / 2 - 1) {
                  colPixl.row(1);
               }

               if (seenThisRun) {
                  ImageActor ia = new ImageActor(Images.heroSeen);
                  hav.addActor(ia);
                  ia.setTouchable(Touchable.disabled);
               }

               if (banned && ht.heroCol != HeroCol.red && ht.heroCol != HeroCol.blue) {
                  ImageActor ia = new ImageActor(Images.heroBanned);
                  hav.addActor(ia);
                  ia.setTouchable(Touchable.disabled);
               }
            }

            if (level - 1 < maxLevel) {
               colPixl.row(7);
            }
         }

         heroesPix.row(10).actor(colPixl.pix());
      }

      return new Pixl(0)
         .forceWidth(contentWidth)
         .text(TextWriter.getTag(HeroType.getIdCol()) + seen + "/" + total + " heroes found")
         .row(5)
         .actor(heroesPix.pix())
         .pix();
   }

   public static Group makeMonsterGroup(Map<String, Stat> allMergedStats, int contentWidth) {
      Pixl monstersPix = new Pixl(2).forceWidth(contentWidth);
      Map<EntSize, List<MonsterType>> map = MonsterTypeLib.getSortedMonsters();
      int total = 0;
      int seen = 0;

      for (EntSize size : EntSize.values()) {
         List<MonsterType> types = new ArrayList<>(map.get(size));
         Collections.sort(types, makeMonsterSorter(allMergedStats));

         for (final MonsterType type : types) {
            final int kills = allMergedStats.get(KillsStat.getStatName(type)).getValue();
            final int victories = allMergedStats.get(MonsterTrackerStat.getNameFrom(type, true)).getValue();
            final int defeats = allMergedStats.get(MonsterTrackerStat.getNameFrom(type, false)).getValue();
            boolean hidden = kills == 0 && defeats == 0 && victories == 0;
            if (!hidden || !type.hideUntilFound()) {
               total++;
               final boolean fullyVisible = kills + victories + defeats > 0;
               if (fullyVisible) {
                  seen++;
               }

               MonsterLedgerView mav = new MonsterLedgerView(type, hidden);
               if (!addStandardListener(mav, hidden, type)) {
                  mav.addListener(new ClickListener() {
                     public void clicked(InputEvent event, float x, float y) {
                        Monster m = new Monster(type);
                        Pixl p = new Pixl(-1);
                        if (!fullyVisible) {
                           p.actor(new Pixl(0, 2).border(Colours.purple).text("[text]Not encountered yet...")).row();
                        } else {
                           TextWriter killsWriter = new TextWriter("[red]" + kills + " " + Words.plural("kill", kills), 999, Colours.purple, 2);
                           float ratio = (float)victories / (victories + defeats);
                           String col;
                           if (ratio < 0.5F) {
                              col = "[red]";
                           } else if (ratio < 0.9F) {
                              col = "[orange]";
                           } else {
                              col = "[green]";
                           }

                           TextWriter vicWriter = new TextWriter(col + victories + "/" + (victories + defeats) + " fights won", 999, Colours.purple, 2);
                           p.actor(killsWriter).gap(3).actor(vicWriter).row();
                        }

                        p.actor(new EntPanelInventory(m));
                        Group g = p.pix();
                        BookPage.push(g);
                        Tann.center(g);
                        super.clicked(event, x, y);
                     }
                  });
               }

               monstersPix.actor(mav, contentWidth - 10);
            }
         }

         monstersPix.row(10);
      }

      return new Pixl(0)
         .forceWidth(contentWidth)
         .text(TextWriter.getTag(MonsterType.getIdCol()) + seen + "/" + total + " monsters found")
         .row(5)
         .actor(monstersPix.pix())
         .pix();
   }

   public static Group makeItemsGroup(Map<String, Stat> allMergedStats, int contentWidth) {
      Pixl itemPixl = new Pixl(2).forceWidth(contentWidth);
      int totalSeen = 0;
      int total = 0;
      List<Integer> qualities = new ArrayList<>();

      for (int i = 1; i <= 20; i++) {
         qualities.add(i);
      }

      for (int i = 0; i >= -7; i--) {
         qualities.add(i);
      }

      for (int quality : qualities) {
         List<Item> itemList = ItemLib.getAllItemsWithQuality(quality, true);
         if (itemList.size() != 0) {
            total += itemList.size();
            Collections.sort(itemList, makeSorter(allMergedStats));
            String colTag = quality >= 0 ? "[grey]" : "[purple]";
            TextWriter tw = new TextWriter(colTag + Math.abs(quality), 999, Colours.grey, 2);
            itemPixl.actor(tw).row(-1);

            for (final Item e : itemList) {
               Stat ps = allMergedStats.get(PickStat.nameFor(e));
               final int chosen = PickStat.val(ps, false);
               final int rejected = PickStat.val(ps, true);
               boolean seen = chosen + rejected > 0;
               if (seen) {
                  totalSeen++;
               }

               ItemLedgerView eav = new ItemLedgerView(
                  e,
                  UnUtil.isLocked(e)
                     ? ItemLedgerView.EquipSeenState.Locked
                     : (seen ? ItemLedgerView.EquipSeenState.Seen : ItemLedgerView.EquipSeenState.Missing)
               );
               itemPixl.actor(eav, contentWidth - 10);
               if (!addStandardListener(eav, !seen, e)) {
                  eav.addListener(new ClickListener() {
                     public void clicked(InputEvent event, float x, float y) {
                        TextWriter chosenWriter = new TextWriter(BookPage.getChosenString(chosen, rejected), 999, Colours.grey, 2);
                        Group exp = new ItemPanel(e, true);
                        exp.addActor(chosenWriter);
                        chosenWriter.setPosition((int)(exp.getWidth() / 2.0F - chosenWriter.getWidth() / 2.0F), (int)(exp.getHeight() - 1.0F));
                        BookPage.push(exp);
                        super.clicked(event, x, y);
                     }
                  });
               }
            }

            itemPixl.row(8);
         }
      }

      Group g = itemPixl.pix();
      return new Pixl(0).forceWidth(contentWidth).text(TextWriter.getTag(Item.getIdCol()) + totalSeen + "/" + total + " items found").row(5).actor(g).pix();
   }

   public static Group makeModifiersGroup(
      final Map<String, Stat> allMergedStats, final int contentWidth, final LedgerPage ledgerPage, final Boolean bless, final LedgerUtils.ModGenType genType
   ) {
      int total = 0;
      String name;
      Color col;
      if (bless == null) {
         name = "modifiers";
         col = Colours.grey;
      } else {
         name = bless ? "blessings" : "curses";
         col = bless ? Colours.green : Colours.purple;
      }

      Pixl blessingsPix = new Pixl(2).forceWidth(contentWidth);
      int amt = 5;
      List<? extends Modifier> modifiers = genType.getExamples(5, bless);
      boolean big = modifiers.size() <= 5;
      if (big) {
         Tann.uniquify(modifiers);
      }

      Collections.sort(modifiers, makeSorter(allMergedStats, bless != null && !bless));
      int prevTier = -9999;

      for (final Modifier modifier : modifiers) {
         int tier = modifier.getTier();
         if (tier != prevTier && genType == LedgerUtils.ModGenType.Designed) {
            if (prevTier != -9999) {
               blessingsPix.row(10);
            } else {
               blessingsPix.row();
            }

            TextWriter tw = new TextWriter(modifier.getTierString(), 999, modifier.getBorderColour(), 2);
            blessingsPix.actor(tw).row(-1);
         }

         prevTier = tier;
         total++;
         Actor a;
         if (UnUtil.isLocked(modifier)) {
            a = new TextWriter("[grey]locked", 9999, modifier.getBorderColour(), 2);
            a.addListener(new TannListener() {
               @Override
               public boolean info(int button, float x, float y) {
                  AchLib.showUnlockFor(modifier);
                  return true;
               }
            });
         } else if (big) {
            a = new ModifierPanel(modifier, false);
         } else {
            a = new SmallModifierPanel(modifier);
            a.addListener(new TannListener() {
               @Override
               public boolean info(int button, float x, float y) {
                  ModifierPanel mp = new ModifierPanel(modifier, true);
                  BookPage.push(mp);
                  return true;
               }
            });
         }

         blessingsPix.actor(a, contentWidth - 10);
      }

      Pixl fPix = new Pixl(3);
      fPix.actor(new Rectactor(150, 1, Colours.grey)).row();

      for (final Boolean blessLoop : new Boolean[]{false, true, null}) {
         Color c = null;
         String text;
         if (blessLoop == null) {
            text = "Both";
            c = Colours.grey;
         } else {
            text = blessLoop ? "Blessings" : "Curses";
            c = blessLoop ? Colours.green : Colours.purple;
         }

         String preTag = c == null ? "" : TextWriter.getTag(c);
         StandardButton tb = new StandardButton(preTag + text, blessLoop == bless ? Colours.light : c);
         tb.setRunnable(new Runnable() {
            @Override
            public void run() {
               ledgerPage.showThing(LedgerUtils.makeModifiersGroup(allMergedStats, contentWidth, ledgerPage, blessLoop, genType));
            }
         });
         fPix.actor(tb);
      }

      fPix.row().actor(new Rectactor(150, 1, Colours.grey)).row();

      for (final LedgerUtils.ModGenType mt : LedgerUtils.ModGenType.values()) {
         String text = mt.name();
         Color c = mt.getCol();
         String preTag = TextWriter.getTag(c);
         StandardButton tb = new StandardButton(preTag + text, mt == genType ? Colours.light : c);
         tb.setRunnable(new Runnable() {
            @Override
            public void run() {
               ledgerPage.showThing(LedgerUtils.makeModifiersGroup(allMergedStats, contentWidth, ledgerPage, bless, mt));
            }
         });
         fPix.actor(tb);
      }

      fPix.row().actor(new Rectactor(150, 1, Colours.grey));
      Actor filterPanel = fPix.pix();
      Pixl main = new Pixl(0).forceWidth(contentWidth);
      main.actor(filterPanel);
      String found = genType == LedgerUtils.ModGenType.Designed ? "found" : "generated";
      switch (genType) {
         case Designed:
            found = "found";
            break;
         case Generated:
            found = "generated";
            break;
         case Wild:
            found = "generated ([red]wild[cu])";
      }

      main.row(5).text(TextWriter.getTag(col) + total + " " + name + " " + found);
      main.row(5).actor(blessingsPix.pix());
      return main.pix();
   }

   public static Group makeKeywordsGroup(Map<String, Stat> allMergedStats, int contentWidth) {
      final Map<Keyword, List<Actor>> keywordActors = new HashMap<>();
      Map<Keyword, List<TextureRegion>> usedRegions = new HashMap<>();

      for (Keyword k : Keyword.values()) {
         keywordActors.put(k, new ArrayList<>());
         usedRegions.put(k, new ArrayList<>());
      }

      for (EntType et : EntTypeUtils.getAll()) {
         if (!UnUtil.isLocked(et) && !shouldSkip(et, allMergedStats)) {
            for (final EntSide es : et.getNiceSides()) {
               for (Keyword k : es.getBaseEffect().getKeywords()) {
                  if (!usedRegions.get(k).contains(es.getTexture())) {
                     usedRegions.get(k).add(es.getTexture());
                     DieSidePanel dsp = new DieSidePanel(es, es.size.getExampleEntity());
                     dsp.addListener(new TannListener() {
                        @Override
                        public boolean info(int button, float x, float y) {
                           Sounds.playSound(Sounds.pip);
                           Actor a = new Explanel(es, null);
                           com.tann.dice.Main.getCurrentScreen().push(a, 0.5F);
                           Tann.center(a);
                           return true;
                        }
                     });
                     keywordActors.get(k).add(dsp);
                  }
               }
            }
         }
      }

      for (EntType etx : EntTypeUtils.getAll()) {
         if (!UnUtil.isLocked(etx) && !shouldSkip(etx, allMergedStats)) {
            List<Keyword> etks = new ArrayList<>();

            for (EntSide es : etx.getNiceSides()) {
               etks.addAll(es.getBaseEffect().getKeywords());
            }

            Tann.uniquify(etks);

            for (Keyword etk : etks) {
               keywordActors.get(etk).add(EntityLedgerView.getLV(etx));
            }
         }
      }

      for (EntType etxx : EntTypeUtils.getAll()) {
         if (!shouldSkip(etxx, allMergedStats)) {
            for (Trait t : etxx.traits) {
               final Ability s = t.personal.getAbility();
               if (s != null) {
                  for (Keyword kx : s.getBaseEffect().getKeywords()) {
                     Actor a = new AbilityPanel(s);
                     a.addListener(new TannListener() {
                        @Override
                        public boolean info(int button, float x, float y) {
                           Explanel explanel = new Explanel(s, false);
                           BookPage.push(explanel);
                           return true;
                        }
                     });
                     keywordActors.get(kx).add(a);
                  }
               }
            }
         }
      }

      for (Item i : ItemLib.getMasterCopy()) {
         if (!shouldSkip(i, allMergedStats)) {
            for (Keyword kx : i.getReferencedKeywords()) {
               ItemLedgerView iav = new ItemLedgerView(i, ItemLedgerView.EquipSeenState.Seen);
               iav.addBasicListener();
               keywordActors.get(kx).add(iav);
            }
         }
      }

      for (Modifier m : ModifierLib.getAll()) {
         if (!shouldSkip(m, allMergedStats)) {
            for (Keyword kx : m.getReferencedKeywords()) {
               SmallModifierPanel smp = new SmallModifierPanel(m);
               smp.addBasicListener();
               keywordActors.get(kx).add(smp);
            }
         }
      }

      Pixl p = new Pixl(2);
      int maxWidth = contentWidth - 10;
      List<Keyword> keywords = Arrays.asList(Keyword.values());
      Collections.sort(keywords, new Comparator<Keyword>() {
         public int compare(Keyword o1, Keyword o2) {
            return o1.getName().compareTo(o2.getName());
         }
      });
      List<Keyword> shown = new ArrayList<>();

      for (final Keyword kx : keywords) {
         if (!kx.skipStats()) {
            List<Actor> actors = keywordActors.get(kx);
            if (actors.size() != 0) {
               shown.add(kx);
               keywordActors.get(kx).addAll(0, KUtils.makeExampleSides(kx));
               StandardButton tb = KUtils.makeKeywordButton(kx);
               p.actor(tb, maxWidth);
               tb.setRunnable(new Runnable() {
                  @Override
                  public void run() {
                     LedgerUtils.showKD(k, keywordActors);
                  }
               });
            }
         }
      }

      Pixl kp = new Pixl(3);

      for (final Keyword kxx : keywords) {
         if (!shown.contains(kxx)) {
            keywordActors.get(kxx).addAll(0, KUtils.makeExampleSides(kxx));
            StandardButton tb = KUtils.makeKeywordButton(kxx);
            kp.actor(tb, maxWidth);
            tb.setRunnable(new Runnable() {
               @Override
               public void run() {
                  LedgerUtils.showKD(k, keywordActors);
               }
            });
         }
      }

      Actor unusedKeywords = kp.pix();
      Group g = new Pixl(0, 1).actor(unusedKeywords).pix();
      Color cover = Colours.withAlpha(Colours.dark, 0.37F).cpy();
      Rectactor ra = new Rectactor((int)g.getWidth(), (int)g.getHeight(), cover, cover);
      ra.setTouchable(Touchable.disabled);
      g.addActor(ra);
      p.row(5).text("locked/unused").row(2).actor(g);
      g = p.pix();
      return new Pixl(0).forceWidth(contentWidth).text("[orange]" + Keyword.values().length + " total keywords").row(5).actor(g).pix();
   }

   private static void showKD(Keyword k, Map<Keyword, List<Actor>> keywordActors) {
      Actor a = makeKeywordDetail(k, keywordActors);
      a = Tann.makeScrollpaneIfNecessary(a);
      BookPage.push(a);
   }

   private static boolean shouldSkip(Unlockable et, Map<String, Stat> allMergedStats) {
      if (!(et instanceof MonsterType)) {
         return false;
      } else {
         MonsterType type = (MonsterType)et;
         int kills = allMergedStats.get(KillsStat.getStatName(type)).getValue();
         int victories = allMergedStats.get(MonsterTrackerStat.getNameFrom(type, true)).getValue();
         int defeats = allMergedStats.get(MonsterTrackerStat.getNameFrom(type, false)).getValue();
         return kills == 0 && defeats == 0 && victories == 0;
      }
   }

   private static Actor makeKeywordDetail(Keyword k, Map<Keyword, List<Actor>> keywordActors) {
      int maxWidth = (int)Math.min(com.tann.dice.Main.width * 0.8F, 200.0F);
      Pixl p = new Pixl(2, 5).border(k.getColour());
      p.actor(KUtils.makeExplanationActor(k, maxWidth)).row();
      List<Actor> acs = new ArrayList<>(keywordActors.get(k));
      Modifier m = ModifierLib.byName("hero." + k.name());
      if (m.getReferencedKeywords().contains(k)) {
         acs.add(new SmallModifierPanel(m).addBasicListener());
      }

      Item i = ItemLib.byName("k." + k.name());
      if (i.getReferencedKeywords().contains(k)) {
         acs.add(new ItemLedgerView(i, ItemLedgerView.EquipSeenState.Seen).addBasicListener());
      }

      for (Actor a : acs) {
         p.actor(a, maxWidth);
      }

      return p.pix(8);
   }

   private static Comparator<Unlockable> makeSorter(Map<String, Stat> allMergedStats) {
      return makeSorter(allMergedStats, false);
   }

   private static Comparator<Unlockable> makeSorter(final Map<String, Stat> allMergedStats, final boolean abs) {
      return new Comparator<Unlockable>() {
         public int compare(Unlockable o1, Unlockable o2) {
            if (LedgerUtils.hasTier(o1)) {
               int tier1 = LedgerUtils.getTier(o1);
               int tier2 = LedgerUtils.getTier(o2);
               if (tier1 != tier2) {
                  int result = tier1 - tier2;
                  if (abs) {
                     return -result;
                  }

                  return result;
               }
            }

            if (UnUtil.isLocked(o1) != UnUtil.isLocked(o2)) {
               return UnUtil.isLocked(o1) ? 1 : -1;
            } else if (o1 instanceof Modifier) {
               return ((Modifier)o1).getName().compareTo(((Modifier)o2).getName());
            } else {
               Stat ps1 = null;
               Stat ps2 = null;
               if (o1 instanceof Choosable && !(o1 instanceof Modifier)) {
                  ps1 = allMergedStats.get(PickStat.nameFor((Choosable)o1));
                  ps2 = allMergedStats.get(PickStat.nameFor((Choosable)o2));
               }

               if (ps1 != null && ps2 != null) {
                  int chosenValue1 = PickStat.val(ps1, false);
                  int rejectedValue1 = PickStat.val(ps1, true);
                  int chosenValue2 = PickStat.val(ps2, false);
                  int rejectedValue2 = PickStat.val(ps2, true);
                  boolean hidden1 = chosenValue1 == 0 && rejectedValue1 == 0;
                  boolean hidden2 = chosenValue2 == 0 && rejectedValue2 == 0;
                  if (hidden1 != hidden2) {
                     return hidden1 ? 1 : -1;
                  } else {
                     float ratio1 = (float)(chosenValue1 + 3) / (chosenValue1 + rejectedValue1 + 6);
                     float ratio2 = (float)(chosenValue2 + 3) / (chosenValue2 + rejectedValue2 + 6);
                     return (int)Math.signum(ratio2 - ratio1);
                  }
               } else {
                  return 0;
               }
            }
         }
      };
   }

   private static int getTier(Unlockable u) {
      if (u instanceof Hero) {
         return ((Hero)u).getLevel();
      } else if (u instanceof Modifier) {
         return ((Modifier)u).getTier();
      } else if (u instanceof Item) {
         return ((Item)u).getTier();
      } else {
         throw new RuntimeException("invalid tier-haver " + u.getClass().getSimpleName());
      }
   }

   private static boolean hasTier(Unlockable o1) {
      return o1 instanceof Hero || o1 instanceof Modifier || o1 instanceof Item;
   }

   private static Comparator<? super MonsterType> makeMonsterSorter(final Map<String, Stat> allMergedStats) {
      return new Comparator<MonsterType>() {
         public int compare(MonsterType o1, MonsterType o2) {
            boolean locked1 = UnUtil.isLocked(o1);
            boolean locked2 = UnUtil.isLocked(o2);
            if (locked1 != locked2) {
               return locked1 ? 1 : -1;
            } else {
               int kills1 = allMergedStats.get(KillsStat.getStatName(o1)).getValue();
               int kills2 = allMergedStats.get(KillsStat.getStatName(o2)).getValue();
               int victories1 = allMergedStats.get(MonsterTrackerStat.getNameFrom(o1, true)).getValue();
               int victories2 = allMergedStats.get(MonsterTrackerStat.getNameFrom(o2, true)).getValue();
               int defeats1 = allMergedStats.get(MonsterTrackerStat.getNameFrom(o1, false)).getValue();
               int defeats2 = allMergedStats.get(MonsterTrackerStat.getNameFrom(o2, false)).getValue();
               boolean hidden1 = kills1 == 0 && defeats1 == 0 && victories1 == 0;
               boolean hidden2 = kills2 == 0 && defeats2 == 0 && victories2 == 0;
               if (hidden1 != hidden2) {
                  return hidden1 ? 1 : -1;
               } else {
                  boolean sortByKills = true;
                  return kills2 - kills1;
               }
            }
         }
      };
   }

   private static boolean addStandardListener(Actor a, boolean hidden, final Unlockable unlockable) {
      if (UnUtil.isLocked(unlockable)) {
         a.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               AchLib.showUnlockFor(unlockable);
               return true;
            }
         });
         return true;
      } else {
         return false;
      }
   }

   public static Actor makePinsGroup(Map<String, Stat> allMergedStats, int contentWidth, LedgerPage ledgerPage) {
      return makePinsGroup(contentWidth, ledgerPage, null);
   }

   private static Actor makePinsGroup(final int contentWidth, final LedgerPage ledgerPage, List<String> pinArg) {
      final List<String> pinnedStrings;
      if (pinArg == null) {
         pinnedStrings = com.tann.dice.Main.getSettings().getPins();
      } else {
         pinnedStrings = pinArg;
         com.tann.dice.Main.getSettings().setPins(pinArg);
      }

      Pixl p = new Pixl(3);
      if (com.tann.dice.Main.self().translator.shouldTranslate()) {
         p.text("([blue]Pin[text] is only available in English.)").row(5);
      }

      final List<TTP<String, Color, ? extends Resolver<? extends Object>>> data = Arrays.asList(new TTP<>("name", Colours.grey, new MetaResolver() {
         @Override
         public void resolve(Object o) {
            LedgerUtils.attemptToAddPin(o, pinnedStrings, contentWidth, ledgerPage);
         }
      }), new TTP<>("desc", Colours.grey, new AnyDescResolver() {
         @Override
         public void resolve(Object o) {
            LedgerUtils.attemptToAddPin(o, pinnedStrings, contentWidth, ledgerPage);
         }
      }));
      List<Actor> butts = new ArrayList<>();

      for (final TTP<String, Color, ? extends Resolver<? extends Object>> datum : data) {
         StandardButton sb = new StandardButton(TextWriter.getTag(datum.b) + datum.a, datum.b);
         sb.setRunnable(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pipSmall);
               datum.c.activate();
            }
         });
         butts.add(sb);
      }

      StandardButton paste = new StandardButton("[purple]paste");
      paste.setRunnable(new Runnable() {
         @Override
         public void run() {
            Pipe.setupChecks();
            this.doInput();
            Pipe.disableChecks();
         }

         private void doInput() {
            String contents = ClipboardUtils.pasteSafer();
            if (contents == null) {
               Sounds.playSound(Sounds.pipSmall);
               com.tann.dice.Main.getCurrentScreen().showDialog("invalid clipboard text", Colours.red);
            } else if (!contents.startsWith("=")) {
               String genericError = PasteMode.getPasteErrorGeneric(contents);
               if (genericError != null) {
                  com.tann.dice.Main.getCurrentScreen().showDialog(genericError, Colours.red);
               } else {
                  boolean success = ((Resolver)data.get(0).c).debugResolve(contents);
                  if (!success) {
                     Sounds.playSound(Sounds.pipSmall);
                     com.tann.dice.Main.getCurrentScreen().showDialog("failed to find", Colours.red);
                  }
               }
            } else {
               contents = contents.substring(1);
               String[] modNames = contents.split(",");
               int amt = pinnedStrings.size();

               for (String modName : modNames) {
                  boolean var8 = ((Resolver)data.get(0).c).debugResolve(modName);
               }

               int added = pinnedStrings.size() - amt;
               com.tann.dice.Main.getCurrentScreen().showDialog("added " + added, Colours.blue);
            }
         }
      });
      butts.add(2, paste);

      for (final boolean gen : Tann.BOTH) {
         StandardButton sb = new StandardButton("[yellow]" + (gen ? "+gen" : "+rng"));
         sb.setRunnable(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pipSmall);
               int r = (int)(Math.random() * 4.0);
               String pinName;
               switch (r) {
                  case 0:
                     pinName = (gen ? PipeItem.makeGen() : ItemLib.random()).getName();
                     break;
                  case 1:
                     pinName = ((EntType)(gen ? PipeMonster.makeGen() : EntTypeUtils.random())).getName();
                     break;
                  case 2:
                     pinName = (gen ? PipeMod.makeGen(Tann.half()) : ModifierLib.random()).getName();
                     break;
                  case 3:
                  default:
                     if (gen) {
                        pinName = PipeMod.makeGen(Tann.half()).getName();
                     } else {
                        pinName = Tann.random(Keyword.values()).name();
                     }
               }

               LedgerUtils.addPin(pinName, pinnedStrings);
               ledgerPage.showThing(LedgerUtils.makePinsGroup(contentWidth, ledgerPage, pinnedStrings));
            }
         });
         butts.add(sb);
      }

      if (pinnedStrings.size() > 4) {
         paste = new StandardButton("[red]clear");
         paste.setRunnable(new Runnable() {
            @Override
            public void run() {
               ChoiceDialog cd = new ChoiceDialog("[purple]Clear " + pinnedStrings.size() + " pins?", ChoiceDialog.ChoiceNames.PurpleYes, new Runnable() {
                  @Override
                  public void run() {
                     LedgerUtils.clearPins(pinnedStrings);
                     ledgerPage.showThing(LedgerUtils.makePinsGroup(contentWidth, ledgerPage, pinnedStrings));
                     Sounds.playSound(Sounds.magic);
                     com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  }
               }, new Runnable() {
                  @Override
                  public void run() {
                     Sounds.playSound(Sounds.pop);
                     com.tann.dice.Main.getCurrentScreen().popSingleMedium();
                  }
               });
               com.tann.dice.Main.getCurrentScreen().push(cd, 0.8F);
               Tann.center(cd);
               Sounds.playSound(Sounds.pipSmall);
            }
         });
         butts.add(paste);
      }

      p.listActor((int)(contentWidth * 0.9F), butts);
      Pixl extraP = new Pixl(2);
      final List<TP<String, Actor>> pinnedElements = new ArrayList<>();
      MetaResolver mr = new MetaResolver() {
         @Override
         public void resolve(Object o) {
            if (o instanceof EntType) {
               pinnedElements.add(LedgerUtils.getExtrasActor((EntType)o));
            } else if (o instanceof Unlockable && o instanceof Choosable) {
               Unlockable u = (Unlockable)o;
               Choosable c = (Choosable)o;
               Actor tst = u.makeUnlockActor(true);
               boolean hasScroll = false;
               if (tst instanceof Group) {
                  Group gtst = (Group)tst;
                  hasScroll = TannStageUtils.hasActor(gtst, ScrollPane.class);
               }

               float wThresh = 0.6F;
               float hThresh = 0.4F;
               boolean tooBig = tst.getWidth() > com.tann.dice.Main.width * 0.6F || tst.getHeight() > com.tann.dice.Main.height * 0.4F;
               if (tooBig || hasScroll) {
                  tst = u.makeUnlockActor(false);
               }

               pinnedElements.add(new TP<>(c.getName(), tst));
            } else if (o instanceof Keyword) {
               Keyword k = (Keyword)o;
               pinnedElements.add(new TP<>(k.getName(), KUtils.makeActor(k, null)));
            }
         }
      };
      List<String> failed = new ArrayList<>();

      for (String pinnedString : pinnedStrings) {
         Pipe.setupChecks();
         boolean success = mr.debugResolve(pinnedString);
         Pipe.disableChecks();
         if (!success) {
            failed.add(pinnedString);
         }
      }

      pinnedStrings.removeAll(failed);

      for (final TP<String, Actor> extra : pinnedElements) {
         Pixl pp = new Pixl();
         Actor closeButt = new ImageActor(Images.tut_close);
         closeButt.addListener(new TannListener() {
            @Override
            public boolean action(int button, int pointer, float x, float y) {
               Sounds.playSound(Sounds.pipSmall);
               pinnedStrings.remove(extra.a);
               ledgerPage.showThing(LedgerUtils.makePinsGroup(contentWidth, ledgerPage, pinnedStrings));
               return true;
            }
         });
         pp.actor(closeButt);
         Actor a = pp.row(-1).actor(extra.b).pix();
         extraP.actor(a, contentWidth * 0.85F);
      }

      p.row(3).actor(extraP.pix(2));
      p.row(3).actor(OptionLib.SHOW_COPY.makeFullDescribedUnlockActor());
      return p.pix();
   }

   private static void attemptToAddPin(Object o, List<String> pinnedStrings, int contentWidth, LedgerPage ledgerPage) {
      if (o instanceof EntType) {
         addPin(((EntType)o).getName(), pinnedStrings);
      } else if (o instanceof Unlockable && o instanceof Choosable) {
         Choosable c = (Choosable)o;
         addPin(c.getName(), pinnedStrings);
      } else {
         if (!(o instanceof Keyword)) {
            return;
         }

         addPin(((Keyword)o).getName(), pinnedStrings);
      }

      Sounds.playSound(Sounds.pipSmall);
      ledgerPage.showThing(makePinsGroup(contentWidth, ledgerPage, pinnedStrings));
   }

   private static void clearPins(List<String> pinnedStrings) {
      pinnedStrings.clear();
   }

   private static boolean addPin(String name, List<String> pinnedStrings) {
      pinnedStrings.remove(name);
      pinnedStrings.add(0, name);
      return true;
   }

   private static TP<String, Actor> getExtrasActor(EntType et) {
      return new TP<>(et.getName(), new EntPanelInventory(et.makeEnt()).withoutDice().getFullActor());
   }

   public static enum ModGenType {
      Designed(Colours.orange),
      Generated(Colours.blue),
      Wild(Colours.red);

      final Color col;

      private ModGenType(Color col) {
         this.col = col;
      }

      public Color getCol() {
         return this.col;
      }

      public List<Modifier> getExamples(int amt, Boolean blessing) {
         switch (this) {
            case Designed:
               return new ArrayList<>(ModifierLib.getAll(blessing));
            case Generated:
               return PipeMod.makeGenerated(amt, blessing, false);
            case Wild:
               return PipeMod.makeGenerated(amt, blessing, true);
            default:
               return new ArrayList<>();
         }
      }
   }
}
