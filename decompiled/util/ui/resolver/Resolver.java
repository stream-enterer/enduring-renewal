package com.tann.dice.util.ui.resolver;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.content.item.Item;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.modifier.Modifier;
import com.tann.dice.gameplay.modifier.SmallModifierPanel;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.screens.dungeon.panels.Explanel.EntPanelInventory;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ItemPanel;
import com.tann.dice.screens.dungeon.panels.entPanel.choosablePanel.ModifierPanel;
import com.tann.dice.util.Colours;
import com.tann.dice.util.ImageActor;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.listener.TannListener;
import com.tann.dice.util.ui.ScrollPane;
import com.tann.dice.util.ui.TextWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Resolver<T> {
   final int MAX = 150;
   final int BIG_LIMIT = 5;
   final int BIG_LIMIT_ENT = 2;
   private final Comparator<? super T> sorter;

   public Resolver(Comparator<? super T> sorter) {
      this.sorter = sorter;
   }

   public void activate() {
      this.activate(null);
   }

   public void activate(String overrideInputDescription) {
      if (overrideInputDescription == null) {
         if (this.getOtherOverrideDesc() != null) {
            overrideInputDescription = this.getColTag() + this.getOtherOverrideDesc();
         } else {
            overrideInputDescription = this.getColTag() + "Search for " + this.getTypeName();
         }
      }

      com.tann.dice.Main.self().control.textInput(new TextInputListener() {
         public void input(String text) {
            if (PipeUtils.unbalancedBrackets(text)) {
               Resolver.this.fail(text, "[orange]unbalanced brackets");
            } else {
               Pipe.setupChecks();
               this.doInput(text);
               Pipe.disableChecks();
            }
         }

         private void doInput(String text) {
            com.tann.dice.Main.getSettings().setLastSearchIfFailed(null);
            boolean resolveToSingle = true;
            T t = (T)Resolver.this.byCacheOrName(text);
            if (t != null) {
               Resolver.this.resolve(t);
            } else {
               List<T> result = Resolver.this.search(text);
               if (result.size() <= 1 && t != null) {
                  Resolver.this.resolve(t);
               } else {
                  if (text.startsWith("=")) {
                     Resolver.this.fail(text, "this is likely a list for custom mode, try pressing the 'paste' button there");
                  }

                  if (result.size() == 0) {
                     Resolver.this.fail(text, "none found");
                  } else if (result.size() > 150) {
                     Resolver.this.fail(text, "too many found: " + result.size());
                  } else {
                     Resolver.this.resolveList(result);
                  }
               }
            }
         }

         public void canceled() {
         }
      }, overrideInputDescription, com.tann.dice.Main.getSettings().getLastSearchIfFailed(), "");
   }

   private T byCacheOrName(String text) {
      T t = this.byCache(text);
      if (t == null) {
         t = this.byName(text);
      }

      return t;
   }

   protected String getOtherOverrideDesc() {
      return null;
   }

   private String getColTag() {
      if (!com.tann.dice.Main.self().control.allowsColourTextInput()) {
         return "";
      } else {
         Color col = this.getCol();
         return col == null ? "" : TextWriter.getTag(col);
      }
   }

   protected abstract Color getCol();

   protected abstract String getTypeName();

   public boolean debugSearch(String text) {
      List<T> rz = this.search(text);
      if (rz != null && rz.size() > 0) {
         this.resolveList(rz);
         return true;
      } else {
         return false;
      }
   }

   protected T byCache(String text) {
      return null;
   }

   public boolean debugResolve(String text) {
      T t = this.byCacheOrName(text);
      if (t != null) {
         this.resolve(t);
         return true;
      } else {
         return false;
      }
   }

   protected abstract T byName(String var1);

   protected abstract List<T> search(String var1);

   protected Actor makeActor(T t, boolean big) {
      if (t instanceof Keyword) {
         return big ? KUtils.makeActor((Keyword)t, null) : makeTiny(((Keyword)t).getColourTaggedString(), ((Keyword)t).getColour());
      } else if (t instanceof Modifier) {
         Modifier m = (Modifier)t;
         return (Actor)(big ? new ModifierPanel(m, false) : new SmallModifierPanel(m));
      } else if (t instanceof Item) {
         return (Actor)(big ? new ItemPanel((Item)t, false) : new ImageActor(((Item)t).getImage()));
      } else if (t instanceof EntType) {
         if (big) {
            EntPanelInventory dp = new EntPanelInventory(((EntType)t).makeEnt());
            dp.removeDice();
            return dp;
         } else {
            return new ImageActor(((EntType)t).portrait, t instanceof MonsterType);
         }
      } else if (!big && t instanceof Choosable) {
         Choosable ch = (Choosable)t;
         return makeTiny(ch.getName(), ch.getColour());
      } else {
         return new Pixl(3, 3).border(Colours.pink).text(t.getClass().getSimpleName()).pix();
      }
   }

   private static Actor makeTiny(String s, Color col) {
      return new TextWriter(TextWriter.getTag(col) + s, 99, col, 2);
   }

   public abstract void resolve(T var1);

   public void resolveList(List<T> list) {
      Pixl p = new Pixl(2, 2).border(Colours.grey);
      if (this.sorter != null) {
         Collections.sort(list, this.sorter);
      }

      if (list.size() > 0) {
         boolean ent = list.get(0) instanceof EntType;
         final boolean big = list.size() <= (ent ? 2 : 5);

         for (final T t : list) {
            Actor a = this.makeActor(t, big);
            Tann.addListenerFirst(a, new TannListener() {
               @Override
               public boolean action(int button, int pointer, float x, float y) {
                  com.tann.dice.Main.getCurrentScreen().popAllLight();
                  com.tann.dice.Main.getCurrentScreen().pop(ScrollPane.class);
                  Resolver.this.resolve(t);
                  return true;
               }

               @Override
               public boolean info(int button, float x, float y) {
                  if (!big) {
                     final Actor bigAc = Resolver.this.makeActor(t, true);
                     com.tann.dice.Main.getCurrentScreen().push(bigAc, 0.8F);
                     Tann.center(bigAc);
                     bigAc.addListener(new TannListener() {
                        @Override
                        public boolean action(int button, int pointer, float xx, float yx) {
                           com.tann.dice.Main.getCurrentScreen().pop(bigAc);
                           return true;
                        }
                     });
                  }

                  return super.info(button, x, y);
               }
            });
            p.actor(a, com.tann.dice.Main.width * 0.5F);
         }
      }

      Actor a = p.pix();
      ScrollPane sp = Tann.makeScrollpane(a);
      sp.setHeight(Math.min(sp.getHeight(), a.getHeight()));
      com.tann.dice.Main.getCurrentScreen().push(sp, true, true, true, 0.8F);
      Tann.center(sp);
   }

   public void fail(String search, String msg) {
      com.tann.dice.Main.getSettings().setLastSearchIfFailed(search);
      com.tann.dice.Main.getCurrentScreen().showDialog(msg, Colours.red);
   }
}
