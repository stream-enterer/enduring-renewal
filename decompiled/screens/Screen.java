package com.tann.dice.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.tann.dice.gameplay.phase.Phase;
import com.tann.dice.gameplay.phase.PhaseManager;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.MessagePhase;
import com.tann.dice.gameplay.save.settings.option.OptionLib;
import com.tann.dice.gameplay.save.settings.option.OptionUtils;
import com.tann.dice.screens.dungeon.DungeonUtils;
import com.tann.dice.screens.dungeon.panels.ExplanelReposition;
import com.tann.dice.screens.dungeon.panels.Explanel.Explanel;
import com.tann.dice.screens.dungeon.panels.book.Book;
import com.tann.dice.screens.dungeon.panels.popup.PopupHolder;
import com.tann.dice.screens.generalPanels.InventoryPanel;
import com.tann.dice.screens.generalPanels.TextUrl;
import com.tann.dice.screens.shaderFx.FXContainer;
import com.tann.dice.statics.sound.Sounds;
import com.tann.dice.util.AlternativePop;
import com.tann.dice.util.Colours;
import com.tann.dice.util.Draw;
import com.tann.dice.util.InputBlocker;
import com.tann.dice.util.KeyListen;
import com.tann.dice.util.Pair;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.PopListen;
import com.tann.dice.util.PopRequirement;
import com.tann.dice.util.PostPop;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import com.tann.dice.util.TannLog;
import com.tann.dice.util.TannStageUtils;
import com.tann.dice.util.ui.Glowverlay;
import com.tann.dice.util.ui.TextWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class Screen extends Group implements ExplanelReposition {
   private PopupHolder popupHolder;
   final InputListener SELF_POP = new InputListener() {
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
         if (event.isHandled()) {
            return true;
         } else {
            boolean popped = Screen.this.pop(event.getListenerActor());
            if (!popped) {
               Screen.this.popSingleLight();
            }

            Sounds.playSound(Sounds.pop);
            return true;
         }
      }
   };
   public List<Pair<Actor, InputBlocker>> modalStack = new ArrayList<>();
   List<Glowverlay> glowverlays = new ArrayList<>();

   public Screen() {
      this.setTransform(false);
      this.setSize(com.tann.dice.Main.width, com.tann.dice.Main.height);
      this.resetPopupHolder();
   }

   protected void resetPopupHolder() {
      if (this.popupHolder != null) {
         this.popupHolder.remove();
      }

      this.popupHolder = new PopupHolder();
      com.tann.dice.Main.unlockManager().registerAchievementListener(this.popupHolder);
      this.addActor(this.popupHolder);
      this.popupHolder.setPosition(this.getWidth() - this.popupHolder.getWidth(), com.tann.dice.Main.height);
   }

   public void draw(Batch batch, float parentAlpha) {
      this.preDraw(batch);
      batch.end();
      batch.begin();
      super.draw(batch, parentAlpha);
      batch.flush();
      this.postDraw(batch);
      if (OptionLib.CR_INDICATOR.c() > 0) {
         if (OptionLib.CR_INDICATOR.c() == 1) {
            batch.setColor(Colours.withAlpha(Colours.light, com.tann.dice.Main.pulsateFactor()).cpy());
         } else {
            batch.setColor(Colours.random().cpy());
         }

         int sz = 4;
         Draw.fillRectangle(batch, (int)(com.tann.dice.Main.width * 3 / 4.0F), com.tann.dice.Main.height - sz + 2, sz, sz);
      }

      if (OptionLib.FPS_COUNTER.c()) {
         batch.setColor(Colours.blue);
         TannFont.font.drawString(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), com.tann.dice.Main.width - 29, com.tann.dice.Main.height - 6);
      }

      if (OptionLib.PHASE_DISPLAY.c()) {
         PhaseManager pm = PhaseManager.get();
         if (pm != null) {
            Phase p = pm.getPhase();
            if (p != null) {
               batch.setColor(Colours.orange);
               TannFont.font.drawString(batch, p.getClass().getSimpleName(), 1.0F, com.tann.dice.Main.height - 6);
            }
         }
      }
   }

   public abstract void preDraw(Batch var1);

   public abstract void postDraw(Batch var1);

   public void popupText(String s) {
      this.popupHolder.addText(s);
   }

   public void addPopup(Actor a) {
      this.popupHolder.addPopup(a);
   }

   public void act(float delta) {
      this.preTick(delta);
      super.act(delta);
      this.postTick(delta);
   }

   public abstract void preTick(float var1);

   public abstract void postTick(float var1);

   public final void mainKeyPress(int keycode) {
      if (!this.genericKeyPress(keycode)) {
         this.keyPress(keycode);
      }
   }

   private boolean genericKeyPress(int keycode) {
      Actor a = this.getTopActualActor();
      if (a instanceof KeyListen && ((KeyListen)a).keyPress(keycode)) {
         return true;
      } else {
         switch (keycode) {
            case 111:
               boolean popped = this.popAllMedium();
               if (popped) {
                  Sounds.playSound(Sounds.pop);
               }

               if (!popped) {
                  Sounds.playSound(Sounds.pip);
                  DungeonUtils.showCogMenu();
               }

               return true;
            default:
               return Book.inBook();
         }
      }
   }

   protected abstract void keyPress(int var1);

   public void push(Actor a) {
      this.push(a, 0.0F);
   }

   public void push(Actor a, float alpha) {
      this.push(a, true, true, true, alpha);
   }

   public void push(Actor actor, boolean blocker, boolean blockerPops, boolean selfPops, float alpha) {
      if (actor == null) {
         actor = Pixl.makeErrorActor("ph");
      }

      InputBlocker ipb = null;
      if (blocker) {
         ipb = new InputBlocker();
         ipb.setMedium(selfPops || blockerPops);
         ipb.setAlpha(alpha);
         this.addActor(ipb);
         ipb.setActiveClicker(blockerPops);
      }

      Pair<Actor, InputBlocker> pair = new Pair<>(actor, ipb);
      this.modalStack.add(pair);
      this.addActor(actor);
      actor.toFront();
      if (selfPops) {
         actor.addListener(this.SELF_POP);
      }

      TannStageUtils.actorJustPushed(actor);
   }

   public void forcePop() {
      this.pop();
   }

   private boolean pop() {
      if (this.modalStack.size() == 0) {
         System.err.println("Trying to pop with nothing to pop");
         return false;
      } else {
         Pair toPop = this.modalStack.get(this.modalStack.size() - 1);
         if (toPop.a instanceof PopRequirement) {
            boolean allow = ((PopRequirement)toPop.a).allowPop();
            if (!allow) {
               return false;
            }
         }

         Pair<Actor, InputBlocker> popped = this.modalStack.remove(this.modalStack.size() - 1);
         if (!(popped.a instanceof AlternativePop) || !((AlternativePop)popped.a).alternativePop()) {
            if (OptionUtils.shouldBeCrazyUi()) {
               FXContainer.randomFx(popped.a).replace();
            } else {
               popped.a.remove();
            }
         }

         popped.a.removeListener(this.SELF_POP);
         if (popped.b != null) {
            popped.b.remove();
         }

         if (popped.a instanceof PostPop) {
            ((PostPop)popped.a).postPop();
         }

         if (this.modalStack.size() > 0) {
            Pair<Actor, InputBlocker> next = this.modalStack.get(this.modalStack.size() - 1);
            if (next.b != null) {
               next.b.toFront();
            }

            next.a.toFront();
            if (next.a instanceof PopListen) {
               ((PopListen)next.a).childPopped(popped.a);
            }

            TannStageUtils.actorSurfacedFromOtherPopping(next.a);
         } else {
            Actor a = this.getTopActualActor();
            if (a != null) {
               TannStageUtils.actorSurfacedFromOtherPopping(a);
            }
         }

         return true;
      }
   }

   public boolean pop(String modal_name) {
      Actor a = this.getTopPushedActor();
      return a != null && modal_name.equalsIgnoreCase(a.getName()) ? this.pop() : false;
   }

   public boolean pop(Class clazz) {
      return clazz.isInstance(this.getTopPushedActor()) ? this.pop() : false;
   }

   public boolean popAllLight() {
      boolean popped = false;

      while (this.popSingleLight()) {
         popped = true;
      }

      return popped;
   }

   public boolean popAllMedium() {
      boolean popped = false;

      while (this.popSingleMedium()) {
         popped = true;
      }

      return popped;
   }

   public boolean popSingleLight() {
      return this.modalStack.size() > 0
            && this.modalStack.get(this.modalStack.size() - 1).b == null
            && !(this.modalStack.get(this.modalStack.size() - 1).a instanceof InventoryPanel)
         ? this.pop()
         : false;
   }

   public boolean popSingleMedium() {
      if (this.popSingleLight()) {
         return true;
      } else {
         return this.modalStack.size() > 0
               && this.modalStack.get(this.modalStack.size() - 1).b != null
               && ((InputBlocker)this.modalStack.get(this.modalStack.size() - 1).b).isMedium()
            ? this.pop()
            : false;
      }
   }

   public boolean pop(Actor a) {
      if (a == null) {
         TannLog.error("Trying to pop a null actor");
         return false;
      } else if (this.modalStack.size() == 0) {
         TannLog.error("Trying to pop with nothing to pop");
         return false;
      } else {
         Pair p = this.modalStack.get(this.modalStack.size() - 1);
         if (p.a != a) {
            TannLog.error("Popping wrong panel. Expected " + a.getClass().getSimpleName() + ", found " + p.a.getClass().getSimpleName());
            return false;
         } else {
            return this.pop();
         }
      }
   }

   public Explanel getTopExplanel() {
      Actor top = this.getTopPushedActor();
      return top instanceof Explanel ? (Explanel)top : null;
   }

   public Actor getTopPushedActor() {
      return this.modalStack.size() == 0 ? null : (Actor)this.modalStack.get(this.modalStack.size() - 1).a;
   }

   public Actor getTopActualActor() {
      return this.getChild(this.getChildren().size - 1);
   }

   public boolean addListener(EventListener listener) {
      return super.addListener(listener);
   }

   public boolean stackContains(Class c) {
      for (Pair p : this.modalStack) {
         if (p.a.getClass() == c) {
            return true;
         }
      }

      return false;
   }

   public abstract Screen copy();

   public void setGlowverlays(List<Glowverlay> glowverlays) {
      this.clearOldGlowverlays();
      this.glowverlays = glowverlays;
   }

   public void setGlowverlay(Glowverlay glowverlay) {
      this.clearOldGlowverlays();
      this.glowverlays.add(glowverlay);
   }

   public void clearOldGlowverlays() {
      for (Glowverlay g : this.glowverlays) {
         g.remove();
      }

      this.glowverlays.clear();
   }

   public Actor getActorUnderMouse() {
      return this.hit(Gdx.input.getX() / com.tann.dice.Main.scale, com.tann.dice.Main.height - Gdx.input.getY() / com.tann.dice.Main.scale, true);
   }

   public void center(Actor a) {
      a.setPosition((int)((this.getWidth() - a.getWidth()) / 2.0F), (int)((this.getHeight() - a.getHeight()) / 2.0F));
   }

   public boolean stackEmpty() {
      return this.getTopPushedActor() == null;
   }

   public final void showDialog(Exception e, String ctx) {
      this.showDialog(MessagePhase.getMsg(e, ctx));
   }

   public final void showDialog(String s) {
      this.showDialog(s, Colours.grey);
   }

   public void showDialog(String s, Color border) {
      this.showPopupDialog(s, border);
   }

   public void showPopupDialog(String s, Color border) {
      TextWriter tw = new TextWriter(s, 120, border, 2);
      this.push(tw, true, true, true, 0.8F);
      Tann.center(tw);
   }

   public String getReportString() {
      return null;
   }

   public void openUrl(String url) {
      this.openUrl(url, null);
   }

   public void openUrl(String url, String description) {
      this.openUrl(url, description, null);
   }

   public void openUrl(String url, String description, Actor extra) {
      Sounds.playSound(Sounds.pip);
      Actor a = TextUrl.getUrlActor(extra, url, description);
      this.push(a, true, true, false, 0.7F);
      Tann.center(a);
   }

   public void afterSet() {
   }

   public boolean skipMonkey() {
      return false;
   }

   public void showMusicPopup(Actor a) {
      float DOWN_TIME = 0.15F;
      float UP_TIME = 0.15F;
      float DELAY = 2.0F;
      float DIST = 1.0F;
      Interpolation terp = Interpolation.pow2Out;
      this.addActor(a);
      a.setPosition(1.0F, com.tann.dice.Main.height);
      a.addAction(
         Actions.sequence(
            Actions.moveTo(this.getX(), com.tann.dice.Main.height - a.getHeight() - 1.0F, 0.15F, terp),
            Actions.delay(2.0F),
            Actions.moveTo(this.getX(), com.tann.dice.Main.height, 0.15F, terp),
            Actions.removeActor()
         )
      );
   }

   public void pushAndCenter(Actor a) {
      this.pushAndCenter(a, 0.8F);
   }

   public void pushAndCenter(Actor a, float alpha) {
      this.pushAndCenter(a, alpha, true);
   }

   public void pushAndCenter(Actor a, float alpha, boolean selfPop) {
      Sounds.playSound(Sounds.pip);
      this.push(a, true, true, selfPop, alpha);
      Tann.center(a);
   }

   @Override
   public void repositionExplanel(Explanel exp) {
      Vector2 v = com.tann.dice.Main.getCursor();
      exp.setPosition(v.x - exp.getWidth() / 2.0F, v.y - exp.getHeight() - 5.0F);
   }

   public boolean needsExtraRender() {
      return false;
   }
}
