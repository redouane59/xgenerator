package io.github.redouane59.quizapp.model.quizz;

import io.github.redouane59.quizapp.functions.ApiException;
import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.Word;
import java.util.Set;

public interface IQuizApp {

  /**
   * Start the quiz
   *
   * @param nbQuestions number of questions to be displayed
   */
  void start(int nbQuestions) throws ApiException;


  /**
   * Display the question
   *
   * @param question the question to be asked
   */
  void showQuestion(final Question question);

  /**
   * Display the propositions
   *
   * @param propositions the propositions to be displayed
   */
  void showPropositions(final Set<Word> propositions);

  /**
   * Get the answer of the user
   *
   * @param question the related question
   * @return true if the answer was correct, else false
   */
  boolean catchAnswer(Question question);

  /**
   * Display something in case the answer was right
   */
  void showCongrats();

  /**
   * Display something in case the answer was wrong
   *
   * @param correctAnswer the expected answer
   */
  void showCorrectAnswer(Word correctAnswer);

  /**
   * Show the final score
   *
   * @param score score of the quiz
   * @param totalQuestions number of answered questions
   */
  void showScore(int score, int totalQuestions);

  /**
   * Display the announcement of the wrongly answered question repetition
   */
  void showExtraWrongQuestionsText();

}

