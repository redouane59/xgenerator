package io.github.redouane59.quizapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

  @JsonProperty("expected_word")
  private Word expectedWord;

  private Set<Word> propositions;

}
