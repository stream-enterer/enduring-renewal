package com.tann.dice.gameplay.phase.levelEndPhase;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.effect.eff.EffBill;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.command.SimpleCommand;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.generalPanels.PartyManagementPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Glowverlay;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.List;

public class LevelEndPanel extends Group {
   public static final int BUTTON_WIDTH = 53;
   public static final int BUTTON_HEIGHT = 20;
   List<Phase> phases;
   FightLog fightLog;
   final boolean fromSave;
   List<TextWriter> underneaths = new ArrayList<>();
   private String unequipReminder;
   float time = 0.0F;
   float threshold = Tann.random(3.0F, 10.0F);
   boolean triggered;

   public LevelEndPanel(List<Phase> phases, FightLog fightLog, boolean fromSave) {
      this.fightLog = fightLog;
      this.setTransform(false);
      this.phases = phases;
      this.fromSave = fromSave;
      this.layout();
   }

   public void layout() {
      DungeonContext dc = DungeonScreen.get().getDungeonContext();
      this.clearChildren();
      Pixl p = new Pixl(2, 4).forceWidth(132);
      p.text(dc.getLevelProgressString(false)).row(4);

      for (int i = this.phases.size() - 1; i >= 0; i--) {
         if (this.phases.get(i).hasActivated()) {
            this.phases.remove(i);
         }
      }

      int MAX_SHOWN = 3;

      for (int ix = 0; ix < this.phases.size(); ix++) {
         if (ix >= 3) {
            p.row(4).text("[text]+" + (this.phases.size() - ix) + " more").row(2);
            break;
         }

         final Phase phase = this.phases.get(ix);
         StandardButton a = phase.getLevelEndButton();
         a.setRunnable(new Runnable() {
            @Override
            public void run() {
               LevelEndPanel.this.clickPhaseStart(phase);
            }
         });
         p.actor(a);
         if (ix < this.phases.size() - 1) {
            p.row(2);
         }
      }

      if (this.phases.size() > 0) {
         p.row(4);
      }

      if (dc.allowInventory()) {
         StandardButton inventory = new StandardButton("[text]Inventory", Colours.grey, 53, 20);
         p.actor(inventory).gap(4);
         inventory.setRunnable(new Runnable() {
            @Override
            public void run() {
               LevelEndPanel.this.inventoryClick();
            }
         });
         if (this.fightLog.getContext().getParty().anyNewItems()) {
            this.showGlowverlay(inventory);
         }
      }

      StandardButton cont = new StandardButton("[text]Continue", Colours.grey, 53, 20);
      p.actor(cont);
      cont.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            LevelEndPanel.this.continueClick();
            return true;
         }

