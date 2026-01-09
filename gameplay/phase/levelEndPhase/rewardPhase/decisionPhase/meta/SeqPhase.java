package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.meta;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MissingnoPhase;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextWriter;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeqPhase extends Phase {
   String message;
   final List<SeqPhase.SPP> spps;
   Actor a;

   public SeqPhase(String message, List<SeqPhase.SPP> spps) {
      this.message = message;
      this.spps = spps;
   }

   public SeqPhase(String data) {
      this.spps = new ArrayList<>();
      String[] parts = data.split("@1");
      if (parts.length < 2) {
         throw new RuntimeException("Invalid seqphase: " + data);
      } else {
         this.message = parts[0];

         for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            String[] p2 = part.split("@2");
            List<String> p2cpy = new ArrayList<>(Arrays.asList(p2));
            p2cpy.remove(0);
            this.spps.add(new SeqPhase.SPP(p2[0], p2cpy));
         }
      }
   }

   @Override
   public String serialise() {
      return "s" + this.message + "@1" + Tann.commaList(this.spps, "@1");
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.pip);
      DungeonScreen ds = DungeonScreen.get();
      if (ds == null) {
         TannLog.error("Activating metaphase when not in dungeonscreen");
      } else {
         Pixl p = new Pixl(8, 8).border(Colours.grey);
         p.text(this.message).row();

         for (final SeqPhase.SPP ph : this.spps) {
            Actor butt = ph.getLevelEndButton();
            p.actor(butt, com.tann.dice.Main.width * 0.5F);
            butt.addListener(new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  SeqPhase.this.pickPhase(ph);
                  return true;
               }
            });
         }

         this.a = p.pix();
         this.a = Tann.makeScrollpaneIfNecessary(this.a);
         ds.addActor(this.a);
         Tann.center(this.a);
         Tann.slideIn(this.a, Tann.TannPosition.Top, (int)(com.tann.dice.Main.height / 2 - this.a.getHeight() / 2.0F));
      }
   }

   private void pickPhase(SeqPhase.SPP ph) {
      Sounds.playSound(Sounds.pip);
      List<String> phases = ph.phases;

      for (int i = phases.size() - 1; i >= 0; i--) {
         String phase = phases.get(i);
         Phase p = Phase.deserialise(phase);
         PhaseManager.get().forceNext(p);
      }

      PhaseManager.get().popPhase(this.getClass());
      DungeonScreen.get().save();
   }

   private Phase roundTrip(Phase a) {
      return Phase.deserialise(a.serialise());
   }

   @Override
   protected StandardButton getLevelEndButtonInternal() {
      return new StandardButton(TextWriter.getTag(this.getLevelEndColour()) + "chain", this.getLevelEndColour(), 53, 20);
   }

   @Override
   public void deactivate() {
      Tann.slideAway(this.a, Tann.TannPosition.Bot, true);
   }

   @Override
   public boolean canSave() {
      return true;
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.purple;
   }

   @Override
   public boolean isInvalid() {
      for (SeqPhase.SPP spp : this.spps) {
         for (String phase : spp.phases) {
            Phase p = Phase.deserialise(phase);
            if (p instanceof MissingnoPhase || p.isInvalid()) {
               return true;
            }
         }
      }

      return false;
   }

   static class SPP {
      final String title;
      final List<String> phases;

      SPP(String title, List<String> phases) {
         this.title = title;
         this.phases = phases;
      }

      public Actor getLevelEndButton() {
         return new StandardButton(this.title);
      }

      @Override
      public String toString() {
         return this.title + "@2" + Tann.commaList(this.phases, "@2");
      }
   }
}
