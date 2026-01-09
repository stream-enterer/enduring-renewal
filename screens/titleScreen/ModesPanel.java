package com.tann.dice.screens.titleScreen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.item.ItemLib;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderMode;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModesPanel extends Group {
   static final int GAP = 5;
   static final int WIDTH = 50;
   static final int LEFT_BORDER = 1;
   List<StandardButton> modeButtons = new ArrayList<>();
   Actor fullGroup = null;
   ImageActor modesTab = null;
   Mode selectedMode;
   Runnable onChangeMode;
   boolean out = false;

   public ModesPanel() {
      this.setSize(61.0F, com.tann.dice.Main.height);
      this.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            return true;
         }
      });
      this.layout();
   }

   public void layout() {
      this.clearChildren();
      TextureRegion tabTexture = Images.moneyButton;
      if (!com.tann.dice.Main.demo) {
         if (com.tann.dice.Main.self().translator.shouldTranslate()) {
            tabTexture = Images.modesButtonArrowLeft;
         } else {
            tabTexture = Images.modesButton;
         }
      }

      this.modesTab = new ImageActor(tabTexture);
      this.addActor(this.modesTab);
      this.modesTab.setPosition(-this.modesTab.getWidth() + 1.0F, (int)(this.getHeight() - this.modesTab.getHeight()) / 2);
      this.modesTab.addListener(new InputListener() {
         public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            ModesPanel.this.slide(!ModesPanel.this.out, true);
            event.cancel();
            return true;
         }
      });
      boolean initiallyVisible = this.out || !com.tann.dice.Main.self().control.hasNotches();
      this.fullGroup = null;
      if (com.tann.dice.Main.demo) {
         Actor fullGroup = makeFullVersionGroup();
         Actor var11 = new Pixl(0, 4).actor(fullGroup).pix();
         this.fullGroup = var11;
         this.fullGroup.setVisible(initiallyVisible);
         this.addActor(var11);
         this.setSize(var11.getWidth() + 10.0F + 1.0F, com.tann.dice.Main.height);
         Tann.center(var11);
      } else {
         int topBotGap = 8;
         Pixl right = new Pixl(0);
         right.row(8);
         List<Mode> modes = new ArrayList<>(Mode.getPlayableModes());
         Collections.sort(modes, new Comparator<Mode>() {
            public int compare(Mode o1, Mode o2) {
               return Boolean.compare(UnUtil.isLocked(o1), UnUtil.isLocked(o2));
            }
         });

         for (int i = 0; i < modes.size(); i++) {
            final Mode m = modes.get(i);
            if (!m.skipFromMainList()) {
               final boolean locked = UnUtil.isLocked(m);
               StandardButton tb = m.makeModeSelectButton();
               tb.setVisible(initiallyVisible);
               this.modeButtons.add(tb);
               tb.setRunnable(new Runnable() {
                  @Override
                  public void run() {
                     if (locked) {
                        if (m instanceof FolderMode) {
                           FolderMode f = (FolderMode)m;
                           List<Mode> cm = f.getContainedModes();
                           if (!cm.isEmpty() && UnUtil.isLocked(cm.get(0))) {
                              AchLib.showUnlockFor(cm.get(0));
                           }

                           if (UnUtil.isLocked(f)) {
                              AchLib.showUnlockFor(f);
                           } else {
                              com.tann.dice.Main.getCurrentScreen().showDialog(m.getTextButtonName() + "[n](a locked folder)");
                              Sounds.playSound(Sounds.pip);
                           }
                        } else {
                           AchLib.showUnlockFor(m);
                        }
                     } else {
                        Sounds.playSound(Sounds.pipSmall);
                        ModesPanel.this.selectMode(m);
                        ModesPanel.this.slide(false, false);
                     }
                  }
               });
               right.actor(tb);
               if (i < modes.size() - 1) {
                  right.row(3);
               }
            }
         }

         right.row(8);
         Actor modesColumn = right.pix();
         int gap = (int)(this.getWidth() - modesColumn.getWidth() - 6.0F) / 2;
         Actor a = new Pixl().gap(gap).actor(modesColumn).gap(gap).pix();
         this.addActor(a);
         a.setPosition((int)(this.getWidth() - a.getWidth()) / 2.0F + 1.0F, (int)(this.getHeight() / 2.0F - a.getHeight() / 2.0F));
      }
   }

   public static Actor makeFullVersionGroup() {
      String text = "- [light]Single purchase\n- [light]No ads, offline\n- [orange]20-level dungeon\n- [orange]Too many modes\n- [yellow]"
         + HeroTypeUtils.getNumNormalHeroes()
         + " heroes\n- [yellow]"
         + MonsterTypeLib.getNumNormalMonsters()
         + " monsters\n- [grey]"
         + ItemLib.getNumNormalItems()
         + " items\n- [grey]"
         + Keyword.values().length
         + " keywords\n- [purple][infinite] curses\n- [purple]Weird modding";
      if (com.tann.dice.Main.self().translator.shouldTranslate()) {
         text = com.tann.dice.Main.t(text);
      }

      List<String> lines = new ArrayList<>(Arrays.asList(text.split("\n")));
      com.tann.dice.Main.self().control.affectAdvertLines(lines);
      Pixl p = new Pixl(3);
      Pixl lp = new Pixl(3);

      for (String line : lines) {
         lp.text("[notranslate]" + line).row();
      }

      p.actor(lp.pix(8)).row();
      p.actor(com.tann.dice.Main.self().control.makePaymentRequestActor());
      return p.pix();
   }

   private void selectMode(Mode m) {
      if (m != this.selectedMode) {
         this.selectedMode = m;

         for (StandardButton tb : this.modeButtons) {
            tb.setBorder(tb.getText().equals(m.getTextButtonName()) ? Colours.light : Colours.grey);
         }

         if (this.onChangeMode != null) {
            this.onChangeMode.run();
         }
      }
   }

   public void setOnChangeMode(Runnable onChangeMode) {
      this.onChangeMode = onChangeMode;
   }

   public Mode getSelectedMode() {
      return this.selectedMode;
   }

   private void updatePanelContentVisibility() {
      if (com.tann.dice.Main.self().control.hasNotches()) {
         for (int i = 0; i < this.modeButtons.size(); i++) {
            this.modeButtons.get(i).setVisible(this.out);
         }

         if (this.fullGroup != null) {
            this.fullGroup.setVisible(this.out);
         }
      }
   }

   public void slide(boolean out, boolean manual) {
      if (this.out != out) {
         this.out = out;
         if (!com.tann.dice.Main.demo && this.modesTab != null) {
            if (com.tann.dice.Main.self().translator.shouldTranslate()) {
               this.modesTab.setImage(out ? Images.modesButtonArrowRight : Images.modesButtonArrowLeft);
            } else {
               this.modesTab.setImage(Images.modesButton);
            }
         }

         this.addAction(Actions.moveTo(com.tann.dice.Main.width - (out ? this.getWidth() : 0.0F) - com.tann.dice.Main.self().notch(1), 0.0F, 0.3F, Chrono.i));
         this.updatePanelContentVisibility();
         if (manual) {
            Sounds.playSound(out ? Sounds.lock : Sounds.unlock);
         }
      }
   }

   public void draw(Batch batch, float parentAlpha) {
      batch.setColor(Colours.light);
      Draw.fillRectangle(batch, this.getX(), this.getY(), 1.0F, this.getHeight());
      batch.setColor(Colours.dark);
      Draw.fillRectangle(batch, this.getX() + 1.0F, this.getY(), this.getWidth() - 1.0F + 50.0F, this.getHeight());
      super.draw(batch, parentAlpha);
   }

   public void selectFirstUnlocked(Mode prefMode) {
      if (prefMode != null) {
         if (!UnUtil.isLocked(prefMode)) {
            this.selectMode(prefMode);
            return;
         }

         TannLog.log("Trying to load into locked mode: " + prefMode);
      }

      for (Mode m : Mode.getPlayableModes()) {
         if (!UnUtil.isLocked(m)) {
            this.selectMode(m);
            return;
         }
      }
   }
}
