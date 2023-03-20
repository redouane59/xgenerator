package io.github.redouane59.quizapp.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import io.github.redouane59.quizapp.model.Question;
import io.github.redouane59.quizapp.model.QuestionResponse;
import io.github.redouane59.quizapp.model.Words;
import io.github.redouane59.quizapp.model.WordsBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class GenerateQuestionsFunction implements HttpFunction {

  public void service(HttpRequest request, HttpResponse response) throws IOException {
    response.appendHeader("Access-Control-Allow-Origin", "http://localhost:8081");
    response.appendHeader("Access-Control-Allow-Methods", "POST");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    // Parse the CSV content from the request body
    String csvContent = getRequestBody(request);

    // Generate the questions from the CSV content
    Words          words     = WordsBuilder.buildFromCSV(csvContent);
    List<Question> questions = words.generateQuestions(10);

    // Write the questions to the response body as JSON
    ObjectMapper mapper = new ObjectMapper();
    String       json   = mapper.writeValueAsString(new QuestionResponse(questions));

    response.setContentType("application/json");
    response.setStatusCode(200);
    response.getWriter().write(json);
  }

  private String getRequestBody(HttpRequest request) throws IOException {
    String contentType = request.getFirstHeader("Content-Type").orElse("");
    if (!contentType.startsWith("multipart/form-data")) {
      throw new IllegalArgumentException("Invalid content type");
    }

    String boundary = getBoundary(contentType);

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
      StringBuilder requestBody       = new StringBuilder();
      String        line;
      boolean       csvContentStarted = false;

      while ((line = reader.readLine()) != null) {
        if (line.startsWith("--" + boundary)) {
          csvContentStarted = true;
          continue;
        }
        if (line.startsWith("Content-Disposition:")) {
          continue;
        }
        if (line.startsWith("Content-Type:")) {
          continue;
        }
        if (csvContentStarted) {
          if (line.startsWith("--")) {
            break;
          }
          if (!line.equals("")) {
            requestBody.append(line).append(System.lineSeparator());
          }
        }
      }

      return requestBody.toString();
    }
  }

  private String getBoundary(String contentType) {
    String[] parts = contentType.split(";");
    for (String part : parts) {
      part = part.trim();
      if (part.startsWith("boundary=")) {
        return part.substring("boundary=".length());
      }
    }
    return "";
  }


}