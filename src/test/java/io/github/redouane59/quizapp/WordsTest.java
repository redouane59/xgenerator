package io.github.redouane59.quizapp;

import static org.junit.Assert.assertEquals;

import io.github.redouane59.quizapp.functions.ApiException;
import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.Word;
import io.github.redouane59.quizapp.model.Words;
import java.util.List;
import org.junit.Test;

public class WordsTest {

  @Test
  public void testGenerateQuestions() throws ApiException {
    Words words = new Words();
    words.addWord(new Word("hello", "bonjour", "noun"));
    words.addWord(new Word("world", "monde", "noun"));
    words.addWord(new Word("cat", "chat", "noun"));
    words.addWord(new Word("dog", "chien", "noun"));
    words.addWord(new Word("goodbye", "au revoir", "noun"));

    List<Question> questions = words.generateQuestions(10);

    assertEquals(10, questions.size());

    for (Question question : questions) {
      assertEquals(4, question.getPropositions().size());
      System.out.println("---");
      System.out.println("Que veut dire " + question.getExpectedWord().getInput() + "?");
      question.getPropositions().forEach(o -> System.out.println(o.getOutput()));
    }
  }


}