         @Override
         public boolean info(int button, float x, float y) {
            if (DungeonScreen.isWish()) {
               int lv = LevelEndPanel.this.fightLog.getContext().getCurrentLevelNumber();
               LevelEndPanel.this.fightLog.addCommand(new SimpleCommand(null, new SimpleTargetable(null, new EffBill().group().kill().bEff())), false);
               int lv2 = LevelEndPanel.this.fightLog.getContext().getCurrentLevelNumber();
               if (lv != lv2 || LevelEndPanel.this.fightLog.isVictoryAssured()) {
                  PhaseManager.get().getPhase().hide();
               }
            }

            return true;
         }
      });
      Actor a = p.pix();
      Tann.become(this, a);
      this.setX((int)(com.tann.dice.Main.width / 2 - this.getWidth() / 2.0F));
      if (this.unequipReminder != null) {
         this.addTextUnderContinue(this.unequipReminder);
         this.unequipReminder = null;
      }
   }

   public void continueClick() {
      if (this.phases.size() > 0) {
         this.clickPhaseStart(this.phases.get(0));
      } else {
         String fail = this.getNoContinueReason();
         if (fail != null) {
            this.addTextUnderContinue(fail);
            Sounds.playSound(Sounds.error);
         } else {
            if (this.fightLog.getContext().getParty().getItems(false).size() > 0 && !this.fightLog.getContext().isCheckedItems()) {
               this.successConfirmDialog();
            } else {
               this.onSuccessfulContinue();
            }
         }
      }
   }

   protected void clickPhaseStart(Phase phase) {
      DungeonScreen.get().popAllLight();
      if (PhaseManager.get().getPhase().getClass() != LevelEndPhase.class) {
         Sounds.playSound(Sounds.error);
      } else {
         this.phases.remove(phase);
         PhaseManager.get().interrupt(phase);
         DungeonScreen.get().save();
      }
   }

   private void successConfirmDialog() {
      ChoiceDialog choiceDialog = new ChoiceDialog("You have new items[n]Continue without equipping?", ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
         @Override
         public void run() {
            DungeonScreen.get().popAllLight();
            DungeonScreen.get().pop(ChoiceDialog.class);
            LevelEndPanel.this.fightLog.getContext().setCheckedItems(true);
            LevelEndPanel.this.onSuccessfulContinue();
         }
      }, new Runnable() {
         @Override
         public void run() {
            DungeonScreen.get().popAllLight();
            DungeonScreen.get().pop(ChoiceDialog.class);
            Sounds.playSound(Sounds.pop);
         }
      });
      DungeonScreen.get().push(choiceDialog, true, true, true, 0.4F);
      Tann.center(choiceDialog);
   }

   private void onSuccessfulContinue() {
      PhaseManager.get().popPhase(LevelEndPhase.class);
      com.tann.dice.Main.getCurrentScreen().popAllLight();
      this.setTouchable(Touchable.disabled);
      this.slideOff();
      com.tann.dice.Main.getCurrentScreen().popAllMedium();
      Sounds.playSound(Sounds.confirm);
   }

   private String getNoContinueReason() {
      if (this.phases.size() > 0) {
         return "[red]You must choose all [yellow]rewards[cu] before continuing";
      } else if (this.fightLog.getContext().getParty().getItems(false).size() > 0 && !com.tann.dice.Main.getSettings().isHasEquipped()) {
         return "[red]Use the [light]Inventory[cu] to equip your new [grey]item[cu] before continuing";
      } else {
         return this.fightLog.getContext().getParty().getForcedItems().size() > 0 ? "[red]Some items must be equipped before continuing" : null;
      }
   }

   public static void showPartyPanel() {
      if (!PhaseManager.get().getPhase().canEquip()) {
         Sounds.playSound(Sounds.error);
      } else {
         com.tann.dice.Main.getCurrentScreen().popAllMedium();
         LevelEndPhase lep = (LevelEndPhase)PhaseManager.get().find(LevelEndPhase.class);
         if (lep != null) {
            lep.cancelUnequipReminder();
         }

         com.tann.dice.Main.getCurrentScreen().clearOldGlowverlays();
         DungeonScreen.get().getDungeonContext().setCheckedItems(true);
         PartyManagementPanel p = DungeonScreen.get().partyManagementPanel;
         p.refresh();
         p.toFront();
         com.tann.dice.Main.getCurrentScreen().push(p, true, true, false, 0.7F);
         p.setX((int)(com.tann.dice.Main.width / 2 - p.getWidth() / 2.0F));
         Tann.slideIn(
            p, Tann.TannPosition.Bot, (int)Math.min(com.tann.dice.Main.height - 5 - p.getHeight(), com.tann.dice.Main.height / 2.0F - p.getHeight() / 2.0F)
         );
         Sounds.playSound(Sounds.pip);
         p.onShow();
      }
   }

   public void slideOff() {
      this.clearUnderneaths();
      Tann.slideAway(this, Tann.TannPosition.Top, true);
   }

   private void clearUnderneaths() {
      for (TextWriter tw : this.underneaths) {
         tw.remove();
      }

      this.underneaths.clear();
   }

   public void draw(Batch batch, float parentAlpha) {
      Draw.fillActor(batch, this, Colours.dark, Colours.purple, 1);
      super.draw(batch, parentAlpha);
   }

   private void addTextUnderContinue(String text) {
      TextWriter tw = new TextWriter(text, (int)this.getWidth(), Colours.purple, 3);
      tw.setPosition((int)(this.getWidth() / 2.0F - tw.getWidth() / 2.0F), (int)(-tw.getHeight() + 1.0F));
      this.addActor(tw);
      this.underneaths.add(tw);
   }

   public void addUnequippedReminder(String oldHeroName) {
      this.unequipReminder = oldHeroName + " items unequipped";
   }

   private void showGlowverlay(StandardButton inventory) {
      Glowverlay glowverlay = new Glowverlay();
      inventory.addActor(glowverlay);
      com.tann.dice.Main.getCurrentScreen().setGlowverlay(glowverlay);
   }

   public void act(float delta) {
      boolean valid = !this.triggered && !DungeonScreen.get().partyManagementPanel.hasParent() && !this.fromSave;
      if (valid) {
         this.time += delta;
         if (this.time > this.threshold) {
            List<Global> globs = this.fightLog.getContext().getModifierGlobals();

            for (int i = 0; i < globs.size(); i++) {
               globs.get(i).levelEndAfterShortWait(this.fightLog.getContext());
            }

            this.triggered = true;
         }
      }

      super.act(delta);
   }

   public void inventoryClick() {
      if (TannStageUtils.isMouseHeld()) {
         Sounds.playSound(Sounds.flap);
      } else {
         com.tann.dice.Main.getCurrentScreen().popAllMedium();
         this.clearUnderneaths();
         showPartyPanel();
      }
   }
}
