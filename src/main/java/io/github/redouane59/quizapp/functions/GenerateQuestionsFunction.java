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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenerateQuestionsFunction implements HttpFunction {

  private static final Logger logger = Logger.getLogger(GenerateQuestionsFunction.class.getName());

  // gcloud functions deploy generate-question-function --entry-point io.github.redouane59.quizapp.functions.GenerateQuestionsFunction --runtime java17 --trigger-http --memory 256MB --timeout=30 --allow-unauthenticated --project=dz-dialect-api
  // npm run build
  // firebase deploy --only hosting:train-mee
  public void service(HttpRequest request, HttpResponse response) throws IOException {
    LOGGER.debug("Entering in the Function");
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
      LOGGER.debug("starting process...");
      // Parse the CSV content from the request body
      String csvContent     = getRequestBody(request);
      String delimiterValue = request.getFirstQueryParameter("delimiter").orElse(",");
      char   delimiter      = delimiterValue.charAt(0);
      // Generate the questions from the CSV content
      Words words = WordsBuilder.buildFromCSV(csvContent, delimiter);
      if (words.getContent().isEmpty()) {
        LOGGER.error("The content could not be parsed as CSV. Check the delimiter.");
        throw new ApiException("The content could not be parsed as CSV. Check the delimiter.", 400);
      }
      // questionCount
      String questionCountValue = request.getFirstQueryParameter("question_count").orElse("5");
      int    questionCount      = 5;
      if (questionCountValue.matches("-?\\d+")) {
        questionCount = Integer.parseInt(questionCountValue);
      } else {
        LOGGER.error("questionCount incorrect format : " + questionCountValue);
      }
      // type
      String type = request.getFirstQueryParameter("type").orElse(null);
      LOGGER.debug("questionCount=" + questionCount + "&type=" + type);
      List<Question> questions = words.generateQuestions(questionCount, type);
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

  private String extractCsvContent(BufferedReader reader, String boundary) throws IOException {
    StringBuilder csvContent        = new StringBuilder();
    String        line;
    boolean       csvContentStarted = false;

    while ((line = reader.readLine()) != null) {
      if (line.startsWith("--" + boundary)) {
        csvContentStarted = !csvContentStarted;
        continue;
      }
      if (csvContentStarted) {
        if (line.startsWith("Content-Disposition:") || line.startsWith("Content-Type:")) {
          continue;
        }
        if (line.startsWith("--")) {
          break;
        }
        if (!line.equals("")) {
          csvContent.append(line).append(System.lineSeparator());
        }
      }
    }

    return csvContent.toString();
  }

  private String getRequestBody(HttpRequest request) throws IOException, ApiException {
    String contentType = request.getFirstHeader("Content-Type").orElse("");
    if (!contentType.startsWith("multipart/form-data")) {
      LOGGER.error("Invalid content type " + contentType);
      throw new ApiException("Invalid content type " + contentType, 400);
    }

    String boundary = getBoundary(contentType);

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
      String csvContent = extractCsvContent(reader, boundary);

      if (csvContent.isEmpty()) {
        LOGGER.error("empty file" + contentType);
        throw new ApiException("Empty file", 400);
      }

      LOGGER.debug("CSVContent : " + csvContent);
      return csvContent;
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