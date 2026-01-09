package com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.tann.dice.gameplay.content.ent.Ent;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.gameplay.trigger.Trigger;
import com.tann.dice.screens.dungeon.panels.book.page.stuffPage.APIUtils;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.Colours;
import com.tann.dice.util.CopyButtonHolder;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.TextMarquee;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class ConcisePanel extends Group implements CopyButtonHolder {
   protected final Choosable choosable;
   protected final boolean big;
   public static final int MARGIN = 3;
   public static final int MULTI_ACTOR_GAP = 7;
   public static final int MAX_TEXT_WIDTH = 95;
   public static final int TEXT_HEIGHT_CUTOFF = 21;
   Vector2 copyOffset = new Vector2();

   public ConcisePanel(Choosable choosable, boolean big) {
      this.setTransform(false);
      this.choosable = choosable;
      this.big = big;
      this.layout();
      if (this.canBeBigger()) {
         this.addListener(new TannListener() {
            @Override
            public boolean info(int button, float x, float y) {
               Sounds.playSound(Sounds.pip);
               ConcisePanel cp = ConcisePanel.this.makeCopy(true);
               com.tann.dice.Main.getCurrentScreen().push(cp, 0.7F);
               Tann.center(cp);
               return true;
            }

            @Override
            public boolean action(int button, int pointer, float x, float y) {
               return false;
            }
         });
      }
   }

   public static String entName(Ent ent) {
      return TextWriter.stripTags(ent.getName(true, false));
   }

   private boolean canBeBigger() {
      if (this.big) {
         return false;
      } else {
         boolean showingHalf = this.getFullDescription() != null && this.getFullDescription().length() > 0 && this.getMiddleActors(false).size() > 0;
         boolean hasKeywords = this.hasKeywords();
         return showingHalf || hasKeywords;
      }
   }

   protected abstract boolean hasKeywords();

   protected abstract ConcisePanel makeCopy(boolean var1);

   private Actor makeDescriptionActor(int width) {
      String full = this.getFullDescription();
      if (full != null && !full.isEmpty()) {
         Pixl p = new Pixl(0);
         TextWriter tw = new TextWriter("[text]" + full, this.getDescriptionImage(), width);
         p.actor(tw);
         return p.pix(8);
      } else {
         return null;
      }
   }

   protected TextureRegion getDescriptionImage() {
      return null;
   }

   protected abstract String getFullDescription();

   private void layout() {
      int SINGLE_ROW_MIDDLE_MAX_WIDTH = (int)(com.tann.dice.Main.width * 0.8F);
      Actor title = this.makeTitle(80);
      int fakeTitleWidth = (int)(title.getWidth() + 6.0F);
      List<Actor> extraTopActors = this.getExtraTopActors();
      int topWidth = fakeTitleWidth - extraTopActors.size();
      int maxTopHeight = 0;

      for (Actor a : extraTopActors) {
         topWidth = (int)(topWidth + a.getWidth());
         maxTopHeight = (int)Math.max((float)maxTopHeight, a.getHeight());
      }

      List<Actor> middleActors = this.getMiddleActors(this.big);
      int triggerWidth = 0;
      boolean addedAnyActors = middleActors.size() > 0;
      int totalMiddleWidth = 6;

      for (int i = 0; i < middleActors.size(); i++) {
         Actor a = middleActors.get(i);
         triggerWidth = (int)Math.max((float)triggerWidth, a.getWidth() + 6.0F);
         totalMiddleWidth = (int)(totalMiddleWidth + a.getWidth());
         if (i > 0) {
            totalMiddleWidth += 7;
         }
      }

      if (totalMiddleWidth < SINGLE_ROW_MIDDLE_MAX_WIDTH) {
         triggerWidth = Math.max(triggerWidth, totalMiddleWidth);
      }

      int combinedWidth = Math.max(topWidth, triggerWidth);
      int texWidth = 0;
      if (this.big || !addedAnyActors) {
         Actor descActor = this.makeDescriptionActor(combinedWidth);
         if (descActor != null) {
            texWidth = (int)Math.max((float)texWidth, descActor.getWidth());
            int tmpTexWidth = texWidth;

            for (float prevHeight = (int)descActor.getHeight(); descActor.getHeight() > 21.0F && tmpTexWidth < 95; tmpTexWidth++) {
               descActor = this.makeDescriptionActor(tmpTexWidth);
               if (descActor == null) {
                  break;
               }

               boolean shorter = descActor.getHeight() < prevHeight;
               prevHeight = descActor.getHeight();
               if (shorter) {
                  texWidth = tmpTexWidth;
               }
            }
         }
      }

      combinedWidth = Math.max(combinedWidth, texWidth + 6);
      texWidth = combinedWidth - 6;
      int titleWidth = combinedWidth;

      for (Actor a : extraTopActors) {
         titleWidth = (int)(titleWidth - a.getWidth());
         titleWidth++;
      }

      int titleBorder = 1;
      Actor titlex = new Pixl(1, 2)
         .forceWidth(titleWidth)
         .border(Colours.dark, this.getBorderColour(), titleBorder)
         .actor(this.makeTitle(titleWidth - titleBorder * 2 - 2))
         .pix();
      Pixl topPixl = new Pixl(0);

      for (Actor a : extraTopActors) {
         topPixl.actor(a).gap(-1);
      }

      topPixl.actor(titlex);
      Actor top = topPixl.pix(2);
      topPixl = new Pixl(0, 3);
      if (addedAnyActors) {
         Pixl iconographyPix = new Pixl(7);

         for (int ix = 0; ix < middleActors.size(); ix++) {
            Actor a = middleActors.get(ix);
            iconographyPix.actor(a, combinedWidth + 7);
         }

         Actor iconography = iconographyPix.pix();
         boolean overridden = false;
         if (this instanceof ItemPanel) {
            int yOffset = -maxTopHeight + (int)Math.max(10.0F, titlex.getHeight()) - 1;
            int weirdGap = (int)((top.getWidth() - maxTopHeight - iconography.getWidth()) / 2.0F);
            if (weirdGap > 1) {
               topPixl.row(yOffset).gap(maxTopHeight + weirdGap - 3).actor(iconography).gap(weirdGap - 3);
               overridden = true;
            }
         }

         if (!overridden) {
            topPixl.actor(iconography);
         }
      }

      if (this.big || !addedAnyActors) {
         Actor descActor = this.makeDescriptionActor(texWidth);
         if (descActor != null) {
            if (addedAnyActors) {
               topPixl.row(3);
            } else {
               topPixl.row(-1);
            }

            topPixl.actor(descActor);
         }
      }

      Actor extraHackyBottom = this.getHackyBottomActor((int)top.getWidth());
      if (extraHackyBottom != null) {
         topPixl.row(3).actor(extraHackyBottom);
      }

      Group mainBody = new Pixl(0).border(this.getBorderColour()).actor(top).row().actor(topPixl).pix();
      Group done;
      if (this.big && this.getReferencedKeywords().size() > 0) {
         Pixl p = new Pixl(0);
         p.actor(mainBody);
         p.row(-1);

         for (Keyword k : this.getReferencedKeywords()) {
            Actor a = KUtils.makeActor(k, this.getSingleEffOrNull(k));
            p.actor(a);
            p.row(-1);
         }

         done = p.pix();
      } else {
         done = mainBody;
      }

      if (done.getHeight() > com.tann.dice.Main.height) {
         done = Tann.makeScrollpane(done);
      }

      Tann.become(this, done);
      this.copyOffset.set(0.0F, 0.0F);
      if (OptionUtils.shouldShowCopy()) {
         this.addCopyButton(mainBody);
      }
   }

   public void addCopyButton(Group mainBody) {
      APIUtils.addCopyButton(mainBody, this.choosable.getName(), null, this.copyOffset);
   }

   @Override
   public void addCopyButton() {
      APIUtils.addCopyButton(this, this.choosable.getName(), null, this.copyOffset);
   }

   protected abstract Eff getSingleEffOrNull(Keyword var1);

   protected abstract Color getBorderColour();

   protected abstract List<Keyword> getReferencedKeywords();

   protected abstract List<Actor> getMiddleActors(boolean var1);

   protected abstract String getTitle();

   public static Actor makeTitle(String text, Color col, int maxWidth, boolean shouldWrap) {
      if (!shouldWrap) {
         return TextMarquee.marqueeOrDots(text, col, maxWidth);
      } else {
         TextWriter wrappedText = new TextWriter(TextWriter.getTag(col) + text, maxWidth);
         return (Actor)(wrappedText.getNumLines() > 2 ? TextMarquee.marqueeOrDots(text, col, maxWidth) : wrappedText);
      }
   }

   private Actor makeTitle(int maxWidth) {
      return makeTitle(this.getTitle(), this.getBorderColour(), maxWidth, true);
   }

   protected abstract List<Actor> getExtraTopActors();

   public static List<Actor> getMiddleActors(List<Trigger> triggers, boolean big) {
      List<Actor> result = new ArrayList<>();
      int numSuccess = 0;

      for (Trigger pt : triggers) {
         if (pt.clearIcons()) {
            result.clear();
         }

         if (!pt.skipEquipImage()) {
            Actor a = pt.makePanelActor(big);
            if (a != null) {
               if (!Trigger.notAlone(a)) {
                  numSuccess++;
               }

               result.add(a);
            }
         }
      }

      return (List<Actor>)(numSuccess == 0 ? new ArrayList<>() : result);
   }

   protected Actor getHackyBottomActor(int width) {
      return null;
   }
}
