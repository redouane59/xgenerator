package io.github.redouane59.quizapp.model.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.Words;
import io.github.redouane59.quizapp.model.WordsBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class GenerateQuestionsFunction implements HttpFunction {

  public void service(HttpRequest request, HttpResponse response) throws IOException {

    // Parse the CSV content from the request body
    String csvContent = getRequestBody(request);

    // Generate the questions from the CSV content
    Words          words     = WordsBuilder.buildFromCSV(csvContent);
    List<Question> questions = words.generateQuestions(10);

    // Write the questions to the response body as JSON
    ObjectMapper mapper = new ObjectMapper();
    String       json   = mapper.writeValueAsString(questions);

    response.setContentType("application/json");
    response.setStatusCode(200);
    response.getWriter().write(json);
  }

  private String getRequestBody(HttpRequest request) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
      StringBuilder requestBody = new StringBuilder();
      String        line;
      while ((line = reader.readLine()) != null) {
        requestBody.append(line).append(System.lineSeparator());
      }
      return requestBody.toString();
    }
  }


}