package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Rectactor;
import com.tann.dice.util.Tann;
import com.tann.dice.util.lang.Words;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;

public class ChallengePhase extends Phase {
   private ChallengeType challengeType;
   private ChallengeReward challengeReward;
   private ChoiceDialog choiceDialog;
   boolean alreadyChosen = false;
   public static final Color CHALLENGE_COL = Colours.make(237, 58, 106);

   public ChallengePhase(ChallengeType challengeType, ChallengeReward challengeReward) {
      this.challengeType = challengeType;
      this.challengeReward = challengeReward;
   }

   public static ChallengePhase make(String saved) {
      return ((ChallengeData)com.tann.dice.Main.getJson().fromJson(ChallengeData.class, saved)).makePhase();
   }

   public static ChallengePhase generate(ChallengePhase.ChallengeDifficulty challengeDifficulty, int currentLevelNumber, DungeonContext dc) {
      ChallengeType challengeType = ChallengeType.generate(challengeDifficulty, currentLevelNumber);
      if (challengeType == null) {
         return null;
      } else {
         ChallengeReward challengeReward = ChallengeReward.generate(challengeDifficulty, dc, currentLevelNumber, challengeType);
         return challengeReward == null ? null : new ChallengePhase(challengeType, challengeReward);
      }
   }

   @Override
   public String serialise() {
      return "9" + com.tann.dice.Main.getJson().toJson(new ChallengeData(this.challengeType, this.challengeReward));
   }

   @Override
   public void activate() {
      Group g = this.makeChallengeActor();
      this.choiceDialog = new ChoiceDialog(null, Arrays.asList(g), ChoiceDialog.ChoiceNames.AcceptDecline, new Runnable() {
         @Override
         public void run() {
            if (ChallengePhase.this.alreadyChosen) {
               Sounds.playSound(Sounds.error);
            } else {
               ChallengePhase.this.accept();
            }
         }
      }, new Runnable() {
         @Override
         public void run() {
            if (ChallengePhase.this.alreadyChosen) {
               Sounds.playSound(Sounds.error);
            } else {
               ChallengePhase.this.reject();
            }
         }
      });
      Sounds.playSound(Sounds.pip);
      DungeonScreen.get().addActor(this.choiceDialog);
      this.choiceDialog.setY((int)(com.tann.dice.Main.height / 2 - this.choiceDialog.getHeight() / 2.0F));
      Tann.slideIn(this.choiceDialog, Tann.TannPosition.Right, (int)((com.tann.dice.Main.width - this.choiceDialog.getWidth()) / 2.0F), 0.3F);
   }

   public Group makeChallengeActor() {
      Actor challenge = this.challengeType.makeActor();
      Actor reward = this.challengeReward.makeActor(false);
      int gap = 4;
      Pixl left = new Pixl(gap).text("[orange]Challenge:").row().actor(challenge);
      Pixl right = new Pixl(gap).text("[green]" + Words.plural("Reward", this.challengeReward.getNumRewards()) + ":").row().actor(reward);
      Actor lAc = left.pix();
      Actor rAc = right.pix();
      return new Pixl(3).actor(lAc).actor(new Rectactor(1, (int)Math.max(lAc.getHeight(), rAc.getHeight()), Colours.orange)).actor(rAc).pix(2);
   }

   private void reject() {
      Sounds.playSound(Sounds.pop);
      this.alreadyChosen = true;
      this.challengeReward.reject(this.getFightLog());
      this.getFightLog().getContext().getStatsManager().onChallenge(false);
      PhaseManager.get().popPhase(ChallengePhase.class);
      DungeonScreen.get().save();
   }

   private void accept() {
      Sounds.playSound(Sounds.boost);
      this.alreadyChosen = true;
      FightLog f = this.getFightLog();
      f.getContext().getStatsManager().onChallenge(true);
      this.challengeType.activate(f);
      this.challengeReward.activate(f);
      Actor a = RandomRevealPhase.makeWholeRevealGroup(
         "[green]Challenge " + Words.plural("reward", this.challengeReward.getNumRewards()) + ":", this.challengeReward.getRewards(), new Runnable() {
            @Override
            public void run() {
               com.tann.dice.Main.getCurrentScreen().popAllMedium();
            }
         }
      );
      com.tann.dice.Main.getCurrentScreen().push(a, true, true, true, 0.7F);
      Tann.center(a);
      PhaseManager.get().popPhase(ChallengePhase.class);
      DungeonScreen.get().save();
   }

   @Override
   public void deactivate() {
      Tann.slideAway(this.choiceDialog, Tann.TannPosition.Right, true);
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton(Images.phaseChallengeIcon, CHALLENGE_COL, 53, 20);
   }

   @Override
   public Color getLevelEndColour() {
      return CHALLENGE_COL;
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   public ChallengeType getChallengeType() {
      return this.challengeType;
   }

   public static enum ChallengeDifficulty {
      Standard,
      Easy;
   }
}
