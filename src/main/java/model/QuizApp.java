package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class QuizApp {

  public void askQuestions() {
    Words words = WordsBuilder.build("src/main/resources/words.csv");

    List<Question> questions = words.generateQuestions(20);
    int            score     = 0;
    Scanner        scanner   = new Scanner(System.in);

    for (Question question : questions) {
      System.out.println("Question : " + question.getExpectedWord().getInput());
      List<Word> shuffledPropositions = new ArrayList<>(question.getPropositions());
      Collections.shuffle(shuffledPropositions);

      int i = 0;
      for (Word proposition : shuffledPropositions) {
        System.out.println("  " + (char) ('A' + i) + ") " + proposition.getOutput());
        i++;
      }

      boolean isValidAnswer = false;
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

      Word userChoice = shuffledPropositions.get(userAnswer.charAt(0) - 'A');
      if (userChoice.equals(question.getExpectedWord())) {
        System.out.println("Bravo, bonne reponse !");
        score++;
      } else {
        System.out.println("Dommage, la reponse correcte etait : " + question.getExpectedWord().getOutput());
      }
      System.out.println();
    }
    System.out.println("Score : " + score + "/" + questions.size());
  }


}
