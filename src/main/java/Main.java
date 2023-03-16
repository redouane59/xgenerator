import model.QuizApp;

public class Main {
  public static void main(String[] args) {
    System.setProperty("console.encoding", "UTF-8");
    QuizApp quizzApp = new QuizApp();
    quizzApp.askQuestions();
  }
}