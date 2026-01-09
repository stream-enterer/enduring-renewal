package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.EntType;
import com.tann.dice.gameplay.content.ent.type.HeroCol;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.bill.HTBill;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeMaster;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroAdjust;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.generate.PipeHeroGenerated;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.side.PipeHeroSides;
import com.tann.dice.gameplay.content.gen.pipe.entity.hero.side.PipeHeroSidesMini;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeCache;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaBracketed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaDocument;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRandomTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRename;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaSetTier;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaX;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceHero;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.PipeMetaTexture;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.trigger.global.Global;
import com.tann.dice.gameplay.trigger.global.heroLevelupAffect.HeroGenType;
import com.tann.dice.gameplay.trigger.personal.RenameHero;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PipeHero {
   private static HeroType missingno;
   private static PipeCache<HeroType> pmc;
   public static List<Pipe<HeroType>> pipes;

   public static void init(List<HeroType> designed) {
      DataSource<HeroType> ds = new DataSourceHero();
      pipes = new ArrayList<>();
      pipes.add(new PipeMaster<>(designed));
      pipes.add(pmc = new PipeCache<>());
      pipes.add(new PipeHeroTw1n());
      pipes.add(new PipeHeroGenerated());
      pipes.add(new PipeHeroHP());
      pipes.add(new PipeHeroCol());
      pipes.add(new PipeHeroAbility());
      pipes.add(new PipeHeroReplica());
      pipes.addAll(PipeMetaSetTier.makeAll(ds));
      pipes.add(new PipeHeroAdjust());
      pipes.addAll(PipeMetaRename.makeAll(ds));
      pipes.add(new PipeHeroSides());
      pipes.add(new PipeHeroSidesMini());
      pipes.add(new PipeHeroSpeech());
      pipes.addAll(PipeMetaX.makeAll(ds));
      pipes.add(new PipeMetaRandomTier<>(ds));
      pipes.addAll(PipeMetaTexture.makeAll(ds));
      pipes.addAll(PipeMetaIndexed.makeAll(ds));
      pipes.addAll(PipeMetaDocument.makeAll(ds));
      pipes.addAll(PipeMetaBracketed.makeAll(ds));
      pipes.add(new PipeHeroItem());
      pipes.add(new PipeHeroGift());
      missingno = makeMissingno();
   }

   private static HeroType makeMissingno() {
      return new HTBill(HeroCol.violet, -1)
         .name("Glitch")
         .hp(6)
         .texture("special/glitch")
         .sides(ESB.wandFightBonus.val(1), ESB.blank, ESB.wandStun, ESB.shieldCrescent.val(1), ESB.heal.val(666).withKeyword(Keyword.rampage), ESB.blank)
         .bEntType();
   }

   @Nonnull
   public static HeroType fetch(String name) {
      return !Pipe.DANGEROUS_NONMODIFIER_PIPE_CHARS.matcher(name).matches() ? getMissingno() : Pipe.checkPipes(pipes, name, pmc, getMissingno());
   }

   public static HeroType getMissingno() {
      return missingno;
   }

   public static HeroGenType getGenType(List<Global> globals) {
      for (int i = 0; i < globals.size(); i++) {
         HeroGenType hgt = globals.get(i).generateHeroes();
         if (hgt != null) {
            return hgt;
         }
      }

      return HeroGenType.Normal;
   }

   public static boolean hasRename(EntType result) {
      for (Trait trait : result.traits) {
         if (trait.personal instanceof RenameHero) {
            return true;
         }
      }

      return false;
   }

   public static HeroType byCache(String text) {
      return pmc.get(text);
   }

   public static void clearCache() {
      pmc.cc();
   }
}
