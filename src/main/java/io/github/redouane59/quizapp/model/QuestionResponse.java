package io.github.redouane59.quizapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionResponse {

  private List<Question> questions;
  @JsonProperty("question_count")
  private int            questionCount;

  public QuestionResponse(List<Question> questions) {
    this.questions     = questions;
    this.questionCount = questions.size();
  }


}
