package io.github.redouane59.quizapp.model.quizz.views;

import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.Word;
import io.github.redouane59.quizapp.model.quizz.AbstractQuizzApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class QuizAppConsoleView extends AbstractQuizzApp {


  public QuizAppConsoleView(final String type) {
    super(type);
  }

  @Override
  public void showScore(final int score, final int totalQuestions) {
    System.out.println("final score : " + score + "/" + totalQuestions);
  }

  @Override
  public void showExtraWrongQuestionsText() {
    System.out.println("Let's try again the previous questions you answered wrongly");
  }

  @Override
  public void showPropositions(final Set<Word> propositions) {
    int i = 0;
    for (Word proposition : propositions) {
      System.out.println("  " + (char) ('A' + i) + ") " + proposition.getOutput());
      i++;
    }
  }

  @Override
  public boolean catchAnswer(Question question) {
    List<Word> propositions = new ArrayList<>(question.getPropositions());
    Scanner    scanner      = new Scanner(System.in);

    boolean isValidAnswer;
    String  userAnswer;
    do {
      System.out.print("Reponse : ");
      userAnswer    = scanner.nextLine();
      isValidAnswer = "A".equals(userAnswer) || "B".equals(userAnswer) || "C".equals(userAnswer)
                      || "D".equals(userAnswer);
      if (!isValidAnswer) {
        System.out.println("Reponse invalide, veuillez réessayer.");
      }
    } while (!isValidAnswer);

    Word userChoice = propositions.get(userAnswer.charAt(0) - 'A');
    return userChoice.equals(question.getExpectedWord());
  }

  @Override
  public void showQuestion(final Question question) {
    System.out.println("Question : " + question.getExpectedWord().getInput());
  }

  @Override
  public void showCorrectAnswer(final Word correctAnswer) {
    System.out.println("Faux, la reponse correcte etait : " + correctAnswer.getOutput());
  }

  @Override
  public void showCongrats() {
    System.out.println("Bravo, bonne reponse !");
  }
}
