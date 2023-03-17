package io.github.redouane59.quizapp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Words {

  Set<Word> content = new HashSet<>();

  public List<Question> generateQuestions(int nbQuestions) {
    return generateQuestions(nbQuestions, null);
  }

  public List<Question> generateQuestions(int nbQuestions, String type) {
    List<Question> questions = new ArrayList<>();

    for (int i = 0; i < nbQuestions; i++) {
      Word expectedWord;
      if (type != null) {
        expectedWord = getRandomWord(type);
      } else {
        expectedWord = getRandomWord();
      }
      Set<Word> propositions = new HashSet<>();
      propositions.add(expectedWord);

      while (propositions.size() < 4) {
        Word randomWord = getRandomWord(expectedWord.getType());
        if (!randomWord.getInput().equals(expectedWord.getInput())
            && !randomWord.getOutput().equals(expectedWord.getInput())) {
          propositions.add(randomWord);
        }
      }

      Question question = new Question();
      question.setExpectedWord(expectedWord);
      question.setPropositions(propositions);
      questions.add(question);
    }

    return questions;
  }


  public void addWord(final Word word) {
    this.content.add(word);
  }

  // @todo check if enough result
  private Word getRandomWord(String type) {
    List<Word> contentList = new ArrayList<>(content);
    List<Word> matches = contentList.stream()
                                    .filter(w -> w.getType().equals(type)).toList();
    if (matches.size() < 4) {
      System.err.println("not enough matching");
      return getRandomWord();
    }
    int randomIndex = ThreadLocalRandom.current().nextInt(matches.size());
    return matches.get(randomIndex);
  }

  private Word getRandomWord() {
    if (content.size() < 10) {
      throw new IllegalArgumentException("not enough worsd");
    }
    List<Word> contentList = new ArrayList<>(content);
    int        randomIndex = ThreadLocalRandom.current().nextInt(contentList.size());
    return contentList.get(randomIndex);
  }

}
