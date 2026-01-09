package com.tann.dice.gameplay.content.gen.pipe.entity.monster;

import com.tann.dice.gameplay.content.ent.EntSize;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.die.side.blob.EntSidesBlobSmall;
import com.tann.dice.gameplay.content.ent.type.MonsterType;
import com.tann.dice.gameplay.content.ent.type.bill.MTBill;
import com.tann.dice.gameplay.content.ent.type.blob.monster.MonsterTypeBlobNightmare;
import com.tann.dice.gameplay.content.gen.pipe.Pipe;
import com.tann.dice.gameplay.content.gen.pipe.PipeMaster;
import com.tann.dice.gameplay.content.gen.pipe.PipeUtils;
import com.tann.dice.gameplay.content.gen.pipe.mod.PipeCache;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaBracketed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaDocument;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaIndexed;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaRename;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.PipeMetaX;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSource;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.srcAlg.DataSourceMonster;
import com.tann.dice.gameplay.content.gen.pipe.regex.meta.texture.PipeMetaTexture;
import com.tann.dice.gameplay.effect.Trait;
import com.tann.dice.gameplay.trigger.global.chance.Rarity;
import com.tann.dice.gameplay.trigger.personal.death.DamageAdjacentsOnDeath;
import com.tann.dice.statics.sound.Sounds;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class PipeMonster {
   public static List<Pipe<MonsterType>> pipes;
   private static PipeCache<MonsterType> pmc;
   private static MonsterType missingno;

   public static void init(List<MonsterType> allMonsters) {
      DataSource<MonsterType> ds = new DataSourceMonster();
      pipes = new ArrayList<>();
      pipes.add(new PipeMaster<>(allMonsters));
      pipes.add(pmc = new PipeCache<>());
      pipes.add(new PipeMaster<>(makeSecretMonsters()));
      pipes.add(new PipeMonsterGenerated());
      pipes.addAll(PipeMetaRename.makeAll(ds));
      pipes.add(new PipeMonsterEgg());
      pipes.add(new PipeMonsterHP());
      pipes.add(new PipeMonsterSides());
      pipes.addAll(PipeMetaX.makeAll(ds));
      pipes.addAll(PipeMetaTexture.makeAll(ds));
      pipes.addAll(PipeMetaIndexed.makeAll(ds));
      pipes.addAll(PipeMetaBracketed.makeAll(ds));
      pipes.addAll(PipeMetaDocument.makeAll(ds));
      pipes.add(new PipeMonsterJinx());
      pipes.add(new PipeMonsterVase());
      pipes.add(new PipeMonsterOrb());
      pipes.add(new PipeMonsterTraited());
      pipes.add(new PipeMonsterBalanced());
      pipes.add(new PipeMonsterItem());
      missingno = createMissingno();
   }

   private static List<MonsterType> makeSecretMonsters() {
      List<MonsterType> ls = new ArrayList<>(MonsterTypeBlobNightmare.make());
      ls.addAll(
         Arrays.asList(
            new MTBill(EntSize.small)
               .name("Test Bones")
               .hp(4)
               .death(Sounds.deathPew)
               .sides(
                  EntSidesBlobSmall.arrow.val(3),
                  EntSidesBlobSmall.arrow.val(3),
                  EntSidesBlobSmall.arrow.val(3),
                  EntSidesBlobSmall.arrow.val(3),
                  EntSidesBlobSmall.arrow.val(3),
                  EntSidesBlobSmall.arrow.val(3)
               )
               .trait(new Trait(new DamageAdjacentsOnDeath(1)))
               .bEntType(),
            new MTBill(EntSize.reg)
               .name("Test Goblin")
               .hp(5)
               .death(Sounds.deathReg)
               .sides(ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmg.val(2), ESB.dmg.val(1), ESB.dmg.val(1), ESB.dmg.val(1))
               .bEntType()
         )
      );
      return ls;
   }

   @Nonnull
   public static MonsterType fetch(String name) {
      return !Pipe.DANGEROUS_NONMODIFIER_PIPE_CHARS.matcher(name).matches() ? getMissingno() : Pipe.checkPipes(pipes, name, pmc, getMissingno());
   }

   public static MonsterType getMissingno() {
      return missingno;
   }

   public static MonsterType createMissingno() {
      return new MTBill(EntSize.reg)
         .name("error")
         .hp(5)
         .death(Sounds.deathReg)
         .sides(ESB.wandChaos.val(3), ESB.blank, ESB.dmgDeath.val(153), ESB.blank, ESB.blank, ESB.dmgCleave.val(9))
         .rarity(Rarity.THOUSANDTH)
         .bEntType();
   }

   public static MonsterType makeGen() {
      boolean wild = false;
      int attempts = 20;
      List<Pipe<MonsterType>> gennablePipes = getGenPipes(wild);

      for (int i = 0; i < 20; i++) {
         Pipe<MonsterType> pm = randomPipeForGen(gennablePipes, wild);
         MonsterType mt = pm.generate(wild);
         if (mt != null && !mt.isMissingno()) {
            return mt;
         }
      }

      return getMissingno();
   }

   private static Pipe<MonsterType> randomPipeForGen(List<Pipe<MonsterType>> gennablePipes, boolean wild) {
      return PipeUtils.randomPipeForGen(gennablePipes, wild);
   }

   private static List<Pipe<MonsterType>> getGenPipes(boolean wild) {
      return PipeUtils.getGenPipes(pipes, wild);
   }

   public static MonsterType byCache(String text) {
      return pmc.get(text);
   }

   public static void clearCache() {
      pmc.cc();
   }
}
