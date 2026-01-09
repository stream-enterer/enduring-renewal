package com.tann.dice.screens.dungeon.panels;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.statics.Images;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.ui.Button;

public class ConfirmButton extends Button {
   int data;
   private ConfirmButton.ConfirmState confirmState = ConfirmButton.ConfirmState.Unset;
   private String textConfirm = "";
   private String[] textConfirmLines = new String[0];
   private int textConfirmWidth = 0;
   private int[] textConfirmLineWidths = new int[0];
   int border = 1;
   TextureRegion tick;

   public ConfirmButton(float width, float height) {
      super(width, height, null, null);
      this.tick = Images.tick;
   }

   public void setState(ConfirmButton.ConfirmState state, int data) {
      this.confirmState = state;
      this.textConfirm = com.tann.dice.Main.t(this.confirmState.confirmText);
      this.textConfirmWidth = TannFont.font.getWidth(this.textConfirm);
      this.textConfirmLines = this.textConfirm.split(" ", 2);
      this.textConfirmLineWidths = new int[this.textConfirmLines.length];

      for (int i = 0; i < this.textConfirmLines.length; i++) {
         this.textConfirmLineWidths[i] = TannFont.font.getWidth(this.textConfirmLines[i]);
      }

      this.data = data;
      this.setBorder(Colours.dark, data > 0 ? Colours.red : Colours.grey);
   }

   public ConfirmButton.ConfirmState getConfirmState() {
      return this.confirmState;
   }

   public void setState(ConfirmButton.ConfirmState state) {
      this.setState(state, -1);
   }

   @Override
   public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      if (this.confirmState.pulsate && OptionUtils.shouldShowFlashyEndTurn()) {
         Tann.drawPatch(
            batch, this, this.getPatch(), Colours.shiftedTowards(Colours.dark, Colours.green, com.tann.dice.Main.pulsateFactor() / 2.0F), Colours.grey, 1
         );
      }

      float gap = (this.getHeight() - this.border * 2 - this.tick.getRegionHeight() - TannFont.font.getHeight()) / 3.0F;
      batch.setColor(this.confirmState.textColour);
      String text = this.confirmState.confirmText;
      if (com.tann.dice.Main.isPortrait() && this.textConfirm.contains(" ")) {
         int dist = TannFont.font.getHeight() + 2;
         int y = (int)(this.getY() + this.border + gap + dist / 2);

         for (int i = 0; i < this.textConfirmLines.length; i++) {
            String s = this.textConfirmLines[i];
            int w = this.textConfirmLineWidths[i];
            TannFont.font.drawString(batch, s, (int)((int)this.getX() + this.getWidth() / 2.0F - w / 2.0F), y);
            y -= dist;
         }
      } else {
         TannFont.font
            .drawString(
               batch,
               this.textConfirm,
               (int)((int)this.getX() + this.getWidth() / 2.0F - this.textConfirmWidth / 2.0F),
               (int)((int)this.getY() + this.border + gap)
            );
      }

      if (this.data > 0 && OptionUtils.cornerSkullsConfirm()) {
         batch.setColor(Colours.z_white);
         int skullGap = 1;
         int skullBorder = 3;

         for (int i = 0; i < this.data; i++) {
            batch.draw(
               Images.confirmSkull,
               this.getX() + skullBorder + (Images.confirmSkull.getRegionHeight() + skullGap) * (i / 3),
               this.getY() + this.getHeight() - skullBorder - Images.confirmSkull.getRegionHeight() * (i % 3 + 1) - skullGap * (i % 3)
            );
         }
      }

      batch.setColor(this.confirmState.tickColour);
      TextureRegion td = this.tick;
      boolean end = text.equalsIgnoreCase("end turn");
      boolean doneRolling = text.equalsIgnoreCase("done rolling");
      if (this.data > 0 && OptionUtils.dyingSkullConfirm() && (end || doneRolling)) {
         batch.setColor(doneRolling ? Colours.grey : Colours.red);
         float xMid = this.getX() + this.getWidth() / 2.0F;
         float skullStart = this.getY() + this.border + gap * 2.0F + TannFont.font.getHeight();

         for (int i = 1; i < this.data; i++) {
            TextureRegion st = Images.skullTiny;
            int yOffset = (i - 1) % 2 * (st.getRegionWidth() + 1);
            int xOffset = (i - 1) / 2 * (st.getRegionWidth() + 1);
            batch.draw(st, (int)(xMid + td.getRegionWidth() / 2.0F) + xOffset, (int)(skullStart + yOffset));
         }

         td = Images.skull;
         batch.draw(td, (int)(xMid - td.getRegionWidth() / 2), (int)skullStart);
      } else {
         batch.draw(
            td,
            (int)((int)this.getX() + this.getWidth() / 2.0F - td.getRegionWidth() / 2),
            (int)((int)this.getY() + this.border + gap * 2.0F + TannFont.font.getHeight())
         );
      }
   }

   public static enum ConfirmState {
      RollingDice("Done Rolling", Colours.grey, Colours.grey, false),
      AllDiceLocked("Done Rolling", Colours.light, Colours.light, true),
      UsingDice("End turn", Colours.grey, Colours.grey, false),
      AllDiceUsed("End turn", Colours.light, Colours.light, true),
      Unset("Unset", Colours.green, Colours.light, false);

      String confirmText;
      Color textColour;
      Color tickColour;
      boolean pulsate;

      private ConfirmState(String confirmText, Color tickColour, Color textColour, boolean pulsate) {
         this.confirmText = confirmText;
         this.textColour = textColour;
         this.tickColour = tickColour;
         this.pulsate = pulsate;
      }
   }
}
