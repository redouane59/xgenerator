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
import java.util.logging.Logger;

public class GenerateQuestionsFunction implements HttpFunction {

  private static final Logger logger = Logger.getLogger(GenerateQuestionsFunction.class.getName());

  // gcloud functions deploy generate-question-function --entry-point io.github.redouane59.quizapp.functions.GenerateQuestionsFunction --runtime java17 --trigger-http --memory 256MB --timeout=30 --allow-unauthenticated --project=dz-dialect-api
  public void service(HttpRequest request, HttpResponse response) throws IOException {
    response.appendHeader("Access-Control-Allow-Origin", "*");
    response.appendHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
    response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

    // Vérifier la méthode de la requête
    if (request.getMethod().equals("OPTIONS")) {
      // Si la méthode est OPTIONS, renvoyer une réponse 200 OK sans corps
      response.setStatusCode(200);
      return;
    }

    try {
      // Parse the CSV content from the request body
      String csvContent     = getRequestBody(request);
      String delimiterValue = request.getFirstQueryParameter("delimiter").orElse(",");
      char   delimiter      = delimiterValue.charAt(0);
      // Generate the questions from the CSV content
      Words words = WordsBuilder.buildFromCSV(csvContent, delimiter);
      if (words.getContent().isEmpty()) {
        throw new ApiException("No words found from the submited content. Check the delimiter.", 400);
      }
      String questionCountValue = request.getFirstQueryParameter("question_count").orElse("0");
      int    questionCount      = 0;
      if (questionCountValue.matches("-?\\d+")) {
        questionCount = Integer.parseInt(questionCountValue);
      }
      List<Question> questions = words.generateQuestions(questionCount);

      // Write the questions to the response body as JSON
      ObjectMapper mapper = new ObjectMapper();
      String       json   = mapper.writeValueAsString(new QuestionResponse(questions));

      response.setContentType("application/json");
      response.setStatusCode(200);
      response.getWriter().write(json);
    } catch (ApiException e) {
      response.setStatusCode(e.getStatusCode());
      response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
    }
  }


  private String getRequestBody(HttpRequest request) throws IOException, ApiException {
    String contentType = request.getFirstHeader("Content-Type").orElse("");
    if (!contentType.startsWith("multipart/form-data")) {
      throw new ApiException("Invalid content type", 400);
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

      if (requestBody.length() == 0) {
        throw new ApiException("Empty file", 400);
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