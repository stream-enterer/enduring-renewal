package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.battleTest.Zone;
import com.tann.dice.gameplay.battleTest.testProvider.TierStats;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.Monster;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.lib.MonsterTypeLib;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.book.views.MonsterLedgerView;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.List;

public class ChallengeType {
   List<String> extraMonsters;

   public ChallengeType(List<String> extraMonsters) {
      this.extraMonsters = extraMonsters;
   }

   public ChallengeType() {
   }

   public static ChallengeType generate(ChallengePhase.ChallengeDifficulty challengeDifficulty, int currentLevelNumber) {
      int attempts = 10;

      for (int attempt = 0; attempt < attempts; attempt++) {
         ChallengeType challengeType = generateHandPicked(currentLevelNumber, challengeDifficulty);
         if (challengeType != null) {
            return challengeType;
         }
      }

      return null;
   }

   public static ChallengeType generateHandPicked(int currentLevelNumber, ChallengePhase.ChallengeDifficulty challengeDifficulty) {
      float levelRatio = TierStats.getLevelRatio(currentLevelNumber);
      switch (challengeDifficulty) {
         case Easy:
            List<MonsterType> options = Tann.asList(
               MonsterTypeLib.byName("slimelet"),
               MonsterTypeLib.byName("rat"),
               MonsterTypeLib.byName("archer"),
               MonsterTypeLib.byName("caw egg"),
               MonsterTypeLib.byName("dragon egg"),
               MonsterTypeLib.byName("Illusion")
            );
            int i = options.size() - 1;

            for (; i >= 0; i--) {
               if (UnUtil.isLocked(options.get(i))) {
                  options.remove(i);
               }
            }

            List<MonsterType> actual = new ArrayList<>();
            MonsterType chosen = Tann.randomChanced(options.toArray(new MonsterType[0]));
            int amt = Math.round(Math.max(1.0F, levelRatio / chosen.getSummonValue() * 2.4F));

            for (int ix = 0; ix < amt; ix++) {
               actual.add(chosen);
            }

            return new ChallengeType(MonsterTypeLib.serialise(actual));
         case Standard:
            float rand = Tann.random();
            Zone lt = Zone.guessFromLevel(currentLevelNumber);
            List<MonsterType> validMonsters = new ArrayList<>();

            for (MonsterType mt : lt.validMonsters) {
               if (!UnUtil.isLocked(mt) && mt.isAllowedInChallenges() && mt.validRarity(rand)) {
                  validMonsters.add(mt);
               }
            }

            float targetStrength = (4 + currentLevelNumber) * 0.33F;
            int ATTEMPTS = 1000;
            List<MonsterType> currentAttempt = new ArrayList<>();

            for (int attempt = 0; attempt < 1000; attempt++) {
               currentAttempt.clear();
               int amtToAdd = (int)(1.0 + Math.random() * 2.0);

               for (int i = 0; i < amtToAdd; i++) {
                  currentAttempt.add(Tann.random(validMonsters));
               }

               float strength = 0.0F;

               for (MonsterType mtx : currentAttempt) {
                  strength += mtx.getSimulatedStrength();
               }

               if (strength >= targetStrength && strength / targetStrength < 1.2F) {
                  return new ChallengeType(MonsterTypeLib.serialise(currentAttempt));
               }
            }

            currentAttempt.clear();

            for (int i = 0; i < 1 + currentLevelNumber / 4; i++) {
               currentAttempt.add(MonsterTypeLib.byName("missingno"));
            }

            return new ChallengeType(MonsterTypeLib.serialise(currentAttempt));
         default:
            return new ChallengeType(MonsterTypeLib.serialise(MonsterTypeLib.listName("bug")));
      }
   }

   public List<MonsterType> getMonsterTypes() {
      List<MonsterType> monsters = new ArrayList<>();

      for (String s : this.extraMonsters) {
         monsters.add(MonsterTypeLib.byName(s));
      }

      return monsters;
   }

   public Actor makeActor() {
      Pixl p = new Pixl(2);
      p.text("[text]Extra " + Words.plural("monster", this.getMonsterTypes().size())).row();

      for (final MonsterType mt : this.getMonsterTypes()) {
         MonsterLedgerView mav = new MonsterLedgerView(mt, false);
         mav.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               Sounds.playSound(Sounds.pip);
               EntPanelInventory entPanelInventory = new EntPanelInventory(new Monster(mt));
               com.tann.dice.Main.getCurrentScreen().push(entPanelInventory, true, true, true, 0.7F);
               Tann.center(entPanelInventory);
               return true;
            }
         });
         p.actor(mav, 100.0F);
      }

      return p.pix();
   }

   public void activate(FightLog fightLog) {
      DungeonContext context = fightLog.getContext();
      List<MonsterType> mTypes = context.getCurrentLevel().getMonsterList();
      mTypes.addAll(this.getMonsterTypes());
      List<Monster> result = new ArrayList<>();

      for (MonsterType mt : mTypes) {
         result.add(new Monster(mt));
      }

      List<Hero> heroes = context.getParty().getHeroes();
      fightLog.resetDueToFiddling(heroes, result);
   }

   public float getPower() {
      float total = 0.0F;

      for (MonsterType monsterType : this.getMonsterTypes()) {
         total += monsterType.getSummonValue();
      }

      return total;
   }
}
