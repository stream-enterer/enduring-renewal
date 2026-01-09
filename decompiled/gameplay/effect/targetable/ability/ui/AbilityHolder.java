package com.tann.dice.gameplay.effect.targetable.ability.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array.ArrayIterator;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.effect.targetable.ability.spell.Spell;
import com.tann.dice.gameplay.effect.targetable.ability.spell.SpellUtils;
import com.tann.dice.gameplay.effect.targetable.ability.tactic.Tactic;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.gameplay.fightLog.listener.SnapshotChangeListener;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.TargetingManager;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Chrono;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TextWisp;
import com.tann.dice.util.listener.MultitapListener;
import com.tann.dice.util.tp.TP;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.action.PixAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AbilityHolder extends Group implements SnapshotChangeListener {
   ManaHolder manaHolder;
   List<AbilityActor> abilityActors = new ArrayList<>();
   Snapshot present;
   Snapshot visual;
   static Comparator<AbilityActor> comp = new Comparator<AbilityActor>() {
      public int compare(AbilityActor o1, AbilityActor o2) {
         Ability a1 = o1.ability;
         Ability a2 = o2.ability;
         boolean o1Burst = a1.getTitle().equalsIgnoreCase("Burst");
         boolean o2Burst = a2.getTitle().equalsIgnoreCase("Burst");
         if (o1Burst) {
            return -1;
         } else if (o2Burst) {
            return 1;
         } else {
            o1Burst = a1 instanceof Spell;
            o2Burst = a2 instanceof Spell;
            if (o1Burst != o2Burst) {
               return o1Burst ? 1 : -1;
            } else {
               return o1Burst && o2Burst ? ((Spell)a1).getBaseCost() - ((Spell)a2).getBaseCost() : a1.getTitle().compareTo(a2.getTitle());
            }
         }
      }
   };
   AbilityHolder.TuckState tuckState = AbilityHolder.TuckState.OnScreen;

   public AbilityHolder(int width, FightLog fightLog) {
      this.setTransform(false);
      this.present = fightLog.getSnapshot(FightLog.Temporality.Present);
      this.visual = fightLog.getSnapshot(FightLog.Temporality.Visual);
      fightLog.registerSnapshotListener(this, FightLog.Temporality.Present, FightLog.Temporality.Visual);
      this.setSize(width, DungeonScreen.getBottomButtonHeight());
      this.manaHolder = new ManaHolder(this.visual);
      this.addActor(this.manaHolder);
      this.updateManaholderPosition();
   }

   private void updateManaholderPosition() {
      this.manaHolder.setPosition((int)(this.getWidth() / 2.0F), this.getHeight() + 1.0F);
   }

   private void updateSpells() {
      boolean port = com.tann.dice.Main.isPortrait();
      List<TP<Ability, Boolean>> spellMap = SpellUtils.getAvailableSpells(this.present);
      List<Ability> availableSpells = new ArrayList<>();

      for (TP<Ability, Boolean> t : spellMap) {
         availableSpells.add(t.a);
      }

      for (int i = this.abilityActors.size() - 1; i >= 0; i--) {
         AbilityActor sa = this.abilityActors.get(i);
         if (!availableSpells.contains(sa.ability)) {
            this.abilityActors.remove(sa);
            sa.addAction(Actions.sequence(Actions.moveTo(sa.getX(), -sa.getHeight(), this.getSpd(), Chrono.i), Actions.removeActor()));
         }
      }

      int spellsPerRow = availableSpells.size();
      boolean staggered = port && availableSpells.size() > 3;
      if (staggered) {
         spellsPerRow = (int)Math.ceil(availableSpells.size() / 2.0F);
      }

      int cardWidth = (int)Math.min(48.0F, this.getWidth() / spellsPerRow);
      boolean showText = true;

      for (TP<Ability, Boolean> s : spellMap) {
         showText &= AbilityActor.canDisplay(s.a, cardWidth);
      }

      for (final TP<Ability, Boolean> s : spellMap) {
         boolean found = false;

         for (AbilityActor sa : this.abilityActors) {
            if (sa.ability == s.a) {
               sa.setDead(!s.b);
               sa.setPresentSnapshot(this.present);
               sa.setShowText(showText);
               found = true;
               break;
            }
         }

         if (!found) {
            AbilityActor sax = new AbilityActor(s.a, cardWidth, this.present);
            sax.setShowText(showText);
            this.abilityActors.add(sax);
            this.addActor(sax);
            sax.setDead(!s.b);
            sax.setPosition(-1.0F, -1.0F);
            sax.addListener(new MultitapListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  AbilityHolder.this.selectForCast(s.a);
                  return true;
               }

               @Override
               public boolean info(int button, float x, float y) {
                  AbilityHolder.this.selectForInfo(s.a);
                  return true;
               }
            });
         }
      }

      Collections.sort(this.abilityActors, comp);
      List<AbilityActor> curr = this.abilityActors;
      float floatGap = (this.getWidth() - spellsPerRow * cardWidth) / (spellsPerRow + 1);
      int gap = (int)floatGap;
      int offset = Math.round(((floatGap - gap) * spellsPerRow - 1.0F) / 2.0F) + (port ? gap : gap);
      if (staggered) {
         int amtSecondRow = (int)Math.ceil(curr.size() / 2.0F);
         int amtFirstRow = curr.size() - amtSecondRow;

         for (int ix = 0; ix < curr.size(); ix++) {
            AbilityActor sp = curr.get(ix);
            sp.reset(cardWidth);
            boolean firstRow = ix < Math.ceil(curr.size() / 2.0F);
            int sOff = offset;
            int offsetI = !firstRow ? ix - curr.size() / 2 : ix;
            if (!firstRow && amtSecondRow != amtFirstRow) {
               sOff = (int)(-sp.getWidth()) / 2;
            }

            int targetX = (int)(offsetI * (gap + sp.getWidth())) + sOff;
            int targetY = !firstRow ? (int)sp.getHeight() : 0;
            if (sp.getX() == -1.0F && sp.getY() == -1.0F) {
               sp.setPosition(targetX, -sp.getHeight());
            }

            sp.addAction(PixAction.moveTo(targetX, targetY, this.getSpd(), Chrono.i));
         }
      } else {
         for (int ix = 0; ix < curr.size(); ix++) {
            AbilityActor spx = curr.get(ix);
            spx.reset(cardWidth);
            int targetX = (int)(ix * (gap + spx.getWidth())) + offset;
            if (spx.getX() == -1.0F && spx.getY() == -1.0F) {
               spx.setPosition(targetX, -spx.getHeight());
            }

            spx.addAction(PixAction.moveTo(targetX, 0, this.getSpd(), Chrono.i));
         }
      }

      this.setHeight(29 * (staggered ? 2 : 1));
      this.updateManaholderPosition();
   }

   private boolean isAutoCast(Ability a) {
      return !a.getDerivedEffects().needsTarget() && PhaseManager.get().getPhase().canTarget();
   }

   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
   }

   @Override
   public void snapshotChanged(FightLog.Temporality temporality, Snapshot newSnapshot) {
      switch (temporality) {
         case Base:
         case Future:
         default:
            break;
         case Visual:
            this.visual = newSnapshot;
            this.manaHolder.update(this.visual.getTotalMana(), this.visual.getMaxMana());
            break;
         case Present:
            this.present = newSnapshot;
            this.updateSpells();
      }
   }

   public void heroDiceRolled() {
      this.updateSpells();
   }

   public void allDiceLanded() {
      this.updateSpells();
   }

   public void addWisp(String s) {
      this.addWisp(s, 1.1F);
   }

   public void addWisp(String s, float delay) {
      TextWisp tw = new TextWisp(s, 0, 0, 0.3F, delay);
      this.addActor(tw);
      int gap = 2;
      int y = 0;
      ArrayIterator var6 = this.getChildren().iterator();

      while (var6.hasNext()) {
         Actor a = (Actor)var6.next();
         y = (int)Math.max((float)y, a.getY() + gap);
      }

      tw.setPosition((int)(this.manaHolder.getX() - tw.getWidth() / 2.0F), y);
   }

   public Ability getByIndex(int index) {
      return this.abilityActors.size() > index ? this.abilityActors.get(index).ability : null;
   }

   public void selectForInfo(Ability ability) {
      TargetingManager tm = DungeonScreen.get().targetingManager;
      if (tm.getSelectedTargetable() == ability) {
         DungeonScreen.get().popSingleLight();
      }

      DungeonScreen.get().popAllLight();
      tm.showExplanelInactive(ability);
   }

   public void selectForCast(Ability ability) {
      if (!PhaseManager.get().getPhase().canTarget()) {
         this.selectForInfo(ability);
      } else if (!this.present.isEnd()) {
         Boolean canCast = null;

         for (AbilityActor sa : this.abilityActors) {
            if (sa.ability == ability) {
               if (sa.isDead()) {
                  canCast = false;
               } else {
                  canCast = true;
               }
               break;
            }
         }

         if (canCast == null) {
            canCast = false;
            TannLog.log("Error finding spell in party: " + ability);
         }

         if (!canCast) {
            Sounds.playSound(Sounds.error);
            showInfo("[red]Can't use abilities from defeated heroes", Colours.red);
         } else {
            TargetingManager tm = DungeonScreen.get().targetingManager;
            if (tm.getSelectedTargetable() == ability) {
               DungeonScreen.get().popSingleLight();
               Sounds.playSound(Sounds.pop);
            } else {
               DungeonScreen.get().popAllLight();
               if (!ability.isUsable(this.present)) {
                  Sounds.playSound(Sounds.error);
                  if (ability instanceof Spell) {
                     showInfo("[red]Not enough [blue]mana", Colours.red);
                  } else if (ability instanceof Tactic) {
                     showInfo("[red]All costs must be present on unused dice", Colours.red);
                  }
               } else {
                  if (this.isAutoCast(ability)) {
                     String invalidReason = tm.getInvalidTargetReason(null, ability, true);
                     if (invalidReason == null) {
                        tm.target(null, ability);
                     } else {
                        Sounds.playSound(Sounds.error);
                        showInfo(invalidReason, Colours.red);
                     }
                  } else {
                     tm.setSelectedTargetable(ability);
                  }
               }
            }
         }
      }
   }

   private float getSpd() {
      return DungeonScreen.get().isLoading() ? 0.0F : 0.3F;
   }

   public void tuck(AbilityHolder.TuckState tuckState, boolean instant) {
      int toX = (int)this.getX();
      int toY = tuckState == AbilityHolder.TuckState.OffScreen ? (int)(-this.getHeight()) : 0;
      if (instant) {
         this.setPosition(toX, toY);
      } else {
         this.addAction(PixAction.moveTo(toX, toY, this.getSpd(), Chrono.i));
      }

      this.tuckState = tuckState;
   }

   public static void showInfo(String message, Color c) {
      TextWriter text = new TextWriter(message, 100, c, 3);
      float displayTime = 0.5F + text.getWidth() * text.getHeight() * 3.5E-4F;
      text.setTouchable(Touchable.disabled);
      com.tann.dice.Main.getCurrentScreen().addActor(text);
      text.setPosition((int)(com.tann.dice.Main.width / 2 - text.getWidth() / 2.0F), -text.getHeight());
      text.addAction(
         Actions.sequence(
            PixAction.moveTo((int)text.getX(), 47, 0.3F, Interpolation.pow2Out),
            Actions.delay(displayTime),
            PixAction.moveTo((int)text.getX(), (int)(-text.getHeight()) - 10, 0.3F, Interpolation.pow2Out),
            Actions.removeActor()
         )
      );
   }

   public static enum TuckState {
      OffScreen,
      Tucked,
      OnScreen;
   }
}
