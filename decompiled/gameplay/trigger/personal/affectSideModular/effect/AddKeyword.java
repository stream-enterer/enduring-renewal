package com.tann.dice.gameplay.trigger.personal.affectSideModular.effect;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.tann.dice.gameplay.content.ent.die.side.EntSide;
import com.tann.dice.gameplay.content.ent.die.side.blob.ESB;
import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.effect.eff.Eff;
import com.tann.dice.gameplay.effect.eff.keyword.KUtils;
import com.tann.dice.gameplay.effect.eff.keyword.Keyword;
import com.tann.dice.gameplay.fightLog.EntSideState;
import com.tann.dice.gameplay.fightLog.EntState;
import com.tann.dice.gameplay.trigger.Collision;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.AffectSides;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.EffectDraw;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.AffectSideCondition;
import com.tann.dice.gameplay.trigger.personal.affectSideModular.condition.HasKeyword;
import com.tann.dice.util.Tann;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddKeyword extends AffectSideEffect {
   final List<Keyword> keywordList;
   final List<String> strings = new ArrayList<>();

   public AddKeyword(Keyword... keyword) {
      this(Arrays.asList(keyword));
   }

   public AddKeyword(List<Keyword> keywordList) {
      this.keywordList = keywordList;

      for (Keyword k : keywordList) {
         this.strings.add(k.getColourTaggedString());
      }
   }

   @Override
   public EffectDraw getAddDraw(boolean hasSideImage, List<AffectSideCondition> conditions) {
      final List<Keyword> keywords = new ArrayList<>();

      for (AffectSideCondition condition : conditions) {
         if (condition instanceof HasKeyword) {
            keywords.addAll(Arrays.asList(((HasKeyword)condition).keywords));
         }
      }

      keywords.addAll(this.keywordList);
      return new EffectDraw() {
         @Override
         public void draw(Batch batch, int x, int y) {
            for (int index = 0; index < keywords.size(); index++) {
               Keyword keyword = keywords.get(index);
               EntSide.drawBonusKeyword(batch, x, y, keyword, index);
            }
         }
      };
   }

   @Override
   public String describe() {
      List<String> list = new ArrayList<>();

      for (Keyword k : this.keywordList) {
         list.add(k.getColourTaggedString());
      }

      return "Add " + Tann.commaList(list);
   }

   @Override
   protected String getGeneralDescription(AffectSideEffect[] affectSideEffectList) {
      List<String> keywordsString = new ArrayList<>();

      for (AffectSideEffect e : affectSideEffectList) {
         if (e instanceof AddKeyword) {
            for (Keyword k : ((AddKeyword)e).keywordList) {
               keywordsString.add(k.getColourTaggedString());
            }
         }
      }

      Tann.clearDupes(keywordsString);
      return "Add " + Tann.commaList(keywordsString, "/", "/") + " to";
   }

   @Override
   public void affect(EntSideState sideState, EntState owner, int index, AffectSides sourceTrigger, int sourceIndex) {
      sideState.getCalculatedEffect().addKeywords(this.keywordList);
   }

   @Override
   public boolean needsGraphic() {
      return true;
   }

   @Override
   public boolean isRecommended(EntState sourceState, EntState targetPresent, EntState targetFuture) {
      if (sourceState != targetPresent && !targetPresent.isUsed()) {
         EntSide current = targetPresent.getEnt().getDie().getCurrentSide();
         if (current == null) {
            return true;
         } else {
            Eff calculated = targetPresent.getSideState(current).getCalculatedEffect();

            for (Keyword k : this.keywordList) {
               if (KUtils.allowAddingKeyword(k, calculated)) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public float getEffectTier(int pips, int tier) {
      if (this.keywordList.get(0) == Keyword.cleanse) {
         return 1.2F;
      } else {
         EntSide baseSide = ESB.dmg.val(2 + tier);
         HeroType ht = HeroTypeUtils.defaultHero(tier);
         float basePower = baseSide.getEffectTier(ht);
         EntSide testSide = baseSide.withKeyword(this.keywordList.toArray(new Keyword[0]));
         float testPower = testSide.getEffectTier(ht);
         float result = testPower - basePower;
         float kExtraMult = 0.7F;

         for (Keyword k : this.keywordList) {
            switch (k) {
               case deathwish:
                  kExtraMult *= 1.28F;
                  break;
               case cruel:
                  kExtraMult *= 1.18F;
                  break;
               case manaGain:
                  kExtraMult *= 1.35F;
            }

            if (k.getAllowType().toString().toLowerCase().contains("kind")) {
               kExtraMult *= 0.8F;
            }
         }

         float add = 0.0F;
         if (this.keywordList.contains(Keyword.growth)) {
            add = -0.35F + tier * 0.3F;
         }

         return result * kExtraMult + add;
      }
   }

   public List<Keyword> getKeywordList() {
      return this.keywordList;
   }

   @Override
   public List<Keyword> getReferencedKeywords() {
      return this.getKeywordList();
   }

   @Override
   public long getCollisionBits(Boolean player) {
      long bit = 0L;

      for (int i = 0; i < this.keywordList.size(); i++) {
         bit |= this.keywordList.get(i).getCollisionBits();
      }

      bit |= Collision.keyword(player);
      return Collision.ignored(bit, Collision.PHYSICAL_DAMAGE | Collision.SHIELD | Collision.HEAL);
   }

   @Override
   public boolean skipMultipliable() {
      return true;
   }

   @Override
   public TextureRegion overrideImage() {
      return this.keywordList.get(0).getImage();
   }
}
