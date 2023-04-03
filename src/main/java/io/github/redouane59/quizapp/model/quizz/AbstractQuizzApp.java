package io.github.redouane59.quizapp.model.quizz;

import io.github.redouane59.quizapp.functions.ApiException;
import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.Word;
import io.github.redouane59.quizapp.model.Words;
import io.github.redouane59.quizapp.model.WordsBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Data;

@Data
public abstract class AbstractQuizzApp implements IQuizApp {

  private int       goodAnswerScore            = 1;
  private int       badAnswerScore             = 0;
  private boolean   skipAlreadyAnsweredWord    = true;
  private boolean   displayUnknowWordsAtTheEnd = true;
  private Set<Word> knownWords                 = new HashSet<>();
  private Set<Word> unknownWords               = new HashSet<>();

  private String type;

  public AbstractQuizzApp(String type) {
    this.type = type;
  }

  /**
   * Start the quiz
   *
   * @param nbQuestions number of questions to be displayed
   */
  public void start(int nbQuestions) throws ApiException {
    int score         = 0;
    int i             = 0;
    int questionCount = 0;
    // in case we want to remove some questions during the tests because word is already mastered
    List<Question> questions = generateQuestions(2 * nbQuestions, type);

    while (questionCount < nbQuestions && i < questions.size()) {
      Question question = questions.get(i);
      if (!skipAlreadyAnsweredWord || !knownWords.contains(question.getExpectedWord())) {
        int result = askQuestion(question);
        questionCount++;
        score += result;
        if (result > 0) {
          knownWords.add(question.getExpectedWord());
        } else {
          unknownWords.add(question.getExpectedWord());
        }
      }
      i++;
    }
    if (displayUnknowWordsAtTheEnd && unknownWords.size() > 0) {
      showExtraWrongQuestionsText();
      for (Word word : unknownWords) {
        Optional<Question> question = questions.stream().filter(q -> q.getExpectedWord() == word).findAny();
        question.ifPresent(this::askQuestion);
      }
    }

    this.showScore(score, questionCount);
  }

  /**
   * Ask a new question to the user
   *
   * @param question the question to be displayed
   * @return the score impact depending on the answer
   */
  public int askQuestion(final Question question) {

    showQuestion(question);
    showPropositions(question.getPropositions());
    boolean result = catchAnswer(question);
    if (result) {
      showCongrats();
      return getGoodAnswerScore();
    } else {
      showCorrectAnswer(question.getExpectedWord());
      return getBadAnswerScore();
    }

  }


  public List<Question> generateQuestions(int nbQuestions) throws ApiException {
    Words words = WordsBuilder.build("src/main/resources/words.csv");
    return words.generateQuestions(nbQuestions);
  }

  public List<Question> generateQuestions(int nbQuestions, String type) throws ApiException {
    Words words = WordsBuilder.build("src/main/resources/words.csv");

    return words.generateQuestions(nbQuestions, type);
  }
}
