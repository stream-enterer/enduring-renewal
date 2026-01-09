package com.tann.dice.gameplay.trigger.global.phase.addPhase;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseGenerator;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.TannLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalAddPhase extends Global {
   final List<PhaseGenerator> phaseGenerators;

   public GlobalAddPhase(PhaseGenerator phaseGenerator) {
      this(Arrays.asList(phaseGenerator));
   }

   public GlobalAddPhase(List<PhaseGenerator> phaseGenerators) {
      this.phaseGenerators = phaseGenerators;
   }

   @Override
   public List<Phase> getPhases(DungeonContext dungeonContext) {
      List<Phase> result = new ArrayList<>();

      for (PhaseGenerator pg : this.phaseGenerators) {
         try {
            result.addAll(pg.get(dungeonContext));
         } catch (Exception var6) {
            TannLog.error(var6.getMessage());
            var6.printStackTrace();
            if (TestRunner.isTesting()) {
               throw var6;
            }

            result.add(new MessagePhase(var6.getClass().getSimpleName() + " from " + pg.getClass().getSimpleName()));
         }
      }

      return result;
   }

   @Override
   public String describeForSelfBuff() {
      String added = "";

      for (PhaseGenerator pg : this.phaseGenerators) {
         String desc = pg.describe();
         if (desc != null) {
            added = added + desc.toLowerCase();
         }
      }

      return added.length() == 0 ? null : added;
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = Collision.PHASE;

      for (PhaseGenerator phaseGenerator : this.phaseGenerators) {
         bit |= phaseGenerator.getCollisionBits(player);
      }

      return bit;
   }

   public List<PhaseGenerator> getPhaseGeneratorsDebug() {
      return this.phaseGenerators;
   }

   @Override
   public Actor makePanelActorI(boolean big) {
      int textWidth = 50;
      Pixl gp = new Pixl(2);

      for (int i = 0; i < this.phaseGenerators.size(); i++) {
         PhaseGenerator pg = this.phaseGenerators.get(i);
         Actor panel = pg.makePanel();
         if (panel != null) {
            gp.actor(panel);
         } else {
            gp.text("[text]" + this.phaseGenerators.get(i).describe(), 50).row();
         }
      }

      Actor a = gp.pix();
      a.setName("notalone");
      return a;
   }

   @Override
   public boolean allTurnsOnly() {
      return true;
   }

   @Override
   public boolean isDescribedAsBeforeFight() {
      return true;
   }

   @Override
   public boolean isMultiplable() {
      return true;
   }

   @Override
   public void onPick(DungeonContext context) {
      List<Phase> phases = this.getPhases(context);
      if (phases != null) {
         for (Phase phase : phases) {
            PhaseManager.get().pushPhaseNext(phase);
         }
      }
   }

   @Override
   public String hyphenTag() {
      return this.phaseGenerators.get(0).hyphenTag();
   }
}
