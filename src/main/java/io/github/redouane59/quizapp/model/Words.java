package io.github.redouane59.quizapp.model;

import io.github.redouane59.quizapp.functions.ApiException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
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
    if (nbQuestions <= 0) {
      return generateAllQuestions(type);
    }

    List<Question> questions      = new ArrayList<>();
    Set<Word>      remainingWords = new HashSet<>(content); // to avoid duplicate in random selection

    for (int i = 0; i < nbQuestions; i++) {
      Word expectedWord;
      if (remainingWords.isEmpty()) {
        remainingWords = new HashSet<>(content);
      }
      long nbPossiblePropositions = this.content.size();
      if (type != null) {
        expectedWord = getRandomWord(remainingWords, type);
      } else {
        expectedWord = getRandomWord(remainingWords);
      }
      remainingWords.remove(expectedWord);
      if (expectedWord.getType() != null) {
        nbPossiblePropositions = this.content.stream().filter(p -> p.getType().equals(expectedWord.getType())).count();
      }
      Set<Word> propositions = new HashSet<>();
      propositions.add(expectedWord);
      int nbPropositions = 4;
      while (propositions.size() < nbPropositions) {
        Word randomWord;
        if (nbPossiblePropositions > nbPropositions) {
          randomWord = getRandomWord(content, expectedWord.getType());
        } else {
          randomWord = getRandomWord(content);
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
    return generateAllQuestions(null);
  }

  public List<Question> generateAllQuestions(String type) throws ApiException {
    List<Question> questions       = new ArrayList<>();
    Set<Word>      matchingContent = new HashSet<>(content);
    if (type != null) {
      matchingContent = content.stream().filter(w -> type.equals(w.getType())).collect(Collectors.toSet());
    }
    for (Word expectedWord : matchingContent) {
      Set<Word> propositions = new HashSet<>();
      propositions.add(expectedWord);
      while (propositions.size() < 4) {
        Word randomWord = getRandomWord(matchingContent, expectedWord.getType());
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
  private Word getRandomWord(Set<Word> words, String type) throws ApiException {
    List<Word> contentList = new ArrayList<>(words);
    List<Word> matches = contentList.stream()
                                    .filter(w -> w.getType() != null)
                                    .filter(w -> w.getType().equals(type)).toList();
    if (matches.size() < 4) {
      return getRandomWord(content);
    }
    int randomIndex = ThreadLocalRandom.current().nextInt(matches.size());
    return matches.get(randomIndex);
  }

  private Word getRandomWord(Set<Word> words) throws ApiException {
    if (words.isEmpty()) {
      LOGGER.error("Not able to get a random word because no more available word");
      throw new ApiException("No more available words", 400);
    }
    List<Word> contentList = new ArrayList<>(words);
    int        randomIndex = ThreadLocalRandom.current().nextInt(contentList.size());
    return contentList.get(randomIndex);
  }

}
