package com.tann.dice.screens.dungeon.panels.book.page.cogPage.menuPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.progress.chievo.AchLib;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.Option;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionsMenu extends MenuPanel {
   private static final int rowSize = 3;
   final int BOX_META_ROW = 12;
   public static final int CHECKBOX_COG_WIDTH = 100;
   private static final int SCALE_BORDER = 4;

   public static String getCheckboxExp() {
      return "[grey](" + com.tann.dice.Main.self().control.getInfoTapString().toLowerCase() + " a checkbox to learn what it does)";
   }

   public OptionsMenu(int contentWidth) {
      int ecw = (int)(contentWidth * 0.98F);
      Pixl p = new Pixl(3).text(getCheckboxExp(), ecw).row().actor(this.makeBoxes(ecw)).row().actor(this.makeResetButton()).row(4);
      Tann.become(this, p.pix());
   }

   private Actor makeResetButton() {
      return new StandardButton("[red]Reset All").setRunnable(new Runnable() {
         @Override
         public void run() {
            ChoiceDialog cd = new ChoiceDialog("Reset all options to unchecked?", ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
               @Override
               public void run() {
                  Sounds.playSound(Sounds.flap);
                  com.tann.dice.Main.getSettings().clearAllOptions();
                  com.tann.dice.Main.getCurrentScreen().popAllMedium();
                  com.tann.dice.Main.getCurrentScreen().showDialog("[red]All options reset");
               }
            }, new Runnable() {
               @Override
               public void run() {
                  com.tann.dice.Main.getCurrentScreen().popSingleMedium();
               }
            });
            com.tann.dice.Main.getCurrentScreen().push(cd, 0.8F);
            Tann.center(cd);
         }
      });
   }

   private Actor makeBoxes(int contentWidth) {
      Pixl boxes = new Pixl(0, 2);
      Pixl left = new Pixl();
      Pixl right = new Pixl();
      List<OptionUtils.EscBopType> leftTypes = new ArrayList<>();
      List<OptionUtils.EscBopType> rightTypes = new ArrayList<>(Arrays.asList(OptionUtils.EscBopType.UI));

      for (OptionUtils.EscBopType value : OptionUtils.EscBopType.values()) {
         if (value.shownInOptions && !rightTypes.contains(value)) {
            leftTypes.add(value);
         }
      }

      for (boolean l : Tann.BOTH) {
         List<OptionUtils.EscBopType> i1 = l ? leftTypes : rightTypes;
         Pixl ip = l ? left : right;

         for (int i = 0; i < i1.size(); i++) {
            OptionUtils.EscBopType t = i1.get(i);
            Actor a = boxType(t);
            ip.actor(a);
            if (i < i1.size() - 1) {
               ip.row(12);
            }
         }
      }

      Actor l = left.pix(8);
      Actor r = right.pix(8);
      if (contentWidth > 209.99998F) {
         return boxes.actor(l).gap(5).actor(r).pix(2);
      } else {
         for (OptionUtils.EscBopType valuex : OptionUtils.EscBopType.values()) {
            if (valuex.shownInOptions) {
               boxes.actor(boxType(valuex)).row(12);
            }
         }

         return boxes.pix(8);
      }
   }

   public static Actor boxType(OptionUtils.EscBopType type) {
      Pixl inner = new Pixl();
      inner.text(TextWriter.getTag(type.getCol()) + type).row(2);
      List<Option> typeBopts = type.getOptions();
      if (com.tann.dice.Main.self().translator.getLanguageCode().equals("ru")) {
         typeBopts.remove(OptionLib.FONT);
      }

      for (boolean locked : Tann.BOTH) {
         List<Option> innerBops = new ArrayList<>();

         for (int boptIndex = 0; boptIndex < typeBopts.size(); boptIndex++) {
            Option option = typeBopts.get(boptIndex);
            if (option.isValid() && !option.isDebug() && UnUtil.isLocked(option) == locked) {
               innerBops.add(option);
            }
         }

         for (int i = 0; i < innerBops.size(); i++) {
            final Option option = innerBops.get(i);
            inner.row(3);
            Actor a;
            if (UnUtil.isLocked(option)) {
               a = new Pixl().image(Images.BOPTION_LOCKED).gap(3).text("[grey]locked").pix();
               a.addListener(new TannListener() {
                  @Override
                  public boolean info(int button, float x, float y) {
                     AchLib.showUnlockFor(option);
                     return true;
                  }
               });
            } else {
               a = option.makeCogActor();
            }

            inner.actor(a);
            if (i < innerBops.size() - 1) {
               inner.row(2);
            }
         }
      }

      if (type == OptionUtils.EscBopType.Music) {
         StandardButton here = new StandardButton("[grey]display/sound");
         here.setRunnable(new Runnable() {
            @Override
            public void run() {
               Sounds.playSound(Sounds.pip);
               DungeonUtils.showCogMenu();
            }
         });
         inner.row(2).actor(here);
      }

      return inner.pix(8);
   }

   public static Group makeScaleAdjust() {
      Pixl scaleRow = new Pixl(2);
      TextWriter tw = new TextWriter("[text]UI size");
      tw.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            com.tann.dice.Main.getCurrentScreen().pushAndCenter(new Pixl(0, 3).border(Colours.grey).text("UI scaling factor").pix());
            return true;
         }
      });
      scaleRow.actor(tw);
      final int maxScaleDelta = Math.max(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 600;
      final int minScaleDelta = Math.min(-1, -Gdx.graphics.getWidth() / 600);
      TextWriter scaleDisplay = new TextWriter(Tann.delta(com.tann.dice.Main.getSettings().getScaleAdjust()));

      for (int i = -1; i <= 1; i++) {
         if (i == 0) {
            scaleRow.actor(scaleDisplay);
         } else {
            String text = i == -1 ? "-" : "+";
            TextWriter adjust = new TextWriter(text, 999, Colours.grey, 4);
            scaleRow.actor(adjust);
            final int diff = i;
            adjust.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  Phase p = PhaseManager.get().getPhase();
                  if (p.disallowRescale()) {
                     Sounds.playSound(Sounds.error);
                     return true;
                  } else {
                     int curScale = com.tann.dice.Main.getSettings().getScaleAdjust();
                     int newScale = curScale + diff;
                     if (newScale <= maxScaleDelta && newScale >= minScaleDelta) {
                        Sounds.playSound(Sounds.pip);
                        com.tann.dice.Main.getSettings().setScaleAdjust(newScale);
                        com.tann.dice.Main.self().setupScale();
                        return true;
                     } else if (curScale <= maxScaleDelta && curScale >= minScaleDelta) {
                        Sounds.playSound(Sounds.error);
                        return true;
                     } else {
                        com.tann.dice.Main.getSettings().setScaleAdjust(0);
                        com.tann.dice.Main.self().setupScale();
                        Sounds.playSound(Sounds.pip);
                        return true;
                     }
                  }
               }
            });
         }
      }

      return scaleRow.pix();
   }
}
