package com.tann.dice.util.translation;

class MatchTranslatorPiece {
   final MatchTranslatorPieceType type;
   final String content;
   final TagType tagType;

   MatchTranslatorPiece(MatchTranslatorPieceType type, String content) {
      this.type = type;
      this.content = content;
      if (type == MatchTranslatorPieceType.Tag) {
         if (content.startsWith("n")) {
            this.tagType = TagType.Number;
         } else if (content.startsWith("ord")) {
            this.tagType = TagType.Ord;
         } else if (content.startsWith("w") || content.startsWith("*w")) {
            this.tagType = TagType.Word;
         } else if (content.startsWith("text") || content.startsWith("*text")) {
            this.tagType = TagType.Text;
         } else if (content.startsWith("keyword") || content.startsWith("*keyword")) {
            this.tagType = TagType.Keyword;
         } else if (content.startsWith("skeyword") || content.startsWith("*skeyword")) {
            this.tagType = TagType.SimpleKeyword;
         } else if (content.startsWith("sidetype") || content.startsWith("*sidetype")) {
            this.tagType = TagType.Sidetype;
         } else if (content.startsWith("tag")) {
            this.tagType = TagType.Tag;
         } else {
            System.err.println("Unrecognised pattern type");
            this.tagType = null;
         }
      } else {
         this.tagType = null;
      }
   }
}
