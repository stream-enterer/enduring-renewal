package com.tann.dice.gameplay.battleTest.template;

import com.tann.dice.gameplay.battleTest.Difficulty;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonster;
import com.tann.dice.gameplay.content.gen.pipe.entity.monster.PipeMonsterGenerated;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelTemplate {
   private final List<MonsterType> fixed;
   private final List<MonsterType> potentialExtras;
   private final int numExtras;
   private final List<MonsterType> actualExtras = new ArrayList<>();
   private boolean boss;

   public LevelTemplate(List<MonsterType> fixed, List<MonsterType> potentialExtras) {
      this.fixed = fixed;
      this.potentialExtras = potentialExtras;
      this.numExtras = potentialExtras.size();
      this.resetExtras();
   }

   public LevelTemplate(Zone zone, Difficulty difficulty, int playerTier, float rarityRandom, List<Global> globals) {
      this.fixed = new ArrayList<>();
      List<MonsterType> pExtras = MonsterTypeLib.getMonsters(zone, playerTier, rarityRandom);
      if (pExtras.size() <= 3) {
         throw new RuntimeException("Too few monsters for zone: " + playerTier + ":" + difficulty + ":" + zone + "(" + pExtras + ")");
      } else {
         this.potentialExtras = pExtras;
         this.affectByGlobals(globals);
         int maxExtras = difficulty.getMaxMonsterTypes() + (Math.random() > 0.5 ? 0 : -1);
         int tmpNumExtras = Math.min(maxExtras, pExtras.size());
         if (Math.random() > 0.85) {
            tmpNumExtras = 1;
         }

         this.numExtras = tmpNumExtras;
         this.resetExtras();
      }
   }

   private void affectByGlobals(List<Global> globals) {
      for (Global global : globals) {
         global.affectMonsterPool(this.potentialExtras);
      }
   }

   public List<MonsterType> getInitialSetup() {
      List<MonsterType> start = new ArrayList<>(this.fixed);

      for (int mIndex = 0; mIndex < this.actualExtras.size(); mIndex++) {
         MonsterType mt = this.actualExtras.get(mIndex);

         for (int i = 0; i < mt.getMinInFight(); i++) {
            start.add(mt);
         }
      }

      if (start.size() == 0) {
         start.add(this.getExtra());
      }

      return start;
   }

   public MonsterType getExtra() {
      return this.actualExtras.size() == 0 ? PipeMonsterGenerated.makeMonstExt() : Tann.random(this.actualExtras);
   }

   @Override
   public String toString() {
      return "fixed: " + this.fixed + " -- variable: " + this.actualExtras;
   }

   public List<MonsterType> getExtrasList() {
      return this.actualExtras;
   }

   public void resetExtras() {
      this.actualExtras.clear();
      this.actualExtras.addAll(this.potentialExtras);
      Collections.shuffle(this.actualExtras);

      while (this.actualExtras.size() > this.numExtras) {
         this.actualExtras.remove(0);
      }

      if (!this.boss && OptionLib.GENERATED_MONSTERS.c()) {
         int amtToGen = Tann.randomRound(this.actualExtras.size() * OptionUtils.genChance());

         for (int i = 0; i < amtToGen; i++) {
            this.actualExtras.set(i, PipeMonster.makeGen());
         }
      }
   }

   public boolean isLocked() {
      for (MonsterType mt : this.fixed) {
         if (UnUtil.isLocked(mt)) {
            return true;
         }
      }

      return false;
   }

   public void markAsBoss() {
      this.boss = true;
   }
}
