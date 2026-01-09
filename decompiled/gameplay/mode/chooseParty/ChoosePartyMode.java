package com.tann.dice.gameplay.mode.chooseParty;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.context.config.ContextConfig;
import com.tann.dice.gameplay.context.config.difficultyConfig.ChoosePartyConfig;
import com.tann.dice.gameplay.mode.Mode;
import com.tann.dice.gameplay.mode.meta.folder.FolderType;
import com.tann.dice.gameplay.save.settings.Settings;
import com.tann.dice.statics.Images;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageButton;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.listener.TannListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChoosePartyMode extends Mode {
   List<HeroType> types = new ArrayList<>();
   final List<HeroSelector> selectors = new ArrayList<>();

   public ChoosePartyMode() {
      super("Choose-Party");
   }

   @Override
   public Color getColour() {
      return Colours.yellow;
   }

   private void setupStuff() {
      this.types.clear();
      this.selectors.clear();

      for (String basic : HeroTypeUtils.BASICS) {
         this.types.add(HeroTypeUtils.byName(basic));
         HeroSelector hs = new HeroSelector();
         hs.setToggleRun(new Runnable() {
            @Override
            public void run() {
               ChoosePartyMode.this.refreshHeroes();
            }
         });
         this.selectors.add(hs);
      }
   }

   @Override
   public String[] getDescriptionLines() {
      return new String[]{"choose a starting party", "duplicate levelups allowed"};
   }

   @Override
   protected List<Actor> getLeftOfTitleActors() {
      return Arrays.asList(this.makeRerollButton(), this.makeSelectorActor());
   }

   @Override
   public boolean skipShowTitleDesc() {
      return com.tann.dice.Main.isPortrait();
   }

   private Actor makeRerollButton() {
      Actor a = new ImageButton(Images.singleDie, 4);
      a.addListener(new TannListener() {
         @Override
         public boolean action(int button, int pointer, float x, float y) {
            Sounds.playSound(Sounds.clacks);
            Sounds.playSound(Sounds.clocks);

            for (HeroSelector hs : ChoosePartyMode.this.selectors) {
               hs.setToRandomHeroType();
            }

            ChoosePartyMode.this.refreshHeroes();
            return true;
         }
      });
      return a;
   }

   private Actor makeSelectorActor() {
      this.setupStuff();
      List<String> customs = com.tann.dice.Main.getSettings().getChoosePartyHeroes();
      if (customs != null && customs.size() == 5) {
         for (int i = 0; i < 5; i++) {
            HeroType t = HeroTypeUtils.byName(customs.get(i));
            this.types.set(i, t);
         }
      }

      for (int i = 0; i < 5; i++) {
         this.selectors.get(i).setInternal(this.types.get(i));
      }

      this.refreshHeroes();
      int gap = 3;
      Pixl selectorPix = new Pixl(3);

      for (int col = 0; col < 2; col++) {
         Pixl colPix = new Pixl(3);
         boolean first = true;

         for (int i = 0; i < this.selectors.size(); i++) {
            if (i % 2 == col) {
               HeroSelector hs = this.selectors.get(i);
               if (!first) {
                  colPix.row();
               }

               colPix.actor(hs);
               first = false;
            }
         }

         selectorPix.actor(colPix.pix());
      }

      return selectorPix.pix();
   }

   @Override
   protected List<ContextConfig> makeAllConfigs() {
      return ChoosePartyConfig.make(this.types);
   }

   private void refreshHeroes() {
      for (int i = 0; i < 5; i++) {
         HeroType ht = this.selectors.get(i).type;
         this.types.set(i, ht);
      }

      List<String> saveTypes = new ArrayList<>();

      for (int i = 0; i < 5; i++) {
         saveTypes.add(this.selectors.get(i).type.getSaveString());
      }

      Settings s = com.tann.dice.Main.getSettings();
      s.setChoosePartyHeroes(saveTypes);
      s.save();
   }

   @Override
   public String getSaveKey() {
      return "custom-party";
   }

   @Override
   public boolean disablePartyLayout() {
      return true;
   }

   @Override
   public FolderType getFolderType() {
      return FolderType.cool;
   }
}
