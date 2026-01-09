package com.tann.dice.gameplay.content.gen.pipe.entity.hero;

import com.tann.dice.gameplay.content.ent.type.HeroType;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeLib;
import com.tann.dice.gameplay.content.ent.type.lib.HeroTypeUtils;
import com.tann.dice.gameplay.content.gen.pipe.regex.PipeRegexNamed;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.PRNPart;
import com.tann.dice.gameplay.content.gen.pipe.regex.prnPart.pos.PRNMid;
import com.tann.dice.gameplay.trigger.personal.chat.ReplaceChat;
import com.tann.dice.util.Separators;
import com.tann.dice.util.Tann;
import java.util.Arrays;

public class PipeHeroSpeech extends PipeRegexNamed<HeroType> {
   private static final PRNPart SEP = new PRNMid("speech");
   private static final String CHAT_SEP = ":";

   public PipeHeroSpeech() {
      super(HERO, SEP, RICH_TEXT_MULTI);
   }

   protected HeroType internalMake(String[] groups) {
      return this.make(groups[0], groups[1].split(":"));
   }

   private HeroType make(String heroName, String[] sayings) {
      if (heroName != null && sayings != null && !heroName.isEmpty() && sayings.length != 0) {
         for (String saying : sayings) {
            if (Separators.bannedFromDocument(saying)) {
               return null;
            }
         }

         String realHeroName = heroName + SEP + Tann.commaList(Arrays.asList(sayings), ":", ":");
         return HeroTypeUtils.withPassive(HeroTypeLib.byName(heroName), realHeroName, new ReplaceChat(sayings));
      } else {
         return null;
      }
   }

   public HeroType example() {
      return this.make(HeroTypeUtils.random().getName(), new String[]{"example chat 1", "example chat 2", "example chat 3"});
   }

   @Override
   public boolean isComplexAPI() {
      return true;
   }
}
