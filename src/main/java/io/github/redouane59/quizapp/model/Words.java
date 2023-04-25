package io.github.redouane59.quizapp.model;

import io.github.redouane59.quizapp.functions.ApiException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Words {

  Set<Word> content = new HashSet<>();

  public List<Question> generateQuestions(int nbQuestions) throws ApiException {
    if (nbQuestions <= 0) {
      return generateAllQuestions();
    }
    return generateQuestions(nbQuestions, null);
  }

  public List<Question> generateQuestions(int nbQuestions, String type) throws ApiException {
    List<Question> questions = new ArrayList<>();

    for (int i = 0; i < nbQuestions; i++) {
      Word expectedWord;
      long nbPossiblePropositions = this.content.size();
      if (type != null) {
        expectedWord = getRandomWord(type);
      } else {
        expectedWord = getRandomWord();
      }
      if (expectedWord.getType() != null) {
        nbPossiblePropositions = this.content.stream().filter(p -> p.getType().equals(expectedWord.getType())).count();
      }
      Set<Word> propositions = new HashSet<>();
      propositions.add(expectedWord);
      int nbPropositions = 4;
      while (propositions.size() < nbPropositions) {
        Word randomWord;
        if (nbPossiblePropositions > nbPropositions) {
          randomWord = getRandomWord(expectedWord.getType());
        } else {
          randomWord = getRandomWord();
        }
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

  public List<Question> generateAllQuestions() throws ApiException {
    List<Question> questions = new ArrayList<>();

    for (Word expectedWord : content) {
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
    LOGGER.debug("adding word " + word.getInput());
    this.content.add(word);
  }

  // @todo check if enough result
  private Word getRandomWord(String type) throws ApiException {
    List<Word> contentList = new ArrayList<>(content);
    List<Word> matches = contentList.stream()
                                    .filter(w -> w.getType() != null)
                                    .filter(w -> w.getType().equals(type)).toList();
    if (matches.size() < 4) {
      return getRandomWord();
    }
    int randomIndex = ThreadLocalRandom.current().nextInt(matches.size());
    return matches.get(randomIndex);
  }

  private Word getRandomWord() throws ApiException {
    if (content.size() < 5) {
      LOGGER.error("Not able to get a random word cause content.size()=" + content.size());
      throw new ApiException("Not enough words. Min = 5 words", 400);
    }
    List<Word> contentList = new ArrayList<>(content);
    int        randomIndex = ThreadLocalRandom.current().nextInt(contentList.size());
    return contentList.get(randomIndex);
  }

}
