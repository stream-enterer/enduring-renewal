package com.tann.dice.gameplay.save.settings.option;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.Checkbox;
import com.tann.dice.util.ui.RadioCheckbox;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public class ChOption extends Option {
   final String[] options;
   int selectedIndex;

   protected ChOption(String name, String desc, String... options) {
      super(name, desc);
      this.options = options;
      this.selectedIndex = 0;
   }

   public void setValue(int index, boolean manual) {
      this.selectedIndex = index;
      if (manual) {
         com.tann.dice.Main.getSettings().saveOptions();
         this.manualSelectAction();
      }
   }

   public String[] getOptions() {
      return this.options;
   }

   public void setValue(String val, boolean manual) {
      int index = Tann.indexOf(this.options, val);
      if (index == -1) {
         TannLog.error(val);
      } else {
         this.setValue(index, manual);
      }
   }

   public int c() {
      if (!this.isValid()) {
         return 0;
      } else if (this.selectedIndex < 0) {
         return 0;
      } else {
         return this.selectedIndex >= this.options.length ? 0 : this.selectedIndex;
      }
   }

   public String cString() {
      return this.options[this.c()];
   }

   @Override
   public Actor makeCogActor() {
      int maxWidth = 100;
      Pixl p = new Pixl(2, 2);
      final List<Checkbox> boxes = new ArrayList<>();
      Pixl boxesPix = new Pixl(0);

      for (int i = 0; i < this.options.length; i++) {
         String op = this.options[i];
         final Checkbox cb = new RadioCheckbox(this.selectedIndex == i);
         Actor a = cb.makeLabelledCheckbox(op, null);
         boxes.add(cb);
         if (!boxesPix.canHandle(a, 100)) {
            boxesPix.row(2);
         }

         boxesPix.actor(a);
         if (i < this.options.length - 1) {
            boxesPix.gap(3);
         }

         final int finalI = i;
         cb.addToggleRunnable(new Runnable() {
            @Override
            public void run() {
               for (int j = 0; j < boxes.size(); j++) {
                  Checkbox other = boxes.get(j);
                  other.force(other == cb);
               }

               Sounds.playSound(Sounds.pip);
               ChOption.this.setValue(finalI, true);
            }
         });
      }

      p.actor(boxesPix.pix(8));
      Actor ax = DipPanel.makeTopPanelGroup(new TextWriter(this.name), p.pix(), Colours.grey, 2);
      ax.addListener(new TannListener() {
         @Override
         public boolean info(int button, float x, float y) {
            return ChOption.this.showExtraInfo();
         }
      });
      return ax;
   }

   @Override
   public void reset() {
      this.selectedIndex = 0;
   }
}
