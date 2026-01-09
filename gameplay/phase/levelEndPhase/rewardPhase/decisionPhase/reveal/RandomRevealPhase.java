package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomRevealPhase extends Phase {
   final List<Choosable> choosables;
   Actor g;

   public RandomRevealPhase(Choosable choosables) {
      this(Arrays.asList(choosables));
   }

   public RandomRevealPhase(List<? extends Choosable> choosables) {
      this.choosables = new ArrayList<>(choosables);
   }

   public RandomRevealPhase(String data) {
      this.choosables = ChoosableUtils.deserialiseList(data);
   }

   @Override
   public void activate() {
      this.g = makeWholeRevealGroup("[yellow]Gained:", this.choosables, new Runnable() {
         @Override
         public void run() {
            RandomRevealPhase.this.onOk();
         }
      });
      this.g = Tann.makeScrollpaneIfNecessary(this.g);
      Sounds.playSound(Sounds.chooseItem);
      DungeonScreen.get().addActor(this.g);
      Tann.center(this.g);
      this.g.setY(ChoicePhase.getShowY(this.g));
   }

   public void onOk() {
      Sounds.playSound(Sounds.pop);
      this.g.remove();
      PhaseManager.get().popPhase(this.getClass());
      DungeonScreen.get().save();
   }

   private static List<Actor> makeActors(List<Choosable> choosables, boolean big) {
      List<Actor> actors = new ArrayList<>();

      for (Choosable c : choosables) {
         Actor a;
         if (c instanceof HeroType) {
            a = new EntPanelInventory(((HeroType)c).makeEnt());
         } else {
            a = c.makeChoosableActor(big, 0);
         }

         actors.add(a);
      }

      return actors;
   }

   public static Actor makeWholeRevealGroup(String title, List<Choosable> choosables, final Runnable okActor) {
      Pixl p = new Pixl(3, 3).border(Colours.grey).text(title).row();
      p.actor(makeGroupNiceSize(choosables));
      StandardButton ok = new StandardButton("[green][b]ok", Colours.green);
      ok.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            okActor.run();
            return true;
         }
      });
      p.row().actor(ok);
      return p.pix();
   }

   public static Actor makeGroupNiceSize(List<Choosable> choosables) {
      int maxWidth = (int)(com.tann.dice.Main.width * 0.8F);
      int maxHeight = (int)(com.tann.dice.Main.height * 0.6F);
      Actor a = Tann.layoutMinArea(makeActors(choosables, true), 3, maxWidth, maxHeight);
      return (Actor)(a.getWidth() < maxWidth && a.getHeight() < maxHeight ? a : Tann.layoutMinArea(makeActors(choosables, false), 3, maxWidth, maxHeight));
   }

   @Override
   public void deactivate() {
      this.g.remove();
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   protected StandardButton getLevelEndButtonInternal() {
      return new StandardButton("??");
   }

   @Override
   public String serialise() {
      return "r" + ChoosableUtils.serialiseList(this.choosables);
   }

   @Override
   public boolean keyPress(int keycode) {
      switch (keycode) {
         case 62:
         case 66:
         case 160:
            this.onOk();
            return true;
         case 111:
            return false;
         default:
            return false;
      }
   }
}
