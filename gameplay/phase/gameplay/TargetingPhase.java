package com.tann.dice.gameplay.phase.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.AbilityUtils;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.effect.targetable.ability.ui.AbilityHolder;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.screens.dungeon.panels.ConfirmButton;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpType;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialHolder;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetingPhase extends Phase {
   int unusedRolls;
   private static int[] spellInts = new int[]{45, 51, 33, 46, 48, 53, 49, 37};

   public TargetingPhase(int unusedRolls) {
      this.unusedRolls = unusedRolls;
   }

   public TargetingPhase() {
   }

   public TargetingPhase(String serial) {
      try {
         this.unusedRolls = Integer.parseInt(serial);
      } catch (Exception var3) {
         TannLog.log(serial + " -bad time " + var3.getClass());
      }
   }

   @Override
   public void activate() {
      DungeonScreen ds = DungeonScreen.get();
      ds.slideButton(ds.rollGroup, false, false);
      ds.slideButton(ds.undoButton, true, false);
      ds.slideButton(ds.doneRollingButton, false, false);
      ds.confirmButton.setState(ConfirmButton.ConfirmState.UsingDice);
      ds.slideButton(ds.confirmButton, true, false, 0.75F);
      ds.slideSpellHolder(AbilityHolder.TuckState.OnScreen, false);
   }

   @Override
   public void tick(float delta) {
      DungeonScreen ds = DungeonScreen.get();
      FightLog f = this.getFightLog();
      int newDead = f.getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities().size()
         - f.getSnapshot(FightLog.Temporality.Future).getAliveHeroEntities().size();
      boolean allDiceUsed = ds.checkAllDiceUsedCached();
      ds.confirmButton.setState(allDiceUsed ? ConfirmButton.ConfirmState.AllDiceUsed : ConfirmButton.ConfirmState.UsingDice, Math.max(0, newDead));
      if (OptionUtils.shouldShowFlashyEndTurn()) {
         com.tann.dice.Main.requestRendering();
      }
   }

   @Override
   public void deactivate() {
      DungeonScreen.get().slideButton(DungeonScreen.get().confirmButton, false, false);
      DungeonScreen.get().slideButton(DungeonScreen.get().undoButton, false, false);
      DungeonScreen.get().abilityHolder.tuck(AbilityHolder.TuckState.OffScreen, false);
      PhaseManager.get().pushPhase(new DamagePhase());
   }

   @Override
   public boolean canTarget() {
      return true;
   }

   @Override
   public HelpType getHelpType() {
      return HelpType.Combat;
   }

   @Override
   public void confirmClicked(boolean fromClick) {
      FightLog f = this.getFightLog();
      Snapshot present = f.getSnapshot(FightLog.Temporality.Present);
      DungeonScreen ds = DungeonScreen.get();
      int manaLeft = present.getTotalMana();
      int numDiceUnused = ds.numUnusedDice(true);
      int numDiceUnusedTotal = ds.numUnusedDice(false);
      int maxMana = present.getMaxMana();
      List<Tactic> usableTactics = new ArrayList<>();
      List<Ability> abilities = SpellUtils.getOnlyLivingSpells(present);
      List<Tactic> tactics = new ArrayList<>();

      for (Ability usableAbility : abilities) {
         if (usableAbility instanceof Tactic) {
            tactics.add((Tactic)usableAbility);
         }
      }

      for (Tactic tactic : tactics) {
         if (tactic.isUsable(present) && !TargetingManager.getRecommendedTargets(present, tactic, true).isEmpty()) {
            usableTactics.add(tactic);
         }
      }

      boolean castableSpellsExist = false;

      for (TP<Ability, Boolean> availableSpell : SpellUtils.getAvailableSpells(present)) {
         if (availableSpell.b) {
            Ability a = availableSpell.a;
            if (a instanceof Spell) {
               Spell s = (Spell)a;
               if (present.getSpellCost(s) <= manaLeft) {
                  castableSpellsExist = true;
               }
            }
         }
      }

      boolean finished = f.isVictoryAssured() || numDiceUnused == 0 && (manaLeft <= maxMana || !castableSpellsExist) && usableTactics.isEmpty();
      boolean banned = present.anyMandatoryUnusedDice();
      if (banned) {
         if (fromClick) {
            Sounds.playSound(Sounds.error);
            AbilityHolder.showInfo(Keyword.mandatory.getColourTaggedString() + " unused dice remaining", Keyword.mandatory.getColour());
         }
      } else {
         if (!finished) {
            if (fromClick) {
               Sounds.playSound(Sounds.error);
            }

            Pixl p = new Pixl(3);
            String start = "[notranslate][grey]-[cu] ";
            if (numDiceUnused > 0) {
               p.text(start + "[yellow]" + com.tann.dice.Main.t(numDiceUnusedTotal + " usable dice remaining")).row();
            }

            if (manaLeft > maxMana && castableSpellsExist) {
               p.text(start + com.tann.dice.Main.t("You can only keep [blue]" + maxMana + " " + Words.manaString())).row();
            }

            if (!usableTactics.isEmpty()) {
               p.text(
                     start
                        + TextWriter.getTag(AbilityUtils.TACTIC_COL)
                        + com.tann.dice.Main.t("Usable " + Words.plural("tactic", usableTactics.size()))
                        + ": "
                        + Tann.translatedCommaList(usableTactics)
                  )
                  .row();
            }

            String choiceDialogString = "[text]Are you sure you want to end turn?";
            if (com.tann.dice.Main.self().translator.shouldTranslate()) {
               choiceDialogString = "[notranslateall]" + Tann.halveString(com.tann.dice.Main.t(choiceDialogString));
            }

            ChoiceDialog choiceDialog = new ChoiceDialog(choiceDialogString, Arrays.asList(p.pix(8)), ChoiceDialog.ChoiceNames.YesCancel, new Runnable() {
               @Override
               public void run() {
                  DungeonScreen.get().popAllLight();
                  DungeonScreen.get().pop(ChoiceDialog.class);
                  Sounds.playSound(Sounds.confirm);
                  PhaseManager.get().popPhase(TargetingPhase.class);
               }
            }, new Runnable() {
               @Override
               public void run() {
                  DungeonScreen.get().popAllLight();
                  DungeonScreen.get().pop(ChoiceDialog.class);
                  Sounds.playSound(Sounds.pop);
               }
            });
            DungeonScreen.get().push(choiceDialog, false, false, true, 0.4F);
            Tann.center(choiceDialog);
         } else {
            if (fromClick) {
               Sounds.playSound(Sounds.confirm);
            }

            PhaseManager.get().popPhase(TargetingPhase.class);
         }
      }
   }

   @Override
   public String serialise() {
      return "1" + this.unusedRolls;
   }

   @Override
   public void positionTutorial(TutorialHolder tutorialHolder) {
      tutorialHolder.setPosition((int)((com.tann.dice.Main.width - tutorialHolder.getWidth()) / 2.0F), com.tann.dice.Main.height);
      Actor a = DungeonScreen.get().optionsButtonsGroup;
      Tann.slideIn(tutorialHolder, Tann.TannPosition.Top, (int)(com.tann.dice.Main.height - a.getY() + 2.0F));
   }

   @Override
   public boolean keyPress(int keycode) {
      if (this.checkForEntPress(keycode)) {
         return true;
      } else if (this.checkForSpellPress(keycode)) {
         return true;
      } else {
         return this.checkForMiscPress(keycode) ? true : super.keyPress(keycode);
      }
   }

   protected boolean checkForMiscPress(int keycode) {
      switch (keycode) {
         case 54:
            DungeonScreen.get().requestUndo();
            return true;
         case 62:
         case 66:
         case 160:
            DungeonScreen.get().confirmClicked(true);
            return true;
         default:
            return false;
      }
   }

   private boolean checkForSpellPress(int keycode) {
      int spellIndex = Tann.indexOf(spellInts, keycode);
      if (spellIndex != -1) {
         DungeonScreen ds = DungeonScreen.get();
         Ability a = ds.abilityHolder.getByIndex(spellIndex);
         if (a != null) {
            ds.popAllMedium();
            ds.abilityHolder.selectForCast(a);
            return true;
         }
      }

      return false;
   }

   private boolean checkForEntPress(int keycode) {
      DungeonScreen ds = DungeonScreen.get();
      boolean opposite = Gdx.input.isKeyPressed(59);
      boolean player = opposite;
      int digit = Tann.getDigit(keycode);
      if (digit >= 0 && digit <= 9) {
         Targetable st = DungeonScreen.get().targetingManager.getSelectedTargetable();
         if (opposite && st == null) {
            Sounds.playSound(Sounds.error);
            return false;
         }

         if (st == null) {
            player = true;
         } else {
            Eff e = st.getDerivedEffects();
            if (e.getOr(false) != null) {
               player = opposite;
            } else if (e.isFriendly()) {
               player = !opposite;
            }
         }

         List<Ent> entities = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getEntities(player, false);
         if (digit < entities.size()) {
            Ent selected = entities.get(digit);
            boolean targetableSelected = ds.targetingManager.getSelectedTargetable() != null;
            if (!targetableSelected && selected.getState(FightLog.Temporality.Present).isUsed()) {
               Sounds.playSound(Sounds.error);
               return true;
            }

            if (!targetableSelected) {
               com.tann.dice.Main.getCurrentScreen().popAllMedium();
            }

            ds.targetingManager.clicked(selected, true);
            return true;
         }
      }

      return false;
   }

   public int getUnusedRolls() {
      return this.unusedRolls;
   }

   @Override
   public boolean isDuringCombat() {
      return true;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean highlightDice() {
      return true;
   }

   @Override
   public boolean canFlee() {
      return true;
   }

   @Override
   public boolean showTargetButton() {
      return true;
   }

   @Override
   public boolean updateDice() {
      return true;
   }
}
