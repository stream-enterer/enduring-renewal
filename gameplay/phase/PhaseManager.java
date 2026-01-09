package com.tann.dice.gameplay.phase;

import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.TargetingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.screens.dungeon.RollManager;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.List;

public class PhaseManager {
   private static PhaseManager self;
   List<PhaseListen> phaseListens = new ArrayList<>();
   private List<Phase> phaseStack = new ArrayList<>();
   NothingPhase nothingPhase = new NothingPhase();

   public static PhaseManager get() {
      if (self == null) {
         self = new PhaseManager();
      }

      return self;
   }

   public static void resetSingleton() {
      self = null;
   }

   public boolean has(Class<? extends Phase> clazz) {
      for (Phase p : this.phaseStack) {
         if (clazz.isInstance(p)) {
            return true;
         }
      }

      return false;
   }

   public Phase find(Class<? extends Phase> clazz) {
      for (Phase p : this.phaseStack) {
         if (clazz.isInstance(p)) {
            return p;
         }
      }

      return null;
   }

   public Phase getPhase() {
      return (Phase)(this.phaseStack.size() == 0 ? this.nothingPhase : this.phaseStack.get(0));
   }

   public void pushPhase(Phase phase) {
      for (Phase p : this.phaseStack) {
         if (p.getClass().equals(phase.getClass())) {
            if (p instanceof EnemyRollingPhase || p instanceof PlayerRollingPhase || p instanceof TargetingPhase) {
               TannLog.log("Skipping duplicate phase: " + p.getClass());
               return;
            }

            TannLog.log("Duplicate phase detected: " + p.getClass());
         }
      }

      this.phaseStack.add(this.phaseStack.size(), phase);
      if (this.phaseStack.size() == 1) {
         this.activatePhase(phase);
      }
   }

   public void popPhase() {
      if (this.phaseStack.size() == 0) {
         System.err.println("uhoh, trying to pop with empty phase stack");
      } else {
         Phase toRemove = this.phaseStack.get(0);
         this.phaseStack.remove(toRemove);
         toRemove.deactivate();
         if (this.phaseStack.size() == 0) {
            System.err.println("popping error, previous phase was " + toRemove.toString());
         }

         if (this.getPhase() != null) {
            this.activatePhase(this.getPhase());
         }
      }
   }

   public void activateCurrentPhase() {
      if (this.getPhase() != null) {
         this.activatePhase(this.getPhase());
      }
   }

   public void popPhase(Class clazz) {
      if (!clazz.isInstance(this.getPhase())) {
         System.err.println("Trying to pop a class of type " + clazz.getSimpleName() + " when the phase is " + this.getPhase().toString());
      } else {
         this.popPhase();
      }
   }

   public void clearPhases() {
      this.phaseStack.clear();
   }

   private void activatePhase(Phase phase) {
      phase.internalActivate();
      this.notifyListeners(phase);
   }

   public void checkPhaseIsDone() {
      Phase p = this.getPhase();
      if (p != null) {
         p.checkIfDone();
      }
   }

   public void tick(float delta) {
      this.checkPhaseIsDone();
      Phase p = this.getPhase();
      if (p != null) {
         p.tick(delta);
      }
   }

   public void interrupt(Phase phase) {
      this.getPhase().hide();
      this.phaseStack.add(0, phase);
      this.activatePhase(this.getPhase());
   }

   public void registerPhaseListen(PhaseListen pl) {
      this.phaseListens.add(pl);
   }

   private void notifyListeners(Phase phase) {
      for (int i = 0; i < this.phaseListens.size(); i++) {
         this.phaseListens.get(i).newPhase(phase);
      }
   }

   public void clearListeners() {
      this.phaseListens.clear();
   }

   public List<String> serialise() {
      List<String> result = new ArrayList<>();

      for (Phase p : this.phaseStack) {
         if (Boolean.FALSE.equals(RollManager.predictionSavePlayer) && p instanceof EnemyRollingPhase) {
            result.add("0");
         } else if (p.requiresSerialisation()) {
            result.add(p.serialise());
         }
      }

      return result;
   }

   public void deserialise(List<String> phases) {
      for (String pd : phases) {
         Phase p = Phase.deserialise(pd);
         this.pushPhase(p);
      }
   }

   public void pushPhaseAfter(Phase phase, Class<? extends Phase>... afters) {
      int li = -1;

      for (Class<? extends Phase> after : afters) {
         li = Math.max(this.lastIndexOf(after), li);
      }

      if (li == -1) {
         this.pushPhaseNext(phase);
      } else {
         this.phaseStack.add(li + 1, phase);
      }
   }

   public void pushPhaseAfter(Phase phase, Class<? extends Phase> after) {
      if (this.has(after)) {
         this.phaseStack.add(this.lastIndexOf(after) + 1, phase);
      } else {
         this.pushPhaseNext(phase);
      }
   }

   public void pushPhaseBefore(LevelEndPhase phase, Class<EnemyRollingPhase> before) {
      if (this.has(before)) {
         this.phaseStack.add(this.lastIndexOf(before), phase);
      } else {
         this.pushPhaseNext(phase);
      }
   }

   private int lastIndexOf(Class<? extends Phase> phaseClass) {
      for (int i = this.phaseStack.size() - 1; i >= 0; i--) {
         if (phaseClass.isInstance(this.phaseStack.get(i))) {
            return i;
         }
      }

      return -1;
   }

   public void forceNext(Phase phase) {
      if (this.phaseStack.size() == 0) {
         this.pushPhase(phase);
      } else {
         this.phaseStack.add(1, phase);
      }
   }

   public void pushPhaseNext(Phase phase) {
      if (this.phaseStack.size() == 0) {
         this.phaseStack.add(phase);
      } else {
         this.phaseStack.add(1, phase);
      }
   }

   public void removePhaseClass(Class<? extends Phase> clazz) {
      for (int i = this.phaseStack.size() - 1; i >= 0; i--) {
         Phase p = this.phaseStack.get(i);
         if (clazz.isInstance(p)) {
            this.phaseStack.remove(i);
         }
      }
   }

   public List<Phase> clearPhasesAndReturnEndable() {
      List<Phase> result = new ArrayList<>();

      for (Phase phase : this.phaseStack) {
         if (phase instanceof RandomRevealPhase) {
            result.add(phase);
         }
      }

      this.phaseStack.clear();
      return result;
   }

   public void pushPhaseZero(Phase phase) {
      this.phaseStack.add(0, phase);
   }

   public void deletePhase(Phase phase) {
      this.phaseStack.remove(phase);
   }
}
