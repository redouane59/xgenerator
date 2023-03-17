package io.github.redouane59.quizapp;

import io.github.redouane59.quizapp.model.quizz.views.QuizAppConsoleView;

public class Main {

  public static void main(String[] args) {
    System.setProperty("console.encoding", "UTF-8");
    String type = null;
    if (args.length > 0) {
      type = args[0];
    }
    QuizAppConsoleView quizzApp = new QuizAppConsoleView(type);
    quizzApp.start(20);
  }
}