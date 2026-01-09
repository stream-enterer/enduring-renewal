package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.misc;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.Hero;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.PipeHero;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.fightLog.FightLog;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.LevelEndPhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.ChoicePhase;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.reveal.RandomRevealPhase;
import com.tann.dice.gameplay.progress.chievo.unlock.UnUtil;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.screens.dungeon.DungeonScreen;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.ui.choice.ChoiceDialog;
import com.tann.dice.util.ui.standardButton.StandardButton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeroChangePhase extends Phase {
   final HeroChangePhase.HeroRerollType type;
   final int index;
   ChoiceDialog cd;

   public HeroChangePhase(int index, HeroChangePhase.HeroRerollType type) {
      if (type == null) {
         type = HeroChangePhase.HeroRerollType.random();
      }

      this.index = index;
      this.type = type;
   }

   public HeroChangePhase(String s) {
      this(getIndex(s), getSwapType(s));
   }

   private static HeroChangePhase.HeroRerollType getSwapType(String s) {
      return HeroChangePhase.HeroRerollType.values()[Integer.parseInt("" + s.charAt(1))];
   }

   private static int getIndex(String s) {
      return Integer.parseInt("" + s.charAt(0));
   }

   @Override
   public String serialise() {
      return "5" + this.index + "" + Tann.indexOf(HeroChangePhase.HeroRerollType.values(), this.type);
   }

   @Override
   public void activate() {
      Sounds.playSound(Sounds.pip);
      List<Hero> heroList = this.getFightLog().getSnapshot(FightLog.Temporality.Present).getAliveHeroEntities();
      if (this.index >= heroList.size()) {
         this.stop();
      }

      final Hero a = heroList.get(this.index);
      Pixl choicePix = new Pixl(3);
      String text = this.type.desc.replaceAll("Z", a.getName(true)) + "?";
      choicePix.text(text).row(5);
      choicePix.actor(new EntPanelInventory(a)).text("->").image(Images.qmark);
      Actor choiceActor = choicePix.pix();
      this.cd = new ChoiceDialog(Arrays.asList(choiceActor), ChoiceDialog.ChoiceNames.YesNo, new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.magic);

            try {
               HeroType newType = HeroChangePhase.this.findNewTypeForHero(HeroChangePhase.this.type, a);
               if (newType != null && !newType.isMissingno()) {
                  String oldName = a.getName(true);
                  a.levelUpTo(newType, HeroChangePhase.this.getContext());
                  LevelEndPhase.unequipHero(HeroChangePhase.this.getContext().getParty(), a, oldName);
                  PhaseManager.get().pushPhaseNext(new RandomRevealPhase(newType));
                  HeroChangePhase.this.stop();
               }
            } catch (Exception var3) {
               TannLog.error(var3);
               HeroChangePhase.this.errorAndStop(a);
               return;
            }

            HeroChangePhase.this.stop();
         }
      }, new Runnable() {
         @Override
         public void run() {
            Sounds.playSound(Sounds.pop);
            HeroChangePhase.this.stop();
         }
      });
      DungeonScreen.get().addActor(this.cd);
      Tann.center(this.cd);
      this.cd.setY(ChoicePhase.getShowY(this.cd));
   }

   private void errorAndStop(Hero a) {
      PhaseManager.get().pushPhaseNext(new MessagePhase("Error changing " + a.getName(true)));
      this.stop();
   }

   private void stop() {
      if (this.cd != null) {
         this.cd.remove();
      }

      PhaseManager.get().popPhase(HeroChangePhase.class);
      DungeonScreen.get().save();
   }

   @Override
   public boolean showCornerInventory() {
      return true;
   }

   @Override
   public void deactivate() {
      if (this.cd != null) {
         this.cd.remove();
      }
   }

   @Override
   public StandardButton getLevelEndButtonInternal() {
      return new StandardButton(Images.phaseRerollIcon, Colours.blue, 53, 20);
   }

   @Override
   public Color getLevelEndColour() {
      return Colours.blue;
   }

   @Override
   public boolean canSave() {
      return true;
   }

   private HeroType findNewTypeForHero(HeroChangePhase.HeroRerollType hrt, Hero a) {
      switch (hrt) {
         case GENERATED:
            return PipeHeroGenerated.generate(a.getHeroCol(), a.getLevel());
         case BASIC:
            HeroCol col = a.getHeroCol();
            List base = HeroTypeUtils.getFilteredTypes(col, a.getLevel(), false);
            List<Global> globs = this.findGlobsHacky();
            if (globs != null) {
               HeroTypeUtils.globalAffect(base, globs, a.getHeroCol(), a.getLevel());
            }

            List<HeroType> options = Tann.getSelectiveRandom(base, 1, PipeHero.getMissingno(), new ArrayList(), Arrays.asList(a.getHeroType()));
            if (options.size() > 0) {
               return options.get(0);
            } else {
               HeroType gen = PipeHeroGenerated.generate(a.getHeroCol(), a.getLevel() + 1);
               if (gen != null) {
                  return gen;
               }

               return null;
            }
         default:
            TannLog.error("Unimplemented herochangephase");
            return PipeHeroGenerated.generate(a.getHeroCol(), a.getLevel());
      }
   }

   public static enum HeroRerollType {
      BASIC("Reroll the class of Z"),
      GENERATED("Replace Z with a [red]generated[cu] hero");

      final String desc;

      private HeroRerollType(String desc) {
         this.desc = desc;
      }

      private static HeroChangePhase.HeroRerollType random() {
         HeroChangePhase.HeroRerollType val = Tann.pick(values());
         return val == GENERATED && UnUtil.isLocked(Mode.GENERATE_HEROES) ? random() : val;
      }
   }
}
