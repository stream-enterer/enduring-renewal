package com.tann.dice.gameplay.fightLog.command;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.effect.targetable.DieTargetable;
import com.tann.dice.gameplay.effect.targetable.SimpleTargetable;
import com.tann.dice.gameplay.effect.targetable.Targetable;
import com.tann.dice.gameplay.effect.targetable.ability.Ability;
import com.tann.dice.gameplay.fightLog.Snapshot;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffect;
import com.tann.dice.screens.dungeon.panels.combatEffects.CombatEffectActor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
   public static String SKIP = "SKIP";
   private transient boolean skipped;
   transient boolean impacted = false;
   private transient List<Actor> linkedActors = new ArrayList<>();
   transient boolean cached;
   transient boolean marked;
   private transient CombatEffect combatEffect;
   List<Ent> empty = Arrays.asList();
   private String lockedSave;
   public static final String TARGETING_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
   public static final int NULL_TARGET_INDEX = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length() - 1;
   public static final int NULL_SPELL_INDEX = NULL_TARGET_INDEX;

   protected Command() {
   }

   public abstract Ent getSource();

   public void enact(Snapshot snapshot) {
      this.internalEnact(snapshot);
      this.postEnact(snapshot);
      snapshot.checkHpLimits(this.getSource(), this);
      snapshot.registerCommand(this);
   }

   public void preEnact(Snapshot snapshot) {
   }

   protected void postEnact(Snapshot snapshot) {
   }

   protected abstract void internalEnact(Snapshot var1);

   private CombatEffect getCombatEffect() {
      if (!this.cached) {
         this.combatEffect = this.makeCombatEffect();
         this.cached = true;
      }

      return this.combatEffect;
   }

   public void startAnimation(Snapshot beforeShot) {
      if (this.shouldSkipAnimation(beforeShot)) {
         this.skipped = true;
      } else {
         this.showAnimation(this.getCombatEffect());
      }
   }

   protected abstract boolean shouldSkipAnimation(Snapshot var1);

   public abstract CombatEffect makeCombatEffect();

   protected abstract void showAnimation(CombatEffect var1);

   public boolean getFinishedAnimating() {
      if (this.skipped) {
         return true;
      } else if (this.getCombatEffect() == null) {
         this.markFinished();
         return true;
      } else {
         return this.getCombatEffect().isFinished();
      }
   }

   private void markFinished() {
      if (!this.marked) {
         this.playSoundNoEffect();
      }

      this.marked = true;
   }

   protected void playSoundNoEffect() {
   }

   public boolean getStartedAnimating() {
      return this.getCombatEffect() != null && !this.skipped ? this.getCombatEffect().isStarted() : true;
   }

   public void overrideSkip() {
      this.skipped = true;
   }

   public boolean getImpacted() {
      return this.skipped || this.impacted;
   }

   public void checkImpacted() {
      if (!this.impacted) {
         if (this.getSource() != null && !this.getSource().getEntPanel().hasParent()) {
            this.impacted = true;
            this.skipped = true;
         }

         boolean newImpacted;
         if (this.getCombatEffect() == null) {
            newImpacted = true;
         } else {
            newImpacted = this.getCombatEffect().isImpacted();
         }

         this.impacted = newImpacted;
      }
   }

   public boolean skipped() {
      return this.skipped;
   }

   public void undo() {
      for (Actor a : this.linkedActors) {
         a.addAction(Actions.sequence(Actions.fadeOut(0.2F), Actions.removeActor()));
      }

      this.linkedActors.clear();
   }

   public abstract boolean canUndo();

   public static Command create(Targetable targetable, Ent target) {
      if (targetable instanceof DieTargetable) {
         return new DieCommand((DieTargetable)targetable, target);
      } else if (targetable instanceof SimpleTargetable) {
         return new SimpleCommand(target, (SimpleTargetable)targetable);
      } else if (targetable instanceof Ability) {
         return new AbilityCommand((Ability)targetable, target);
      } else {
         throw new RuntimeException("No command for " + target.getClass());
      }
   }

   public void linkActor(CombatEffectActor actor) {
      this.linkedActors.add(actor);
   }

   public List<Ent> getAllTargets() {
      return this.empty;
   }

   public boolean onRescue(Hero saved, Ent saver, Snapshot present, Snapshot prePresent) {
      return false;
   }

   public boolean onKill(Ent killer, Snapshot prePresent, Snapshot present) {
      return false;
   }

   public String toSave(Snapshot previous) {
      return "Cannot serialise " + this.getClass().getSimpleName();
   }

   public void lockSave(Snapshot previous) {
      this.lockedSave = this.toSave(previous);
   }

   public String getLockedSave() {
      return this.lockedSave;
   }

   public static Command.CommandType getType(String command) {
      char type = command.charAt(0);
      switch (type) {
         case '0':
            return Command.CommandType.Start;
         case '1':
         case '3':
            return Command.CommandType.Hero;
         case '2':
            return Command.CommandType.Monster;
         case '4':
         case '5':
            return Command.CommandType.End;
         default:
            throw new RuntimeException("uhoh, unknown serialisation type " + type);
      }
   }

   public static Command load(String saved, Snapshot snapshot) {
      char type = saved.charAt(0);

      try {
         switch (type) {
            case '0':
               return new StartTurnCommand(saved);
            case '1':
            case '2':
               return new DieCommand(saved, snapshot);
            case '3':
               return new AbilityCommand(saved, snapshot);
            case '4':
            case '5':
               return new EndTurnCommand(saved);
            default:
               throw new RuntimeException("uhoh, unknown serialisation type " + type);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         throw new RuntimeException("error loading command from " + saved);
      }
   }

   public void forceImpacted() {
      this.skipped = true;
   }

   public static String intToChar(int input) {
      if (input >= 0 && input < "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length()) {
         return "" + "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(input);
      } else {
         throw new RuntimeException("unable to convert int to char: " + input);
      }
   }

   public static int charToInt(char c) {
      int result = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c);
      if (result < 0) {
         throw new RuntimeException("unable to convert char to int: " + c);
      } else {
         return result;
      }
   }

   public static enum CommandType {
      Start,
      Hero,
      Monster,
      End,
      Simple;
   }
}
