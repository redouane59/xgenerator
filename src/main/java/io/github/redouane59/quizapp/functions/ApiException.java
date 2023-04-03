package io.github.redouane59.quizapp.functions;

import lombok.Getter;

@Getter
public
class ApiException extends Exception {

  private int statusCode;

  public ApiException(String message, int statusCode) {
    super(message);
    this.statusCode = statusCode;
  }


}