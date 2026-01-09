package com.tann.dice.gameplay.mode.debuggy;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.group.Party;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.misc.PickConfig;
import com.tann.dice.gameplay.level.Level;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.progress.chievo.unlock.Unlockable;
import com.tann.dice.gameplay.save.SaveState;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.titleScreen.GameStart;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.resolver.MetaResolver;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;
import java.util.List;

public class PickMode extends Mode {
   static Unlockable lastPicked;
   static final int ATTEMPTS = 200;

   public PickMode() {
      super("Pick");
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"pick any hero, item or monster", "play a random classic fight featuring it", "no textmod allowed"};
   }

   @Override
   public Actor makeStartGameCard(List<ContextConfig> all) {
      Pixl p = new Pixl().actor(this.makeSelectorActor());
      Actor loadButton = SaveState.getLoadButton(this.getConfigs().get(0).getGeneralSaveKey());
      if (loadButton != null) {
         p.row(5).actor(loadButton);
      }

      return p.pix();
   }

   private Actor makeSelectorActor() {
      StandardButton sb = new StandardButton("pick");
      sb.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            (new MetaResolver() {
               @Override
               public void resolve(Object o) {
                  Screen s = com.tann.dice.Main.getCurrentScreen();
                  if (o instanceof Modifier) {
                     s.popAllMedium();
                     s.showDialog("no modifiers!");
                  } else if (!(o instanceof Unlockable)) {
                     s.popAllMedium();
                     s.showDialog("no whatever this is!");
                  } else {
                     Unlockable u = (Unlockable)o;
                     if (PickMode.skipForTestMode(u)) {
                        s.popAllMedium();
                        s.showDialog("banned, no appeals");
                     } else {
                        DungeonContext dc = PickMode.makeContextIncluding(u);
                        if (dc == null) {
                           String text = com.tann.dice.Main.t("[red]Failed to start after 200 tries\n[grey]oh well");
                           text = "[notranslateall]" + text.replaceAll("\n", "[n]");
                           com.tann.dice.Main.getCurrentScreen().showDialog(text);
                        } else {
                           PickMode.lastPicked = u;
                           GameStart.start(dc);
                        }
                     }
                  }
               }
            }).activate();
            return true;
         }
      });
      return sb;
   }

   public static boolean skipForTestMode(Unlockable u) {
      if (UnUtil.isLocked(u)) {
         return true;
      } else {
         if (u instanceof HeroType) {
            HeroType ht = (HeroType)u;
            if (ht.isBannedFromLateStart()) {
               return true;
            }
         }

         if (u instanceof EntType) {
            EntType et = (EntType)u;
            return et.isMissingno();
         } else if (!(u instanceof Item)) {
            return false;
         } else {
            Item i = (Item)u;
            return i.getTier() < 1 || i.getTier() > 9;
         }
      }
   }

   public static DungeonContext makeRestartContext() {
      if (lastPicked == null) {
         TannLog.error("null lastpicked");
         return makeFailedContext();
      } else {
         DungeonContext test = makeContextIncluding(lastPicked);
         return test == null ? makeFailedContext() : test;
      }
   }

   private static DungeonContext makeFailedContext() {
      TannLog.error("Failed to make restart context");
      return new DungeonContext(new PickConfig(), Party.generate(0), 1);
   }

   public static DungeonContext makeContextIncluding(Unlockable u) {
      PickConfig tc = new PickConfig();

      for (int i = 0; i < 200; i++) {
         int level = (int)(Math.random() * 20.0) + 1;
         Party p = Party.generate(level - 1);
         DungeonContext dc = new DungeonContext(tc, p, level);
         if (u instanceof Item) {
            Item item = (Item)u;
            if (p.getItems(null).contains(item)) {
               return dc;
            }
         } else if (u instanceof HeroType) {
            HeroType ht = (HeroType)u;
            if (p.getByType(ht) != null) {
               return dc;
            }
         } else if (u instanceof MonsterType) {
            MonsterType mt = (MonsterType)u;
            Level l = dc.getCurrentLevel();
            if (l.getMonsterList().contains(mt)) {
               return dc;
            }
         }
      }

      return null;
   }

   @Override
   public Color getColour() {
      return Colours.grey;
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return Arrays.asList(new PickConfig());
   }

   @Override
   public String getSaveKey() {
      return "tst";
   }

   @Override
   public boolean skipStats() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.crappy;
   }

   @Override
   public boolean skipShowBoss() {
      return true;
   }
}
