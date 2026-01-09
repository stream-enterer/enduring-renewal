package com.tann.dice.gameplay.phase;

import com.badlogic.gdx.graphics.Color;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.phase.endPhase.runEnd.RunEndPhase;
import com.tann.dice.gameplay.phase.gameplay.DamagePhase;
import com.tann.dice.gameplay.phase.gameplay.EnemyRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.PlayerRollingPhase;
import com.tann.dice.gameplay.phase.gameplay.TargetingPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPanel;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MissingnoPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.challenge.ChallengePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.SimpleChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.BooleanPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.BooleanPhase2;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.LinkedPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.PhaseGeneratorTransformPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta.SeqPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.HeroChangePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.ItemCombinePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc.PositionSwapPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.trade.TradePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.resetPhase.ResetPhase;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.phase.addPhase.phaseGen.PhaseProducer;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.book.page.helpPage.HelpType;
import com.tann.dice.screens.dungeon.panels.tutorial.TutorialHolder;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.test.util.TestRunner;
import com.tann.dice.util.Colours;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.Arrays;
import java.util.List;

public abstract class Phase implements PhaseProducer {
   boolean activated;
   long switchStart = -1L;
   protected boolean fromSave;

   protected Phase() {
   }

   public void internalActivate() {
      if (!this.activated) {
         this.activated = true;
         boolean actual = this.checkedActivate();
         if (!actual) {
            return;
         }
      } else {
         this.reactivate();
      }

      if (DungeonScreen.get() != null && !this.isEphemeral()) {
         DungeonScreen.get().enterPhase(this);
      }
   }

   protected boolean isEphemeral() {
      return false;
   }

   private boolean checkedActivate() {
      try {
         this.activate();
         return true;
      } catch (Exception var3) {
         if (TestRunner.isTesting()) {
            throw var3;
         } else {
            com.tann.dice.Main.getCurrentScreen().popAllMedium();
            String ctx = "activating " + this.getClass().getSimpleName();
            TannLog.error(var3, ctx);
            DungeonScreen.get().showDialog(var3, ctx);
            PhaseManager.get().deletePhase(this);
            PhaseManager.get().activateCurrentPhase();
            return false;
         }
      }
   }

   public void reactivate() {
   }

   public void hide() {
   }

   public abstract void activate();

   public abstract void deactivate();

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   public final void checkIfDone() {
      if (this.doneCheck()) {
         if (this.switchStart == -1L) {
            this.switchStart = System.currentTimeMillis();
         }

         if (System.currentTimeMillis() - this.switchStart >= this.getSwitchingDelay()) {
            this.switchStart = -1L;
            PhaseManager.get().popPhase();
         }
      }
   }

   public void tick(float delta) {
   }

   public long getSwitchingDelay() {
      return 0L;
   }

   protected boolean doneCheck() {
      return false;
   }

   public boolean canRoll() {
      return false;
   }

   public boolean canTarget() {
      return false;
   }

   public void refreshPhase() {
   }

   public HelpType getHelpType() {
      return null;
   }

   public void confirmClicked(boolean fromClick) {
   }

   public String serialise() {
      String s = "Invalid phase to serialise " + this.getClass().getSimpleName();
      TannLog.log(s, TannLog.Severity.error);
      throw new RuntimeException(s);
   }

   public static Phase deserialise(String saved) {
      return deserialise(saved, true);
   }

   public static Phase deserialise(String saved, boolean safe) {
      Phase p = actuallyDeserialise(saved, safe);
      p.markFromSave();
      return p;
   }

   private void markFromSave() {
      this.fromSave = true;
   }

   private static Phase actuallyDeserialise(String saved, boolean safe) {
      char phaseId = saved.charAt(0);
      String data = saved.substring(1);

      try {
         switch (phaseId) {
            case '!':
               return new SimpleChoicePhase(data);
            case '"':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'a':
            case 'f':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'u':
            case 'v':
            case 'w':
            case 'y':
            default:
               defaultWithError(null, saved, safe);
               return defaultWithError(null, saved, safe);
            case '0':
               return new PlayerRollingPhase(data);
            case '1':
               return new TargetingPhase(data);
            case '2':
               return new LevelEndPhase(data);
            case '3':
               return new EnemyRollingPhase();
            case '4':
               return new MessagePhase(data);
            case '5':
               return new HeroChangePhase(data);
            case '6':
               return new ResetPhase();
            case '7':
               return new ItemCombinePhase(data);
            case '8':
               return new PositionSwapPhase(data);
            case '9':
               return ChallengePhase.make(data);
            case 'b':
               return new BooleanPhase(data);
            case 'c':
               return new ChoicePhase(data);
            case 'd':
               return new DamagePhase();
            case 'e':
               return new RunEndPhase(data);
            case 'g':
               return new PhaseGeneratorTransformPhase(data);
            case 'l':
               return new LinkedPhase(data);
            case 'r':
               return new RandomRevealPhase(data);
            case 's':
               return new SeqPhase(data);
            case 't':
               return TradePhase.makeFrom(data);
            case 'x':
               return new MissingnoPhase(data);
            case 'z':
               return new BooleanPhase2(data);
         }
      } catch (Exception var5) {
         return defaultWithError(var5, saved, safe);
      }
   }

   private static Phase defaultWithError(Exception e, String fromString, boolean safe) {
      String errString = "err creating phase from " + fromString;
      if (e != null) {
         e.printStackTrace();
         errString = e.getClass().getSimpleName() + errString;
      }

      TannLog.error(errString);
      if (safe) {
         return new MissingnoPhase(errString);
      } else {
         throw new RuntimeException(errString);
      }
   }

   public void positionTutorial(TutorialHolder tutorialHolder) {
   }

   public final StandardButton getLevelEndButton() {
      return this.getLevelEndButtonInternal();
   }

   protected StandardButton getLevelEndButtonInternal() {
      return new StandardButton(this.getClass().getSimpleName());
   }

   public boolean hasActivated() {
      return this.activated;
   }

   public void reset() {
      this.activated = false;
   }

   public boolean highlightDice() {
      return false;
   }

   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 37:
            if (TannStageUtils.isMouseHeld()) {
               Sounds.playSound(Sounds.flap);
               return true;
            } else if (this.showCornerInventory()) {
               LevelEndPanel.showPartyPanel();
               return true;
            }
         default:
            return false;
      }
   }

   public boolean canEquip() {
      return this.showCornerInventory();
   }

   public boolean showCornerInventory() {
      return false;
   }

   public Color getLevelEndColour() {
      return Colours.pink;
   }

   public boolean isPositive() {
      return false;
   }

   protected FightLog getFightLog() {
      return DungeonScreen.get().getFightLog();
   }

   protected DungeonContext getContext() {
      return this.getFightLog().getContext();
   }

   public boolean requiresSerialisation() {
      return true;
   }

   public boolean isDuringCombat() {
      return false;
   }

   public abstract boolean canSave();

   public boolean isPastey() {
      return this.canSave();
   }

   public boolean canFlee() {
      return false;
   }

   @Override
   public List<Phase> get(DungeonContext dc) {
      return Arrays.asList(this);
   }

   public boolean disallowRescale() {
      return false;
   }

   public boolean showTargetButton() {
      return false;
   }

   public boolean isInvalid() {
      return false;
   }

   public boolean updateDice() {
      return this.canRoll();
   }

   public Phase copy() {
      return deserialise(this.serialise());
   }

   protected List<Global> findGlobsHacky() {
      FightLog f = this.getFightLog();
      return f == null ? null : f.getSnapshot(FightLog.Temporality.Present).getGlobals();
   }
}
