package com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.special;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.tann.dice.gameplay.context.DungeonContext;
import com.tann.dice.gameplay.modifier.modBal.TierUtils;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.Choosable;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableType;
import com.tann.dice.gameplay.phase.levelEndPhase.rewardPhase.decisionPhase.choice.choosable.ChoosableUtils;
import com.tann.dice.gameplay.progress.stats.stat.Stat;
import com.tann.dice.gameplay.trigger.global.linked.DipPanel;
import com.tann.dice.util.Pixl;
import com.tann.dice.util.Tann;
import com.tann.dice.util.TannFont;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OrChoosable implements Choosable {
   String raw;
   private List<Choosable> privateCache;

   public OrChoosable() {
   }

   public OrChoosable(Choosable... oneAtRandom) {
      this(Arrays.asList(oneAtRandom));
   }

   public OrChoosable(List<Choosable> in) {
      in = this.flatten(in);
      this.raw = ChoosableUtils.serialiseList(in, "@4");
   }

   private List<Choosable> flatten(List<Choosable> in) {
      List<Choosable> result = new ArrayList<>();

      for (int i = 0; i < in.size(); i++) {
         Choosable c = in.get(i);
         if (c instanceof OrChoosable) {
            result.addAll(((OrChoosable)c).createAll());
         } else {
            result.add(c);
         }
      }

      return result;
   }

   private List<Choosable> createAll() {
      return ChoosableUtils.deserialiseList(this.raw, "@4");
   }

   public List<Choosable> getAll() {
      if (this.privateCache == null) {
         this.privateCache = this.createAll();
      }

      return this.privateCache;
   }

   private Choosable first() {
      return this.createAll().get(0);
   }

   @Override
   public boolean isPositive() {
      return this.first().isPositive();
   }

   @Override
   public Color getColour() {
      return this.first().getColour();
   }

   @Override
   public String getSaveString() {
      return this.raw;
   }

   public static Choosable byName(String n) {
      List<Choosable> lst = ChoosableUtils.deserialiseList(n, "@4");
      return (Choosable)(ChoosableUtils.isMissingno(lst) ? new MissingnoChoosable() : new OrChoosable(lst));
   }

   @Override
   public ChoosableType getType() {
      return ChoosableType.Or;
   }

   private Choosable getActual() {
      return Tann.random(this.createAll());
   }

   @Override
   public void onChoose(DungeonContext dc, int index) {
      Choosable c = this.getActual();
      c.onChoose(dc, index);
   }

   @Override
   public void onReject(DungeonContext dc) {
   }

   @Override
   public Actor makeChoosableActor(boolean big, int index) {
      List<Actor> actors = new ArrayList<>();
      List<Choosable> choosables = this.getAll();
      if (choosables.size() == 2 && choosables.get(0) instanceof RandomTieredChoosable && choosables.get(1) instanceof RandomTieredChoosable) {
         RandomTieredChoosable rtca = (RandomTieredChoosable)choosables.get(0);
         RandomTieredChoosable rtcb = (RandomTieredChoosable)choosables.get(1);
         if (rtca.n == 1 && rtcb.n == 1 && rtcb.ty == rtca.ty) {
            return new Pixl(3, 3)
               .border(this.getColour())
               .text("A random tier " + rtca.getTierString() + " or " + rtcb.getTierString() + " " + rtca.ty.name().toLowerCase())
               .pix();
         }
      }

      for (Choosable c : choosables) {
         actors.add(c.makeChoosableActor(big, index));
      }

      String text = Tann.halveString(com.tann.dice.Main.t("one at random"));
      return makeHackySeamless(actors, "[notranslateall]" + text, this.getColour());
   }

   public static Actor makeHackySeamless(List<Actor> actors, String text, Color border) {
      return DipPanel.makeSidePanelGroup(
         new Pixl(1).text(text).pix(), Tann.layoutMinArea(actors, 2, com.tann.dice.Main.width / 2, com.tann.dice.Main.height / 2), border
      );
   }

   @Override
   public int getTier() {
      return this.first().getTier();
   }

   @Override
   public float getModTier() {
      List<Choosable> all = this.getAll();
      return TierUtils.totalModTier(all) / all.size();
   }

   @Override
   public String describe() {
      List<String> parts = new ArrayList<>();

      for (Choosable ch : this.getAll()) {
         String s = Tann.makeEllipses(ch.getName(), TannFont.guessMaxTextLength(2.5F));
         if (ChoosableUtils.shouldBracket(ch)) {
            s = "(" + s + ")";
         }

         parts.add(s);
      }

      String sep = " [yellow]OR[cu] ";
      return Tann.commaList(parts, sep, sep);
   }

   @Override
   public float chance() {
      return 0.0F;
   }

   @Override
   public String getTierString() {
      return "?";
   }

   @Override
   public String getName() {
      return this.describe();
   }

   @Override
   public boolean encountered(Map<String, Stat> allMergedStats) {
      return false;
   }

   @Override
   public int getPicks(Map<String, Stat> allMergedStats, boolean reject) {
      return 0;
   }

   @Override
   public long getCollisionBits() {
      return 0L;
   }
}
