package com.tann.dice.screens.dungeon;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.screens.Screen;
import com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel.EscMenuUtils;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.APIUtils;
import com.tann.dice.screens.dungeon.panels.hourglass.HourglassUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DungeonUtils {
   private static final int BUTT_BORDER = 2;

   public static void placeButtonsGroup(Actor a, boolean portrait) {
      if (portrait) {
         a.setPosition(com.tann.dice.Main.self().notch(3) + 2, com.tann.dice.Main.height - a.getHeight() - com.tann.dice.Main.self().notch(0) - 2.0F);
      } else {
         a.setPosition(
            (int)(com.tann.dice.Main.width / 2 - a.getWidth() / 2.0F), com.tann.dice.Main.height - a.getHeight() - 2.0F - com.tann.dice.Main.self().notch(0)
         );
      }
   }

   public static Group makeButtonsGroup(DungeonContext dungeonContext, FightLog fightLog, DungeonScreen ds) {
      List<Actor> butts = new ArrayList<>(Arrays.asList(makeCog(), dungeonContext.makeHashButton()));
      Actor hourglass = HourglassUtils.makeHourglassButton(fightLog.getSnapshot());
      if (hourglass != null) {
         butts.add(hourglass);
      }

      if (shouldShowTargetButton(dungeonContext)) {
         butts.add(makeTargetButton(ds));
      }

      if (OptionLib.SEARCH_BUTT.c()) {
         butts.add(makeSearchButton());
      }

      return makeButtonsGroup(com.tann.dice.Main.isPortrait(), butts);
   }

   public static Group makeButtonsGroup(boolean portrait, List<Actor> butts) {
      int gap = 5;
      Pixl p = new Pixl(gap);

      for (int i = 0; i < butts.size(); i++) {
         Actor butt = butts.get(i);
         if (i > 0 && portrait) {
            p.row();
         }

         p.actor(butt);
      }

      return p.pix();
   }

   public static boolean shouldShowTargetButton(DungeonContext dc) {
      return dc.getParty().anyDuplicateCols() || OptionLib.ALWAYS_SHOW_TARG_BUTTON.c();
   }

   public static Actor makeBasicButton(TextureRegion tr) {
      return makeBasicButton(tr, false);
   }

   public static Actor makeBasicButton(TextureRegion tr, boolean big) {
      ImageActor ia = new ImageActor(big ? Images.minibuttBaseBig : Images.minibuttBase);
      Group g = Tann.makeGroup(ia);
      Actor middle = new ImageActor(tr);
      g.addActor(middle);
      Tann.center(middle);
      return g;
   }

   public static TextureRegion getBaseImage() {
      return Images.minibuttBase;
   }

   public static Actor makeSearchButton() {
      Actor cog = makeBasicButton(Images.searchIcon);
      cog.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            APIUtils.showSearch();
            Sounds.playSound(Sounds.pip);
            return true;
         }

         @Override
         public boolean info(int button, float x, float y) {
            APIUtils.showDescSearch();
            Sounds.playSound(Sounds.pip);
            return true;
         }
      });
      return cog;
   }

   public static Actor makeTargetButton(final DungeonScreen ds) {
      final TextureRegion base = getBaseImage();
      Actor target = new Actor() {
         public void draw(Batch batch, float parentAlpha) {
            batch.setColor(Colours.z_white);
            batch.draw(base, this.getX(), this.getY());
            if (PhaseManager.get().getPhase().showTargetButton()) {
               batch.setColor(Colours.red);
            } else {
               batch.setColor(Colours.withAlpha(Colours.grey, 0.5F));
            }

            TextureRegion tr = Images.targetIcon;
            batch.draw(
               tr,
               (int)(this.getX() + this.getWidth() / 2.0F - tr.getRegionWidth() / 2),
               (int)(this.getY() + this.getHeight() / 2.0F - tr.getRegionHeight() / 2)
            );
            super.draw(batch, parentAlpha);
         }
      };
      target.setSize(base.getRegionWidth(), base.getRegionHeight());
      target.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            for (Ent entity : ds.hero.getEntities()) {
               entity.getEntPanel().setArrowIntensity(1.0F, -1.0F);
            }

            return true;
         }

         public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            for (Ent entity : ds.hero.getEntities()) {
               entity.getEntPanel().setArrowIntensity(0.0F, -1.0F);
            }
         }
      });
      return target;
   }

   public static Actor makeCog() {
      Actor cog = makeBasicButton(Images.cog);
      cog.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            DungeonUtils.showCogMenu();
            Sounds.playSound(Sounds.pip);
            return true;
         }
      });
      return cog;
   }

   public static void showCogMenu() {
      String t = "cog";
      Screen s = com.tann.dice.Main.getCurrentScreen();
      Actor a = s.getTopPushedActor();
      if (a != null && "cog".equals(a.getName())) {
         com.tann.dice.Main.getCurrentScreen().pop(a);
      }

      a = EscMenuUtils.makeFullEscMenu();
      s.push(a, true, true, false, 0.5F);
      a.setName("cog");
      Tann.center(a);
   }

   public static class CogTag extends Actor {
   }
}
