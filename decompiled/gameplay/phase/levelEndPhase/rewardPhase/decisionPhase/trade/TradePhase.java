package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.trade;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Json;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradePhase extends Phase {
   List<Choosable> gain;
   ChoiceDialog cd;

   public TradePhase(List<Choosable> gain) {
      this.gain = gain;
   }

   public static TradePhase makeFrom(String data) {
      return new TradePhase(ChoosableUtils.deserialiseList(data));
   }

   private static Json getJson() {
      return com.tann.dice.Main.getJson(true);
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.deboost);
      Actor cd = this.makeDungeonActor();
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         ds.addActor(cd);
         Tann.center(cd);
         Tann.slideFromTopToCenter(cd);
      }
   }

   public Actor makeDungeonActor() {
      return this.cd = new ChoiceDialog(
         "[purple]Open cursed chest?", Arrays.asList(this.makeActor(this.gain)), ChoiceDialog.ChoiceNames.AcceptDecline, new Runnable() {
            @Override
            public void run() {
               TradePhase.this.end(true);
            }
         }, new Runnable() {
            @Override
            public void run() {
               TradePhase.this.end(false);
            }
         }
      );
   }

   private void end(boolean accept) {
      DungeonScreen ds = DungeonScreen.get();
      if (ds != null) {
         DungeonContext dc = ds.getDungeonContext();
         if (accept) {
            Sounds.playSound(Sounds.boost);
         } else {
            Sounds.playSound(Sounds.pop);
         }

         this.cd.setTouchable(Touchable.disabled);
         if (accept) {
            ChoosableUtils.checkedOnChoose(this.gain, dc, "trade");
         }

         PhaseManager.get().popPhase(TradePhase.class);
         DungeonScreen.get().save();
      }
   }

   private Actor makeActor(List<Choosable> chz) {
      List<Actor> actors = new ArrayList<>();

      for (int i = 0; i < chz.size(); i++) {
         Choosable choosable = chz.get(i);
         Actor a = choosable.makeChoosableActor(false, 0);
         actors.add(a);
      }

      return Tann.layoutMinArea(actors, 5, (int)(com.tann.dice.Main.width * 0.8F), (int)(com.tann.dice.Main.height * 0.8F));
   }

   @Override
   public void deactivate() {
      Tann.slideAway(this.cd, Tann.TannPosition.Top, true);
   }

   @Override
   public String serialise() {
      return "t" + ChoosableUtils.serialiseList(this.gain);
   }

   @Override
   protected StandardButton getLevelEndButtonInternal() {
      return new StandardButton(TextWriter.getTag(this.getLevelEndColour()) + "chest", this.getLevelEndColour(), 53, 20);
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.purple;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }
}
