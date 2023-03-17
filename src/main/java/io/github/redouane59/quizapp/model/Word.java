package io.github.redouane59.quizapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Word {

  private String input;
  private String output;
  private String type;

  public Word(String input, String output) {
    this.input  = input;
    this.output = output;
    this.type   = "";
  }

}